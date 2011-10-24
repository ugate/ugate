package org.ugate.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;

/**
 * Mail provider for sending UGate results and receiving incoming commands. 
 * NOTE: Due to IMAP idle processes constantly listening for new messages the process is blocking.
 */
public class EmailAgent implements Runnable {

	private static final Logger log = Logger.getLogger(EmailAgent.class);
	public static final String GMAIL_SMTP_HOST = "smtp.gmail.com";
	public static final String GMAIL_IMAP_HOST = "imap.gmail.com";
	public static final String GMAIL_STMP_TLS_PORT = "587";
	public static final String GMAIL_STMP_SSL_PORT = "465";
	public static final String GMAIL_IMAP_PORT = "993";

	private static final List<IEmailListener> LISTENERS = new CopyOnWriteArrayList<IEmailListener>();
	private AtomicBoolean runIt = new AtomicBoolean(true);
	private final URLName smtpUrlName;
	private final URLName imapUrlName;
	private final String mainFolderName;
	private final boolean debug;
	private final Properties props;
	private final Thread emailThread;
	
	private volatile IMAPFolder mainFolder;
	private volatile IMAPStore store;
	
	public EmailAgent(String username, String password, String mainFolderName, boolean debug, IEmailListener... listener) {
		this(GMAIL_SMTP_HOST, GMAIL_STMP_SSL_PORT, GMAIL_IMAP_HOST, GMAIL_IMAP_PORT, 
				username, password, mainFolderName, debug, listener);
	}
	public EmailAgent(final String smtpHost, final String smtpPort, final String imapHost, final String imapPort, 
			final String username, final String password, String mainFolderName, boolean debug, IEmailListener... listener) {
		// connect using TLS
		this.props = new Properties();
		props.put("mail.smtps.auth", "true");
		props.put("mail.smtps.user", username);
		props.put("mail.smtps.host", smtpHost);
		props.put("mail.smtps.port", smtpPort);
		props.put("mail.smtps.starttls.enable", "true");
		props.setProperty("mail.imaps.starttls.enable", "true");
		this.smtpUrlName = new URLName("smtps", smtpHost, Integer.parseInt(smtpPort), null, username, password);
		this.imapUrlName = new URLName("imaps", imapHost, Integer.parseInt(imapPort), null, username, password);
		this.mainFolderName = mainFolderName;
		this.debug = debug;
		
		LISTENERS.addAll(Arrays.asList(listener));
		
		this.emailThread = new Thread(this, getThreadName("main"));
		this.runIt.set(true);
		this.emailThread.start();
	}
	
	@Override
	public void run() {
		while(runIt.get()) {
			try {
				disconnect(store, mainFolder);
				
				log.info("Connecting to store/floder...");
				final Session session = Session.getInstance(props);
				session.setDebug(debug);
	
				store = (IMAPStore) session.getStore(imapUrlName);
	
				final InternalConnectionListener connectionListener = new InternalConnectionListener();
				//store.addConnectionListener(connectionListener);
				store.connect(imapUrlName.getHost(), imapUrlName.getPort(), imapUrlName.getUsername(), imapUrlName.getPassword());
				mainFolder = (IMAPFolder) store.getFolder(mainFolderName);
				mainFolder.addConnectionListener(connectionListener);
				mainFolder.open(Folder.READ_ONLY);
				mainFolder.addMessageCountListener(new MessageCountAdapter() {
	
					@Override
					public void messagesAdded(MessageCountEvent event) {
						log.debug("Incoming messages...");
						try {
							if (LISTENERS.isEmpty()) {
								log.warn("No listeners available for executing remote commands");
								return;
							}
							Message[] msgs = event.getMessages();
							IMAPMessage msg;
							for (final Message rmsg : msgs) {
								if (!(rmsg instanceof IMAPMessage)) {
									log.info("Expected \"" + IMAPMessage.class.getSimpleName() + "\", but received \"" + 
											rmsg.getClass().getSimpleName() + "\" subject: " + rmsg.getSubject());
									continue;
								}
								msg = (IMAPMessage) rmsg;
								final Address[] froms = msg.getFrom();
								if (hasCommandPermission(froms)) {
									final StringBuffer errorMessages = new StringBuffer();
									final List<Integer> commands = getValidCommands(msg, errorMessages);
									if (errorMessages.length() > 0) {
										if (log.isInfoEnabled()) {
											log.info("Invalid command(s) received from: " + Arrays.toString(froms));
										}
										sendReply(msg, errorMessages.toString());
										return;
									}
									if (log.isInfoEnabled()) {
										log.info("Received raw commands: " + Arrays.toString(commands.toArray()) + " from: " + Arrays.toString(froms));
									}
									final Thread newThread = new Thread(getThreadName(msg.getMessageID())) {
										
										@Override
										public void run() {
											final EmailEvent event = new EmailEvent(EmailEvent.TYPE_EXECUTE_COMMAND, commands, froms);
											for (IEmailListener listener : LISTENERS) {
												listener.handle(event);
											}
										}
									};
									newThread.start();
								} else if (log.isInfoEnabled()) {
									log.info(Arrays.toString(froms) + " does not have permission to execute commands");
								}
							}
						} catch (Exception e) {
							log.error("Error when processing incoming message", e);
						}
					}
				});
				log.info("Connected to " + imapUrlName.getHost() + " waiting for messages...");
				// when idle (blocking) gets interrupted (by host, sending an email, getting message content, etc.)
				// try to reinstate the idle process, if that fails (usually due to a closed folder) try to reconnect
				while (runIt.get()) {
					mainFolder.idle();
					if (runIt.get()) {
						log.info("Stopped listening for incoming messages... attempting to reconnect...");
					} else {
						log.info("Stopped listening for incoming messages (from disconnect)");
					}
				}
			} catch (FolderClosedException e) {
				if (runIt.get()) {
					log.info("Folder closed... attempting to reconnect...");
				}
			} catch (Exception e) {
				if (runIt.get()) {
					log.warn("Unable to connect... attempting to reconnect...", e);
				}
			}
		}
	}
	
	/**
	 * Disconnects the email agent
	 */
	public void disconnect() {
		runIt.set(false);
		disconnect(store, mainFolder);
	}
	
	private void disconnect(IMAPStore store, IMAPFolder mainFolder) {		
		try {
			if (mainFolder != null && mainFolder.isOpen()) {
				mainFolder.close(false);
				log.info("Disconnected from folder");
			}
			if (store != null && store.isConnected()) {
				store.close();
				log.info("Disconnected from store");
			}
		} catch (Exception e) {
			log.error("Unable to disconnect", e);
		}
	}

	public void send(String subject, String message, String from, String[] to, String... fileNames) {
		try {
			log.info("Sending message...");
			final Session session = Session.getInstance(props);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] addresses = new InternetAddress[to.length];
			int toIndex = 0;
			for (String t : to) {
				addresses[toIndex++] = new InternetAddress(t);
			}
			msg.setRecipients(Message.RecipientType.TO, addresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(message);
			mp.addBodyPart(mbp1);
			if (fileNames != null && fileNames.length > 0) {
				for (String fileName : fileNames) {
					MimeBodyPart mbp = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(fileName);
					mbp.setDataHandler(new DataHandler(fds));
					mbp.setFileName(fds.getName());
					mp.addBodyPart(mbp);
				}
			}
			// text attachment
			// MimeBodyPart mbp2 = new MimeBodyPart();
			// mbp2.setText("some text in an attachment form");
			// mp.addBodyPart(mbp2);
			msg.setContent(mp);
			
			// connect to SMTP transport and send message
			send(session, msg);
		} catch (Exception e) {
			log.error("Unable to send mail message", e);
			return;
		}
	}
	
	/**
	 * Connects to the SMTP transport and send the message
	 * 
	 * @param session the email session
	 * @param msg the message to send
	 */
	protected void send(Session session, MimeMessage msg) {
		try {
			log.debug("Opening transport to: " + smtpUrlName);
			Transport transport = session.getTransport(smtpUrlName);
			transport.connect(smtpUrlName.getHost(), smtpUrlName.getPort(), smtpUrlName.getUsername(), smtpUrlName.getPassword());
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			log.info("Message sent");
		} catch (Exception e) {
			log.error("Unable to send message", e);
		} finally {
			//readyToListen();
		}
	}
	
	protected void sendReply(MimeMessage originalMessage, String replyMessage) {
		try {
			MimeMessage replyMsg = (MimeMessage) originalMessage.reply(false);
			replyMsg.setFrom(originalMessage.getFrom()[0]);
			send(Session.getInstance(props), replyMsg);
		} catch (Exception e) {
			log.error("Unable to send reply message", e);
		}
	}
	
	protected List<Integer> getValidCommands(MimeMessage msg, final StringBuffer invalidCommandErrors) {
		final List<Integer> validCommands = new ArrayList<Integer>();
		try {
			List<String> rawCommands = new ArrayList<String>();
			if (msg.getSubject() != null && !msg.getSubject().isEmpty()) {
				log.debug("Checking message subject for commands");
				rawCommands.addAll(Arrays.asList(msg.getSubject().replace("Re:", "").replace("RE:", "").trim().split(UGateUtil.MAIL_COMMAND_DELIMITER)));
			}
			log.debug("Checking message body for commands");
			String msgRawContent = null;
			if (msg.getContentType().toLowerCase().indexOf("text") > -1) {
				msgRawContent = (String) msg.getContent();
			} else if (msg.getContentType().toLowerCase().indexOf("multipart") > -1) {
				Multipart multipart = (Multipart) msg.getContent();
				BodyPart bodyPart = multipart.getBodyPart(0);
				msgRawContent = bodyPart.getContent().toString(); 
			} else {
				log.warn("Commands being ignored for content type: " + msg.getContentType());
			}
			if (msgRawContent != null) {
				rawCommands.addAll(Arrays.asList(msgRawContent.trim().split(UGateUtil.MAIL_COMMAND_DELIMITER)));
			}
			int cmd = -1;
			for (String command : rawCommands) {
				try {
					cmd = Integer.parseInt(command);
				} catch (NumberFormatException e) {
					log.warn("Non-numeric command received: " + command, e);
					continue;
				}
				if (UGateUtil.CMDS.keySet().contains(cmd)) {
					validCommands.add(cmd);
				} else if (invalidCommandErrors != null) {
					invalidCommandErrors.append("Invalid Command \"" + cmd + "\"\n");
				}
			}
		} catch (Exception e) {
			log.error("Unable to get valid commands from message", e);
		}
		return validCommands;
	}
	
	protected boolean hasCommandPermission(Address... addresses) {
		List<String> authRecipients = UGateKeeper.DEFAULT.preferences.get(UGateUtil.SV_MAIL_RECIPIENTS_KEY, UGateUtil.MAIL_RECIPIENTS_DELIMITER);
		boolean hasPermission = false; 
		InternetAddress inernetAddress;
		for (Address from : addresses) {
			inernetAddress = (InternetAddress) from;
			if (authRecipients.contains(inernetAddress.getAddress())) {
				hasPermission = true;
				break;
			}
		}
		return hasPermission;
	}
	
	public void addListener(IEmailListener listener) {
		LISTENERS.add(listener);
	}
	
	public void removeListener(IEmailListener listener) {
		LISTENERS.remove(listener);
	}
	
	private static String getThreadName(String postfix) {
		return EmailAgent.class.getSimpleName() + '-' + postfix;
	}
	
	/**
	 * Internal connection listener used to spawn new threads for external email listeners
	 */
	class InternalConnectionListener implements ConnectionListener {
		
		@Override
		public void opened(ConnectionEvent event) {
			log.info("Mail Store/Folder opened: " + event.getType());
			final Thread newThread = new Thread(getThreadName("opened")) {
				
				@Override
				public void run() {
					final EmailEvent event = new EmailEvent(EmailEvent.TYPE_CONNECT, null, null);
					for (IEmailListener listener : LISTENERS) {
						listener.handle(event);
					}
				}
			};
			newThread.start();
		}
		
		@Override
		public void disconnected(ConnectionEvent event) {
			log.info("Mail Store/Folder disconnected unexpectedly: " + event.getType());
			final Thread newThread = new Thread(getThreadName("disconnected")) {
				
				@Override
				public void run() {
					final EmailEvent event = new EmailEvent(EmailEvent.TYPE_DISCONNECT, null, null);
					for (IEmailListener listener : LISTENERS) {
						listener.handle(event);
					}
				}
			};
			newThread.start();
		}
		
		@Override
		public void closed(ConnectionEvent event) {
			log.info("Mail Store/Folder disconnected: " + event.getType());
			final Thread newThread = new Thread(getThreadName("closed")) {
				
				@Override
				public void run() {
					final EmailEvent event = new EmailEvent(EmailEvent.TYPE_CLOSED, null, null);
					for (IEmailListener listener : LISTENERS) {
						listener.handle(event);
					}
				}
			};
			newThread.start();
		}
	};
}

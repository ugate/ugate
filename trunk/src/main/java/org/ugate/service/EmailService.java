package org.ugate.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.ugate.Command;
import org.ugate.UGateKeeper;
import org.ugate.UGateEvent;
import org.ugate.UGateUtil;
import org.ugate.mail.EmailAgent;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.MailRecipient;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Email service
 */
public class EmailService {

	private final Logger log = UGateUtil.getLogger(EmailService.class);
	private EmailAgent emailAgent;
	private boolean isEmailConnected;

	/**
	 * Only {@linkplain ServiceProvider} constructor
	 */
	EmailService() {
	}
	
	/**
	 * Connects to an {@linkplain EmailAgent}
	 * 
	 * @param host the {@linkplain Host}
	 */
	public boolean connect(final Host host) {
		// test email connection
//		isEmailConnected = true;
//		if (true) {
//			return;
//		}
		// wirelessBtn to email
		//emailDisconnect();
		if (host == null || host.getId() <= 0) {
			return false;
		}
		if (isEmailConnected) {
			disconnect();
		}
		String msg;
		UGateEvent<EmailService, Void> event;
		final String smtpHost = host.getMailSmtpHost();
		final int smtpPort = host.getMailImapPort();
		final String imapHost = host.getMailImapHost();
		final int imapPort = host.getMailImapPort();
		final String username = host.getMailUserName();
		final String password = host.getMailPassword();
		final String mainFolderName = host.getMailInboxName();
		try {
			msg = RS.rbLabel(KEYS.MAIL_CONNECTING);
			log.info(msg);
			event = new UGateEvent<>(this, UGateEvent.Type.EMAIL_CONNECTING, false);
			event.addMessage(msg);
			UGateKeeper.DEFAULT.notifyListeners(event);
			final List<IEmailListener> listeners = new ArrayList<IEmailListener>();
			listeners.add(new IEmailListener() {
				@Override
				public void handle(final EmailEvent event) {
					String msg;
					if (event.type == EmailEvent.Type.EXECUTE_COMMAND) {
						if (ServiceProvider.IMPL.getWirelessService().isConnected()) {
							RemoteNode rn;
							final List<String> commandMsgs = new ArrayList<String>();
							// send command to all the nodes defined in the email
							for (final String toAddress : event.toAddresses) {
								commandMsgs.clear();
								rn = ServiceProvider.IMPL.getWirelessService().findRemoteNodeByAddress(toAddress);
								for (final Command command : event.commands) {
									try {
										if (rn == null) {
											throw new IllegalArgumentException(
													"Invalid remote address: "
															+ toAddress);
										}
										ServiceProvider.IMPL.getWirelessService().sendData(rn, command);
										msg = RS.rbLabel(KEYS.SERVICE_EMAIL_CMD_EXEC, command, event.from, toAddress);
										log.info(msg);
									} catch (final Throwable t) {
										msg = RS.rbLabel(KEYS.SERVICE_EMAIL_CMD_EXEC_FAILED, command, event.from, toAddress);
										log.error(msg, t);
									}
									commandMsgs.add(msg);
								}
								if (rn != null && !commandMsgs.isEmpty()) {
									UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<RemoteNode, List<Command>>(
											rn, UGateEvent.Type.EMAIL_EXECUTED_COMMANDS, 
											false, RemoteNodeType.WIRELESS_ADDRESS, null, null, event.commands, 
											commandMsgs.toArray(new String[]{})));
								} else {
									UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<RemoteNode, List<Command>>(
											rn, UGateEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, 
											false, RemoteNodeType.WIRELESS_ADDRESS, null, null, event.commands, 
											commandMsgs.toArray(new String[]{})));
								}
							}
						} else {
							msg = RS.rbLabel(KEYS.SERVICE_EMAIL_CMD_EXEC_FAILED, UGateUtil.toString(event.commands), UGateUtil.toString(event.from), 
									UGateUtil.toString(event.toAddresses), RS.rbLabel(KEYS.SERVICE_WIRELESS_CONNECTION_REQUIRED));
							log.warn(msg);
							UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<EmailService, List<Command>>(
									EmailService.this, UGateEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, false, 
									RemoteNodeType.WIRELESS_ADDRESS, null, null, event.commands, msg));
						}
					} else if (event.type == EmailEvent.Type.CONNECT) {
						notifyListeners(UGateEvent.Type.EMAIL_CONNECTED, KEYS.MAIL_CONNECTED, event.commands);
					} else if (event.type == EmailEvent.Type.DISCONNECT) {
						notifyListeners(UGateEvent.Type.EMAIL_DISCONNECTED, KEYS.MAIL_DISCONNECTED, event.commands);
					} else if (event.type == EmailEvent.Type.CLOSED) {
						notifyListeners(UGateEvent.Type.EMAIL_CLOSED, KEYS.MAIL_CLOSED, event.commands);
					} else if (event.type == EmailEvent.Type.AUTH_FAILED) {
						notifyListeners(UGateEvent.Type.EMAIL_AUTH_FAILED, KEYS.MAIL_AUTH_FAILED, event.commands);
					}
				}
			});
			List<String> authEmails = new ArrayList<>(host.getMailRecipients().size());
			for (final MailRecipient mr : host.getMailRecipients()) {
				authEmails.add(mr.getEmail());
			}
			this.emailAgent = EmailAgent.start(smtpHost, String.valueOf(smtpPort), imapHost, String.valueOf(imapPort), 
					username, password, mainFolderName, authEmails, listeners.toArray(new IEmailListener[0]));
			return true;
		} catch (final Throwable t) {
			msg = RS.rbLabel(KEYS.MAIL_CONNECT_FAILED, 
					smtpHost, smtpPort, imapHost, imapPort, username, mainFolderName);
			log.error(msg, t);
			event = new UGateEvent<>(
					this, UGateEvent.Type.EMAIL_CONNECT_FAILED, false);
			event.addMessage(msg);
			event.addMessage(t.getMessage());
			UGateKeeper.DEFAULT.notifyListeners(event);
			return false;
		}
	}

	/**
	 * General {@linkplain EmailService} notification for a
	 * {@linkplain UGateEvent}
	 * 
	 * @param type
	 *            the {@linkplain UGateEvent.Type}
	 * @param messageKey
	 *            the {@linkplain KEYS} for the message associated with the
	 *            notification
	 * @param commands
	 *            any {@linkplain Command}s associated with the notification
	 */
	protected void notifyListeners(final UGateEvent.Type type,
			final KEYS messageKey, final List<Command> commands) {
		isEmailConnected = false;
		final String msg = RS.rbLabel(messageKey);
		log.info(msg);
		UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<EmailService, List<Command>>(
				this, type, false, null, null, null, commands, msg));
	}

	/**
	 * Disconnects from email
	 */
	public void disconnect() {
		if (emailAgent != null) {
			final String msg = RS.rbLabel(KEYS.MAIL_DISCONNECTING);
			log.info(msg);
			UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<EmailService, Void>(
					this, UGateEvent.Type.EMAIL_DISCONNECTING, false, msg));
			emailAgent.disconnect();
			emailAgent = null;
		}
	}
	
	/**
	 * Sends an email
	 * 
	 * @param subject the subject of the email
	 * @param message the email message
	 * @param from who the email is from
	 * @param to the recipients of the email
	 * @param fileNames file name paths to any attachments (optional)
	 */
	public void send(String subject, String message, String from, String[] to, String... fileNames) {
		if (emailAgent != null) {
			emailAgent.send(subject, message, from, to, fileNames);
		} else {
			log.warn("Unable to send email... no connection established");
		}
	}
	
	/**
	 * @return true if the {@linkplain EmailAgent} is connected
	 */
	public boolean isConnected() {
		return isEmailConnected;
	}
}

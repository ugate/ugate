package org.ugate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.ugate.Command;
import org.ugate.RemoteSettings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.UGateUtil;
import org.ugate.mail.EmailAgent;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Host;

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
		// connect to email
		//emailDisconnect();
		if (host == null || host.getId() <= 0) {
			return false;
		}
		if (isEmailConnected) {
			disconnect();
		}
		String msg;
		UGateKeeperEvent<Void> event;
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
			event = new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_CONNECTING, false);
			event.addMessage(msg);
			UGateKeeper.DEFAULT.notifyListeners(event);
			final List<IEmailListener> listeners = new ArrayList<IEmailListener>();
			listeners.add(new IEmailListener() {
				@Override
				public void handle(final EmailEvent event) {
					String msg;
					if (event.type == EmailEvent.Type.EXECUTE_COMMAND) {
						if (ServiceProvider.IMPL.getWirelessService().wirelessIsConnected()) {
							int nodeIndex;
							final Map<Integer, String> addys = new HashMap<Integer, String>();
							final List<String> commandMsgs = new ArrayList<String>();
							for (final Command command : event.commands) {
								// send command to all the nodes defined in the email
								for (final String toAddress : event.toAddresses) {
									try {
										nodeIndex = ServiceProvider.IMPL.getWirelessService().getNodeAddressIndex(toAddress);
										addys.put(nodeIndex, toAddress);
										ServiceProvider.IMPL.getWirelessService().sendData(nodeIndex, command);
										msg = RS.rbLabel(KEYS.SERVICE_EMAIL_CMD_EXEC, command, event.from, toAddress);
										log.info(msg);
										commandMsgs.add(msg);
									} catch (final Throwable t) {
										msg = RS.rbLabel(KEYS.SERVICE_EMAIL_CMD_EXEC_FAILED, command, event.from, toAddress);
										log.error(msg, t);
										commandMsgs.add(msg);
									}
								}
							}
							if (!addys.isEmpty()) {
								UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<List<Command>>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.EMAIL_EXECUTED_COMMANDS, 
										false, addys, RemoteNodeType.WIRELESS_ADDRESS_NODE_, null, null, event.commands, 
										commandMsgs.toArray(new String[]{})));
							} else {
								UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<List<Command>>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, 
										false, addys, RemoteNodeType.WIRELESS_ADDRESS_NODE, null, null, event.commands, 
										commandMsgs.toArray(new String[]{})));
							}
						} else {
							msg = RS.rbLabel(KEYS.SERVICE_EMAIL_CMD_EXEC_FAILED, UGateUtil.toString(event.commands), UGateUtil.toString(event.from), 
									UGateUtil.toString(event.toAddresses), RS.rbLabel(KEYS.SERVICE_WIRELESS_CONNECTION_REQUIRED));
							log.warn(msg);
							UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<List<Command>>(UGateKeeper.DEFAULT, UGateKeeperEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, false, null,
									RemoteNodeType.WIRELESS_ADDRESS_NODE, null, null, event.commands, msg));
						}
					} else if (event.type == EmailEvent.Type.CONNECT) {
						isEmailConnected = true;
						msg = RS.rbLabel(KEYS.MAIL_CONNECTED);
						log.info(msg);
						UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_CONNECTED, false, null,
								null, null, null, event.commands, msg));
					} else if (event.type == EmailEvent.Type.DISCONNECT) {
						isEmailConnected = false;
						msg = RS.rbLabel(KEYS.MAIL_DISCONNECTED);
						log.info(msg);
						UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_DISCONNECTED, false, null,
								null, null, null, event.commands, msg));
					} else if (event.type == EmailEvent.Type.CLOSED) {
						isEmailConnected = false;
						msg = RS.rbLabel(KEYS.MAIL_CLOSED);
						log.info(msg);
						UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<List<Command>>(this, UGateKeeperEvent.Type.EMAIL_CLOSED, false, null, 
								null, null, null, event.commands, msg));
					}
				}
			});
			this.emailAgent = EmailAgent.start(smtpHost, String.valueOf(smtpPort), imapHost, String.valueOf(imapPort), 
					username, password, mainFolderName, listeners.toArray(new IEmailListener[0]));
			return true;
		} catch (final Throwable t) {
			msg = RS.rbLabel(KEYS.MAIL_CONNECT_FAILED, 
					smtpHost, smtpPort, imapHost, imapPort, username, mainFolderName);
			log.error(msg, t);
			event = new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_CONNECT_FAILED, false);
			event.addMessage(msg);
			event.addMessage(t.getMessage());
			UGateKeeper.DEFAULT.notifyListeners(event);
			return false;
		}
	}

	/**
	 * Disconnects from email
	 */
	public void disconnect() {
		if (emailAgent != null) {
			final String msg = RS.rbLabel(KEYS.MAIL_DISCONNECTING);
			log.info(msg);
			UGateKeeper.DEFAULT.notifyListeners(new UGateKeeperEvent<Void>(this, UGateKeeperEvent.Type.EMAIL_DISCONNECTING, false, msg));
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

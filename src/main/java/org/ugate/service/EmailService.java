package org.ugate.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateEvent;
import org.ugate.UGateUtil;
import org.ugate.mail.EmailAgent;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.entity.Command;
import org.ugate.service.entity.EntityExtractor;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.Host;
import org.ugate.service.entity.jpa.MailRecipient;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * Email service
 */
public class EmailService extends ExtractorService<Actor> {

	private final Logger log = UGateUtil.getLogger(EmailService.class);
	private EmailAgent emailAgent;
	private boolean isEmailConnected;

	/**
	 * Constructor
	 * 
	 * @param extractor
	 *            the {@link EntityExtractor} for the {@link Actor} used by the
	 *            {@link EmailService}
	 */
	EmailService(final EntityExtractor<Actor> extractor) {
		super(extractor);
	}
	
	/**
	 * Connects to an {@linkplain EmailAgent}
	 * 
	 * @param host the {@linkplain Host}
	 */
	public boolean connect() {
		// test email connection
//		isEmailConnected = true;
//		if (true) {
//			return;
//		}
		// wirelessBtn to email
		//emailDisconnect();
		if (extract() == null || extract().getHost() == null
				|| extract().getHost().getId() <= 0) {
			return false;
		}
		if (isEmailConnected) {
			disconnect();
		}
		String msg;
		UGateEvent<EmailService, Void> event;
		try {
			msg = RS.rbLabel(KEY.MAIL_CONNECTING);
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
								rn = ServiceProvider.IMPL.getRemoteNodeService().findByAddress(toAddress);
								for (final Command command : event.commands) {
									try {
										if (rn == null) {
											throw new IllegalArgumentException(
													"Invalid remote address: "
															+ toAddress);
										}
										ServiceProvider.IMPL.getWirelessService().sendData(rn, command, 0, false);
										msg = RS.rbLabel(KEY.SERVICE_EMAIL_CMD_EXEC, command, event.from, toAddress);
										log.info(msg);
									} catch (final Throwable t) {
										msg = RS.rbLabel(KEY.SERVICE_EMAIL_CMD_EXEC_FAILED, command, event.from, toAddress);
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
							msg = RS.rbLabel(KEY.SERVICE_EMAIL_CMD_EXEC_FAILED, UGateUtil.toString(event.commands), UGateUtil.toString(event.from), 
									UGateUtil.toString(event.toAddresses), RS.rbLabel(KEY.SERVICE_WIRELESS_CONNECTION_REQUIRED));
							log.warn(msg);
							UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<EmailService, List<Command>>(
									EmailService.this, UGateEvent.Type.EMAIL_EXECUTE_COMMANDS_FAILED, false, 
									RemoteNodeType.WIRELESS_ADDRESS, null, null, event.commands, msg));
						}
					} else if (event.type == EmailEvent.Type.CONNECT) {
						notifyListeners(UGateEvent.Type.EMAIL_CONNECTED, event.commands, KEY.MAIL_CONNECTED,
								emailAgent.getOptions());
					} else if (event.type == EmailEvent.Type.DISCONNECT) {
						notifyListeners(UGateEvent.Type.EMAIL_DISCONNECTED, event.commands, KEY.MAIL_DISCONNECTED,
								emailAgent.getOptions());
					} else if (event.type == EmailEvent.Type.CLOSED) {
						notifyListeners(UGateEvent.Type.EMAIL_CLOSED, event.commands, KEY.MAIL_CLOSED,
								emailAgent.getOptions());
					} else if (event.type == EmailEvent.Type.AUTH_FAILED) {
						notifyListeners(UGateEvent.Type.EMAIL_AUTH_FAILED,event.commands, KEY.MAIL_AUTH_FAILED,
								emailAgent.getOptions());
					}
				}
			});
			this.emailAgent = EmailAgent.start(new EmailAgent.Options() {
				@Override
				public boolean isCommandAuthorized(final String email) {
					for (final MailRecipient mr : extract().getHost()
							.getMailRecipients()) {
						if (mr.getEmail().equalsIgnoreCase(email)) {
							return true;
						}
					}
					return false;
				}

				@Override
				public String getSmtpUsername() {
					return extract().getHost().getMailUserName();
				}

				@Override
				public String getSmtpPassword() {
					return extract().getHost().getMailPassword();
				}

				@Override
				public String getSmtpHost() {
					return extract().getHost().getMailSmtpHost();
				}

				@Override
				public int getSmtpPort() {
					return extract().getHost().getMailSmtpPort();
				}

				@Override
				public String getImapUsername() {
					return extract().getHost().getMailUserName();
				}

				@Override
				public String getImapPassword() {
					return extract().getHost().getMailPassword();
				}

				@Override
				public String getImapHost() {
					return extract().getHost().getMailImapHost();
				}

				@Override
				public int getImapPort() {
					return extract().getHost().getMailImapPort();
				}

				@Override
				public String getImapFolder() {
					return extract().getHost().getMailInboxName();
				}

				@Override
				public boolean useSmtpSsl() {
					return extract().getHost().getMailUseSSL() >= 1;
				}

				@Override
				public boolean useImapSsl() {
					return extract().getHost().getMailUseSSL() >= 1;
				}

				@Override
				public boolean useTls() {
					return extract().getHost().getMailUseTLS() >= 1;
				}

				@Override
				public boolean useStartTls() {
					return extract().getHost().getMailUseTLS() >= 1;
				}

				@Override
				public boolean isDebug() {
					return log.isDebugEnabled();
				}
			}, listeners.toArray(new IEmailListener[0]));
			return true;
		} catch (final Throwable t) {
			msg = RS.rbLabel(KEY.MAIL_CONNECT_FAILED,
					this.emailAgent.getOptions());
			log.error(msg, t);
			event = new UGateEvent<>(this,
					UGateEvent.Type.EMAIL_CONNECT_FAILED, false);
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
	 * @param commands
	 *            any {@linkplain Command}s associated with the notification
	 * @param messageKey
	 *            the {@linkplain KEY} for the message associated with the
	 *            notification
	 * @param messageParameters
	 *            the {@link KEY} message parameters
	 */
	protected void notifyListeners(final UGateEvent.Type type,
			final List<Command> commands, final KEY messageKey,
			final Object... messageParameters) {
		isEmailConnected = false;
		final String msg = messageParameters.length > 0 ? RS.rbLabel(
				messageKey, messageParameters) : RS.rbLabel(messageKey);
		log.info(msg);
		UGateKeeper.DEFAULT
				.notifyListeners(new UGateEvent<EmailService, List<Command>>(
						this, type, false, null, null, null, commands, msg));
	}

	/**
	 * Disconnects from email
	 */
	public void disconnect() {
		if (emailAgent != null) {
			final String msg = RS.rbLabel(KEY.MAIL_DISCONNECTING);
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
	 * @param subject
	 *            the subject of the email
	 * @param message
	 *            the email message
	 * @param from
	 *            who the email is from
	 * @param to
	 *            the recipients of the email
	 * @param paths
	 *            file name {@link Path}(s) to any attachments (optional)
	 */
	public void send(String subject, String message, String from, String[] to, Path... paths) {
		if (emailAgent != null) {
			emailAgent.send(subject, message, from, to, paths);
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

	/**
	 * @return email options used for the email connection (if any)
	 */
	public String getOptions() {
		return emailAgent != null ? emailAgent.getOptions().toString() : "";
	}
}

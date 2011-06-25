package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;

/**
 * Mail connection GUI responsible for connecting to the mail service
 */
public abstract class MailConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(MailConnectionView.class);
	public static final String LABEL_CONNECT = "Connect To Mail";
	public static final String LABEL_CONNECTING = "Connecting To Mail...";
	public static final String LABEL_RECONNECT = "Reconnect To Mail";
	public final UGateTextControl smtpHost;
	public final UGateTextControl smtpPort;
	public final UGateTextControl imapHost;
	public final UGateTextControl imapPort;
	public final UGateTextControl username;
	public final UGateTextControl password;
	public final Button connect;

	public MailConnectionView() {
	    super(20);
		smtpHost = new UGateTextControl("SMTP Host", UGateKeeper.MAIL_SMTP_HOST_KEY, false);
		smtpPort = new UGateTextControl("SMTP Port", UGateKeeper.MAIL_SMTP_PORT_KEY, false);
	    imapHost = new UGateTextControl("IMAP Host", UGateKeeper.MAIL_IMAP_HOST_KEY, false);
		imapPort = new UGateTextControl("IMAP Port", UGateKeeper.MAIL_IMAP_PORT_KEY, false);
		username = new UGateTextControl("Username", UGateKeeper.MAIL_USERNAME_KEY, false);
		password = new UGateTextControl("Password", UGateKeeper.MAIL_PASSWORD_KEY, true);

		connect = new Button();
	    connectionHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				if (smtpHost.textBox.getText().length() > 0 && smtpPort.textBox.getText().length() > 0 && 
						imapHost.textBox.getText().length() > 0 && imapPort.textBox.getText().length() > 0 && 
						username.textBox.getText().length() > 0 && password.passwordBox.getText().length() > 0) {
					log.debug("Connecting to email...");
					connect(smtpHost.textBox.getText(), smtpPort.textBox.getText(),
							imapHost.textBox.getText(), imapPort.textBox.getText(), 
							username.textBox.getText(), password.passwordBox.getText());
				}
			}
	    };
	    connect.addEventHandler(MouseEvent.MOUSE_CLICKED, connectionHandler);
	    connect.setText(LABEL_CONNECT);
	    
	    final HBox hostContainer = new HBox(10);
	    hostContainer.getChildren().addAll(smtpHost, smtpPort, imapHost, imapPort);
	    final HBox credContainer = new HBox(10);
	    credContainer.getChildren().addAll(username, password);
	    getChildren().addAll(hostContainer, credContainer, statusIcon, connect);
	}
	
	public void connect(final String smtpHost, final String smtpPort, final String imapHost, 
			final String imapPort, final String username, final String password) {
		connect.setDisable(true);
		connect.setText(LABEL_CONNECTING);
		UGateKeeper.DEFAULT.emailConnect(smtpHost, smtpPort, imapHost, 
				imapPort, username, password, "InBox", true, new IEmailListener(){

					@Override
					public void handle(EmailEvent event) {
						if (event.type == EmailEvent.TYPE_CONNECT) {
							connect.setDisable(false);
							connect.setText(LABEL_RECONNECT);
							log.debug("Turning ON email connection icon");
							setStatusFill(statusIcon, true);
						} else if (event.type == EmailEvent.TYPE_DISCONNECT) {
							connect.setDisable(false);
							connect.setText(LABEL_CONNECT);
							log.debug("Turning OFF email connection icon");
							setStatusFill(statusIcon, false);
						}
					}
		});
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_SMTP_HOST_KEY, smtpHost);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_SMTP_PORT_KEY, smtpPort);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_IMAP_HOST_KEY, imapHost);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_IMAP_PORT_KEY, imapPort);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_USERNAME_KEY, username);
		UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_PASSWORD_KEY, password);
	}
	
	public void disconnect() {
		log.info("Disconnecting from Email");
		UGateKeeper.DEFAULT.emailDisconnect();
	}
}

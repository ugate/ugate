package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.mail.EmailEvent;
import org.ugate.mail.IEmailListener;
import org.ugate.resources.RS;

/**
 * Mail connection GUI responsible for connecting to the mail service
 */
public abstract class MailConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(MailConnectionView.class);
	public static final String LABEL_CONNECT = RS.rbLabel("mail.connect");
	public static final String LABEL_CONNECTING = RS.rbLabel("mail.connecting");
	public static final String LABEL_RECONNECT = RS.rbLabel("mail.reconnect");
	public final UGateTextFieldPreferenceView smtpHost;
	public final UGateTextFieldPreferenceView smtpPort;
	public final UGateTextFieldPreferenceView imapHost;
	public final UGateTextFieldPreferenceView imapPort;
	public final UGateTextFieldPreferenceView username;
	public final UGateTextFieldPreferenceView password;
	public final UGateToggleSwitchPreferenceView soundsToggleSwitch;
	public final UGateToggleSwitchPreferenceView emailToggleSwitch;
	public final UGateTextFieldPreferenceView recipients;
	public final Button connect;

	public MailConnectionView() {
	    super(20);
		final ImageView icon = RS.imgView(RS.IMG_EMAIL_ICON);
		soundsToggleSwitch = new UGateToggleSwitchPreferenceView(
				Settings.SOUNDS_ON_KEY, RS.IMG_SOUND_ON, RS.IMG_SOUND_OFF);
		emailToggleSwitch = new UGateToggleSwitchPreferenceView(Settings.MAIL_ALARM_ON_KEY, 
				RS.IMG_EMAIL_NOTIFY_ON, RS.IMG_EMAIL_NOTIFY_OFF);
		//controls.addHelpTextTrigger(recipientsToggleSwitch, RS.rbLabel("mail.alarm.notify.desc"));
		recipients = new UGateTextFieldPreferenceView(
				Settings.MAIL_RECIPIENTS_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT_AREA, 
				RS.rbLabel("mail.alarm.notify.emails"), "");
		recipients.textArea.setPrefRowCount(5);
		recipients.textArea.setWrapText(true);
		//controls.addHelpTextTrigger(recipients, RS.rbLabel("mail.alarm.notify.emails.desc"));
		smtpHost = new UGateTextFieldPreferenceView(Settings.MAIL_SMTP_HOST_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("mail.smtp.host"), 
				RS.rbLabel("mail.smtp.host.desc"));
		smtpPort = new UGateTextFieldPreferenceView(Settings.MAIL_SMTP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("mail.smtp.port"), 
				RS.rbLabel("mail.smtp.port.desc"));
	    imapHost = new UGateTextFieldPreferenceView(Settings.MAIL_IMAP_HOST_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("mail.imap.host"), 
				RS.rbLabel("mail.imap.host.desc"));
		imapPort = new UGateTextFieldPreferenceView(Settings.MAIL_IMAP_PORT_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("mail.imap.port"), 
				RS.rbLabel("mail.imap.port.desc"));
		username = new UGateTextFieldPreferenceView(Settings.MAIL_USERNAME_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("mail.username"), 
				RS.rbLabel("mail.username.desc"));
		password = new UGateTextFieldPreferenceView(Settings.MAIL_PASSWORD_KEY, 
				UGateTextFieldPreferenceView.Type.TYPE_PASSWORD, RS.rbLabel("mail.password"), 
				RS.rbLabel("mail.password.desc"));

		connect = new Button();
	    connectionHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				if (smtpHost.textField.getText().length() > 0 && smtpPort.textField.getText().length() > 0 && 
						imapHost.textField.getText().length() > 0 && imapPort.textField.getText().length() > 0 && 
						username.textField.getText().length() > 0 && password.passwordField.getText().length() > 0) {
					log.debug("Connecting to email...");
					connect(smtpHost.textField.getText(), smtpPort.textField.getText(),
							imapHost.textField.getText(), imapPort.textField.getText(), 
							username.textField.getText(), password.passwordField.getText());
				}
			}
	    };
	    connect.addEventHandler(MouseEvent.MOUSE_CLICKED, connectionHandler);
	    connect.setText(LABEL_CONNECT);
	   
	    final GridPane grid = new GridPane();
	    grid.setHgap(10d);
	    grid.setVgap(30d);
	    
	    final VBox toggleView = new VBox(10d);
	    toggleView.getChildren().addAll(icon, soundsToggleSwitch, emailToggleSwitch);
	    
	    final GridPane connectionGrid = new GridPane();
	    connectionGrid.setPadding(new Insets(20d, 0, 0, 0));
		connectionGrid.setHgap(15d);
		connectionGrid.setVgap(15d);
	    connectionGrid.add(smtpHost, 0, 0);
	    connectionGrid.add(smtpPort, 1, 0);
	    connectionGrid.add(imapHost, 0, 1);
	    connectionGrid.add(imapPort, 1, 1);
	    connectionGrid.add(username, 0, 2);
	    connectionGrid.add(password, 1, 2);
	    connectionGrid.add(recipients, 0, 3, 2, 1);
	    
	    grid.add(toggleView, 0, 0);
	    grid.add(connectionGrid, 1, 0);
	    grid.add(connect, 1, 1, 2, 1);
	    getChildren().add(grid);
	}
	
	public void connect(final String smtpHost, final String smtpPort, final String imapHost, 
			final String imapPort, final String username, final String password) {
		connect.setText(LABEL_CONNECTING);
		UGateKeeper.DEFAULT.emailConnect(smtpHost, smtpPort, imapHost,
				imapPort, username, password,
				UGateKeeper.DEFAULT.preferencesGet(Settings.MAIL_INBOX_NAME),
				true, new IEmailListener() {

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
		UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_SMTP_HOST_KEY, smtpHost);
		UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_SMTP_PORT_KEY, smtpPort);
		UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_IMAP_HOST_KEY, imapHost);
		UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_IMAP_PORT_KEY, imapPort);
		UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_USERNAME_KEY, username);
		UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_PASSWORD_KEY, password);
	}
	
	public void disconnect() {
		log.info("Disconnecting from Email");
		UGateKeeper.DEFAULT.emailDisconnect();
	}
}

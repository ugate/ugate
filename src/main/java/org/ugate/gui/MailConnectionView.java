package org.ugate.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.ugate.IGateKeeperListener;
import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.UGateKeeperEvent;
import org.ugate.gui.components.UGateTextFieldPreferenceView;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;

/**
 * Mail connection GUI responsible for connecting to the mail service
 */
public abstract class MailConnectionView extends StatusView {
	
	private static final Logger log = Logger.getLogger(MailConnectionView.class);
	public final UGateTextFieldPreferenceView smtpHost;
	public final UGateTextFieldPreferenceView smtpPort;
	public final UGateTextFieldPreferenceView imapHost;
	public final UGateTextFieldPreferenceView imapPort;
	public final UGateTextFieldPreferenceView username;
	public final UGateTextFieldPreferenceView password;
	public final UGateTextFieldPreferenceView inboxFolder;
	public final UGateToggleSwitchPreferenceView soundsToggleSwitch;
	public final UGateToggleSwitchPreferenceView emailToggleSwitch;
	public final UGateTextFieldPreferenceView recipients;
	public final Button connect;

	public MailConnectionView(final ControlBar controlBar) {
	    super(controlBar, 20);
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
		inboxFolder = new UGateTextFieldPreferenceView(Settings.MAIL_INBOX_NAME, 
				UGateTextFieldPreferenceView.Type.TYPE_TEXT, RS.rbLabel("mail.folder"), 
				RS.rbLabel("mail.folder.desc"));
		
	    // update the status when email connections are made/lost
		UGateKeeper.DEFAULT.addListener(new IGateKeeperListener() {
			@Override
			public void handle(final UGateKeeperEvent<?> event) {
				if (event.getType() == UGateKeeperEvent.Type.EMAIL_CONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel("mail.connecting"));
				} else if (event.getType() == UGateKeeperEvent.Type.EMAIL_CONNECTED) {
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_SMTP_HOST_KEY, smtpHost.textField.getText());
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_SMTP_PORT_KEY, smtpPort.textField.getText());
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_IMAP_HOST_KEY, imapHost.textField.getText());
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_IMAP_PORT_KEY, imapPort.textField.getText());
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_USERNAME_KEY, username.textField.getText());
					UGateKeeper.DEFAULT.preferencesSet(Settings.MAIL_PASSWORD_KEY, password.passwordField.getText());
					connect.setDisable(false);
					connect.setText(RS.rbLabel("mail.reconnect"));
					log.debug("Turning ON email connection icon");
					setStatusFill(statusIcon, true);
				} else if (event.getType() == UGateKeeperEvent.Type.EMAIL_CONNECT_FAILED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel("mail.connect"));
					controlBar.setHelpText(event.getMessageString());
					setStatusFill(statusIcon, false);
				} else if (event.getType() == UGateKeeperEvent.Type.EMAIL_DISCONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel("mail.disconnecting"));
				} else if (event.getType() == UGateKeeperEvent.Type.EMAIL_DISCONNECTED || 
						event.getType() == UGateKeeperEvent.Type.EMAIL_CLOSED) {
					// run later in case the application is going to exit which will cause an issue with FX thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							connect.setDisable(false);
							connect.setText(RS.rbLabel("mail.connect"));
							log.debug("Turning OFF email connection icon");
							setStatusFill(statusIcon, false);
						}
					});
				}
			}
		});

		connect = new Button();
	    connectionHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				connect();
			}
	    };
	    connect.addEventHandler(MouseEvent.MOUSE_CLICKED, connectionHandler);
	    connect.setText(RS.rbLabel("mail.connect"));
	   
	    final GridPane grid = new GridPane();
	    grid.setHgap(10d);
	    grid.setVgap(30d);
	    
	    final VBox toggleView = new VBox(10d);
	    toggleView.getChildren().addAll(icon, soundsToggleSwitch, emailToggleSwitch);
	    
	    final GridPane connectionGrid = new GridPane();
	    connectionGrid.setPadding(new Insets(20d, 5, 5, 5));
		connectionGrid.setHgap(15d);
		connectionGrid.setVgap(15d);
	    connectionGrid.add(smtpHost, 0, 0);
	    connectionGrid.add(smtpPort, 1, 0);
	    connectionGrid.add(imapHost, 0, 1);
	    connectionGrid.add(imapPort, 1, 1);
	    connectionGrid.add(username, 0, 2);
	    connectionGrid.add(password, 1, 2);
	    connectionGrid.add(inboxFolder, 0, 3, 2, 1);
	    connectionGrid.add(recipients, 0, 4, 2, 1);
	    
	    grid.add(toggleView, 0, 0);
	    grid.add(connectionGrid, 1, 0);
	    grid.add(connect, 1, 1, 2, 1);
	    getChildren().add(grid);
	}
	
	/**
	 * Establishes an email connection using internal parameters
	 */
	public void connect() {
		if (smtpHost.textField.getText().length() > 0 && smtpPort.textField.getText().length() > 0 && 
				imapHost.textField.getText().length() > 0 && imapPort.textField.getText().length() > 0 && 
				username.textField.getText().length() > 0 && password.passwordField.getText().length() > 0) {
			log.debug("Connecting to email...");
			controlBar.createEmailConnectionService(smtpHost.textField.getText(), smtpPort.textField.getText(),
					imapHost.textField.getText(), imapPort.textField.getText(), 
					username.textField.getText(), password.passwordField.getText(), 
					inboxFolder.textField.getText()).start();
		}
	}
}

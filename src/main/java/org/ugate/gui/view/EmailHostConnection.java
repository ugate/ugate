package org.ugate.gui.view;

import java.util.Arrays;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.UGateEvent;
import org.ugate.UGateKeeper;
import org.ugate.UGateListener;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.FunctionButton;
import org.ugate.gui.components.StatusIcon;
import org.ugate.gui.components.UGateCtrlBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.MailRecipientType;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.MailRecipient;

/**
 * Responsible for connecting to the mail service
 */
public class EmailHostConnection extends VBox {

	private static final Logger log = LoggerFactory
			.getLogger(EmailHostConnection.class);
	public final UGateCtrlBox<Actor, Void, Void> smtpHost;
	public final UGateCtrlBox<Actor, Void, Void> smtpPort;
	public final UGateCtrlBox<Actor, Void, Void> imapHost;
	public final UGateCtrlBox<Actor, Void, Void> imapPort;
	public final UGateCtrlBox<Actor, Void, Void> username;
	public final UGateCtrlBox<Actor, Void, Void> password;
	public final UGateCtrlBox<Actor, Void, Void> inboxFolder;
	public final TextField recipient;
	public final UGateCtrlBox<Actor, MailRecipient, String> recipients;
	public final Button connect;
	public final ControlBar cb;

	public EmailHostConnection(final ControlBar controlBar) {
		super(20);
		this.cb = controlBar;
		final StatusIcon emailIcon = new StatusIcon(
				RS.imgView(RS.IMG_EMAIL_ICON), GuiUtil.COLOR_OFF);
		smtpHost = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_SMTP_HOST, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.MAIL_SMTP_HOST), null);
		controlBar.addHelpTextTrigger(smtpHost,
				RS.rbLabel(KEYS.MAIL_SMTP_HOST_DESC));
		smtpPort = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_SMTP_PORT, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.MAIL_SMTP_PORT), null);
		controlBar.addHelpTextTrigger(smtpPort,
				RS.rbLabel(KEYS.MAIL_SMTP_PORT_DESC));
		imapHost = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_IMAP_HOST, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.MAIL_IMAP_HOST), null);
		controlBar.addHelpTextTrigger(imapHost,
				RS.rbLabel(KEYS.MAIL_IMAP_HOST_DESC));
		imapPort = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_IMAP_PORT, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.MAIL_IMAP_PORT), null);
		controlBar.addHelpTextTrigger(imapPort,
				RS.rbLabel(KEYS.MAIL_IMAP_PORT_DESC));
		username = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_USERNAME, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.MAIL_USERNAME), null);
		controlBar.addHelpTextTrigger(username,
				RS.rbLabel(KEYS.MAIL_USERNAME_DESC));
		password = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_PASSWORD, UGateCtrlBox.Type.PASSWORD,
				RS.rbLabel(KEYS.MAIL_PASSWORD), null);
		controlBar.addHelpTextTrigger(password,
				RS.rbLabel(KEYS.MAIL_PASSWORD_DESC));
		inboxFolder = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_INBOX_NAME, UGateCtrlBox.Type.TEXT,
				RS.rbLabel(KEYS.MAIL_FOLDER_NAME), null);
		controlBar.addHelpTextTrigger(inboxFolder,
				RS.rbLabel(KEYS.MAIL_FOLDER_DESC));
		recipient = new TextField();
		HBox.setHgrow(recipient, Priority.ALWAYS);
		recipient.setPromptText(RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_EMAILS_ADD_DESC));
		final FunctionButton recipientAdd = new FunctionButton(FunctionButton.Function.ADD, 
				new Runnable() {
					@Override
					public void run() {
						if (recipient.getText().isEmpty()) {
							return;
						}
						final String raddy = recipient.getText();
						recipients.getListView().getItems().add(raddy);
						try {
							ServiceProvider.IMPL.getCredentialService().mergeHost(
									cb.getActor().getHost());
						} catch (final Throwable e) {
							for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
							    log.info("Exception:" + t);
							}
							log.info(String.format(
									"Unable to add mail recipient \"%1$s\" in host with ID = %2$s",
									raddy, cb.getActor().getHost().getId()), e);
							controlBar.setHelpText(RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_EMAILS_ADD_FAILED));
							recipients.getListView().getItems().remove(raddy);
						}
					}
				});
		controlBar.addHelpTextTrigger(recipientAdd,
				RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_EMAILS_ADD));
		final FunctionButton recipientRem = new FunctionButton(FunctionButton.Function.REMOVE, 
				new Runnable() {
					@Override
					public void run() {
						if (recipients.getListView().getSelectionModel()
								.getSelectedItems().isEmpty()) {
							return;
						}
						final MailRecipient[] ms = cb.getActor().getHost()
								.getMailRecipients()
								.toArray(new MailRecipient[] {});
						final Object[] rmaddyi = recipients.getListView()
								.getSelectionModel().getSelectedIndices()
								.toArray(new Object[] {});
						final java.util.List<String> rmaddys = recipients
								.getListView()
								.getSelectionModel()
								.getSelectedItems()
								.subList(
										0,
										recipients.getListView()
												.getSelectionModel()
												.getSelectedItems().size());
						try {
							recipients.getListView().getItems()
									.removeAll(rmaddys);
							// need to manually remove the mail recipients due
							// to many-to-many relationship
							final MailRecipient[] mrr = new MailRecipient[rmaddyi.length];
							for (final Object i : rmaddyi) {
								mrr[mrr.length - 1] = ms[(int) i];
							}
							ServiceProvider.IMPL.getCredentialService().mergeHost(
									cb.getActor().getHost(), mrr);
						} catch (final Throwable e) {
							log.info(String.format(
									"Unable to remove mail recipient(s) \"%1$s\" in host with ID = %2$s",
									rmaddys, cb.getActor().getHost().getId()), e);
							controlBar.setHelpText(RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_EMAILS_REMOVE_FAILED));
							cb.getActor().getHost().getMailRecipients().clear();
							cb.getActor().getHost().getMailRecipients().addAll(Arrays.asList(ms));
							cb.getActorPA().setBean(cb.getActor());
							
						}
					}
				});
		controlBar.addHelpTextTrigger(recipientRem,
				RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_EMAILS_REMOVE));
		final HBox recipientFuncBox = new HBox(5);
		recipientFuncBox.getChildren().addAll(recipient, recipientAdd, recipientRem);
		recipients = new UGateCtrlBox<>(controlBar.getActorPA(),
				ActorType.MAIL_RECIPIENTS, MailRecipientType.EMAIL,
				MailRecipient.class, RS.rbLabel(KEYS.MAIL_ALARM_NOFITY_EMAILS),
				null, 100d, "", new String[] {}, String.class);
		controlBar.addHelpTextTrigger(recipients,
				RS.rbLabel(KEYS.MAIL_ALARM_NOTIFY_EMAILS_DESC));

		// update the status when email connections are made/lost
		UGateKeeper.DEFAULT.addListener(new UGateListener() {
			@Override
			public void handle(final UGateEvent<?, ?> event) {
				if (event.getType() == UGateEvent.Type.EMAIL_CONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel(KEYS.MAIL_CONNECTING));
					emailIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OPEN, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.EMAIL_CONNECTED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel(KEYS.MAIL_CONNECTED));
					emailIcon.setStatusFill(GuiUtil.COLOR_ON);
				} else if (event.getType() == UGateEvent.Type.EMAIL_CONNECT_FAILED) {
					connect.setDisable(false);
					connect.setText(RS.rbLabel(KEYS.MAIL_CONNECT));
					emailIcon.setStatusFill(GuiUtil.COLOR_OFF);
				} else if (event.getType() == UGateEvent.Type.EMAIL_DISCONNECTING) {
					connect.setDisable(true);
					connect.setText(RS.rbLabel(KEYS.MAIL_DISCONNECTING));
					emailIcon.setStatusFill(Duration.seconds(1), 
							GuiUtil.COLOR_OFF, GuiUtil.COLOR_CLOSED, 
							Timeline.INDEFINITE);
				} else if (event.getType() == UGateEvent.Type.EMAIL_DISCONNECTED
						|| event.getType() == UGateEvent.Type.EMAIL_CLOSED || 
						event.getType() == UGateEvent.Type.EMAIL_AUTH_FAILED) {
					// run later in case the application is going to exit which
					// will cause an issue with FX thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							connect.setDisable(false);
							connect.setText(RS.rbLabel(KEYS.MAIL_CONNECT));
							emailIcon.setStatusFill(GuiUtil.COLOR_OFF);
						}
					});
				}
			}
		});

		connect = new Button();
		cb.addServiceBehavior(connect, null, ServiceProvider.Type.EMAIL,
				KEYS.MAIL_CONNECT);

		final GridPane grid = new GridPane();
		grid.setHgap(10d);
		grid.setVgap(30d);

		final VBox toggleView = new VBox(10d);
		toggleView.getChildren().addAll(emailIcon);

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
		connectionGrid.add(recipientFuncBox, 0, 5, 2, 1);

		grid.add(toggleView, 0, 0);
		grid.add(connectionGrid, 1, 0);
		grid.add(connect, 1, 1, 2, 1);
		getChildren().add(grid);
	}
}

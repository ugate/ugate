package org.ugate.gui.components;

import java.util.InputMismatchException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.control.PasswordField;
import javafx.scene.control.PasswordFieldBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.security.sasl.AuthenticationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.ugate.UGateUtil;
import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.RoleType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.AppInfo;
import org.ugate.service.entity.jpa.Host;

/**
 * Responsible for starting a {@link DialogService} that will prompt the user
 * for credentials to access the application or will prompt the user to enter
 * setup information when it's the first time the application is ran or required
 * information is missing.
 */
public enum UGateStartupDialog {
	DFLT;

	private static final Logger log = UGateUtil.getLogger(UGateStartupDialog.class);

	/**
	 * Starts a {@link DialogService} that will prompt the user for credentials
	 * to access the application or will prompt the user to enter setup
	 * information when it's the first time the application is ran or required
	 * information is missing.
	 * 
	 * @param startupHandler
	 *            the {@link StartupHandler} that will call
	 *            {@link StartupHandler#handle(Stage, Actor)} when auto-login is
	 *            detected
	 * @param stage
	 *            the {@link Stage}
	 * @param controlBar
	 *            the {@link ControlBar}
	 */
	public void start(final StartupHandler startupHandler, final Stage stage, final ControlBar controlBar) {
		final String appVersion = RS.rbLabel(KEY.APP_VERSION);
		if (hasAutoLogin(startupHandler, stage, controlBar, appVersion)) {
			return;
		}
		// if there are no users then the user needs to be prompted for a username/password
		final boolean isAuth = ServiceProvider.IMPL.getCredentialService().getActorCount() > 0;
		String dialogHeader;
		if (isAuth) {
			dialogHeader = RS.rbLabel(KEY.APP_DIALOG_AUTH);
			log.debug("Presenting authentication dialog prompt");
		} else {
			dialogHeader = RS.rbLabel(KEY.APP_DIALOG_SETUP);
			log.info("Initializing post installation dialog prompt");
		}
		final UGateDirectory wirelessRemoteNodeDirBox = isAuth ? null : new UGateDirectory(stage);
		if (!isAuth) {
			wirelessRemoteNodeDirBox.getTextField().setPromptText(RS.rbLabel(KEY.WIRELESS_WORKING_DIR));
		}
		final ComboBox<String> wirelessPort = isAuth ? null : ComboBoxBuilder.<String> create()
				.items(FXCollections.observableArrayList(ServiceProvider.IMPL
						.getWirelessService().getSerialPorts())).maxWidth(Double.MAX_VALUE).promptText(RS.rbLabel(KEY.WIRELESS_PORT)).build();
		final ComboBox<Integer> wirelessBaud = isAuth ? null : ComboBoxBuilder.<Integer> create()
				.items(FXCollections.observableArrayList(ActorType.HOST_BAUD_RATES)).maxWidth(Double.MAX_VALUE).promptText(RS.rbLabel(KEY.WIRELESS_SPEED)).build();
		final TextField wirelessHostAddy = isAuth ? null : TextFieldBuilder.create().promptText(RS.rbLabel(KEY.WIRELESS_HOST_ADDY)).build();
		final TextField wirelessRemoteNodeAddy = isAuth ? null : TextFieldBuilder.create().promptText(RS.rbLabel(KEY.WIRELESS_NODE_REMOTE_ADDY)).build();
		//final FileChooser wirelessRemoteNodeDir = new FileChooser();
		//wirelessRemoteNodeDir.setTitle(RS.rbLabel(KEYS.WIRELESS_WORKING_DIR));
		final TextField username = TextFieldBuilder.create().promptText(RS.rbLabel(KEY.APP_DIALOG_USERNAME)).build();
		final PasswordField password = PasswordFieldBuilder.create().promptText(RS.rbLabel(KEY.APP_DIALOG_PWD)).build();
		final PasswordField passwordVerify = isAuth ? null : PasswordFieldBuilder.create().promptText(
				RS.rbLabel(KEY.APP_DIALOG_PWD_VERIFY)).build();
		final CheckBox autoActor = CheckBoxBuilder.create().text(RS.rbLabel(KEY.APP_DIALOG_DEFAULT_USER)).build();
		final Button closeBtn = ButtonBuilder.create().text(RS.rbLabel(KEY.CLOSE)).build();
		final GuiUtil.DialogService dialogService = GuiUtil.dialogService(stage, KEY.APP_TITLE, dialogHeader, null, 550d, isAuth ? 200d : 400d, new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					private Actor actor;
					@Override
					protected Void call() throws Exception {
						final boolean hasWirelessPort = isAuth ? true : wirelessPort.getValue() != null && 
								!wirelessPort.getValue().isEmpty();
						final boolean hasWirelessBaud = isAuth ? true : wirelessBaud.getValue() != null && 
								wirelessBaud.getValue() >= 0;
						final boolean hasWirelessHostAddy = isAuth ? true : !wirelessHostAddy.getText().isEmpty();
						final boolean hasWirelessRemoteNodeAddy = isAuth ? true : !wirelessRemoteNodeAddy.getText().isEmpty();
						final boolean hasWirelessRemoteNodeDir = isAuth ? true : !wirelessRemoteNodeDirBox.getTextField().getText().isEmpty();
						final boolean hasUsername = !username.getText().isEmpty();
						final boolean hasPassword = !password.getText().isEmpty();
						final boolean hasPasswordVerify = isAuth ? true : !passwordVerify.getText().isEmpty();
						if (hasWirelessPort && hasWirelessBaud && hasWirelessRemoteNodeAddy && hasWirelessRemoteNodeDir && 
								hasUsername && hasPassword && hasPasswordVerify) {
							try {
								if (isAuth) {
									actor = ServiceProvider.IMPL.getCredentialService().authenticate(
											username.getText(), password.getText());
									if (actor == null) {
										throw new AuthenticationException(RS.rbLabel(KEY.APP_DIALOG_AUTH_ERROR, 
												username.getText()));
									}
								} else {
									if (!password.getText().equals(passwordVerify.getText())) {
										throw new InputMismatchException(RS.rbLabel(
												KEY.APP_DIALOG_SETUP_ERROR_PWD_MISMATCH));
									}
									final Host host = ActorType.newDefaultHost();
									host.setComPort(wirelessPort.getValue());
									host.setComBaud(wirelessBaud.getValue());
									host.setComAddress(wirelessHostAddy.getText());
									host.setMailUserName(username.getText());
									host.setMailPassword(password.getText());
									host.getRemoteNodes().iterator().next().setAddress(wirelessRemoteNodeAddy.getText());
									host.getRemoteNodes().iterator().next().setWorkingDir(
											wirelessRemoteNodeDirBox.getTextField().getText());
									final String hvm = controlBar.validate(host);
									if (hvm != null && hvm.length() > 0) {
										throw new ValidationException(hvm);
									}
									final Actor a = ActorType.newActor(username.getText(), password.getText(), 
											host, RoleType.ADMIN.newRole());
									final String avm = controlBar.validate(a);
									if (avm != null && avm.length() > 0) {
										throw new ValidationException(avm);
									}
									actor = ServiceProvider.IMPL.getCredentialService().addUser(a,
													autoActor.isSelected() ? appVersion : null);
									if (actor == null) {
										throw new IllegalArgumentException("Unable to add user " + username.getText());
									}
									controlBar.setDefaultActor(autoActor.isSelected(), false);
								}
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										startupHandler.handle(stage, actor);
									}
								});
							} catch (final Throwable t) {
								String errorMsg;
								if (t instanceof AuthenticationException || t instanceof InputMismatchException || 
										t instanceof ValidationException) {
									errorMsg = t.getMessage();
								} else {
									errorMsg = RS.rbLabel(isAuth ? 
											KEY.APP_DIALOG_AUTH_ERROR : KEY.APP_DIALOG_SETUP_ERROR, username.getText());
									log.warn(errorMsg, t);
								}
								throw new RuntimeException(errorMsg, t);
							}
						} else {
							final String invalidFields = (!hasWirelessPort ? wirelessPort.getPromptText() : "") + ' ' +
									(!hasWirelessBaud ? wirelessBaud.getPromptText() : "") + ' ' +
									(!hasWirelessHostAddy ? wirelessHostAddy.getPromptText() : "") + ' ' +
									(!hasWirelessRemoteNodeAddy ? wirelessRemoteNodeAddy.getPromptText() : "") +
									(!hasWirelessRemoteNodeDir ? wirelessRemoteNodeDirBox.getTextField().getPromptText() : "") +
									(!hasUsername ? username.getPromptText() : "") + 
									(!hasPassword ? password.getPromptText() : "") + 
									(!hasPasswordVerify ? passwordVerify.getPromptText() : "");
							throw new RuntimeException(RS.rbLabel(KEY.APP_DIALOG_REQUIRED, invalidFields));
						}
						return null;
					}
				};
			}
		}, null, closeBtn, wirelessPort, wirelessBaud, wirelessHostAddy, wirelessRemoteNodeAddy, wirelessRemoteNodeDirBox, 
		username, password, passwordVerify, autoActor);
		if (closeBtn != null) {
			closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					dialogService.hide();
				}
			});	
		}
		dialogService.start();
	}

	/**
	 * Determines if there is an {@link Actor} available for auto-login in
	 * {@link AppInfo}
	 * 
	 * @param startupHandler
	 *            the {@link StartupHandler} that will call
	 *            {@link StartupHandler#handle(Stage, Actor)} when auto-login is
	 *            detected
	 * @param stage
	 *            the {@link Stage}
	 * @param controlBar
	 *            the {@link ControlBar}
	 * @param appVersion
	 *            the {@link AppInfo#getVersion()}
	 * @return true when auto-login has been detected and
	 *         {@link StartupHandler#handle(Stage, Actor)} will be called
	 */
	private boolean hasAutoLogin(final StartupHandler startupHandler,
			final Stage stage, final ControlBar controlBar,
			final String appVersion) {
		final AppInfo appInfo = ServiceProvider.IMPL.getCredentialService()
				.addAppInfoIfNeeded(appVersion);
		if (appInfo.getDefaultActor() != null
				&& appInfo.getDefaultActor().getId() > 0) {
			log.info(String
					.format("Default %1$s (ID: %2$s) for application verion %3$s found",
							Actor.class.getName(), appInfo.getDefaultActor()
									.getId(), appInfo.getVersion()));
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					startupHandler.handle(stage, appInfo.getDefaultActor());
					controlBar.setDefaultActor(true, false);
				}
			});
			return true;
		}
		return false;
	}

	/**
	 * Handler called after a
	 * {@link UGateStartupDialog#start(StartupHandler, Stage, ControlBar)} operation
	 * completes
	 */
	public static interface StartupHandler {

		/**
		 * Handles {@link UGateStartupDialog} completion
		 * 
		 * @param stage
		 *            the {@link Stage}
		 * @param actor
		 *            the authenticated {@link Actor}
		 */
		public void handle(final Stage stage, final Actor actor);
	}
}

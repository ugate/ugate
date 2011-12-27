package org.ugate.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.log4j.Logger;
import org.ugate.resources.RS;

/**
 * System tray that uses AWT until JavaFX 3.0 is released 
 * {@linkplain http://javafx-jira.kenai.com/browse/RT-17503}.
 */
public class SystemTray {

	private static final Logger log = Logger.getLogger(SystemTray.class);
	private static volatile Stage stage;
	private static volatile Stage dummyPopup;
	//private static final Semaphore waitForFX = new Semaphore(-1, true);
	
	private SystemTray() {
	}
	
	/**
	 * Creates a system tray icon that when clicked will restore the specified stage.
	 * When the stage is {@linkplain Stage#setIconified(boolean)} is set to <code>true</code>
	 * the stage is hidden/closed. When it is set to <code>false</code> the stage will be restored.
	 * 
	 * @param stage the stage that will be controlled by the system tray
	 * @return true when the creation is successful
	 */
	public static boolean createSystemTray(final Stage stage) {
		if (!java.awt.SystemTray.isSupported()) {
			return false;
		}
		if (stage == null) {
			throw new NullPointerException("Stage cannot be null and must not be showing");
		}
		if (SystemTray.stage != null) {
			throw new IllegalStateException(SystemTray.class.getName() + " can only be created once");
		}
		if (!Platform.isFxApplicationThread()) {
			throw new IllegalStateException(SystemTray.class.getName() + 
					" can only be create within the JavaFX application thread");
		}
		final java.awt.SystemTray st = java.awt.SystemTray.isSupported() ? java.awt.SystemTray.getSystemTray() : null;
		if (st != null && st.getTrayIcons().length == 0) {
			// listen for minimize changes and handle minimize/restore functions from the system tray
			SystemTray.stage = stage;
			SystemTray.stage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> paramObservableValue, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						minimizeToSystemTray();
					} else {
						restoreFromSystemTray();
					}
				}
			});
			final String imageName = st.getTrayIconSize().width > 16 ? 
					st.getTrayIconSize().width > 64 ? RS.IMG_LOGO_128 : RS.IMG_LOGO_64 : RS.IMG_LOGO_16;
			try {
				final java.awt.Image image = javax.imageio.ImageIO.read(RS.stream(imageName));
				final java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
				//UGateKeeper.DEFAULT.
				trayIcon.setToolTip("UGate");
				st.add(trayIcon);
				trayIcon.addMouseListener(new java.awt.event.MouseListener() {
					@Override
					public void mouseReleased(java.awt.event.MouseEvent e) {
					}
					@Override
					public void mousePressed(java.awt.event.MouseEvent e) {
					}
					@Override
					public void mouseExited(java.awt.event.MouseEvent e) {
					}
					@Override
					public void mouseEntered(java.awt.event.MouseEvent e) {
					}
					@Override
					public void mouseClicked(java.awt.event.MouseEvent e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								restoreFromSystemTray();
							}
						});
					}
				});
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						exit();
					}
				});
				return false;
			} catch (final java.io.IOException e) {
				log.error("Unable to add system tray icons", e);
			} catch (java.awt.AWTException e) {
				log.error("Unable to add system tray icons", e);
			}
		}
		return false;
	}
	
	public static void minimizeToSystemTray() {
		if (dummyPopup == null) {
			// javafx.stage.Popup does not work
			dummyPopup = new Stage();
			dummyPopup.initModality(Modality.NONE);
			dummyPopup.initStyle(StageStyle.UTILITY);
			dummyPopup.setWidth(10d);
			dummyPopup.setHeight(10d);
			dummyPopup.setOpacity(0d);
			final Group root = new Group();
			root.getChildren().add(new Text("Close"));
			dummyPopup.setScene(new Scene(root, 10d, 10d, Color.TRANSPARENT));
			dummyPopup.show();
		}
		stage.hide();
	}
	
	public static void restoreFromSystemTray() {
		stage.show();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.toFront();
			}
		});
		if (dummyPopup != null) {
			dummyPopup.close();
			dummyPopup = null;
		}
	}
	
	public static void exit() {
		final java.awt.SystemTray st = java.awt.SystemTray.isSupported() ? java.awt.SystemTray.getSystemTray() : null;
		if (st != null && st.getTrayIcons().length > 0) {
			log.debug("Removing system tray icon(s)...");
			for (java.awt.TrayIcon trayIcon : st.getTrayIcons()) {
				try {
					st.remove(trayIcon);
				} catch (final Throwable t) {
					log.warn("Unable to remove system tray icon", t);
				}
			}
		}
		// TODO : shutdown AWT
	}
}

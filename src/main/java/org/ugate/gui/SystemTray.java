package org.ugate.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.log4j.Logger;
import org.ugate.resources.RS;

public class SystemTray {

	private static final Logger log = Logger.getLogger(SystemTray.class);
	private static volatile Stage stage;
	private static volatile Stage dummyPopup;
	private static volatile java.awt.SystemTray st;
	//private static final Semaphore waitForFX = new Semaphore(-1, true);
	
	private SystemTray() {
	}
	
	public static boolean createSystemTray(final Stage stage) {
		if (!java.awt.SystemTray.isSupported()) {
			return false;
		}
		if (st != null) {
			throw new IllegalStateException(SystemTray.class.getName() + " can only be created once");
		}
		if (!Platform.isFxApplicationThread()) {
			throw new IllegalStateException(SystemTray.class.getName() + 
					" can only be create within the JavaFX application thread");
		}
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
		if (st == null) {
			st = java.awt.SystemTray.isSupported() ? java.awt.SystemTray.getSystemTray() : null;
			if (st != null && st.getTrayIcons().length == 0) {
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
				} catch (final java.io.IOException e) {
					log.error("Unable to add system tray icons", e);
				} catch (java.awt.AWTException e) {
					log.error("Unable to add system tray icons", e);
				}
			}
		}
		return true;
	}
	
	public static void minimizeToSystemTray() {
		if (dummyPopup == null) {
			// javafx.stage.Popup does not work
			dummyPopup = new Stage();
			dummyPopup.initModality(Modality.NONE);
			dummyPopup.initStyle(StageStyle.UTILITY);
			dummyPopup.setWidth(0);
			dummyPopup.setHeight(0);
			dummyPopup.setOpacity(0);
			final Scene dummyScene = new Scene(new Group(), 10d, 10d, Color.TRANSPARENT);
			dummyScene.setFill(null);
			dummyPopup.show();
		}
		stage.hide();
	}
	
	public static void restoreFromSystemTray() {
		stage.show();
		stage.toFront();
		if (dummyPopup != null) {
			dummyPopup.close();
			dummyPopup = null;
		}
	}
	
	public static void exit() {
		if (st != null) {
			log.debug("Removing system tray icon(s)...");
			for (java.awt.TrayIcon trayIcon : st.getTrayIcons()) {
				try {
					st.remove(trayIcon);
				} catch (final Throwable t2) {
					log.warn("Unable to remove system tray icon", t2);
				}
			}
			// TODO : shutdown AWT
		}
	}
}

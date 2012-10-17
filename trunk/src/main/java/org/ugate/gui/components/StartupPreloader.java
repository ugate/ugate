package org.ugate.gui.components;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.stage.Stage;

import org.ugate.gui.GuiUtil.Dialog;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;

/**
 * {@linkplain Preloader} shown when the application is starting
 */
public class StartupPreloader extends Preloader {

	public static final double WIDTH = 550d;
	public static final double HEIGHT = 100d;
	private ProgressBar bar;
	private Stage stage;
	boolean noLoadingProgress = true;

	/**
	 * @return the {@linkplain Preloader} {@linkplain Scene}
	 */
	private void createPreloaderScene() {
		bar = new ProgressBar();
		bar.setMinWidth(WIDTH / 1.3d);
		final Light light = new Light.Distant();
		final Lighting lighting = new Lighting();
		lighting.setSurfaceScale(3d);
		lighting.setLight(light);
		bar.setEffect(lighting);
		new Dialog(null, stage, KEY.APP_TITLE, RS.rbLabel(KEY.LOADING), null,
				WIDTH, HEIGHT, bar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;
		stage.setTitle(RS.rbLabel(KEY.APP_TITLE));
		createPreloaderScene();
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleProgressNotification(ProgressNotification pn) {
		// application loading progress is rescaled to be first 50%
		// Even if there is nothing to load 0% and 100% events can be
		// delivered
		if (pn.getProgress() != 1.0 || !noLoadingProgress) {
			bar.setProgress(pn.getProgress() / 2);
			if (pn.getProgress() > 0) {
				noLoadingProgress = false;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleStateChangeNotification(StateChangeNotification evt) {
		// ignore, hide after application signals it is ready
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleApplicationNotification(PreloaderNotification pn) {
		if (pn instanceof ProgressNotification) {
			// expect application to send us progress notifications
			// with progress ranging from 0 to 1.0
			double v = ((ProgressNotification) pn).getProgress();
			if (!noLoadingProgress) {
				// if we were receiving loading progress notifications
				// then progress is already at 50%.
				// Rescale application progress to start from 50%
				v = 0.5 + v / 2;
			}
			bar.setProgress(v);
		} else if (pn instanceof StateChangeNotification) {
			stage.hide();
			// fade out, hide stage at the end of animation
			// final FadeTransition ft = new
			// FadeTransition(Duration.millis(1000),
			// stage.getScene().getRoot());
			// ft.setFromValue(1.0);
			// ft.setToValue(0.0);
			// final Stage s = stage;
			// final EventHandler<ActionEvent> eh = new
			// EventHandler<ActionEvent>() {
			// public void handle(ActionEvent t) {
			// s.hide();
			// }
			// };
			// ft.setOnFinished(eh);
			// ft.play();
		}
	}
}

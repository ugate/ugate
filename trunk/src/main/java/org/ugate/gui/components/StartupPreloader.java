package org.ugate.gui.components;

import org.ugate.resources.RS;

import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * {@linkplain Preloader} shown when the application is starting
 */
public class StartupPreloader extends Preloader {
	
	ProgressBar bar;
	Stage stage;
	boolean isEmbedded = false;

	/**
	 * @return the {@linkplain Preloader} {@linkplain Scene}
	 */
	private Scene createPreloaderScene() {
		bar = new ProgressBar();
		final Label lbl = new Label(RS.rbLabel("app.title") + ' ' + RS.rbLabel("loading"));
		final BorderPane p = new BorderPane();
		p.setTop(lbl);
		p.setCenter(bar);
		return new Scene(p, 300, 150);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final Stage stage) throws Exception {
	    //embedded stage has preset size
	    isEmbedded = (stage.getWidth() > 0);
	    this.stage = stage;
	    stage.setTitle(RS.rbLabel("app.title"));
	    stage.setScene(createPreloaderScene());  
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleProgressNotification(final ProgressNotification pn) {
		if (pn.getProgress() != 1 && !stage.isShowing()) {
			stage.show();
		}
		bar.setProgress(pn.getProgress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleStateChangeNotification(final StateChangeNotification evt) {
	    if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
	        if (isEmbedded && stage.isShowing()) {
	            //fade out, hide stage at the end of animation
	            final FadeTransition ft = new FadeTransition(
	                Duration.millis(1000), stage.getScene().getRoot());
	                ft.setFromValue(1.0);
	                ft.setToValue(0.0);
	                final Stage s = stage;
	                final EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
	                    public void handle(ActionEvent t) {
	                        s.hide();
	                    }
	                };
	                ft.setOnFinished(eh);
	                ft.play();
	        } else {
	            stage.hide();
	        }
	    }
	}
}

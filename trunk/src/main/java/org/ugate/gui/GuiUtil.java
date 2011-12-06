package org.ugate.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * General GUI utility
 */
public class GuiUtil {

	/**
	 * Private utility constructor
	 */
	private GuiUtil() {
	}
	
	/**
	 * Adds the help text when right clicked
	 * 
	 * @param helpText the scroll pane the contains a label as it's content
	 * @param node the node to trigger the text
	 * @param text the text to show
	 */
	public static void addHelpText(final ScrollPane helpText, final Node node, final String text) {
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					helpText.setVvalue(helpText.getVmin());
					((Label) helpText.getContent()).setText(text);
					event.consume();
				}
			}
		});
	}
	
	/**
	 * Creates a time line that will alternate the colors of the drop shadow when played
	 *  
	 * @param ds the drop shadow
	 * @param color1 color one
	 * @param color2 color two
	 * @param cycleCount the cycle count
	 * @return the time line
	 */
	public static Timeline createDropShadowColorIndicatorTimline(final DropShadow ds, 
			final Color color1, final Color color2, final int cycleCount) {
		final Timeline timeline = new Timeline();
		timeline.setCycleCount(cycleCount <=0 ? Timeline.INDEFINITE : cycleCount);
		timeline.setAutoReverse(true);
		final KeyFrame kf = new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				ds.setColor(ds.getColor().equals(color2) ? color1 : color2);
			}
		});
		timeline.getKeyFrames().add(kf);
		return timeline;
	}
}

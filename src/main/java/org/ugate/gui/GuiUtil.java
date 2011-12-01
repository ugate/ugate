package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

/**
 * General GUI utility
 */
public class GuiUtil {

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
}

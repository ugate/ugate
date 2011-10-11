package org.ugate.gui;

import org.ugate.UGateKeeper;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordBox;
import javafx.scene.control.TextBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Wrapper for label and text/password controls
 */
public class UGateTextControl extends VBox {
	
	public final Label label;
	public final TextBox textBox;
	public final PasswordBox passwordBox;

	public UGateTextControl(String labelText, String preferencesTextKey, boolean isPassword) {
	    super();
		label = new Label();
	    label.setText(labelText);
	    final String textValue = UGateKeeper.DEFAULT.preferences.get(preferencesTextKey);
	    if (isPassword) {
	    	textBox = null;
	    	passwordBox = new PasswordBox();
	    	passwordBox.setPrefWidth(100);
	    	passwordBox.setTooltip(new Tooltip(label.getText()));
	    	passwordBox.setText(textValue);
		    getChildren().addAll(label, passwordBox);
	    } else {
	    	passwordBox = null;
		    textBox = new TextBox();
		    textBox.setPrefWidth(100);
		    textBox.setTooltip(new Tooltip(label.getText()));
		    textBox.setText(textValue);
		    getChildren().addAll(label, textBox);
	    }
	}
}

package org.ugate.gui.components;

import org.ugate.UGateKeeper;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Wrapper for label and text/password controls
 */
public class UGateTextField extends VBox {
	
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_TEXT_AREA = 1;
	public static final int TYPE_PASSWORD = 2;
	public final Label label;
	public final TextField textField;
	public final TextArea textArea;
	public final PasswordField passwordField;

	public UGateTextField(String labelText, String toolTip, String preferencesTextKey, int type) {
	    super();
		label = new Label();
	    label.setText(labelText);
	    label.setTooltip(new Tooltip(toolTip));
	    final String textValue = preferencesTextKey != null ? 
	    		UGateKeeper.DEFAULT.preferences.get(preferencesTextKey) : "";
	    if (type == TYPE_PASSWORD) {
	    	textField = null;
	    	textArea = null;
	    	passwordField = new PasswordField();
	    	passwordField.setPrefWidth(100);
	    	passwordField.setText(textValue);
		    getChildren().addAll(label, passwordField);
	    } else if (type == TYPE_TEXT_AREA) {
	    	textField = null;
	    	passwordField = null;
	    	textArea = new TextArea();
	    	textArea.setPrefWidth(100);
	    	textArea.setText(textValue);
		    getChildren().addAll(label, textArea);
	    } else {
	    	textArea = null;
	    	passwordField = null;
		    textField = new TextField();
		    textField.setPrefWidth(100);
		    textField.setText(textValue);
		    getChildren().addAll(label, textField);
	    }
	}
}

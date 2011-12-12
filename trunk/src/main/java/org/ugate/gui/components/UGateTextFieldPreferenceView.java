package org.ugate.gui.components;

import org.ugate.Settings;
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
public class UGateTextFieldPreferenceView extends VBox {
	
	public final Label label;
	public final TextField textField;
	public final TextArea textArea;
	public final PasswordField passwordField;

	/**
	 * Creates a text field preference view
	 * 
	 * @param preferenceKey the preference key
	 * @param type the type
	 * @param labelText the label text
	 * @param toolTip the tool tip
	 */
	public UGateTextFieldPreferenceView(final Settings preferenceKey, 
			final Type type, final String labelText, final String toolTip) {
	    super();
		label = new Label();
	    label.setText(labelText);
	    label.setTooltip(new Tooltip(toolTip));
	    final String textValue = preferenceKey != null ? 
	    		UGateKeeper.DEFAULT.preferencesGet(preferenceKey) : "";
	    if (type == Type.TYPE_PASSWORD) {
	    	textField = null;
	    	textArea = null;
	    	passwordField = new PasswordField();
	    	passwordField.setPrefWidth(100);
	    	passwordField.setText(textValue);
		    getChildren().addAll(label, passwordField);
	    } else if (type == Type.TYPE_TEXT_AREA) {
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
	
	/**
	 * The type of text control
	 */
	public enum Type {
		TYPE_TEXT,
		TYPE_TEXT_AREA,
		TYPE_PASSWORD;
	}
}

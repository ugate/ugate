package org.ugate.gui.components;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Wrapper for label and choice box controls
 */
public class UGateChoiceBox<T> extends VBox {

	public final Label label;
	public final ChoiceBox<T> choice;

	public UGateChoiceBox(String labelText, T[] choices) {
	    super();
		label = new Label();
	    label.setText(labelText);
	    choice = new ChoiceBox<T>(FXCollections.observableArrayList(choices));
	    choice.setTooltip(new Tooltip(label.getText()));
	    if (!choice.getItems().isEmpty()) {
	    	choice.getSelectionModel().select(0);
	    }
	    getChildren().addAll(label, choice);
	}
	
}

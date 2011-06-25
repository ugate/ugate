package org.ugate.gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Wrapper for label and choice box controls
 */
public class UGateChoiceBoxControl<T> extends VBox {

	public final Label label;
	public final ChoiceBox<T> choice;

	public UGateChoiceBoxControl(String labelText, T[] choices) {
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

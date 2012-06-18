package org.ugate.gui.components;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.ugate.resources.RS;

/**
 * Wrapper for {@linkplain Label} and {@linkplain ComboBox} controls
 */
public class UGateComboBox<T> extends VBox {

	private final Label label;
	private final ComboBox<T> comboBox;

	/**
	 * Creates a choice box with a label
	 * 
	 * @param labelText
	 *            the label text
	 * @param choices
	 *            the choices
	 */
	public UGateComboBox(final String labelText, final List<T> choices) {
		this(labelText, FXCollections.observableArrayList(choices));
	}

	/**
	 * Creates a choice box with a label
	 * 
	 * @param labelText
	 *            the label text
	 * @param choices
	 *            the choices
	 */
	@SafeVarargs
	public UGateComboBox(final String labelText, final T... choices) {
		this(labelText, FXCollections.observableArrayList(choices));
	}
	
	/**
	 * Creates a choice box with a label
	 * 
	 * @param labelText
	 *            the label text
	 * @param choices
	 *            the choices
	 */
	public UGateComboBox(final String labelText, final ObservableList<T> choices) {
		super();
		label = new Label();
		label.setText(labelText);
		comboBox = new ComboBox<T>(choices);
		// choice.setTooltip(new Tooltip(label.getText()));
		comboBox.setPromptText(RS.rbLabel("select"));
		comboBox.autosize();
		getChildren().addAll(label, comboBox);
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @return the comboBox
	 */
	public ComboBox<T> getComboBox() {
		return comboBox;
	}
}

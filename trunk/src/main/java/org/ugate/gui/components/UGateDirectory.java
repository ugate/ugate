package org.ugate.gui.components;

import java.io.File;

import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * {@linkplain HBox} working directory chooser
 */
public class UGateDirectory extends HBox {

	private final TextField textField;

	/**
	 * Constructor
	 * 
	 * @param stage
	 *            the {@linkplain Stage}
	 */
	public UGateDirectory(final Stage stage) {
		super(10);
		textField = TextFieldBuilder.create().editable(false).build();
		HBox.setHgrow(textField, Priority.ALWAYS);
		final Button wirelessRemoteNodeDirBtn = new FunctionButton(
				FunctionButton.Function.ADD, new Runnable() {
					@Override
					public void run() {
						final DirectoryChooser dc = new DirectoryChooser();
						dc.setTitle(RS.rbLabel(KEYS.WIRELESS_WORKING_DIR));
						final File wdir = dc.showDialog(stage);
						if (wdir != null) {
							textField.setText(wdir.getAbsolutePath());
						}
					}
				});
		getChildren().addAll(textField, wirelessRemoteNodeDirBtn);
	}

	/**
	 * @return the {@linkplain TextField} that will contain the chosen directory
	 */
	public TextField getTextField() {
		return textField;
	}
}

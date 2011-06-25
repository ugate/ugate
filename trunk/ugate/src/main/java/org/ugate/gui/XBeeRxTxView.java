package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;

/**
 * XBee GUI view responsible for communicating with local and remote XBees once a connection is established
 */
public class XBeeRxTxView extends StatusView {

	private static final Logger log = Logger.getLogger(XBeeRxTxView.class);
	public final UGateChoiceBoxControl<String> txCommands;
	public final UGateTextControl txRecipients;
	public final Button tx;

	public XBeeRxTxView() {
		super(10);
		final VBox txView = new VBox(20);
		final HBox txElementView = new HBox(20);
		txCommands = new UGateChoiceBoxControl<String>("Gate Commands", 
				UGateKeeper.GATE_COMMANDS.keySet().toArray(new String[]{}));
		txRecipients = new UGateTextControl("Recipients (semi-colon delimited emails)", UGateKeeper.MAIL_RECIPIENTS_KEY, false);
		txElementView.getChildren().addAll(txCommands, txRecipients);
		tx = new Button();
		final EventHandler<MouseEvent> txBtnHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (UGateKeeper.DEFAULT.xbeeIsConnected() && !txCommands.choice.getSelectionModel().isEmpty()) {
					if (txRecipients.textBox.getText().isEmpty()) {
						log.info("No recipients selected");
						return;
					}
					UGateKeeper.DEFAULT.preferences.set(UGateKeeper.MAIL_RECIPIENTS_KEY, txRecipients.textBox.getText());
					final String key = txCommands.choice.getSelectionModel().getSelectedItem();
					final String value = UGateKeeper.GATE_COMMANDS.get(key);
					log.debug("Sending XBee command: " + value + " (" + key + ')');
					UGateKeeper.DEFAULT.xbeeSendData(
							UGateKeeper.DEFAULT.GATE_XBEE_ADDRESS, value);
				}
			}
		};
		tx.addEventHandler(MouseEvent.MOUSE_CLICKED, txBtnHandler);
		tx.setText("Send Command");
		txView.getChildren().addAll(txElementView, tx);
		final SplitPane splitPane = new SplitPane();
		HBox.setMargin(splitPane, new Insets(10, 10, 10, 10));
		HBox.setHgrow(splitPane, Priority.ALWAYS);
		VBox.setMargin(splitPane, new Insets(10, 10, 10, 10));
		VBox.setVgrow(splitPane, Priority.ALWAYS);
		splitPane.setPrefHeight(getMaxHeight());
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.getItems().addAll(txView, new VBox());
		getChildren().addAll(splitPane);
	}

	@Override
	public void handleStatusChange(Boolean on) {
		
	}
}

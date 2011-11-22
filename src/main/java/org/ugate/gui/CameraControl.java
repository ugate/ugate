package org.ugate.gui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateGaugeDisplay;
import org.ugate.gui.components.UGateTextField;
import org.ugate.resources.RS;

/**
 * Camera control view
 */
public class CameraControl extends ControlPane {
	
	private UGateTextField recipients;
	private ToggleSwitchPreferenceView recipientsToggleSwitch;

	@Override
	protected Node[] createLeftViewChildren() {
		final Label header = new Label("Email Configuration");
		header.getStyleClass().add("gauge-header");
		recipientsToggleSwitch = new ToggleSwitchPreferenceView(UGateUtil.SV_MAIL_ALARM_ON_KEY, 
				RS.IMG_EMAIL_SELECTED, RS.IMG_EMAIL_DESELECTED, 
				"Toggle sending email notifications for images taken (by alarm trip or manually)");
		recipients = new UGateTextField("Recipients (semi-colon delimited emails)", 
				"Semi-colon delimited list of emails to send image to (blank if no emails should be sent)",
				UGateUtil.SV_MAIL_RECIPIENTS_KEY, UGateTextField.TYPE_TEXT_AREA);
		recipients.textArea.setPrefRowCount(5);
		return new Node[] { header, recipientsToggleSwitch, recipients };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final Label header = new Label("Movement");
		header.getStyleClass().add("gauge-header");
		final GridPane grid = new GridPane();
		GridPane.setMargin(grid, PADDING_INSETS);
		//grid.setHgap(5d);
		//grid.setVgap(5d);
		final UGateGaugeDisplay camPanGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, 90d, "%03d", RS.IMG_PAN,
				"Camera Pan: Current camera pan angle (in degrees)", Color.YELLOW, null);
		grid.add(camPanGauge, 0, 0);
		final UGateGaugeDisplay camTiltGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 0, 90d, "%03d", RS.IMG_TILT,
				"Camera Tilt: Current camera tilt angle (in degrees)", Color.YELLOW, null);
		grid.add(camTiltGauge, 1, 0);
		return new Node[] { header, grid };
	}

	@Override
	protected Node[] createRightViewChildren() {
		final Label header = new Label("Actions");
		header.getStyleClass().add("gauge-header");
		final HBox camSendView = new HBox();
		camSendView.setAlignment(Pos.TOP_CENTER);
		camSendView.setPadding(new Insets(0, 0, 0, 5));
		camSendView.getChildren().add(UGateGUI.genFisheye(RS.IMG_CAM_VGA, 30, 30, 1.3, 1.3, false, new Runnable() {
			@Override
            public void run() {
            	
            }
        }));
		camSendView.getChildren().add(UGateGUI.genFisheye(RS.IMG_CAM_VGA, 60, 60, 1.3, 1.3, false, new Runnable() {
			@Override
            public void run() {
            	
            }
        }));
		HBox.setMargin(camSendView.getChildren().get(0), new Insets(0, 5, 0, 0));
		return new Node[] { header, camSendView };
	}
	
	public boolean addValues(final List<Integer> values) {
		// values need to be added in a predefined order
		UGateKeeper.DEFAULT.preferences.set(UGateUtil.SV_MAIL_RECIPIENTS_ON_KEY, 
				String.valueOf(recipientsToggleSwitch.toggleSwitch.selectedProperty().get()));
		UGateKeeper.DEFAULT.preferences.set(UGateUtil.SV_MAIL_RECIPIENTS_KEY, recipients.textField.getText());
		values.add(recipientsToggleSwitch.toggleSwitch.selectedProperty().get() ? 1 : 0);
		//values.add(recipientsToggleSwitch.toggleSwitch.selectedProperty().get() ? 1 : 0);
		return true;
	}
}

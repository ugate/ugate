package org.ugate.gui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateSliderGauge;
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
		final UGateSliderGauge camPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Camera Pan: Current camera pan angle (in degrees)", false, Color.AQUA, null);
		final UGateSliderGauge camTiltGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_TILT,
				"Camera Tilt: Current camera tilt angle (in degrees)", false, Color.AQUA, null);
		final HBox camSendView = new HBox();
		camSendView.setAlignment(Pos.TOP_CENTER);
		camSendView.setPadding(new Insets(0, 0, 0, 5));
		camSendView.getChildren().add(UGateGUI.genFisheye(RS.IMG_CAM_VGA, 20, 20, 1.3, 1.3, false, new Runnable() {
			@Override
            public void run() {
            	
            }
        }));
		camSendView.getChildren().add(UGateGUI.genFisheye(RS.IMG_CAM_VGA, 30, 30, 1.3, 1.3, false, new Runnable() {
			@Override
            public void run() {
            	
            }
        }));
		HBox.setMargin(camSendView.getChildren().get(0), new Insets(0, 5, 0, 0));
		return new Node[] { camPanGauge, camTiltGauge, camSendView };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final ImageView camNavStatusButton = RS.imgView(RS.IMG_SENSOR_ARM);
		final ImageView camNavButton = RS.imgView(RS.IMG_NAV_CAM);
		return new Node[] { camNavStatusButton, camNavButton };
	}

	@Override
	protected Node[] createRightViewChildren() {
		recipientsToggleSwitch = new ToggleSwitchPreferenceView(UGateUtil.SV_MAIL_ALARM_ON_KEY, 
				RS.IMG_EMAIL_SELECTED, RS.IMG_EMAIL_DESELECTED, 
				"Toggle sending email notifications for images taken (by alarm trip or manually)");
		final VBox mailView = new VBox();
		recipients = new UGateTextField("Recipients (semi-colon delimited emails)", 
				"Semi-colon delimited list of emails to send image to (blank if no emails should be sent)",
				UGateUtil.SV_MAIL_RECIPIENTS_KEY, UGateTextField.TYPE_TEXT_AREA);
		recipients.textArea.setPrefRowCount(5);
		mailView.getChildren().addAll(recipients);
		return new Node[] { recipientsToggleSwitch, mailView };
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

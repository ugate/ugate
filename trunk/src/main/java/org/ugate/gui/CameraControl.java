package org.ugate.gui;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.Gauge;
import org.ugate.gui.components.Gauge.IndicatorType;
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
	protected Node[] createTopViewChildren() {
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
		return new Node[] { camSendView };
	}

	@Override
	protected Node[] createLeftViewChildren() {
		final GridPane grid = new GridPane();
		GridPane.setMargin(grid, new Insets(20d, 20d, 20d, 20d));
		grid.setHgap(20d);
		grid.setVgap(20d);
		final UGateSliderGauge camPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Camera Pan: Current camera pan angle (in degrees)", false, Color.AQUA, null);
		final Gauge panCtrl = new Gauge(IndicatorType.KNOB, 0.3d, 10d, 0, 0d, 180d, 19, 4);
		panCtrl.snapToTicksProperty.set(true);
		panCtrl.tickMarkLabelFillProperty.set(Color.TRANSPARENT);
		panCtrl.intensityIndicatorRegionsProperty.set(new Gauge.IntensityIndicatorRegions(50d, 30d, 20d, 
				Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT));
		panCtrl.tickValueProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				camPanGauge.sliderValue.setValue(newValue.toString());
			}
		});
		camPanGauge.sliderValue.setValue(String.valueOf(panCtrl.getTickValue()));
		grid.add(camPanGauge, 0, 0);
		grid.add(panCtrl, 0, 1);
		final UGateSliderGauge camTiltGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_TILT,
				"Camera Tilt: Current camera tilt angle (in degrees)", false, Color.AQUA, null);
		final Gauge tiltCtrl = new Gauge(IndicatorType.KNOB, 0.3d, 10d, 0, 0d, 180d, 19, 0);
		tiltCtrl.snapToTicksProperty.set(true);
		tiltCtrl.tickMarkLabelFillProperty.set(Color.TRANSPARENT);
		tiltCtrl.intensityIndicatorRegionsProperty.set(new Gauge.IntensityIndicatorRegions(50d, 30d, 20d, 
				Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT));
		tiltCtrl.tickValueProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				camTiltGauge.sliderValue.setValue(newValue.toString());
			}
		});
		camTiltGauge.sliderValue.setValue(String.valueOf(tiltCtrl.getTickValue()));
		grid.add(camTiltGauge, 1, 0);
		grid.add(tiltCtrl, 1, 1);
		return new Node[] { grid };
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

package org.ugate.gui.view;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.ugate.gui.ControlBar;
import org.ugate.gui.GuiUtil;
import org.ugate.gui.components.UGateToggleSwitchBox;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * {@linkplain RemoteNode#getMultiAlarmTripState()} view 
 */
public class AlarmMultistates extends VBox {

	private final ControlBar cb;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, CHILD_PADDING, CHILD_PADDING);

	/**
	 * Constructor
	 * 
	 * @param controlBar
	 *            the {@linkplain ControlBar}
	 * @param orientation
	 *            the {@linkplain Orientation} of the view
	 */
	public AlarmMultistates(final ControlBar controlBar, final Orientation orientation) {
		this.cb = controlBar;
		final UGateToggleSwitchBox<RemoteNode> multiAlarmToggleSwitch = new UGateToggleSwitchBox<>(
				cb.getRemoteNodePA(),RemoteNodeType.MULTI_ALARM_TRIP_STATE, 
				new UGateToggleSwitchBox.ToggleItem(RS.IMG_SONAR_ALARM_MULTI, 
						RS.IMG_SONAR_ALARM_OFF, RS.IMG_SONAR_ALARM_ANY, null, false),
				new UGateToggleSwitchBox.ToggleItem(RS.IMG_PIR_ALARM_MULTI, 
						RS.IMG_PIR_ALARM_OFF, RS.IMG_PIR_ALARM_ANY, null, false),
				new UGateToggleSwitchBox.ToggleItem(RS.IMG_MICROWAVE_ALARM_MULTI,
						RS.IMG_MICROWAVE_ALARM_OFF, RS.IMG_MICROWAVE_ALARM_ANY, null, false),
				new UGateToggleSwitchBox.ToggleItem(RS.IMG_LASER_ALARM_MULTI, 
						RS.IMG_LASER_ALARM_OFF, RS.IMG_LASER_ALARM_ANY, null, false));
		final UGateToggleSwitchBox<RemoteNode> offToggleSwitch = new UGateToggleSwitchBox<>(
				controlBar.getRemoteNodePA(), RemoteNodeType.ALARMS_ON);
		GridPane multiAlarmGroup;
		if (orientation == Orientation.HORIZONTAL) {
			multiAlarmGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS,
					CHILD_SPACING, 10, false, 0, 0, multiAlarmToggleSwitch,
					offToggleSwitch);
			getChildren().addAll(new Label(RS.rbLabel(KEY.SENSOR_TRIP_MULTI)),
					multiAlarmGroup);
		} else {
			multiAlarmGroup = GuiUtil.createBackgroundDisplay(PADDING_INSETS,
					CHILD_SPACING, 1, false, 0, 0, multiAlarmToggleSwitch);
			getChildren().addAll(multiAlarmGroup, offToggleSwitch);
		}
		VBox.setVgrow(multiAlarmGroup, Priority.NEVER);
		cb.addHelpTextTrigger(multiAlarmGroup, RS.rbLabel(KEY.SENSOR_TRIP_MULTI_DESC));
	}
}

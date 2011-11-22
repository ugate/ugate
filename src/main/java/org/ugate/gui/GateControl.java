package org.ugate.gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateGaugeDisplay;
import org.ugate.gui.components.Gauge.IndicatorType;
import org.ugate.resources.RS;

public class GateControl extends ControlPane {
	
	@Override
	protected Node[] createLeftViewChildren() {
		final UGateGaugeDisplay gateGauge = new UGateGaugeDisplay(IndicatorType.KNOB, KNOB_SIZE_SCALE,
				10d, 0, 0, 180d, 19, 4, 90d, "%03d", RS.IMG_PAN,
				"Gate Pan: Current trip alram sensor pan angle (in degrees)",
				Color.YELLOW, null);
		return new Node[] { gateGauge };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		return new Node[] { gateToggleButton };
	}

	@Override
	protected Node[] createRightViewChildren() {
		final ToggleSwitchPreferenceView gateToggleSwitchView = new ToggleSwitchPreferenceView(
				UGateUtil.SV_GATE_ACCESS_ON_KEY, RS.IMG_GATE_SELECTED,
				RS.IMG_GATE_DESELECTED, "Toogle gate access");
		return new Node[] { gateToggleSwitchView };
	}
}

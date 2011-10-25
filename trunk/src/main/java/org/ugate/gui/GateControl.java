package org.ugate.gui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.gui.components.UGateSliderGauge;
import org.ugate.resources.RS;

public class GateControl extends ControlPane {

	@Override
	protected Node[] createTopViewChildren() {
		return new Node[] {  };
	}
	
	@Override
	protected Node[] createLeftViewChildren() {
		final UGateSliderGauge gateGauge = new UGateSliderGauge(1, 180, 90, 1,
				"%03d", RS.IMG_PAN,
				"Gate Pan: Current trip alram sensor pan angle (in degrees)",
				false, Color.YELLOW, null);
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

package org.ugate.gui;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import org.ugate.UGateUtil;
import org.ugate.resources.RS;

public class GateControl extends ControlPane {

	public GateControl(double toolbarTopHeight, double middleSpacing,
			double toolBarBottomHeight) {
		super(toolbarTopHeight, middleSpacing, toolBarBottomHeight);
	}

	@Override
	protected Node[] getToolBarTopItems() {
		final UGateSliderGauge gateGauge = new UGateSliderGauge(1, 180, 90, 1,
				"%03d", RS.IMG_PAN,
				"Gate Pan: Current trip alram sensor pan angle (in degrees)",
				false, Color.YELLOW, null);
		return new Node[] { gateGauge };
	}

	@Override
	protected Node[] getMiddleViewChildren() {
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		return new Node[] { gateToggleButton };
	}

	@Override
	protected Node[] getBottomViewChildren() {
		final ToggleSwitchPreferenceView gateToggleSwitchView = new ToggleSwitchPreferenceView(
				UGateUtil.GATE_ACCESS_ON_KEY, RS.IMG_GATE_SELECTED,
				RS.IMG_GATE_DESELECTED, "Toogle gate access");
		return new Node[] { gateToggleSwitchView };
	}

	@Override
	public boolean preSubmit(final List<Integer> values) {
		return false;
	}
}

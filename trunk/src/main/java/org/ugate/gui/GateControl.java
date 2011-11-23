package org.ugate.gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

import org.ugate.UGateUtil;
import org.ugate.gui.components.ToggleSwitchPreferenceView;
import org.ugate.resources.RS;

public class GateControl extends ControlPane {
	
	@Override
	protected Node[] createLeftViewChildren() {
		final Label header = new Label("Gate Configuration");
		header.getStyleClass().add("gauge-header");
		final ToggleSwitchPreferenceView gateToggleSwitchView = new ToggleSwitchPreferenceView(
				UGateUtil.SV_GATE_ACCESS_ON_KEY, RS.IMG_GATE_SELECTED,
				RS.IMG_GATE_DESELECTED, "Toogle gate access");
		return new Node[] { gateToggleSwitchView };
	}

	@Override
	protected Node[] createCenterViewChildren() {
		final Label header = new Label("Gate State (open/close)");
		header.getStyleClass().add("gauge-header");
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		final Group readingsGroup = createReadingsDisplay(gateToggleButton);
		return new Node[] { header, readingsGroup };
	}

	@Override
	protected Node[] createRightViewChildren() {
		final Label header = new Label("Actions");
		header.getStyleClass().add("gauge-header");
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		settingsSet.setEffect(new DropShadow());
		return new Node[] { header, settingsSet };
	}
}

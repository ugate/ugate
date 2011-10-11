package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import org.ugate.UGateKeeper;
import org.ugate.resources.RS;

/**
 * Toggle switch view that shows an image as an indicator as to what is being toggled on/off.
 * When turned on/off the preference will be automatically updated in the UGate preferences.
 * 
 * @see ToggleSwitch
 */
public class ToggleSwitchPreferenceView extends VBox {
	
	public final String preferenceKey;
	public final ImageView toggleSwitchImageView;
	public final ToggleSwitch toggleSwitch;
	public final Image imgOn;
	public final Image imgOff;
	
	/**
	 * Creates a toggle switch preference view
	 * 
	 * @param preferenceKey the preference key for getting/saving the preference option as it's selected
	 * @param onImageFileName the file name of the image shown when the toggled on
	 * @param offImageFileName the file name of the image shown when the toggled off
	 * @param toolTip the tool tip to show
	 */
	public ToggleSwitchPreferenceView(final String preferenceKey, final String onImageFileName, final String offImageFileName, final String toolTip) {
		this.preferenceKey = preferenceKey;
		this.imgOn = RS.img(onImageFileName);
		this.imgOff = RS.img(offImageFileName);
		setSpacing(5);
		toggleSwitch = new ToggleSwitch();
		final boolean isOn = Boolean.valueOf(UGateKeeper.DEFAULT.preferences.get(this.preferenceKey));
		toggleSwitch.selectedProperty().set(isOn);
		if (toolTip != null && !toolTip.isEmpty()) {
			toggleSwitch.tooltipProperty().set(new Tooltip(toolTip));
		}
		toggleSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				UGateKeeper.DEFAULT.preferences.set(preferenceKey, String.valueOf(newValue));
				toggleSwitchImageView.setImage(newValue ? imgOn : imgOff);
			}
		});
		toggleSwitchImageView = RS.imgView(isOn ? imgOn : imgOff);
		getChildren().addAll(toggleSwitchImageView, toggleSwitch);
	}
}

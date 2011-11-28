package org.ugate.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import org.ugate.UGateKeeper;
import org.ugate.resources.RS;

/**
 * Toggle switch view that shows an image as an indicator as to what is being toggled on/off.
 * When turned on/off the preference will be automatically updated in the UGate preferences.
 * 
 * @see ToggleSwitch
 */
public class ToggleSwitchPreferenceView extends HBox {
	
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
	 */
	public ToggleSwitchPreferenceView(final String preferenceKey, final String onImageFileName, 
			final String offImageFileName) {
		this(preferenceKey, onImageFileName, offImageFileName, ToggleSwitch.DEFAULT_ON_TEXT, 
				ToggleSwitch.DEFAULT_OFF_TEXT);
	}
	
	/**
	 * Creates a toggle switch preference view
	 * 
	 * @param preferenceKey the preference key for getting/saving the preference option as it's selected
	 * @param onImageFileName the file name of the image shown when the toggled on
	 * @param offImageFileName the file name of the image shown when the toggled off
	 * @param onText the text to show when on
	 * @param offText the text to show when off
	 */
	public ToggleSwitchPreferenceView(final String preferenceKey, final String onImageFileName, 
			final String offImageFileName, final String onText, final String offText) {
		this.preferenceKey = preferenceKey;
		this.imgOn = RS.img(onImageFileName);
		this.imgOff = RS.img(offImageFileName);
		setSpacing(5);
		toggleSwitch = new ToggleSwitch(onText, offText, true);
		final String onStr = UGateKeeper.DEFAULT.preferences.get(this.preferenceKey);
		final boolean isOn = onStr.length() == 1 && Integer.valueOf(onStr) == 1;
		toggleSwitch.selectedProperty().set(isOn);
		toggleSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				UGateKeeper.DEFAULT.preferences.set(preferenceKey, String.valueOf(newValue ? 1 : 0));
				toggleSwitchImageView.setImage(newValue ? imgOn : imgOff);
			}
		});
		setAlignment(Pos.BOTTOM_LEFT);
		toggleSwitchImageView = RS.imgView(isOn ? imgOn : imgOff);
		getChildren().addAll(toggleSwitchImageView, toggleSwitch);
	}
}

package org.ugate.gui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import org.ugate.ISettings;
import org.ugate.UGateKeeper;
import org.ugate.gui.GuiUtil;
import org.ugate.resources.RS;

/**
 * Toggle switch view that shows an image as an indicator as to what is being toggled on/off.
 * When turned on/off the preference will be automatically updated in the application preferences.
 * 
 * @see ToggleSwitch
 */
public class UGateToggleSwitchPreferenceView extends HBox {
	
	public static final int TOGGLE_ITEM_START_INDEX = 0;
	public final ISettings key;
	public final Integer nodeIndex;
	private final List<ToggleItem> toggleItems;
	private final IntegerProperty preferenceValueProperty;
	private boolean toggleItemsNeedSelectionUpdates = true;
	private boolean settingsNeedsUpdate = true;
	
	/**
	 * Creates a toggle switch preference view
	 * 
	 * @param key the settings key for getting/saving the settings option as it's selected
	 * @param nodeIndex the settings index
	 * @param onImageFileName the file name of the image shown when the toggled on
	 * @param offImageFileName the file name of the image shown when the toggled off
	 */
	public UGateToggleSwitchPreferenceView(final ISettings key, final Integer nodeIndex, final String onImageFileName, 
			final String offImageFileName) {
		this(key, nodeIndex, onImageFileName, offImageFileName, RS.rbLabel("toggleswitch.on"), 
				RS.rbLabel("toggleswitch.off"));
	}
	
	/**
	 * Creates a single toggle switch preference view
	 * 
	 * @param key the settings key for getting/saving the settings option as it's selected
	 * @param nodeIndex the settings index
	 * @param onImageFileName the file name of the image shown when the toggled on
	 * @param offImageFileName the file name of the image shown when the toggled off
	 * @param onText the text to show when on
	 * @param offText the text to show when off
	 */
	public UGateToggleSwitchPreferenceView(final ISettings key, final Integer nodeIndex, final String onImageFileName, 
			final String offImageFileName, final String onText, final String offText) {
		this(key, nodeIndex, new ToggleItem(onImageFileName, offImageFileName, null, null, 
				onText, offText, false, true));
	}
	
	/**
	 * Creates a toggle switch preference view using the supplied toggle items
	 * 
	 * @param key the settings key for getting/saving the settings option as it's selected
	 * @param nodeIndex the settings index
	 * @param toggleItems the toggle items
	 */
	public UGateToggleSwitchPreferenceView(final ISettings key, final Integer nodeIndex, final ToggleItem... toggleItems) {
		this.key = key;
		this.nodeIndex = nodeIndex;
		setSpacing(5d);
		setAlignment(Pos.BOTTOM_LEFT);
		this.preferenceValueProperty = new SimpleIntegerProperty(0) {
			@Override
			public final void set(final int v) {
				if (v >= 0 && v <= getMaxPreferenceValue() && v != get()) {
					super.set(v);
					if (settingsNeedsUpdate) {
						UGateKeeper.DEFAULT.settingsSet(UGateToggleSwitchPreferenceView.this.key, 
								UGateToggleSwitchPreferenceView.this.nodeIndex, String.valueOf(v));
					}
					if (toggleItemsNeedSelectionUpdates) {
						updateToggleItems();
					}
				}
			}
		};

		// add the toggle items
		this.toggleItems = new ArrayList<UGateToggleSwitchPreferenceView.ToggleItem>(toggleItems.length);
		for (final ToggleItem item : toggleItems) {
			this.toggleItems.add(item);
			item.toggleSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					//item.toggleSwitchImageView.setImage(newValue ? item.imgOn : item.imgOff);
					setPreferenceValueNoSelectionUpdate(compositePreferenceValue(item, newValue));
				}
			});
			getChildren().add(item.toggleSwitchImageView);
			if (item.showToggleSwitch) {
				getChildren().add(item.toggleSwitch);
			}
		}
		
		// set the initial preference value
		final String prefStrValue = UGateKeeper.DEFAULT.settingsGet(this.key, this.nodeIndex);
		int initialPrefValue = prefStrValue != null && prefStrValue.length() > 0 ? Integer.valueOf(prefStrValue) : 0;
		setPreferenceValueNoPreferenceUpdate(Math.min(initialPrefValue, getMaxPreferenceValue()));
	}
	
	/**
	 * Converts the all of the toggle items selection states from a boolean to binary value 
	 * and returns the integer value of the collective binary state of the controls.
	 * 
	 * @param changingItem the item that has a changing value
	 * @param newValue the new value of the changing item
	 * @return the composite value
	 */
	protected int compositePreferenceValue(final ToggleItem changingItem, final boolean newValue) {
		final StringBuffer prefValue = new StringBuffer("");
		for (final ToggleItem itemX : toggleItems) {
			if (itemX == changingItem) {
				prefValue.append(newValue ? '1' : '0');
			} else {
				prefValue.append(itemX.toggleSwitch.selectedProperty().get() ? '1' : '0');
			}
		}
		return prefValue.length() > 0 ? Integer.parseInt(prefValue.toString(), 2) : 0;
	}
	
	/**
	 * @return the largest selection value for the toggle control
	 */
	protected int maxSelectionValue() {
		return (int) Math.pow(2, toggleItems.size()) - 1;
	}
	
	/**
	 * Updates the toggle items selected property based upon the current preference value
	 */
	protected final void updateToggleItems() {
		final String prefValue = Integer.toBinaryString(getPreferenceValue());
		int i = 0;
		for (final ToggleItem item : toggleItems) {
			item.toggleSwitch.selectedProperty().set(prefValue.length() > i ? prefValue.charAt(i) == '1' : false);
			i++;
		}
	}
	
	/**
	 * Gets the maximum allowed preference value for a given item count (zero is always the minimum)
	 * 
	 * @param numItems the number of items
	 * @return the maximum allowed preference value
	 */
	private int getMaxPreferenceValue(final int numItems) {
		int max = (int) Math.pow(numItems, 2);
		return max <= 1 ? max : max - 2;
	}
	
	/**
	 * @return the maximum allowed preference value
	 */
	public int getMaxPreferenceValue() {
		return getMaxPreferenceValue(toggleItems.size());
	}
	
	/**
	 * @return the first toggle item
	 */
	public ToggleItem getToggleItem() {
		return getToggleItem(0);
	}
	
	/**
	 * @return the preference value property
	 */
	public IntegerProperty getPreferenceValueProperty() {
		return preferenceValueProperty;
	}
	
	/**
	 * Sets the preference value (must be greater than or equal to zero and less than or 
	 * equal to {@linkplain #getMaxPreferenceValue()}
	 * 
	 * @param preferenceValue the preference value to set
	 */
	public void setPreferenceValue(final int preferenceValue) {
		getPreferenceValueProperty().set(preferenceValue);
		for (final ToggleItem itemX : toggleItems) {
			if ((itemX.imgNone != null && preferenceValue == 0) || (itemX.imgAll != null && 
					preferenceValue == maxSelectionValue())) {
				itemX.toggleSwitchImageView.setImage(preferenceValue == 0 ? itemX.imgNone : itemX.imgAll);
			} else {
				itemX.toggleSwitchImageView.setImage(itemX.toggleSwitch.selectedProperty().get() ? 
						itemX.imgOn : itemX.imgOff);
			}
		}
	}
	
	/**
	 * Sets the preference value (must be greater than or equal to zero and less than or 
	 * equal to {@linkplain #getMaxPreferenceValue()} without a need to update the toggle
	 * items
	 * 
	 * @param preferenceValue the preference value to set
	 */
	protected void setPreferenceValueNoSelectionUpdate(final int preferenceValue) {
		toggleItemsNeedSelectionUpdates = false;
		setPreferenceValue(preferenceValue);
		toggleItemsNeedSelectionUpdates = true;
	}
	
	/**
	 * Sets the preference value (must be greater than or equal to zero and less than or 
	 * equal to {@linkplain #getMaxPreferenceValue()} without a need to update the preference
	 * (typically only used to initialize the preference value)
	 * 
	 * @param preferenceValue the preference value to set
	 */
	protected void setPreferenceValueNoPreferenceUpdate(final int preferenceValue) {
		settingsNeedsUpdate = false;
		setPreferenceValue(preferenceValue);
		settingsNeedsUpdate = true;
	}
	
	/**
	 * @return the preference value
	 */
	public int getPreferenceValue() {
		return getPreferenceValueProperty().get();
	}
	
	/**
	 * Gets a toggle item at the specified index
	 * 
	 * @param index the toggle item index
	 * @return the toggle item
	 */
	public ToggleItem getToggleItem(final int index) {
		return toggleItems.get(index);
	}
	
	/**
	 * Toggle item that holds reference to the GUI controls. An image and {@linkplain ToggleSwitch} 
	 * will be created. When either control is clicked the state of the control will be toggled on/off.
	 */
	public static class ToggleItem {
		public final ImageView toggleSwitchImageView;
		public final ToggleSwitch toggleSwitch;
		public final Image imgOn;
		public final Image imgOff;
		public final Image imgNone;
		public final Image imgAll;
		public final boolean showToggleSwitch;
		
		/**
		 * Creates a toggle item
		 * 
		 * @param onImageFileName the file name of the image that will be shown when selected
		 * @param offImageFileName the file name of the image that will be shown when not selected
		 * @param noneImageFileName the file name of the image that will be shown when none of the 
		 * 			selections are selected (only applicable when multiple toggles exist)
		 * @param allImageFileName the file name of the image that will be shown when all of the 
		 * 			selections are selected (only applicable when multiple toggles exist)
		 * @param onText the text of the toggle switch to show when selected
		 * @param offText the text of the toggle switch to show when not selected
		 */
		public ToggleItem(final String onImageFileName, final String offImageFileName, 
				final String noneImageFileName, final String allImageFileName,
				final String onText, final String offText) {
			this(onImageFileName, offImageFileName, noneImageFileName, allImageFileName, 
					onText, offText, false, true);
		}
		
		/**
		 * Creates a toggle item
		 * 
		 * @param onImageFileName the file name of the image that will be shown when selected
		 * @param offImageFileName the file name of the image that will be shown when not selected
		 * @param noneImageFileName the file name of the image that will be shown when none of the 
		 * 			selections are selected (only applicable when multiple toggles exist)
		 * @param allImageFileName the file name of the image that will be shown when all of the 
		 * 			selections are selected (only applicable when multiple toggles exist)
		 * @param showToggleSwitch true to show the toggle switch
		 */
		public ToggleItem(final String onImageFileName, final String offImageFileName, 
				final String noneImageFileName, final String allImageFileName,
				final boolean showToggleSwitch) {
			this(onImageFileName, offImageFileName, noneImageFileName, 
					allImageFileName, RS.rbLabel("toggleswitch.on"), 
					RS.rbLabel("toggleswitch.off"), false, showToggleSwitch);
		}
		
		/**
		 * Full constructor
		 * 
		 * @param onImageFileName the file name of the image that will be shown when selected
		 * @param offImageFileName the file name of the image that will be shown when not selected
		 * @param noneImageFileName the file name of the image that will be shown when none of the 
		 * 			selections are selected (only applicable when multiple toggles exist)
		 * @param allImageFileName the file name of the image that will be shown when all of the 
		 * 			selections are selected (only applicable when multiple toggles exist)
		 * @param onText the text of the toggle switch to show when selected
		 * @param offText the text of the toggle switch to show when not selected
		 * @param isOn true to show the initial state of the toggle as selected
		 * @param showToggleSwitch true to show the toggle switch
		 */
		public ToggleItem(final String onImageFileName, final String offImageFileName,
				final String noneImageFileName, final String allImageFileName,
				final String onText, final String offText, 
				final boolean isOn, final boolean showToggleSwitch) {
			this.imgOn = RS.img(onImageFileName);
			this.imgOff = RS.img(offImageFileName);
			this.imgNone = noneImageFileName != null ? RS.img(noneImageFileName) : null;
			this.imgAll = allImageFileName != null ? RS.img(allImageFileName) : null;
			this.showToggleSwitch = showToggleSwitch;
			toggleSwitch = new ToggleSwitch(onText, offText, true);
			toggleSwitch.selectedProperty().set(isOn);
			toggleSwitchImageView = RS.imgView(isOn ? imgOn : imgOff);
			toggleSwitchImageView.setCursor(Cursor.HAND);
			toggleSwitchImageView.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					if (GuiUtil.isPrimaryPress(event)) {
						toggleSwitch.selectedProperty().set(!toggleSwitch.selectedProperty().get());
					}
				}
			});
		}
	}
}

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

import org.ugate.gui.GuiUtil;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.ugate.service.entity.IModelType;

/**
 * Toggle switch view that shows an image as an indicator as to what is being
 * toggled on/off. When turned on/off the {@linkplain #valueProperty()} will be
 * automatically updated with an value of zero = off, and a binary equivalent
 * value when on. The on value depends on how may {@linkplain ToggleItem}s are
 * used.
 * 
 * @see ToggleSwitch
 */
public class UGateToggleSwitchBox<T> extends HBox {
	
	public static final int TOGGLE_ITEM_START_INDEX = 0;
	private final List<ToggleItem> toggleItems;
	private final IntegerProperty valueProperty;
	private boolean toggleItemsNeedSelectionUpdates = true;

	/**
	 * Constructor
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param key
	 *            the {@linkplain IModelType#getKey()} for getting/saving the settings option as
	 *            it's selected
	 */
	public UGateToggleSwitchBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey) {
		this(beanPathAdapter, modelKey, null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param key
	 *            the {@linkplain IModelType#getKey()} for getting/saving the settings option as
	 *            it's selected
	 * @param onImageFileName
	 *            the file name of the image shown when the toggled on
	 * @param offImageFileName
	 *            the file name of the image shown when the toggled off
	 */
	public UGateToggleSwitchBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, 
			final String onImageFileName, final String offImageFileName) {
		this(beanPathAdapter, modelKey, onImageFileName, offImageFileName, RS
				.rbLabel(KEYS.LABEL_TOGGLE_SWITCH_ON), RS.rbLabel(KEYS.LABEL_TOGGLE_SWITCH_OFF));
	}
	
	/**
	 * Constructor
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param key
	 *            the {@linkplain IModelType#getKey()} for getting/saving the
	 *            settings option as it's selected
	 * @param onImageFileName
	 *            the file name of the image shown when the toggled on
	 * @param offImageFileName
	 *            the file name of the image shown when the toggled off
	 * @param onText
	 *            the text to show when on
	 * @param offText
	 *            the text to show when off
	 */
	public UGateToggleSwitchBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final String onImageFileName, 
			final String offImageFileName, final String onText, final String offText) {
		this(beanPathAdapter, modelKey, new ToggleItem(onImageFileName, offImageFileName, null, null, 
				onText, offText, false, true));
	}
	
	/**
	 * Constructor
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param key
	 *            the {@linkplain IModelType#getKey()} for getting/saving the
	 *            settings option as it's selected
	 * @param toggleItems
	 *            the toggle items
	 */
	public UGateToggleSwitchBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final ToggleItem... toggleItems) {
		setSpacing(5d);
		setAlignment(Pos.BOTTOM_LEFT);
		this.valueProperty = new SimpleIntegerProperty(0) {
			@Override
			public final void set(final int v) {
				if (v >= 0 && v <= getMaxValue() && v != get()) {
					super.set(v);
					if (toggleItemsNeedSelectionUpdates) {
						updateToggleItems();
					}
				}
			}
		};

		// add the toggle items
		this.toggleItems = new ArrayList<UGateToggleSwitchBox.ToggleItem>(toggleItems.length);
		for (final ToggleItem item : toggleItems) {
			if (item.imgOn == null && item.imgOff != null) {
				throw new IllegalArgumentException("On image cannot be null while off image is provided");
			}
			if (item.imgOn != null && item.imgOff == null) {
				throw new IllegalArgumentException("Off image cannot be null while on image is provided");
			}
			this.toggleItems.add(item);
			item.toggleSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					setValueNoSelectionUpdate(compositeBinaryValue(item, newValue));
				}
			});
			getChildren().add(item.toggleSwitchImageView);
			if (item.showToggleSwitch) {
				getChildren().add(item.toggleSwitch);
			}
		}
		beanPathAdapter.bindBidirectional(modelKey.getKey(), valueProperty());
	}
	
	/**
	 * Converts the all of the toggle items selection states from a boolean to
	 * binary value and returns the integer value of the collective binary state
	 * of the controls.
	 * 
	 * @param changingItem
	 *            the item that has a changing value
	 * @param newValue
	 *            the new value of the changing item
	 * @return the composite value
	 */
	protected int compositeBinaryValue(final ToggleItem changingItem, final boolean newValue) {
		final StringBuffer valueStr = new StringBuffer("");
		for (final ToggleItem itemX : toggleItems) {
			if (itemX == changingItem) {
				valueStr.append(newValue ? '1' : '0');
			} else {
				valueStr.append(itemX.toggleSwitch.selectedProperty().get() ? '1' : '0');
			}
		}
		return valueStr.length() > 0 ? Integer.parseInt(valueStr.toString(), 2) : 0;
	}
	
	/**
	 * @return the largest selection value for the toggle control
	 */
	protected int maxSelectionValue() {
		return (int) Math.pow(2, toggleItems.size()) - 1;
	}
	
	/**
	 * Updates the toggle items selected property based upon the current
	 * {@linkplain UGateToggleSwitchBox} value
	 */
	protected final void updateToggleItems() {
		final String val = Integer.toBinaryString(getValue());
		int i = 0;
		for (final ToggleItem item : toggleItems) {
			item.toggleSwitch.selectedProperty().set(val.length() > i ? val.charAt(i) == '1' : false);
			i++;
		}
	}
	
	/**
	 * Gets the maximum allowed {@linkplain UGateToggleSwitchBox} value for a
	 * given item count (zero is always the minimum)
	 * 
	 * @param numItems
	 *            the number of items
	 * @return the maximum allowed {@linkplain UGateToggleSwitchBox} value
	 */
	private int getMaxValue(final int numItems) {
		int max = (int) Math.pow(numItems, 2) - 1;
		return max <= 0 ? 1 : max;
	}
	
	/**
	 * @return the maximum allowed {@linkplain UGateToggleSwitchBox} value
	 */
	public int getMaxValue() {
		return getMaxValue(toggleItems.size());
	}

	/**
	 * @return the minimum allowed {@linkplain UGateToggleSwitchBox} value
	 */
	public int getMinValue() {
		return 0;
	}

	/**
	 * @return the first toggle item
	 */
	public ToggleItem getToggleItem() {
		return getToggleItem(0);
	}
	
	/**
	 * @return the {@linkplain UGateToggleSwitchBox#getValue()} property
	 */
	public IntegerProperty valueProperty() {
		return valueProperty;
	}

	/**
	 * Sets the {@linkplain #getMaxValue()} on {@linkplain #setValue(int)}
	 */
	public void setValueMax() {
		setValue(getMaxValue());
	}

	/**
	 * Sets the {@linkplain #getMinValue()} on {@linkplain #setValue(int)}
	 */
	public void setValueMin() {
		setValue(getMinValue());
	}

	/**
	 * Sets the {@linkplain UGateToggleSwitchBox} value (must be greater than
	 * or equal to {@linkplain #getMinValue()} and less than or equal to
	 * {@linkplain #getMaxValue()})
	 * 
	 * @param value
	 *            the {@linkplain UGateToggleSwitchBox} value to set
	 */
	public void setValue(final int value) {
		final int maxValue = getMaxValue();
		final int minValue = getMinValue();
		final int finalValue = value > maxValue ? maxValue : value < minValue ? minValue : value;
		valueProperty().set(finalValue);
		for (final ToggleItem itemX : toggleItems) {
			if ((itemX.imgNone != null && finalValue == minValue) || (itemX.imgAll != null && 
					finalValue == maxSelectionValue())) {
				itemX.toggleSwitchImageView.setImage(finalValue == minValue ? itemX.imgNone : itemX.imgAll);
			} else if (itemX.imgOn != null && itemX.imgOff != null) {
				itemX.toggleSwitchImageView.setImage(itemX.toggleSwitch.selectedProperty().get() ? 
						itemX.imgOn : itemX.imgOff);
			}
		}
	}
	
	/**
	 * Sets the {@linkplain UGateToggleSwitchBox} value (must be greater than
	 * or equal to zero and less than or equal to
	 * {@linkplain #getMaxValue()} without a need to update the toggle
	 * items
	 * 
	 * @param value
	 *            the {@linkplain UGateToggleSwitchBox} value to set
	 */
	protected void setValueNoSelectionUpdate(final int value) {
		toggleItemsNeedSelectionUpdates = false;
		setValue(value);
		toggleItemsNeedSelectionUpdates = true;
	}
	
	/**
	 * @return the {@linkplain UGateToggleSwitchBox} value
	 */
	public int getValue() {
		return valueProperty().get();
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
					allImageFileName, RS.rbLabel(KEYS.LABEL_TOGGLE_SWITCH_ON), 
					RS.rbLabel(KEYS.LABEL_TOGGLE_SWITCH_OFF), false, showToggleSwitch);
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
			this.imgOn = onImageFileName != null ? RS.img(onImageFileName) : null;
			this.imgOff = offImageFileName != null ? RS.img(offImageFileName) : null;
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

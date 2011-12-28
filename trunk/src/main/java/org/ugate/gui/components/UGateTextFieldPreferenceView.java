package org.ugate.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import org.ugate.Settings;
import org.ugate.UGateKeeper;
import org.ugate.gui.GuiUtil;

/**
 * Wrapper for label and text/password/text area/numeric stepper controls
 */
public class UGateTextFieldPreferenceView extends VBox {
	
	public final Label label;
	public final TextField textField;
	public final TextArea textArea;
	public final PasswordField passwordField;
	private final Digits numericStepperDigits;
	public final Type type;
	protected static final double NUMERIC_STEPPER_WIDTH = 30d;
	protected static final double NUMERIC_STEPPER_HEIGHT = 30d;
	protected static final double NUMERIC_STEPPER_HALF_HEIGHT = NUMERIC_STEPPER_HEIGHT / 2d;
	protected static final double NUMERIC_STEPPER_QUARTER_HEIGHT = NUMERIC_STEPPER_HALF_HEIGHT / 2d;
	protected static final double NUMERIC_STEPPER_EIGHTH_HEIGHT = NUMERIC_STEPPER_QUARTER_HEIGHT / 2d;
	private final String numericStepperFormat;
	private final boolean numericStepperUseInt;
	private final int numericStepperDigitCount;
	private final Number minValue;
	private final Number maxValue;
	private final Settings settings;
	
	/**
	 * Creates a text field preference view
	 * 
	 * @param preferenceKey the preference key
	 * @param preferenceIndex the preference index to append to the {@linkplain Settings#key}
	 * @param type the type
	 * @param labelText the label text
	 * @param toolTip the tool tip
	 */
	public UGateTextFieldPreferenceView(final Settings preferenceKey, final int preferenceIndex, 
			final Type type, final String labelText, final String toolTip) {
		this(preferenceKey, preferenceIndex, type, null, null, null, labelText, toolTip);
	}
	
	/**
	 * Creates a text field preference view
	 * 
	 * @param preferenceKey the preference key
	 * @param type the type
	 * @param labelText the label text
	 * @param toolTip the tool tip
	 */
	public UGateTextFieldPreferenceView(final Settings preferenceKey, 
			final Type type, final String labelText, final String toolTip) {
		this(preferenceKey, null, type, null, null, null, labelText, toolTip);
	}
	
	/**
	 * Creates a numeric stepper that can be incremented/decremented by clicking on up/down arrows
	 *  
	 * @param preferenceKey the preference key
	 * @param numericStepperFormat the {@linkplain String#format(String, Object...)} (integer of float)
	 * @param minValue the minimum allowed value
	 * @param maxValue the maximum allowed value
	 * @param labelText the label text
	 * @param toolTip the tool tip
	 */
	public UGateTextFieldPreferenceView(final Settings preferenceKey, 
			final String numericStepperFormat, final Number minValue, final Number maxValue, 
			final String labelText, final String toolTip) {
		this(preferenceKey, null, Type.TYPE_NUMERIC_STEPPER, numericStepperFormat, minValue, maxValue, labelText, toolTip);
	}

	/**
	 * Creates a text field preference view
	 * 
	 * @param settings the settings
	 * @param preferenceIndex the preference index to append to the {@linkplain Settings#key}
	 * @param type the type
	 * @param numericStepperFormat the {@linkplain String#format(String, Object...)} (integer of float)
	 * @param minValue the minimum allowed value
	 * @param maxValue the maximum allowed value
	 * @param labelText the label text
	 * @param toolTip the tool tip
	 */
	protected UGateTextFieldPreferenceView(final Settings settings, final Integer preferenceIndex, final Type type, 
			final String numericStepperFormat, final Number minValue, final Number maxValue, 
			final String labelText, final String toolTip) {
	    super();
	    this.settings = settings;
	    this.type = type;
		label = new Label();
	    label.setText(labelText);
	    if (toolTip != null && !toolTip.isEmpty()) {
	    	label.setTooltip(new Tooltip(toolTip));
	    }
	    final String textValue = settings != null ? 
	    		preferenceIndex != null ? UGateKeeper.DEFAULT.preferencesGet(settings, preferenceIndex) : 
	    			UGateKeeper.DEFAULT.preferencesGet(settings) : "";
		this.numericStepperFormat = type != Type.TYPE_NUMERIC_STEPPER ? null :
			numericStepperFormat == null || numericStepperFormat.length() == 0 ? 
				"%03d" : numericStepperFormat;
		this.numericStepperUseInt = type != Type.TYPE_NUMERIC_STEPPER ? false : 
			this.numericStepperFormat.indexOf("d") > -1;
		this.numericStepperDigitCount = type != Type.TYPE_NUMERIC_STEPPER ? 0 :
			calculateDigitCount(this.numericStepperFormat, this.numericStepperUseInt);
	    this.minValue = type != Type.TYPE_NUMERIC_STEPPER ? 0 : adjustedMinMax(minValue, true);
	    this.maxValue = type != Type.TYPE_NUMERIC_STEPPER ? 0 : adjustedMinMax(maxValue, false);
	    if (type == Type.TYPE_PASSWORD) {
	    	textField = null;
	    	textArea = null;
	    	numericStepperDigits = null;
	    	passwordField = new PasswordField();
	    	passwordField.setText(textValue);
	    	addPreferenceUpdateListener(passwordField);
		    getChildren().addAll(label, passwordField);
	    } else if (type == Type.TYPE_TEXT_AREA) {
	    	textField = null;
	    	passwordField = null;
	    	numericStepperDigits = null;
	    	textArea = new TextArea();
	    	textArea.setText(textValue);
	    	addPreferenceUpdateListener(textArea);
		    getChildren().addAll(label, textArea);
	    } else if (type == Type.TYPE_NUMERIC_STEPPER) {
	    	textField = null;
	    	textArea = null;
	    	passwordField = null;
		    
		    // create/add numeric stepper
	    	numericStepperDigits = new Digits(String.format(this.numericStepperFormat, Integer.parseInt(textValue)),
					0.15f, Color.ORANGERED, null);
	    	final VBox stepperBar = new VBox(NUMERIC_STEPPER_QUARTER_HEIGHT);
	    	stepperBar.getChildren().addAll(createArrowButton(true), createArrowButton(false));
	    	
	    	final Group digitsDisplay = GuiUtil.createBackgroundDisplay(new Insets(NUMERIC_STEPPER_EIGHTH_HEIGHT, 
	    			NUMERIC_STEPPER_QUARTER_HEIGHT, NUMERIC_STEPPER_EIGHTH_HEIGHT, NUMERIC_STEPPER_QUARTER_HEIGHT), 
	    			NUMERIC_STEPPER_QUARTER_HEIGHT, 2, numericStepperDigits, stepperBar);
	    	getChildren().addAll(label, digitsDisplay);
	    } else {
	    	textArea = null;
	    	passwordField = null;
	    	numericStepperDigits = null;
		    textField = new TextField();
		    textField.setText(textValue);
		    addPreferenceUpdateListener(textField);
		    getChildren().addAll(label, textField);
	    }
	}
	
	/**
	 * Adds a listener that will update the preference when focus is lost
	 * 
	 * @param control the control to add the listener to
	 */
	protected void addPreferenceUpdateListener(final TextInputControl control) {
		control.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					UGateKeeper.DEFAULT.preferencesSet(settings, control.getText());
				}
			}
		});
	}
	
	/**
	 * Creates an arrow button that will increment/decrement the numeric stepper
	 *  
	 * @param isUp true for an up arrow, false for a down arrow
	 * @return the arrow
	 */
	protected Shape createArrowButton(final boolean isUp) {
    	final Polygon arrow = isUp ? new Polygon(NUMERIC_STEPPER_EIGHTH_HEIGHT, 0, 0, NUMERIC_STEPPER_QUARTER_HEIGHT, 
    			NUMERIC_STEPPER_QUARTER_HEIGHT, NUMERIC_STEPPER_QUARTER_HEIGHT, NUMERIC_STEPPER_EIGHTH_HEIGHT, 0) :
    				new Polygon(0, 0, NUMERIC_STEPPER_EIGHTH_HEIGHT, NUMERIC_STEPPER_QUARTER_HEIGHT, 
    		    			NUMERIC_STEPPER_QUARTER_HEIGHT, 0, 0, 0);
    	arrow.setFill(Color.WHITESMOKE);
    	arrow.setCursor(Cursor.HAND);
    	arrow.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_PRESSED && GuiUtil.isPrimaryPress(event)) {
					Number newValue;
					if (numericStepperUseInt) {
						newValue = Integer.parseInt(numericStepperDigits.getValue()) + (adjustedIncrementDecrementAmount(1).intValue() * (isUp ? 1 : -1));
					} else {
						newValue = Float.parseFloat(numericStepperDigits.getValue()) + (adjustedIncrementDecrementAmount(1).floatValue() * (isUp ? 1 : -1));
					}
					if (newValue.floatValue() >= minValue.floatValue() && 
							newValue.floatValue() <= maxValue.floatValue()) {
						numericStepperDigits.setValue(String.format(numericStepperFormat, newValue));
						UGateKeeper.DEFAULT.preferencesSet(settings, newValue.toString());
					}
				}
			}
		});
    	return arrow;
	}
	
	/**
	 * Adjusts the minimum/maximum numeric stepper digit value
	 * 
	 * @param minMax the min/max value (validated against the {@linkplain #numericStepperDigitCount}
	 * @param isMin true when calculating the minimum value, false for the maximum value
	 * @return the adjusted min/max value
	 */
	protected Number adjustedMinMax(final Number minMax, final boolean isMin) {
		final String minMaxStr = minMax == null ? null : String.valueOf(minMax);
		if (minMaxStr != null && ((!isMin && minMaxStr.length() < numericStepperDigitCount) || 
				(isMin && minMaxStr.length() > numericStepperDigitCount))) {
			return minMax;
		}
		if (isMin) {
			if (numericStepperUseInt) {
				return 0;
			} else {
				return 0.0f;
			}
		}
		final StringBuffer newValue = new StringBuffer('0');
		for (int i=0; i<numericStepperDigitCount; i++) {
			newValue.append('9');
		}
		if (numericStepperUseInt) {
			return Integer.parseInt(newValue.toString());
		} else {
			return Float.parseFloat(newValue.toString());
		}
	}
	
	/**
	 * Adjusts an increment/decrement value
	 * 
	 * @param amount the increment/decrement amount
	 * @return the adjusted increment/decrement amount
	 */
	protected Number adjustedIncrementDecrementAmount(final int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		final StringBuffer newValue = new StringBuffer();
		for (int i=0; i<numericStepperDigitCount; i++) {
			newValue.append('0');
		}
		newValue.append(String.valueOf(amount));
		if (numericStepperUseInt) {
			return Integer.parseInt(newValue.toString()) ;
		} else {
			return Float.parseFloat('.' + newValue.toString());
		}
	}
	
	/**
	 * Calculates the maximum number of digits allowed
	 *  
	 * @param format the {@linkplain String#format(String, Object...)}
	 * @param useInt true to use the integer portion of the formated value, false to use float
	 * @return the digit count
	 */
	protected static int calculateDigitCount(final String format, final boolean useInt) {
		return useInt ? String.format(format, 0).length() : String.format(format, 0.0f).split(".")[1].length();
	}
	
	/**
	 * @return gets the value of the control
	 */
	public Object getValue() {
	    if (type == Type.TYPE_PASSWORD) {
	    	return passwordField.getText();
	    } else if (type == Type.TYPE_TEXT_AREA) {
	    	return textArea.getText();
	    } else if (type == Type.TYPE_NUMERIC_STEPPER) {
	    	if (numericStepperUseInt) {
	    		return Integer.parseInt(numericStepperDigits.getValue());
	    	} else {
	    		return Float.parseFloat(numericStepperDigits.getValue());
	    	}
	    } else {
	    	return textField.getText();
	    }
	}
	/**
	 * The type of text control
	 */
	public enum Type {
		TYPE_TEXT,
		TYPE_TEXT_AREA,
		TYPE_PASSWORD,
		TYPE_NUMERIC_STEPPER;
	}
}

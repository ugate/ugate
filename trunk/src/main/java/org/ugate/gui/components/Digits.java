package org.ugate.gui.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * 7-segment digits display
 */
public class Digits extends HBox {

	private final Color onColor;
	private final Color offColor;
	private final double scale;
	private final StringProperty valueProperty = new SimpleStringProperty();

	/**
	 * Creates a digits display
	 * 
	 * @param value the initial value
	 * @param scale the scale used for the value
	 */
	public Digits(final String value, final double scale) {
		this(value, scale, null, null);
	}
	
	/**
	 * Creates a digits display
	 * 
	 * @param value the initial value
	 * @param scale the scale used for the value
	 * @param onColor the color used for a digit segment when on
	 * @param offColor the color used for a digit segment when off
	 */
	public Digits(final String value, final double scale, final Color onColor, final Color offColor) {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		this.onColor = onColor != null ? onColor : Digit.getDefaultOnColor();
		this.offColor = offColor != null ? offColor : Digit.getDefaultOffColor();
		this.scale = scale;
		setPadding(new Insets(5, 5, 5, 5));
		this.valueProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				updateValue(newValue);
			}
		});
		setValue(value);
	}
	
	/**
	 * Sets the numeric value for all digits. The value can be zero padded, contain a negative sign, and contain
	 * a decimal place (as long as the decimal location does not change position).
	 * 
	 * @param value the value of the digits
	 */
	private void updateValue(final String value) {
		//value = value.replaceAll("[^\\d.]", "0");
		if (!value.equals(valueProperty.get())) {
			final int remCnt = getChildren().size() - value.length();
// TODO : java.util.ConcurrentModificationException thrown when retaining list (workaround implemented)
//			if (remCnt > 0) {
//				getChildren().retainAll(getChildren().subList(remCnt, getChildren().size()));
//			} else if (remCnt < 0) {
//				for (int i=0; i<(remCnt * -1); i++) {
//					getChildren().add(i, new Digit());
//				}
//			}
			if (remCnt != 0) {
				getChildren().clear();
			}
			VBox circleBox;
			Digit digit;
			char c;
			int d;
			Circle circle;
			for (int i=0; i<value.length(); i++) {
				c = value.charAt(i);
				if (remCnt != 0) {
					if (c == '.') {
						circleBox = new VBox();
						circleBox.setCache(true);
						circleBox.setCacheHint(CacheHint.SPEED);
						circleBox.setStyle("-fx-alignment: BOTTOM_LEFT;");
						circle = new Circle(0, 0, 6 * getScale(), onColor);
						circle.setCache(true);
						circle.setSmooth(false);
						circleBox.getChildren().add(circle);
						getChildren().add(circleBox);
					} else {
						d = c == '-' ? Digit.NEGATIVE_SIGN_DIGIT : Integer.valueOf(String.valueOf(c));
						digit = new Digit(getScale(), d, onColor, offColor);
						getChildren().add(digit);
						setMargin(digit, new Insets(0, 0, 0, 1));
						//digit.setLayoutX(i * 80);
					}
				} else if (c != '.') {
					d = c == '-' ? Digit.NEGATIVE_SIGN_DIGIT : Integer.valueOf(String.valueOf(c));
					digit = (Digit) getChildren().get(i);
					digit.showNumber(d);
				}
			}
			valueProperty.set(value);
		}
	}

	/**
	 * @return gets the scale
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * @return gets the digits value
	 */
	public String getValue() {
		return valueProperty.get();
	}
	
	/**
	 * @param the digits value to set
	 */
	public void setValue(final String value) {
		valueProperty.set(value);
	}
	
	/**
	 * @return the value property
	 */
	public StringProperty getValueProperty() {
		return valueProperty;
	}

	/**
	 * @return the color used for a digit segment when on
	 */
	public Color getOnColor() {
		return onColor;
	}

	/**
	 * @return the color used for a digit segment when off
	 */
	public Color getOffColor() {
		return offColor;
	}

	/**
	 * Increments the value
	 */
	public void increment() {
		setValue(getValue() + 1);
	}
}

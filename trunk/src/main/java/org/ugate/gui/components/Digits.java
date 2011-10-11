package org.ugate.gui.components;

import javafx.geometry.Insets;
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
	private String value;

	public Digits(final String value, final double scale) {
		this(value, scale, null, null);
	}
	public Digits(final String value, final double scale, final Color onColor, final Color offColor) {
		this.onColor = onColor != null ? onColor : Digit.getDefaultOnColor();
		this.offColor = offColor != null ? offColor : Digit.getDefaultOffColor();
		this.scale = scale;
		setPadding(new Insets(5, 5, 5, 5));
		setValue(value);
	}

	public String getValue() {
		return value;
	}

	public double getScale() {
		return scale;
	}

	/**
	 * Sets the numeric value for all digits. The value can be zero padded, contain a negative sign, and contain
	 * a decimal place (as long as the decimal location does not change position).
	 * 
	 * @param value the value of the digits
	 */
	public void setValue(String value) {
		//value = value.replaceAll("[^\\d.]", "0");
		if (!value.equals(this.value)) {
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
			for (int i=0; i<value.length(); i++) {
				c = value.charAt(i);
				if (remCnt != 0) {
					if (c == '.') {
						circleBox = new VBox();
						circleBox.setStyle("-fx-alignment: BOTTOM_LEFT;");
						circleBox.getChildren().add(new Circle(0, 0, 6 * getScale(), onColor));
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
			this.value = value;
		}
	}

	public void increment() {
		setValue(getValue() + 1);
	}
}

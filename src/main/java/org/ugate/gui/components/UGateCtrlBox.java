package org.ugate.gui.components;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import org.ugate.gui.GuiUtil;
import org.ugate.service.SecurityEncryptionProvider;
import org.ugate.service.entity.IModelType;
import org.ugate.service.entity.Model;

/**
 * Wrapper for a {@linkplain Label} and {@linkplain TextField},
 * {@linkplain PasswordField}, {@linkplain TextArea}, or numeric stepper
 * {@linkplain Digits}
 * 
 * @param <T>
 *            the bean {@linkplain IModelType}
 * @param <IT>
 *            the item {@linkplain IModelType} for the items of the bean (if
 *            used)
 * @param <IVT>
 *            the item type used in the {@linkplain ListView} (if used)
 */
public class UGateCtrlBox<T extends Model, IT extends Model, IVT> extends VBox {

	public final Label label;
	private final TextField textField;
	private final TextArea textArea;
	private final PasswordField passwordField;
	private final Digits numericStepperDigits;
	private final ListView<IVT> listView;
	public final Type type;
	private final ReadOnlyObjectWrapper<Object> valuePropertyWrapper = new ReadOnlyObjectWrapper<Object>();
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
	private final BeanPathAdapter<T> beanPathAdapter;
	private final IModelType<T> modelKey;
	private final IModelType<IT> modelItemKey;
	private final boolean encryptResult;
	private boolean encrypt = true;
	private SecurityEncryptionProvider encryptionProvider;

	/**
	 * Creates a {@link TextField} / {@link PasswordField} version of a
	 * {@linkplain UGateCtrlBox}
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKey
	 *            the {@linkplain IModelType} of the field
	 * @param type
	 *            the {@linkplain Type}
	 * @param labelText
	 *            the label text
	 * @param toolTip
	 *            the tool tip
	 */
	public UGateCtrlBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final Type type,
			final String labelText, final String toolTip) {
		this(beanPathAdapter, modelKey, null, null, type, null, null, null,
				null, labelText, null, null, toolTip, null, null, false);
	}

	/**
	 * Creates a {@link TextField} / {@link PasswordField} version of a
	 * {@linkplain UGateCtrlBox}
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKey
	 *            the {@linkplain IModelType} of the field
	 * @param type
	 *            the {@linkplain Type}
	 * @param labelText
	 *            the label text
	 * @param toolTip
	 *            the tool tip
	 * @param encryptResult
	 *            the {@link #encryptResult()}
	 */
	public UGateCtrlBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final Type type,
			final String labelText, final String toolTip,
			final boolean encryptResult) {
		this(beanPathAdapter, modelKey, null, null, type, null, null, null,
				null, labelText, null, null, toolTip, null, null,
				encryptResult);
	}

	/**
	 * Creates a {@link TextField} / {@link PasswordField} version of a
	 * {@linkplain UGateCtrlBox}
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKey
	 *            the {@linkplain IModelType} of the field
	 * @param type
	 *            the {@linkplain Type}
	 * @param labelText
	 *            the label text
	 * @param width
	 *            the width of the control (for {@linkplain TextArea} column
	 *            count)
	 * @param height
	 *            the height of the control (for {@linkplain TextArea} row
	 *            count)
	 * @param toolTip
	 *            the tool tip
	 * @param encryptResult
	 *            the {@link #encryptResult()}
	 */
	public UGateCtrlBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final Type type,
			final String labelText, final Number width, final Number height,
			final String toolTip, final boolean encryptPassphrase) {
		this(beanPathAdapter, modelKey, null, null, type, null, null, null,
				null, labelText, width, height, toolTip, null, null,
				encryptPassphrase);
	}

	/**
	 * Creates a numeric stepper that can be incremented/decremented by clicking
	 * on up/down arrows
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKey
	 *            the {@linkplain IModelType} of the field
	 * @param numericStepperFormat
	 *            the {@linkplain String#format(String, Object...)} (integer of
	 *            float)
	 * @param numericStepperColor
	 *            the color of the numeric stepper
	 * @param minValue
	 *            the minimum allowed value
	 * @param maxValue
	 *            the maximum allowed value
	 * @param labelText
	 *            the label text
	 * @param toolTip
	 *            the tool tip
	 */
	public UGateCtrlBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final String numericStepperFormat,
			final Color numericStepperColor, final Number minValue,
			final Number maxValue, final String labelText, final String toolTip) {
		this(beanPathAdapter, modelKey, null, null, Type.NUMERIC_STEPPER,
				numericStepperFormat, numericStepperColor, minValue, maxValue,
				labelText, null, null, toolTip, null, null, false);
	}

	/**
	 * Creates a {@linkplain ListView} version of a {@linkplain UGateCtrlBox}
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKey
	 *            the {@linkplain IModelType} of the field
	 * @param modelItemKey
	 *            the {@linkplain IModelType} for each item field within the
	 *            collection/map
	 * @param modelItemClassType
	 *            the {@linkplain Class} type of the object that represents the
	 *            {@linkplain IModelType}
	 * @param labelText
	 *            the label text
	 * @param width
	 *            the width of the control (for {@linkplain TextArea} column
	 *            count)
	 * @param height
	 *            the height of the control (for {@linkplain TextArea} row
	 *            count)
	 * @param toolTip
	 *            the tool tip
	 * @param items
	 *            the items added to
	 *            {@linkplain ListView#setItems(ObservableList)}
	 * @param itemValueType
	 *            the type of items in the {@linkplain ListView#getItems()}
	 */
	public UGateCtrlBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final IModelType<IT> modelItemKey,
			final Class<IT> modelItemClassType, final String labelText,
			final Number width, final Number height, final String toolTip,
			final IVT[] items, final Class<IVT> itemValueType) {
		this(beanPathAdapter, modelKey, modelItemKey, modelItemClassType,
				Type.LIST_VIEW, null, null, null, null, labelText, width,
				height, toolTip, items, itemValueType, false);
	}

	/**
	 * Creates a {@linkplain UGateCtrlBox}
	 * 
	 * @param beanPathAdapter
	 *            the {@linkplain BeanPathAdapter} to bind to
	 * @param modelKey
	 *            the {@linkplain IModelType} of the field
	 * @param modelItemKey
	 *            the {@linkplain IModelType} for each item within the
	 *            collection/map
	 * @param modelItemClassType
	 *            the {@linkplain Class} type of the object that represents the
	 *            {@linkplain IModelType}
	 * @param type
	 *            the {@linkplain UGateCtrlBox.Type}
	 * @param numericStepperFormat
	 *            the {@linkplain String#format(String, Object...)} (integer of
	 *            float)
	 * @param numericStepperColor
	 *            the color of the numeric stepper
	 * @param minValue
	 *            the minimum allowed value
	 * @param maxValue
	 *            the maximum allowed value
	 * @param labelText
	 *            the label text
	 * @param width
	 *            the width of the control (for {@linkplain TextArea} column
	 *            count)
	 * @param height
	 *            the height of the control (for {@linkplain TextArea} row
	 *            count)
	 * @param toolTip
	 *            the tool tip
	 * @param items
	 *            the items added to
	 *            {@linkplain ListView#setItems(ObservableList)}
	 * @param itemValueType
	 *            the type of items in the
	 *            {@linkplain ListView#getItems()}
	 * @param encryptResult
	 *            the {@link #encryptResult()}
	 */
	protected UGateCtrlBox(final BeanPathAdapter<T> beanPathAdapter,
			final IModelType<T> modelKey, final IModelType<IT> modelItemKey,
			final Class<IT> modelItemClassType, final Type type,
			final String numericStepperFormat, final Color numericStepperColor,
			final Number minValue, final Number maxValue,
			final String labelText, final Number width, final Number height,
			final String toolTip, final IVT[] items,
			final Class<IVT> itemValueType, final boolean encryptResult) {
		super();
		this.encryptResult = encryptResult;
		this.beanPathAdapter = beanPathAdapter;
		this.modelKey = modelKey;
		this.modelItemKey = modelItemKey;
		this.type = type;
		label = new Label();
		label.setText(labelText);
		if (toolTip != null && !toolTip.isEmpty()) {
			label.setTooltip(new Tooltip(toolTip));
		}
		this.numericStepperFormat = type != Type.NUMERIC_STEPPER ? null
				: numericStepperFormat == null
						|| numericStepperFormat.length() == 0 ? "%03d"
						: numericStepperFormat;
		this.numericStepperUseInt = type != Type.NUMERIC_STEPPER ? false
				: this.numericStepperFormat.indexOf("d") > -1;
		this.numericStepperDigitCount = type != Type.NUMERIC_STEPPER ? 0
				: calculateDigitCount(this.numericStepperFormat,
						this.numericStepperUseInt);
		this.minValue = type != Type.NUMERIC_STEPPER ? 0 : adjustedMinMax(
				minValue, true);
		this.maxValue = type != Type.NUMERIC_STEPPER ? 0 : adjustedMinMax(
				maxValue, false);
		if (type == Type.PASSWORD) {
			textField = null;
			textArea = null;
			numericStepperDigits = null;
			listView = null;
			passwordField = new PasswordField();
			passwordField.setPromptText(labelText);
			if (width != null) {
				passwordField.setPrefWidth(width.doubleValue());
			}
			if (height != null) {
				passwordField.setPrefHeight(height.doubleValue());
			}
			getChildren().addAll(label, passwordField);
			bindTextOrPasswordField(passwordField);
		} else if (type == Type.TEXT_AREA) {
			textField = null;
			passwordField = null;
			numericStepperDigits = null;
			listView = null;
			textArea = new TextArea();
			textArea.setWrapText(true);
			textArea.setPromptText(labelText);
			if (width != null) {
				textArea.setPrefColumnCount(width.intValue());
			}
			if (height != null) {
				textArea.setPrefRowCount(height.intValue());
			}
			getChildren().addAll(label, textArea);
			this.beanPathAdapter.bindBidirectional(this.modelKey.getKey(),
					textArea.textProperty());
		} else if (type == Type.NUMERIC_STEPPER) {
			textField = null;
			textArea = null;
			passwordField = null;
			listView = null;
			label.getStyleClass().add("gauge-header");
			// create/add numeric stepper
			numericStepperDigits = new Digits(String.format(
					this.numericStepperFormat, 0), 0.15f, numericStepperColor,
					null);
			final VBox stepperBar = new VBox(NUMERIC_STEPPER_QUARTER_HEIGHT);
			stepperBar.getChildren().addAll(createArrowButton(true),
					createArrowButton(false));

			final Region digitsDisplay = GuiUtil.createBackgroundDisplay(
					new Insets(NUMERIC_STEPPER_EIGHTH_HEIGHT,
							NUMERIC_STEPPER_QUARTER_HEIGHT,
							NUMERIC_STEPPER_EIGHTH_HEIGHT,
							NUMERIC_STEPPER_QUARTER_HEIGHT),
					NUMERIC_STEPPER_QUARTER_HEIGHT, 2, true,
					0, 0, numericStepperDigits, stepperBar);
			getChildren().addAll(label, digitsDisplay);
			setValue(numericStepperDigits.getValue());
			this.beanPathAdapter.bindBidirectional(this.modelKey.getKey(),
					numericStepperDigits.valueProperty());
		} else if (type == Type.LIST_VIEW) {
			textField = null;
			textArea = null;
			passwordField = null;
			numericStepperDigits = null;
			listView = new ListView<>(FXCollections.observableArrayList(items));
			if (width != null) {
				listView.setPrefWidth(width.doubleValue());
			}
			if (height != null) {
				listView.setPrefHeight(height.doubleValue());
			}
			getChildren().addAll(label, listView);
			this.beanPathAdapter.bindContentBidirectional(this.modelKey
					.getKey(),
					this.modelItemKey != null ? this.modelItemKey.getKey()
							: null, modelItemClassType, listView.getItems(),
					itemValueType, null, null);
		} else {
			textArea = null;
			passwordField = null;
			numericStepperDigits = null;
			listView = null;
			if (type == Type.DIR_CHOOSER) {
				final UGateDirectory dir = new UGateDirectory(null);
				textField = dir.getTextField();
				getChildren().addAll(label, dir);
				this.beanPathAdapter.bindBidirectional(this.modelKey.getKey(),
						textField.textProperty());
			} else {
				textField = new TextField();
				getChildren().addAll(label, textField);
				bindTextOrPasswordField(textField);
			}
			if (width != null) {
				textField.setPrefWidth(width.doubleValue());
			}
			if (height != null) {
				textField.setPrefHeight(height.doubleValue());
			}
			textField.setPromptText(labelText);
		}
		// if (type != Type.NUMERIC_STEPPER) {
		// setValue(textValue);
		// }
	}

	/**
	 * Creates an arrow button that will increment/decrement the numeric stepper
	 * 
	 * @param isUp
	 *            true for an up arrow, false for a down arrow
	 * @return the arrow
	 */
	protected Shape createArrowButton(final boolean isUp) {
		final Polygon arrow = isUp ? new Polygon(NUMERIC_STEPPER_EIGHTH_HEIGHT,
				0, 0, NUMERIC_STEPPER_QUARTER_HEIGHT,
				NUMERIC_STEPPER_QUARTER_HEIGHT, NUMERIC_STEPPER_QUARTER_HEIGHT,
				NUMERIC_STEPPER_EIGHTH_HEIGHT, 0) : new Polygon(0, 0,
				NUMERIC_STEPPER_EIGHTH_HEIGHT, NUMERIC_STEPPER_QUARTER_HEIGHT,
				NUMERIC_STEPPER_QUARTER_HEIGHT, 0, 0, 0);
		arrow.setFill(Color.WHITESMOKE);
		arrow.setCursor(Cursor.HAND);
		arrow.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_PRESSED
						&& GuiUtil.isPrimaryPress(event)) {
					Number newValue;
					if (numericStepperUseInt) {
						newValue = Integer.parseInt(numericStepperDigits
								.getValue())
								+ (adjustedIncrementDecrementAmount(1)
										.intValue() * (isUp ? 1 : -1));
					} else {
						newValue = Float.parseFloat(numericStepperDigits
								.getValue())
								+ (adjustedIncrementDecrementAmount(1)
										.floatValue() * (isUp ? 1 : -1));
					}
					setNumericStepperValue(newValue);
				}
			}
		});
		return arrow;
	}

	/**
	 * Adjusts the minimum/maximum numeric stepper digit value
	 * 
	 * @param minMax
	 *            the min/max value (validated against the
	 *            {@linkplain #numericStepperDigitCount}
	 * @param isMin
	 *            true when calculating the minimum value, false for the maximum
	 *            value
	 * @return the adjusted min/max value
	 */
	protected Number adjustedMinMax(final Number minMax, final boolean isMin) {
		final String minMaxStr = minMax == null ? null : String.valueOf(minMax);
		if (minMaxStr != null
				&& ((!isMin && minMaxStr.length() <= numericStepperDigitCount) || (isMin && minMaxStr
						.length() >= numericStepperDigitCount))) {
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
		for (int i = 0; i < numericStepperDigitCount; i++) {
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
	 * @param amount
	 *            the increment/decrement amount
	 * @return the adjusted increment/decrement amount
	 */
	protected Number adjustedIncrementDecrementAmount(final int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException(
					"Amount must be greater than zero");
		}
		final StringBuffer newValue = new StringBuffer();
		for (int i = 0; i < numericStepperDigitCount; i++) {
			newValue.append('0');
		}
		newValue.append(String.valueOf(amount));
		if (numericStepperUseInt) {
			return Integer.parseInt(newValue.toString());
		} else {
			return Float.parseFloat('.' + newValue.toString());
		}
	}

	/**
	 * Calculates the maximum number of digits allowed
	 * 
	 * @param format
	 *            the {@linkplain String#format(String, Object...)}
	 * @param useInt
	 *            true to use the integer portion of the formated value, false
	 *            to use float
	 * @return the digit count
	 */
	protected static int calculateDigitCount(final String format,
			final boolean useInt) {
		return useInt ? String.format(format, 0).length() : String.format(
				format, 0.0f).split(".")[1].length();
	}

	/**
	 * @return gets the value of the control
	 */
	public Object getValue() {
		if (type == Type.PASSWORD) {
			return passwordField.getText();
		} else if (type == Type.TEXT_AREA) {
			return textArea.getText();
		} else if (type == Type.NUMERIC_STEPPER) {
			if (numericStepperUseInt) {
				return Integer.valueOf(numericStepperDigits.getValue());
			} else {
				return Float.valueOf(numericStepperDigits.getValue());
			}
		} else if (type == Type.LIST_VIEW) {
			return listView.getItems();
		} else {
			return textField.getText();
		}
	}

	/**
	 * @return gets the value of the control
	 */
	@SuppressWarnings("unchecked")
	public void setValue(final Object value) {
		if (type == Type.PASSWORD) {
			passwordField.setText(value == null ? "" : value.toString());
			valuePropertyWrapper.set(passwordField.getText());
		} else if (type == Type.TEXT_AREA) {
			textArea.setText(value == null ? "" : value.toString());
			valuePropertyWrapper.set(textArea.getText());
		} else if (type == Type.NUMERIC_STEPPER) {
			setNumericStepperValue(value);
		} else if (type == Type.LIST_VIEW) {
			listView.setItems((ObservableList<IVT>) value);
			valuePropertyWrapper.set(listView.getItems());
		} else {
			textField.setText(value == null ? "" : value.toString());
			valuePropertyWrapper.set(textField.getText());
		}
	}

	/**
	 * Sets the numeric stepper value
	 * 
	 * @param value
	 *            the value to set
	 */
	protected void setNumericStepperValue(final Object value) {
		Number newValue;
		if (value instanceof Number) {
			if (numericStepperUseInt) {
				newValue = ((Number) value).intValue();
			} else {
				newValue = ((Number) value).floatValue();
			}
		} else if (numericStepperUseInt) {
			newValue = Integer.parseInt(value == null ? "0" : value.toString());
		} else {
			newValue = Float.parseFloat(value == null ? "0" : value.toString());
		}
		if (newValue.floatValue() >= minValue.floatValue()
				&& newValue.floatValue() <= maxValue.floatValue()) {
			numericStepperDigits.setValue(String.format(numericStepperFormat,
					newValue));
			valuePropertyWrapper.set(numericStepperDigits.getValue());
		}
	}

	private void bindTextOrPasswordField(final TextField tf) {
		if (!encryptResult) {
			this.beanPathAdapter.bindBidirectional(this.modelKey.getKey(),
					tf.textProperty());
		} else {
			try {
				final StringProperty msp = new SimpleStringProperty(
						tf.getText()) {
					@Override
					public void set(final String value) {
						super.set(value);
						if (value == null || value.isEmpty()) {
							tf.setText(value);
							return;
						}
						encrypt = false;
						try {
							final String clearText = getEncryptionProvider()
									.decrypt(value);
							tf.setText(clearText);
						} catch (final Throwable t) {
							throw new RuntimeException("Unable to decrypt", t);
						} finally {
							encrypt = true;
						}
					}
				};
				this.beanPathAdapter.bindBidirectional(this.modelKey.getKey(),
						msp);
				tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
					private String ltv = tf.getText();
					@Override
					public void changed(
							final ObservableValue<? extends Boolean> observable,
							final Boolean oldValue, final Boolean newValue) {
						if (encrypt && (newValue == null || !newValue)
								&& tf.getText() != null
								&& !tf.getText().isEmpty()
								&& !tf.getText().equals(ltv)) {
							try {
								final String encrypted = getEncryptionProvider()
										.encrypt(tf.getText());
								ltv = tf.getText();
								msp.set(encrypted);
							} catch (final Throwable t) {
								throw new RuntimeException("Unable to encrypt",
										t);
							}
						}
					}
				});
			} catch (final Throwable t) {
				throw new RuntimeException("Unable to add ecryption", t);
			}
		}
	}

	/**
	 * Gets the {@link SecurityEncryptionProvider} used by the
	 * {@link UGateCtrlBox} when {@link #getEncryptPassphrase()} returns a valid
	 * value and {@link #encryptPassphrase()} is <code>true</code>
	 * 
	 * @return the {@link SecurityEncryptionProvider}
	 * @throws Exception
	 *             thrown when unable to initialize the
	 *             {@link SecurityEncryptionProvider}
	 */
	protected SecurityEncryptionProvider getEncryptionProvider() throws Exception {
		if (encryptionProvider == null) {
			encryptionProvider = new SecurityEncryptionProvider(getEncryptPassphrase());
		}
		return encryptionProvider;
	}

	/**
	 * Override when {@link #encryptPassphrase()} is initialized as true
	 * 
	 * @return the password phrase used for encryption of the
	 *         {@link UGateCtrlBox}
	 */
	public String getEncryptPassphrase() {
		return null;
	}

	/**
	 * @return true for {@link UGateCtrlBox}s that are a {@link TextField} or
	 *         {@link PasswordField} encryption will occur when setting the
	 *         bound {@link BeanPathAdapter} from the control and decryption
	 *         when the value is coming from the {@link BeanPathAdapter} <b>must override </b>
	 */
	public boolean encryptPassphrase() {
		return encryptResult;
	}

	/**
	 * @return the value property
	 */
	public ReadOnlyObjectProperty<Object> valueProperty() {
		return valuePropertyWrapper.getReadOnlyProperty();
	}

	/**
	 * @return the textField
	 */
	public TextField getTextField() {
		return textField;
	}

	/**
	 * @return the textArea
	 */
	public TextArea getTextArea() {
		return textArea;
	}

	/**
	 * @return the passwordField
	 */
	public PasswordField getPasswordField() {
		return passwordField;
	}

	/**
	 * @return the numericStepperDigits
	 */
	public Digits getNumericStepperDigits() {
		return numericStepperDigits;
	}

	/**
	 * @return the listView
	 */
	public ListView<IVT> getListView() {
		return listView;
	}

	/**
	 * The type of text control
	 */
	public enum Type {
		TEXT, TEXT_AREA, PASSWORD, NUMERIC_STEPPER, LIST_VIEW, DIR_CHOOSER;
	}
}

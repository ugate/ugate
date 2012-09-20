package org.ugate.gui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Toggle switch that can be toggled on and off
 */
public class ToggleSwitch extends Group {
	
	public static final double TEXT_SPACING = 4D;
	public static final double TEXT_TRANS_X = 2D;
	public static final double REC_STROKE_WIDTH = .5D;
	public static final double REC_ARC = 15D;
	public static final Insets TEXT_INSETS = new Insets(0.3, 5, 0.3, 5);
	private final String onText;
	private final String offText;
	private BooleanProperty boolProperty;
	private Rectangle mainRec;
	private Rectangle middleRec;

	/**
	 * Default toggle switch constructor
	 */
	public ToggleSwitch(final String onText, final String offText) {
		this(onText, offText, true);
	}

	/**
	 * Full constructor
	 * 
	 * @param onText the on text
	 * @param offText the off text
	 * @param on the initial state of the toggle switch
	 */
	public ToggleSwitch(final String onText, final String offText, final boolean on) {
		setCursor(Cursor.HAND);
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		this.onText = onText;
		this.offText = offText;
		this.boolProperty = new SimpleBooleanProperty(on);
		selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				update();
			}
		});
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isMetaDown() || event.isControlDown() || event.isAltDown() || 
						event.isShiftDown() || event.isShortcutDown() || !event.isPrimaryButtonDown()) {
					return;
				}
				selectedProperty().set(!selectedProperty().get());
			}
		});
		draw();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	protected void layoutChildren() {
		super.layoutChildren();
		update();
	}
	
	/**
	 * Updates the toggle switch display to represent on or off
	 */
	protected void update() {
        // Create and set a gradient for the inside of the button
        final Stop[] mainStops = new Stop[] {
            new Stop(0.0, boolProperty.get() ? Color.BLUE : Color.GRAY),
            new Stop(1.0, boolProperty.get() ? Color.DODGERBLUE : Color.DARKGRAY)
        };
        final LinearGradient mainLG =
            new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, mainStops);
        mainRec.setFill(mainLG);
        middleRec.setX(boolProperty.get() ? (mainRec.getWidth() / 2) : mainRec.getX());
	}
	
	/**
	 * Draws the toggle switch
	 */
    protected void draw() {
    	// skin approach uses more resources to manage
    	final HBox textView = new HBox(TEXT_SPACING);
    	textView.setCache(true);
    	textView.setCacheHint(CacheHint.SPEED);
    	textView.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				final Bounds textViewBounds = textView.getBoundsInLocal();
				mainRec.setX(textViewBounds.getMinX());
				mainRec.setWidth(textViewBounds.getWidth());
				middleRec.setWidth(mainRec.getWidth() / 2);
			}
		});
    	textView.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				final Bounds textViewBounds = textView.getBoundsInLocal();
				mainRec.setY(textViewBounds.getMinY());
				mainRec.setHeight(textViewBounds.getHeight());
				middleRec.setHeight(mainRec.getHeight());
			}
		});
    	
        final Label onLabel = new Label();
        onLabel.setCache(true);
        onLabel.setCacheHint(CacheHint.SPEED);
        onLabel.setText(onText);
        onLabel.setTextFill(Color.WHITE);
        onLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12pt; -fx-font-weight: bold;");
        final Label offLabel = new Label();
        offLabel.setCache(true);
        offLabel.setText(offText);
        offLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 12pt; -fx-font-weight: bold;");
        HBox.setMargin(onLabel, TEXT_INSETS);
        HBox.setMargin(offLabel, TEXT_INSETS);
		textView.getChildren().addAll(onLabel, offLabel);
		
    	mainRec = new Rectangle();
    	mainRec.setSmooth(false);
    	mainRec.setCache(true);
    	mainRec.setCacheHint(CacheHint.SPEED);
    	mainRec.setStroke(Color.BLACK);
    	mainRec.setStrokeWidth(REC_STROKE_WIDTH);
    	mainRec.setX(0);
    	mainRec.setY(0);
    	mainRec.setHeight(0);
    	mainRec.setArcWidth(REC_ARC);
    	mainRec.setArcHeight(REC_ARC);
    	
    	middleRec = new Rectangle();
    	middleRec.setSmooth(false);
    	middleRec.setCache(true);
    	middleRec.setCacheHint(CacheHint.SPEED);
    	middleRec.setStroke(Color.BLACK);
    	middleRec.setStrokeWidth(REC_STROKE_WIDTH);
    	middleRec.setX(0);
    	middleRec.setY(0);
    	middleRec.setWidth(0);
    	middleRec.setHeight(0);
    	middleRec.setArcWidth(REC_ARC);
    	middleRec.setArcHeight(REC_ARC);
        
        // Create and set a gradient for the inside of the button
        Stop[] middleStops = new Stop[] {
            new Stop(0.0, Color.WHITE),
            new Stop(1.0, Color.GRAY)
        };
        LinearGradient middleLG =
            new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, middleStops);
        middleRec.setFill(middleLG);
        
        getChildren().setAll(mainRec, textView, middleRec);
    }
	
	/**
	 * The selected property
	 * 
	 * @return the selected property
	 */
	public BooleanProperty selectedProperty() {
		return this.boolProperty;
	}
}

package org.ugate.gui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
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
	public static final double REC_ARC = 5D;
	
	public static final String DEFAULT_ON_TEXT = "ON";
	public static final String DEFAULT_OFF_TEXT = "OFF";
	private final String onText;
	private final String offText;
	private SimpleBooleanProperty boolproperty;
	private Rectangle mainRec;
	private Rectangle middleRec;

	public ToggleSwitch() {
		this(null, null, true);
	}

	public ToggleSwitch(final String onText, final String offText, final boolean on) {
		this.onText = onText != null ? onText : DEFAULT_ON_TEXT;
		this.offText = offText != null ? offText : DEFAULT_OFF_TEXT;
		this.boolproperty = new SimpleBooleanProperty(on);
		selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				update();
			}
		});
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
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
	
	protected void update() {
        // Create and set a gradient for the inside of the button
        final Stop[] mainStops = new Stop[] {
            new Stop(0.0, boolproperty.get() ? Color.DARKBLUE : Color.GRAY),
            new Stop(1.0, boolproperty.get() ? Color.DODGERBLUE : Color.DARKGRAY)
        };
        final LinearGradient mainLG =
            new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, mainStops);
        mainRec.setFill(mainLG);
        middleRec.setX(boolproperty.get() ? (mainRec.getWidth() / 2) : mainRec.getX());
	}
	
	/**
	 * Draws the toggle switch
	 */
    protected void draw() {
    	// skin approach uses more resources to manage
    	final HBox textView = new HBox(TEXT_SPACING);
    	textView.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				final Bounds textViewBounds = textView.getBoundsInLocal();
				mainRec.setX(textViewBounds.getMinX());
				mainRec.setWidth(textViewBounds.getWidth() + TEXT_TRANS_X);
				middleRec.setWidth(mainRec.getWidth() / 2);
			}
		});
    	textView.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				final Bounds textViewBounds = textView.getBoundsInLocal();
				mainRec.setY(textViewBounds.getMinY());
				mainRec.setWidth(textViewBounds.getWidth() + TEXT_TRANS_X);
				mainRec.setHeight(textViewBounds.getHeight());
				middleRec.setHeight(mainRec.getHeight());
			}
		});
		textView.setTranslateX(TEXT_TRANS_X);
    	
        final Label onLabel = new Label();
        onLabel.setStyle("-fx-color: #FFFFFF;");
        onLabel.setText(onText);
        final Label offLabel = new Label();
        offLabel.setStyle("-fx-color: #FFFFFF;");
        offLabel.setText(offText);
		textView.getChildren().addAll(onLabel, offLabel);
		
    	mainRec = new Rectangle();
    	mainRec.setStroke(Color.BLACK);
    	mainRec.setStrokeWidth(REC_STROKE_WIDTH);
    	mainRec.setX(0);
    	mainRec.setY(0);
    	mainRec.setHeight(0);
    	mainRec.setArcWidth(REC_ARC);
    	mainRec.setArcHeight(REC_ARC);
    	
    	middleRec = new Rectangle();
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
            new Stop(0.0, Color.GRAY),
            new Stop(1.0, Color.WHITE)
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
		return this.boolproperty;
	}
}

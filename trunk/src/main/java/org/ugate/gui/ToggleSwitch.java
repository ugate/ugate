package org.ugate.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
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
public class ToggleSwitch extends Control {
	
	public static final String DEFAULT_ON_TEXT = "ON";
	public static final String DEFAULT_OFF_TEXT = "OFF";
	private SimpleBooleanProperty boolproperty;

	public ToggleSwitch() {
		this(null, null, true);
	}

	public ToggleSwitch(final String onText, final String offText, final boolean on) {
		this.boolproperty = new SimpleBooleanProperty(on);
		this.boolproperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				((ToggleSwitchSkin) getSkin()).setOn(newValue);
			}
		});
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectedProperty().set(!selectedProperty().get());
			}
		});
		this.skinProperty().set(new ToggleSwitchSkin(this, 
				onText != null ? onText : DEFAULT_ON_TEXT, 
						offText != null ? offText : DEFAULT_OFF_TEXT, on));
		this.boolproperty.set(on);
	}
	
	/**
	 * The selected property
	 * 
	 * @return the selected property
	 */
	public BooleanProperty selectedProperty() {
		return this.boolproperty;
	}
	
	/**
	 * The toggle switch skin that will draw the on/off switch
	 */
	public class ToggleSwitchSkin implements Skin<ToggleSwitch> {
		
		public static final double TEXT_SPACING = 4D;
		public static final double TEXT_TRANS_X = 2D;
		public static final double REC_STROKE_WIDTH = .5D;
		public static final double REC_ARC = 5D;
		private Group rootNode;
		private ToggleSwitch control;
		private Label onLabel;
		private Label offLabel;
		private HBox textView;
		private boolean on;
		private boolean initialized;
		
	    public ToggleSwitchSkin(final ToggleSwitch control, final String onText, 
	    		final String offText, final boolean on) {
	        this.rootNode = new Group();
	        this.onLabel = new Label();
	        this.onLabel.setStyle("-fx-color: #FFFFFF;");
	        this.onLabel.setText(onText);
	        this.offLabel = new Label();
	        this.offLabel.setStyle("-fx-color: #FFFFFF;");
	        this.offLabel.setText(offText);
	    	this.control = control;
			this.textView = new HBox(TEXT_SPACING);
			this.textView.setTranslateX(TEXT_TRANS_X);
			this.textView.getChildren().addAll(onLabel, offLabel);
			this.control.heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					draw();
				}
			});
			this.on = on;
			this.initialized = true;
	    }

		@Override
		public void dispose() {
			control = null;
			rootNode = null;
			onLabel = offLabel = null;
			textView = null;
		}

		@Override
		public Node getNode() {
			return rootNode;
		}

		@Override
		public ToggleSwitch getSkinnable() {
			return control;
		}
		
		/**
		 * Gets the toggle value on/off
		 * 
		 * @return true for on, false for off
		 */
		public boolean getOn() {
			return this.on;
		}
		
		/**
		 * Sets the toggle value on/off
		 * 
		 * @param on true for on, false for off
		 */
		public void setOn(final boolean on) {
			this.on = on;
			if (initialized) {
				draw();
			}
		}
	    
		/**
		 * Draws the toggle switch
		 */
	    public void draw() {
			final Bounds textViewBounds = textView.getBoundsInLocal();
	        
	    	final Rectangle mainRec = new Rectangle();
	    	mainRec.setStroke(Color.BLACK);
	    	mainRec.setStrokeWidth(REC_STROKE_WIDTH);
	    	mainRec.setX(textViewBounds.getMinX());
	    	mainRec.setY(textViewBounds.getMinY());
	    	mainRec.setWidth(textViewBounds.getWidth() + TEXT_TRANS_X);
	    	mainRec.setHeight(textViewBounds.getHeight());
	    	mainRec.setArcWidth(REC_ARC);
	    	mainRec.setArcHeight(REC_ARC);
	    	
	    	final Rectangle middleRec = new Rectangle();
	    	middleRec.setStroke(Color.BLACK);
	    	middleRec.setStrokeWidth(REC_STROKE_WIDTH);
	    	middleRec.setX(getOn() ? (mainRec.getWidth() / 2) : mainRec.getX());
	    	middleRec.setY(0);
	    	middleRec.setWidth(mainRec.getWidth() / 2);
	    	middleRec.setHeight(mainRec.getHeight());
	    	middleRec.setArcWidth(REC_ARC);
	    	middleRec.setArcHeight(REC_ARC);
	    	
	        // Create and set a gradient for the inside of the button
	        Stop[] mainStops = new Stop[] {
	            new Stop(0.0, getOn() ? Color.DARKBLUE : Color.GRAY),
	            new Stop(1.0, getOn() ? Color.DODGERBLUE : Color.DARKGRAY)
	        };
	        LinearGradient mainLG =
	            new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, mainStops);
	        mainRec.setFill(mainLG);
	        
	        // Create and set a gradient for the inside of the button
	        Stop[] middleStops = new Stop[] {
	            new Stop(0.0, Color.GRAY),
	            new Stop(1.0, Color.WHITE)
	        };
	        LinearGradient middleLG =
	            new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, middleStops);
	        middleRec.setFill(middleLG);
	        
	        rootNode.getChildren().setAll(mainRec, textView, middleRec);
	    }
		
	}
}

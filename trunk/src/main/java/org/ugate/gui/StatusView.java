package org.ugate.gui;

import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * Status view for creating status icon indicators
 */
public abstract class StatusView extends VBox {
	
	public final Group statusIcon;
	protected final ControlBar controlBar;

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	protected StatusView(final ControlBar controlBar) {
		super();
		this.controlBar = controlBar;
		this.statusIcon = createStatusImage();
	}

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 * @param spacing the spacing
	 */
	protected StatusView(final ControlBar controlBar, double spacing) {
		super(spacing);
		this.controlBar = controlBar;
		this.statusIcon = createStatusImage();
	}
	
	/**
	 * @return a status indicator {@linkplain Group} that will visually indicate an on/off status
	 */
	protected Group createStatusImage() {
		final Group circles = new Group();
		circles.getChildren().add(createStatusCircle(null));
		circles.getChildren().add(createStatusCircle(false));
		return circles;
	}
	
	/**
	 * Creates status indicator {@linkplain Circle} that will visually indicate an on/off status
	 * 
	 * @param on true to show the the status {@linkplain Circle} as on, false to show it as off
	 * @return the status {@linkplain Circle}
	 */
	private Circle createStatusCircle(Boolean on) {
		final Circle circle = new Circle(on == null ? 7 : 5, Color.web("white", 0.05));
		circle.setFill(getStatusFill(on));
		circle.setStroke(Color.web("#D0E6FA", 0.16));
		circle.setStrokeType(StrokeType.OUTSIDE);
		circle.setStrokeWidth(2);
		return circle;
	}
	
	/**
	 * Gets the status indicator fill
	 * 
	 * @param on true for a an on fill, false for an off fill
	 * @return the status fill
	 */
	private LinearGradient getStatusFill(Boolean on) {
		final String color1 = on == null ? "#4977A3" : on ? "#00FF00" : "#FF0000";
		final String color2 = on == null ? "#B0C6DA" : on ? "#00FF99" : "#FF3333";
		final String color3 = on == null ? "#9CB6CF" : on ? "#00FF00" : "#FF0000";
		return new LinearGradient(0,0,0,1, true, CycleMethod.NO_CYCLE,
	    new Stop[]{
			new Stop(0, Color.web(color1, 0.7)),
			new Stop(0.5, Color.web(color2, 0.7)),
			new Stop(1, Color.web(color3, 0.7))
	    });
	}
	
	/**
	 * Sets the status fill for the indicator
	 * 
	 * @param group the group that contains the status {@linkplain Circle}
	 * @param on true to show the the status {@linkplain Circle} as on, false to show it as off
	 */
	protected void setStatusFill(final Group group, final Boolean on) {
		if (group != null) {
			((Circle)group.getChildren().get(1)).setFill(getStatusFill(on));
		}
	}
	
	/**
	 * Connects to the status service
	 */
	public abstract void connect();
}

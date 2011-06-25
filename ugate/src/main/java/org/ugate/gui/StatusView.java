package org.ugate.gui;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
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
	protected EventHandler<MouseEvent> connectionHandler;

	public StatusView() {
		super();
		statusIcon = createStatusImage();
	}

	public StatusView(double spacing) {
		super(spacing);
		statusIcon = createStatusImage();
	}
	
	protected Group createStatusImage() {
		Group circles = new Group();
		circles.getChildren().add(createStatusCircle(null));
		circles.getChildren().add(createStatusCircle(false));
		return circles;
	}
	
	private Circle createStatusCircle(Boolean on) {
		Circle circle = new Circle(on == null ? 7 : 5, Color.web("white", 0.05));
		circle.setFill(getStatusFill(on));
		circle.setStroke(Color.web("#D0E6FA", 0.16));
		circle.setStrokeType(StrokeType.OUTSIDE);
		circle.setStrokeWidth(2);
		return circle;
	}
	
	private LinearGradient getStatusFill(Boolean on) {
		String color1 = on == null ? "#4977A3" : on ? "#00FF00" : "#FF0000";
		String color2 = on == null ? "#B0C6DA" : on ? "#00FF99" : "#FF3333";
		String color3 = on == null ? "#9CB6CF" : on ? "#00FF00" : "#FF0000";
		return new LinearGradient(0,0,0,1, true, CycleMethod.NO_CYCLE,
	    new Stop[]{
			new Stop(0, Color.web(color1, 0.7)),
			new Stop(0.5, Color.web(color2, 0.7)),
			new Stop(1, Color.web(color3, 0.7))
	    });
	}
	
	protected void setStatusFill(Group group, Boolean on) {
		if (group != null) {
			((Circle)group.getChildren().get(1)).setFill(getStatusFill(on));
			handleStatusChange(on);
		}
	}
	
	public EventHandler<MouseEvent> getStatusHandler() {
		return connectionHandler;
	}
	
	public abstract void handleStatusChange(Boolean on);
}

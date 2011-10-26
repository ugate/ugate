package org.ugate.gui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Control view
 */
public abstract class ControlPane extends BorderPane {

	public final ToolBar topView;
	public final ToolBar leftView;
	public final VBox centerView;
	public final ToolBar rightView;
	
	public ControlPane() {
		setStyle("-fx-background-color: #000000;");
		// top
		topView = new ToolBar();
		topView.getItems().addAll(createTopViewChildren());
		// left
		leftView = new ToolBar();
		leftView.setOrientation(Orientation.VERTICAL);
		leftView.getStyleClass().add("control-toolbar");
		leftView.getItems().addAll(createLeftViewChildren());
		// middle
		centerView = new VBox();
		centerView.setAlignment(Pos.CENTER);
		centerView.getChildren().add(createGauge());
		centerView.getChildren().addAll(createCenterViewChildren());
		// bottom
		rightView = new ToolBar();
		rightView.setOrientation(Orientation.VERTICAL);
		rightView.getStyleClass().add("control-toolbar");
		rightView.getItems().addAll(createRightViewChildren());
		setTop(topView);
        setLeft(leftView);
        setCenter(centerView);
        setRight(rightView);

        setPrefHeight(Integer.MAX_VALUE);
	}
	
	private Group createGauge() {
		final int numOfMarks = 12;
		final Group pg = new Group();
		pg.setCache(true);
		pg.setCacheHint(CacheHint.SPEED);
		final Group og = new Group();
		og.setCache(true);
		og.setCacheHint(CacheHint.SPEED);
		og.translateXProperty().set(40);
		og.translateYProperty().set(60);
		final Circle c1 = new Circle(140, 140, 134);
		c1.fillProperty().set(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#3c3c3c")), new Stop(1, Color.web("#010101"))));
		final Circle c2 = new Circle(140, 140, 140);
		c2.fillProperty().set(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.rgb(20, 20, 20)), new Stop(0.9499, Color.rgb(20, 20, 20)),
				new Stop(0.95, Color.rgb(20, 20, 20)), new Stop(0.975, Color.rgb(20, 20, 20)),
				new Stop(1, Color.rgb(84, 84, 84, 0.0))));
		og.getChildren().addAll(c1, c2);
        final double rtbase = 360 / numOfMarks;
		for (int i=0; i<numOfMarks; i++) {
            Rectangle tm = new Rectangle(0, 108, 4, 13);
            tm.fillProperty().set(Color.web("#9fff81"));
            tm.rotateProperty().set(rtbase * i);
            tm.translateXProperty().set(140);
            tm.translateYProperty().set(140);
            og.getChildren().add(tm);
        }
		pg.getChildren().addAll(og);
		return pg;
	}
	
	protected abstract Node[] createTopViewChildren();
	
	protected abstract Node[] createLeftViewChildren();
	
	protected abstract Node[] createCenterViewChildren();
	
	protected abstract Node[] createRightViewChildren();
}

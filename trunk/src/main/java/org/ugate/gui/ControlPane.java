package org.ugate.gui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.ugate.gui.components.NeedleGauge;

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
		centerView.getChildren().add(new NeedleGauge());
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
	
	protected abstract Node[] createTopViewChildren();
	
	protected abstract Node[] createLeftViewChildren();
	
	protected abstract Node[] createCenterViewChildren();
	
	protected abstract Node[] createRightViewChildren();
}

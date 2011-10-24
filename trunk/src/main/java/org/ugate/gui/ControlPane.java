package org.ugate.gui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Control view
 */
public abstract class ControlPane extends BorderPane {

	public final ToolBar leftView;
	public final VBox centerView;
	public final ToolBar rightView;
	
	public ControlPane() {
		setStyle("-fx-background-color: #000000;");
		VBox.setVgrow(this, Priority.ALWAYS);
		HBox.setHgrow(this, Priority.ALWAYS);
		// top
		leftView = new ToolBar();
		leftView.setOrientation(Orientation.VERTICAL);
		//leftView.setPrefHeight(toolbarTopHeight);
		VBox.setVgrow(leftView, Priority.ALWAYS);
		leftView.getStyleClass().add("rxtx-toolbar");
		leftView.getItems().addAll(createLeftViewChildren());
		// middle
		centerView = new VBox();
		centerView.setAlignment(Pos.CENTER);
		centerView.getChildren().addAll(createCenterViewChildren());
		// bottom
		rightView = new ToolBar();
		rightView.setOrientation(Orientation.VERTICAL);
		//rightView.setPrefHeight(toolBarBottomHeight);
		VBox.setVgrow(rightView, Priority.ALWAYS);
		rightView.getStyleClass().add("rxtx-toolbar");
		rightView.getItems().addAll(createRightViewChildren());
		// add gate views
        setLeft(leftView);
        setCenter(centerView);
        setRight(rightView);
	}
	
	protected abstract Node[] createLeftViewChildren();
	
	protected abstract Node[] createCenterViewChildren();
	
	protected abstract Node[] createRightViewChildren();
}

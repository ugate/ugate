package org.ugate.gui;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Control view
 */
public abstract class ControlPane extends BorderPane {

	public final ToolBar topView;
	public final VBox middleView;
	public final ToolBar bottomView;
	
	public ControlPane(final double toolbarTopHeight, final double middleSpacing, final double toolBarBottomHeight) {
		setStyle("-fx-background-color: #000000;");
		// top
		topView = new ToolBar();
		topView.setPrefHeight(toolbarTopHeight);
		topView.getStyleClass().add("rxtx-toolbar");
		topView.getItems().addAll(getToolBarTopItems());
		// middle
		middleView = new VBox(middleSpacing);
		middleView.setAlignment(Pos.CENTER);
		middleView.getChildren().addAll(getMiddleViewChildren());
		// bottom
		bottomView = new ToolBar();
		bottomView.setPrefHeight(toolBarBottomHeight);
		bottomView.getStyleClass().add("rxtx-toolbar");
		bottomView.getItems().addAll(getBottomViewChildren());
		// add gate views
        setTop(topView);
        setCenter(middleView);
        setBottom(bottomView);
	}
	
	protected abstract Node[] getToolBarTopItems();
	
	protected abstract Node[] getMiddleViewChildren();
	
	protected abstract Node[] getBottomViewChildren();
	
	/**
	 * Prepares the control for submission to remote device by adding the needed values
	 * 
	 * @param values the values to add to
	 * @return true when the control is ready for submission
	 */
	public abstract boolean preSubmit(final List<Integer> values);
}

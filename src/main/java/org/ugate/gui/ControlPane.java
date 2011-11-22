package org.ugate.gui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Control view
 */
public abstract class ControlPane extends BorderPane {

	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 30d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);
	public static final double KNOB_SIZE_SCALE = 0.3d;
	public static final double NEEDLE_SIZE_SCALE = 0.4d;
	public final Group leftGroup;
	public final Group centerGroup;
	public final Group rightGroup;
	public final ToolBar topView;
	public final VBox leftView;
	public final VBox centerView;
	public final VBox rightView;
	
	public ControlPane() {
		getStyleClass().add("gauge-control-pane");
		// top
		topView = new ToolBar();
		topView.getItems().addAll(createTopViewChildren());
		
		// left
		leftGroup = new Group();
		leftView = new VBox(CHILD_SPACING);
		leftView.setPadding(PADDING_INSETS);
		//leftView.setOrientation(Orientation.VERTICAL);
		//leftView.getStyleClass().add("control-toolbar");
		//leftView.getItems().addAll(createLeftViewChildren());
		leftView.getChildren().addAll(createLeftViewChildren());
		leftGroup.getChildren().addAll(createBackground(leftView, false), leftView);
		
		// middle
		centerGroup = new Group();
		centerView = new VBox(CHILD_SPACING);
		centerView.setPadding(PADDING_INSETS);
		centerView.setAlignment(Pos.TOP_CENTER);
		BorderPane.setAlignment(centerGroup, Pos.TOP_CENTER);
		//centerView.getChildren().add(new GaugeDemo());
		centerView.getChildren().addAll(createCenterViewChildren());
		centerGroup.getChildren().addAll(createBackground(centerView, false), centerView);

		// right
		rightGroup = new Group();
		rightView = new VBox(CHILD_SPACING);
		rightView.setPadding(PADDING_INSETS);
		//rightView.setOrientation(Orientation.VERTICAL);
		//rightView.getStyleClass().add("control-toolbar");
		//rightView.getItems().addAll(createRightViewChildren());
		rightView.getChildren().addAll(createRightViewChildren());
		rightGroup.getChildren().addAll(createBackground(rightView, false), rightView);
		
		setTop(topView);
		setLeft(leftGroup);
		setCenter(centerGroup);
		setRight(rightGroup);

        setPrefHeight(Integer.MAX_VALUE);
	}
	
	private Rectangle createBackground(final VBox vbox, final boolean maxWidth) {
		final Rectangle backgroundRec = new Rectangle(vbox.getWidth(), vbox.getHeight(), Color.LIGHTGRAY);
		backgroundRec.setX(PADDING_INSETS.getLeft() / 2d);
		backgroundRec.setY(PADDING_INSETS.getTop() / 2d);
		backgroundRec.setArcWidth(10d);
		backgroundRec.setArcHeight(10d);
		backgroundRec.setOpacity(0.1d);
		backgroundRec.setStroke(Color.WHITE);
		backgroundRec.setStrokeWidth(2d);
		//backgroundLeftRec.getStyleClass().add("control-toolbar");
		vbox.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateBackgroundRec(backgroundRec, vbox, maxWidth);
			}
		});
		vbox.heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateBackgroundRec(backgroundRec, vbox, maxWidth);
			}
		});
		if (maxWidth) {
			widthProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					updateBackgroundRec(backgroundRec, vbox, maxWidth);
				}
			});
		}
		heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateBackgroundRec(backgroundRec, vbox, maxWidth);
			}
		});
		return backgroundRec;
	}
	
	private void updateBackgroundRec(final Rectangle backgroundRec, final VBox vbox, final boolean maxWidth) {
		final double contentWidth = vbox.getWidth() - PADDING_INSETS.getLeft();
		final double maxSpanWidth = getWidth() - PADDING_INSETS.getLeft() - PADDING_INSETS.getRight();
		backgroundRec.setWidth(maxWidth ? Math.max(maxSpanWidth, contentWidth) : contentWidth);
		backgroundRec.setHeight(Math.max(getHeight() - getTop().getBoundsInLocal().getHeight() - 
				PADDING_INSETS.getTop() - PADDING_INSETS.getBottom(), vbox.getHeight()));
	}
	
	protected Node[] createTopViewChildren() {
		final Label header = new Label("Transmission Status");
		header.getStyleClass().add("gauge-header");
		return new Node[] { header };

	}
	
	protected abstract Node[] createLeftViewChildren();
	
	protected abstract Node[] createCenterViewChildren();
	
	protected abstract Node[] createRightViewChildren();
}
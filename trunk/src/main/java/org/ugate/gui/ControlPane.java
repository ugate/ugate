package org.ugate.gui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;

/**
 * Base view for control settings
 */
public abstract class ControlPane extends GridPane {

	public static final double LABEL_WIDTH = 110d;
	public static final String ACCESS_KEY_CODE_FORMAT = "%01d";
	public static final String PRIORITY_FORMAT = "%01d";
	public static final String FORMAT_DELAY = "%03d";
	public static final String FORMAT_ANGLE = "%03d";
	public static final String FORMAT_SONAR = "%04.1f";
	public static final String FORMAT_PIR = "%04.1f";
	public static final String FORMAT_MW = "%03d";
	public static final String FORMAT_LASER = "%04.1f";
	public static final Color COLOR_SONAR = Color.CYAN;
	public static final Color COLOR_PIR = Color.ORANGE.brighter();
	public static final Color COLOR_MW = Color.CHARTREUSE;
	public static final Color COLOR_LASER = Color.RED;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 30d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);
	public static final double KNOB_SIZE_SCALE = 0.25d;
	public static final double THRESHOLD_SIZE_SCALE = 0.8d;
	public static final double DELAY_SIZE_SCALE = 0.54d;
	public static final Color COLOR_CAM = Color.YELLOW;
	public static final Color COLOR_MULTI = Color.ORANGE;
	protected final ControlBar controlBar;
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public ControlPane(final ControlBar controlBar) {
		this.controlBar = controlBar;
		getStyleClass().add("gauge-control-pane");
        setPrefHeight(Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a label
	 * 
	 * @param key the {@linkplain KEYS} resource key
	 * @return the label
	 */
	protected Label createLabel(final KEYS key) {
		final Label label = new Label(RS.rbLabel(key));
		label.setWrapText(true);
		label.setPrefWidth(LABEL_WIDTH);
		label.getStyleClass().add("gauge-header");
		return label;
	}
	
	/**
	 * Creates a cell of the grid pane
	 * 
	 * @param resizeWidth  true when the width should be resized
	 * @param resizeHeight true when the height should be resized
	 * @param nodes the nodes to add to the side (when null, the group returned will be null)
	 * @return the group
	 */
	protected Parent createCell(final boolean resizeWidth, final boolean resizeHeight, final Node... nodes) {
		final VBox view = new VBox(CHILD_SPACING);
		for (final Node node : nodes) {
			VBox.setVgrow(node, Priority.NEVER);
		}
		view.setPadding(PADDING_INSETS);
		view.getChildren().addAll(nodes);
		
//		VBox.setVgrow(view, Priority.ALWAYS);
//		view.setAlignment(Pos.TOP_CENTER);
//		view.getStyleClass().add("content-background");
//		group.getChildren().addAll(view);
		final Parent pc = new PaneCell(createBackground(view, resizeWidth, resizeHeight), view);
		//final StackPane pc = new StackPane();
		//pc.setAlignment(Pos.TOP_LEFT);
		//pc.getChildren().addAll(createBackground(view, resizeWidth, resizeHeight), view);
		return pc;
	}

	protected static class PaneCell extends Parent {
		
		public PaneCell(final Node... nodes) {
			getChildren().addAll(nodes);
		}
	}

	/**
	 * Creates a background
	 * 
	 * @param node the node that will be used to adjust the background to
	 * @param resizeWidth  true when the width should be resized
	 * @param resizeHeight true when the height should be resized
	 * @return a background
	 */
	private Rectangle createBackground(final VBox node, final boolean resizeWidth, final boolean resizeHeight) {
		final Rectangle backgroundRec = new Rectangle(node.getWidth(), node.getHeight(), Color.LIGHTGRAY);
		backgroundRec.setX(PADDING_INSETS.getLeft() / 2d);
		backgroundRec.setY(PADDING_INSETS.getTop() / 2d);
		backgroundRec.setArcWidth(10d);
		backgroundRec.setArcHeight(10d);
		backgroundRec.setOpacity(0.1d);
		backgroundRec.setStroke(Color.WHITE);
		backgroundRec.setStrokeWidth(2d);
		//backgroundLeftRec.getStyleClass().add("control-toolbar");
		node.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateBackgroundRec(backgroundRec, node, resizeWidth, resizeHeight);
			}
		});
		node.heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateBackgroundRec(backgroundRec, node, resizeWidth, resizeHeight);
			}
		});
		heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateBackgroundRec(backgroundRec, node, resizeWidth, resizeHeight);
			}
		});
		return backgroundRec;
	}
	
	/**
	 * Updates the background rectangle so that it displays properly
	 * 
	 * @param backgroundRec the display rectangle
	 * @param node the node that will be used to adjust the rectangle to
	 * @param resizeWidth  true when the width should be resized
	 * @param resizeHeight true when the height should be resized
	 */
	private void updateBackgroundRec(final Rectangle backgroundRec, final VBox node, 
			final boolean resizeWidth, final boolean resizeHeight) {
		final double contentWidth = resizeWidth ? Math.max(getWidth() - PADDING_INSETS.getLeft(), 
				node.getWidth()) : node.getWidth() - PADDING_INSETS.getLeft();
		final double contentHeight = resizeHeight ? Math.max(getHeight() - PADDING_INSETS.getTop() - 
				PADDING_INSETS.getBottom(), node.getHeight()) : node.getHeight() - PADDING_INSETS.getTop();
		backgroundRec.setWidth(contentWidth);
		backgroundRec.setHeight(contentHeight);
	}
}
package org.ugate.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;

/**
 * Base view for control settings
 */
public abstract class ControlPane extends GridPane {

	public static final double LABEL_WIDTH = 125d;
	public static final String ACCESS_KEY_CODE_FORMAT = "%01d";
	public static final String PRIORITY_FORMAT = "%01d";
	public static final String FORMAT_DELAY = "%03d";
	public static final String FORMAT_ANGLE = "%03d";
	public static final String FORMAT_SONAR = "%04.2f";
	public static final String FORMAT_PIR = "%03d";
	public static final String FORMAT_MW = "%03d";
	public static final String FORMAT_LASER = "%04.2f";
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 40d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);
	public static final double KNOB_SIZE_SCALE = 0.25d;
	public static final double THRESHOLD_SIZE_SCALE = 0.80d;
	public static final double DELAY_SIZE_SCALE = 0.54d;
	protected final ControlBar controlBar;
	
	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public ControlPane(final ControlBar controlBar) {
		this.controlBar = controlBar;
		getStyleClass().add("section-grid");
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
	 * @param nodes
	 *            the nodes to add to the side (when null, the group returned
	 *            will be null)
	 * @return the {@linkplain Parent}
	 */
	public static Parent createCell(final Node... nodes) {
		final VBox view = new VBox();
		VBox.setVgrow(view, Priority.ALWAYS);
		view.getStyleClass().add("section-pane");
		view.getChildren().addAll(nodes);
		return view;
	}
}
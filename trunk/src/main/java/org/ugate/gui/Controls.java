package org.ugate.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.resources.RS;
import org.w3c.dom.Element;

/**
 * XBee GUI view responsible for communicating with local and remote XBees once a connection is established
 */
public class Controls extends SplitPane {

	private static final Logger log = Logger.getLogger(Controls.class);
	public static final double TOOLBAR_TOP_HEIGHT = 50;
	public static final double MIDDLE_SPACING = 10;
	public static final double TOOLBAR_BOTTOM_HEIGHT = 110;
	public final CameraControl camView;
	public final SonarIrControl sonarIrControl;
	public final MicrowaveControl mwView;
	public final GateControl gateControl;
	protected static final String NAVIGATE_JS = "nav";
	protected static final String NAVIGATE_JS_UP = "UP";
	protected static final String NAVIGATE_JS_DOWN = "DOWN";
	protected static final String NAVIGATE_JS_LEFT = "LEFT";
	protected static final String NAVIGATE_JS_RIGHT = "RIGHT";

	public Controls() {
		super();
		this.setStyle("-fx-background-color: #000000;");
		
		// create tabs
		camView = new CameraControl(TOOLBAR_TOP_HEIGHT, MIDDLE_SPACING, TOOLBAR_BOTTOM_HEIGHT);
		sonarIrControl = new SonarIrControl(TOOLBAR_TOP_HEIGHT, MIDDLE_SPACING, TOOLBAR_BOTTOM_HEIGHT);
		mwView = new MicrowaveControl(TOOLBAR_TOP_HEIGHT, MIDDLE_SPACING, TOOLBAR_BOTTOM_HEIGHT);
		gateControl = new GateControl(TOOLBAR_TOP_HEIGHT, MIDDLE_SPACING, TOOLBAR_BOTTOM_HEIGHT);

		setDividerPositions(0.29f, 0.75f, 0f, 0f);
		HBox.setHgrow(this, Priority.ALWAYS);
		VBox.setVgrow(this, Priority.ALWAYS);
		setOrientation(Orientation.HORIZONTAL);
		getItems().addAll(camView, sonarIrControl, mwView, gateControl);
	}
	
	/**
	 * Creates a controls web view. When the view is loaded the images will be updated with the ones provided
	 * 
	 * @param fileName the HTML file name to load
	 * @param navResultImgSrc the navigation result image source
	 * @param navImgSrc the navigation image source
	 * @param isCam true if the web view is for the camera
	 * @return the web view
	 */
	protected WebView createWebView(final String fileName, final String navResultImgSrc, 
			final String navImgSrc, final boolean isCam) {
		final WebView webView = new WebView();
		webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable,
					State oldValue, State newValue) {
				if (newValue == State.SUCCEEDED && webView.getEngine().getDocument() != null) {
					final Element navImg = webView.getEngine().getDocument().getElementById("navImg");
					// link = item.getAttributes().getNamedItem("src").getTextContent();
					navImg.getAttributes().getNamedItem("src").setNodeValue(navImgSrc);
					final Element navResultImg = webView.getEngine().getDocument().getElementById("navResultImg");
					navResultImg.getAttributes().getNamedItem("src").setNodeValue(navResultImgSrc);
				}
			}
		});
		webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> event) {
				if (event.getData().indexOf(NAVIGATE_JS) > -1) {
					
				}
				log.debug(event.getData());
			}
		});
		webView.getEngine().load(RS.path(fileName));
		return webView;
	}
	
	/**
	 * Creates a color bar that will display a visual indicator when the node has focus
	 * 
	 * @param color the color of the bar
	 * @param node the node associated with the color bar
	 * @param otherColorBar other color bar(s)
	 * @return the color bar
	 */
	protected Separator createColorBar(final String color, final Node node, final Separator... otherColorBar) {
        final Separator colorBar = new Separator();
        colorBar.setOrientation(Orientation.HORIZONTAL);
        colorBar.setPrefHeight(5);
        colorBar.setStyle("-fx-background-color: " + color + ';');
        configColorBar(colorBar, node, otherColorBar);
        return colorBar;
	}
	
	/**
	 * Configures a color bar so that it will display a visual indicator when the node has focus
	 * 
	 * @param colorBar the color bar to configure
	 * @param node the node associated with the color bar
	 * @param otherColorBar other color bar(s)
	 */
	protected void configColorBar(final Separator colorBar, final Node node, final Separator... otherColorBar) {
        if (otherColorBar != null) {
        	node.setOnMouseEntered(new EventHandler<MouseEvent>() {
	        	@Override
	            public void handle(MouseEvent event) {
	        		colorBar.setVisible(true);
	            	for (Separator cb : otherColorBar) {
	            		cb.setVisible(false);
	            	}
	            }
        	});
        }
	}
	
	/**
	 * Creates a button that will reload the specified web view when clicked
	 * 
	 * @param webView the web view to reload
	 * @return the created button
	 */
	protected Button createReloadButton(final WebView webView) {
		final Button reloadBtn = new Button("Reload");
		reloadBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				webView.getEngine().reload();
			}
		});
		return reloadBtn;
	}
	
	/**
	 * Sends a sync command to the remote device along with the values of the 
	 * 
	 * @param command the command to send
	 */
	protected void sync(final int command) {
		if (UGateKeeper.DEFAULT.isXbeeConnected()) {
			final List<Integer> values = new ArrayList<Integer>();
			values.add(command);
			values.add(0); // TODO : failure code is hard coded
			if (camView.preSubmit(values) && sonarIrControl.preSubmit(values) && 
					mwView.preSubmit(values) && gateControl.preSubmit(values)) {
				log.debug("Sending XBee command: " + command);
				UGateKeeper.DEFAULT.xbeeSendData(
						UGateKeeper.DEFAULT.GATE_XBEE_ADDRESS, values);
			}
		}
	}
}
package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import org.apache.log4j.Logger;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.resources.RS;
import org.w3c.dom.Element;

/**
 * XBee GUI view responsible for communicating with local and remote XBees once a connection is established
 */
public class ControlsView extends SplitPane {

	private static final Logger log = Logger.getLogger(ControlsView.class);
	public static final double TOOLBAR_TOP_HEIGHT = 50;
	public static final double MIDDLE_SPACING = 10;
	public static final double TOOLBAR_BOTTOM_HEIGHT = 110;
	protected final UGateTextField recipients;
	protected final ToggleSwitchPreferenceView recipientsToggleSwitch;
	protected final ToggleSwitchPreferenceView sonarToggleSwitchView;
	protected final ToggleSwitchPreferenceView irToggleSwitchView;
	protected final ToggleSwitchPreferenceView mwToggleSwitchView;
	protected final ToggleSwitchPreferenceView gateToggleSwitchView;
	protected static final String NAVIGATE_JS = "nav";
	protected static final String NAVIGATE_JS_UP = "UP";
	protected static final String NAVIGATE_JS_DOWN = "DOWN";
	protected static final String NAVIGATE_JS_LEFT = "LEFT";
	protected static final String NAVIGATE_JS_RIGHT = "RIGHT";

	public ControlsView() {
		super();
		this.setStyle("-fx-background-color: #000000;");
		
		//####### Camera view
		final BorderPane camView = new BorderPane();
		camView.setStyle("-fx-background-color: #000000;");
		// top
		final ToolBar camToolBarTop = new ToolBar();
		camToolBarTop.setPrefHeight(TOOLBAR_TOP_HEIGHT);
        camToolBarTop.getStyleClass().add("rxtx-toolbar");
		final UGateSliderGauge camPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Camera Pan: Current camera pan angle (in degrees)", false, Color.AQUA, null);
		final UGateSliderGauge camTiltGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_TILT,
				"Camera Tilt: Current camera tilt angle (in degrees)", false, Color.AQUA, null);
		final HBox camSendView = new HBox();
		camSendView.setAlignment(Pos.TOP_CENTER);
		camSendView.setPadding(new Insets(0, 0, 0, 5));
		camSendView.getChildren().add(UGateGUI.genFisheye(RS.IMG_CAM_VGA, 20, 20, 1.3, 1.3, false, new Runnable() {
			@Override
            public void run() {
            	
            }
        }));
		camSendView.getChildren().add(UGateGUI.genFisheye(RS.IMG_CAM_VGA, 30, 30, 1.3, 1.3, false, new Runnable() {
			@Override
            public void run() {
            	
            }
        }));
		HBox.setMargin(camSendView.getChildren().get(0), new Insets(0, 5, 0, 0));
        camToolBarTop.getItems().addAll(camPanGauge, camTiltGauge, camSendView);
        // middle
		final HBox camMiddleView = new HBox(MIDDLE_SPACING); //createWebView(RS.HTML_SERVO_NAV, RS.IMG_SENSOR_ARM, RS.IMG_NAV_CAM, true);
		camMiddleView.setAlignment(Pos.CENTER);
		final ImageView camNavStatusButton = RS.imgView(RS.IMG_SENSOR_ARM);
		final ImageView camNavButton = RS.imgView(RS.IMG_NAV_CAM);
		camMiddleView.getChildren().addAll(camNavStatusButton, camNavButton);
        // bottom
		final ToolBar camToolBarBottom = new ToolBar();
		camToolBarBottom.setPrefHeight(TOOLBAR_BOTTOM_HEIGHT);
        camToolBarBottom.getStyleClass().add("rxtx-toolbar");
		recipientsToggleSwitch = new ToggleSwitchPreferenceView(UGateUtil.MAIL_ALARM_ON_KEY, 
				RS.IMG_EMAIL_SELECTED, RS.IMG_EMAIL_DESELECTED, 
				"Toggle sending email notifications for images taken (by alarm trip or manually)");
		final VBox mailView = new VBox();
		recipients = new UGateTextField("Recipients (semi-colon delimited emails)", 
				"Semi-colon delimited list of emails to send image to (blank if no emails should be sent)",
				UGateUtil.MAIL_RECIPIENTS_KEY, UGateTextField.TYPE_TEXT_AREA);
		recipients.textArea.setPrefRowCount(5);
		mailView.getChildren().addAll(recipients);
		camToolBarBottom.getItems().addAll(recipientsToggleSwitch, mailView);
		camView.setTop(camToolBarTop);
		camView.setCenter(camMiddleView);
		camView.setBottom(camToolBarBottom);
		
		//####### Sonar/IR view
		final BorderPane sonarIrView = new BorderPane();
		sonarIrView.setStyle("-fx-background-color: #000000;");
		// top
		final ToolBar sonarIrToolBarTop = new ToolBar();
		sonarIrToolBarTop.setPrefHeight(TOOLBAR_TOP_HEIGHT);
        sonarIrToolBarTop.getStyleClass().add("rxtx-toolbar");
		final UGateSliderGauge sonarIrPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Sonar/IR Pan: Current trip alram sensor pan angle (in degrees)", false, Color.YELLOW, null);
		final UGateSliderGauge sonarIrTiltGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_TILT,
				"Sonar/IR Tilt: Current trip alarm sensor tilt angle (in degrees)", false, Color.YELLOW, null);
        sonarIrToolBarTop.getItems().addAll(sonarIrPanGauge, sonarIrTiltGauge);
		// middle
		final HBox sonarIrMiddleView = new HBox(MIDDLE_SPACING);
		sonarIrMiddleView.setAlignment(Pos.CENTER);
		final ImageView sonarIrNavStatusButton = RS.imgView(RS.IMG_SENSOR_ARM);
		final ImageView sonarIrNavButton = RS.imgView(RS.IMG_NAV_SENSOR);
		sonarIrMiddleView.getChildren().addAll(sonarIrNavStatusButton, sonarIrNavButton);
        // bottom
        final ToolBar sonarIrToolBarBottom = new ToolBar();
        sonarIrToolBarBottom.setPrefHeight(TOOLBAR_BOTTOM_HEIGHT);
        sonarIrToolBarBottom.getStyleClass().add("rxtx-toolbar");
        // create sonar view
        sonarToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.SONAR_ALARM_ON_KEY, 
				RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, 
				"Toggle sonar intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		final VBox sonarTripView = new VBox();
		final UGateSliderGauge sonarTripGauge = new UGateSliderGauge(1.0f, 26.0f, 10.0f, 0.5f, "%04.1f", RS.IMG_RULER,
				"Sonar Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				true, null, null);
		final UGateSliderGauge sonarTripRateGauge = new UGateSliderGauge(0, 120, 0, 1, "%03d", RS.IMG_STOPWATCH,
				"Sonar Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				true, null, null);
		sonarTripView.getChildren().addAll(sonarTripGauge, sonarTripRateGauge);
		// create IR view
		irToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.IR_ALARM_ON_KEY, 
				RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF,
				"Toggle IR intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		final VBox irTripView = new VBox();
		final UGateSliderGauge irTripGauge = new UGateSliderGauge(1.0f, 26.0f, 10.0f, 0.5f, "%04.1f", RS.IMG_RULER,
				"IR Distance Threshold: Distance at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				true, null, null);
		final UGateSliderGauge irTripRateGauge = new UGateSliderGauge(0, 120, 0, 1, "%03d", RS.IMG_STOPWATCH,
				"IR Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the distance threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				true, null, null);
		irTripView.getChildren().addAll(irTripGauge, irTripRateGauge);
		// add sonar/IR views
		sonarIrToolBarBottom.getItems().addAll(sonarToggleSwitchView, sonarTripView, new Separator(Orientation.VERTICAL), irToggleSwitchView, irTripView);
		sonarIrView.setTop(sonarIrToolBarTop);
		sonarIrView.setCenter(sonarIrMiddleView);
		sonarIrView.setBottom(sonarIrToolBarBottom);
        
        // Microwave view
		final BorderPane mwView = new BorderPane();
		mwView.setStyle("-fx-background-color: #000000;");
		// top
		final ToolBar mwToolBarTop = new ToolBar();
		mwToolBarTop.setPrefHeight(TOOLBAR_TOP_HEIGHT);
        mwToolBarTop.getStyleClass().add("rxtx-toolbar");
		final UGateSliderGauge mwPanGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Microwave Pan: Current trip alram sensor pan angle (in degrees)", false, Color.YELLOW, null);
		mwToolBarTop.getItems().addAll(mwPanGauge);
		// middle
		final HBox mwMiddleView = new HBox(MIDDLE_SPACING);
		//VBox.getVgrow(mwMiddleView);
		mwMiddleView.setAlignment(Pos.CENTER);
		final ImageView mwNavStatusButton = RS.imgView(RS.IMG_SENSOR_ARM);
		final ImageView mwNavButton = RS.imgView(RS.IMG_NAV_CAM);
		mwMiddleView.getChildren().addAll(mwNavStatusButton, mwNavButton);
        // bottom
		final ToolBar mwToolBarBottom = new ToolBar();
		mwToolBarBottom.setPrefHeight(TOOLBAR_BOTTOM_HEIGHT);
        mwToolBarBottom.getStyleClass().add("rxtx-toolbar");
		mwToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.MICROWAVE_ALARM_ON_KEY, 
				RS.IMG_MICROWAVE_ALARM_ON, RS.IMG_MICROWAVE_ALARM_OFF, 
				"Toggle Microwave intruder alarm that takes a picture, stores it on the computer, and sends email notification with image attachment (if on)");
		final VBox mwTripView = new VBox();
		final UGateSliderGauge mwTripGauge = new UGateSliderGauge(1.0f, 26.0f, 10.0f, 0.5f, "%04.1f", RS.IMG_SPEEDOMETER,
				"Microwave Speed Threshold: Cycles/Second at which an image will be taken and sent to the computer and recipients (if alarm is turned on)", 
				true, null, null);
		final UGateSliderGauge mwTripRateGauge = new UGateSliderGauge(0, 120, 0, 1, "%03d", RS.IMG_STOPWATCH,
				"Microwave Delay Between Photos: Delay in minutes between pictures taken/sent when an object is within the speed threshold.\n" +
				"When zero, there may still be a few seconds beween photos due to the wireless transfer rate", 
				true, null, null);
		mwTripView.getChildren().addAll(mwTripGauge, mwTripRateGauge);
		mwToolBarBottom.getItems().addAll(mwToggleSwitchView, mwTripView);
		// add microwave views
		mwView.setTop(mwToolBarTop);
		mwView.setCenter(mwMiddleView);
		mwView.setBottom(mwToolBarBottom);
		
        // Gate view
		final BorderPane gateView = new BorderPane();
		gateView.setStyle("-fx-background-color: #000000;");
		// top
		final ToolBar gateToolBarTop = new ToolBar();
		gateToolBarTop.setPrefHeight(TOOLBAR_TOP_HEIGHT);
        mwToolBarTop.getStyleClass().add("rxtx-toolbar");
		final UGateSliderGauge gateGauge = new UGateSliderGauge(1, 180, 90, 1, "%03d", RS.IMG_PAN,
				"Gate Pan: Current trip alram sensor pan angle (in degrees)", false, Color.YELLOW, null);
		gateToolBarTop.getItems().addAll(gateGauge);
		// middle
		final VBox gateMiddleView = new VBox(MIDDLE_SPACING);
		gateMiddleView.setAlignment(Pos.CENTER);
		final ImageView gateToggleButton = RS.imgView(RS.IMG_GATE_CLOSED);
		gateMiddleView.getChildren().addAll(gateToggleButton);
		// bottom
		final ToolBar gateToolBarBottom = new ToolBar();
		gateToolBarBottom.setPrefHeight(TOOLBAR_BOTTOM_HEIGHT);
		gateToolBarBottom.getStyleClass().add("rxtx-toolbar");
        gateToggleSwitchView = new ToggleSwitchPreferenceView(UGateUtil.GATE_ACCESS_ON_KEY, 
        		RS.IMG_GATE_SELECTED, RS.IMG_GATE_DESELECTED, 
				"Toogle gate access");
        gateToolBarBottom.getItems().addAll(gateToggleSwitchView);
		// add gate views
        gateView.setTop(gateToolBarTop);
        gateView.setCenter(gateMiddleView);
        gateView.setBottom(gateToolBarBottom);

		setDividerPositions(0.29f, 0.75f, 0f, 0f);
		HBox.setHgrow(this, Priority.ALWAYS);
		VBox.setVgrow(this, Priority.ALWAYS);
		setOrientation(Orientation.HORIZONTAL);
		getItems().addAll(camView, sonarIrView, mwView, gateView);
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
	 * Sends a command to the remote device
	 * 
	 * @param command the command to send
	 */
	protected void sendCommand(final int command) {
		if (UGateKeeper.DEFAULT.isXbeeConnected()) {
			UGateKeeper.DEFAULT.preferences.set(UGateUtil.MAIL_RECIPIENTS_ON_KEY, 
					String.valueOf(!recipientsToggleSwitch.toggleSwitch.selectedProperty().get()));
			UGateKeeper.DEFAULT.preferences.set(UGateUtil.MAIL_RECIPIENTS_KEY, recipients.textField.getText());
			log.debug("Sending XBee command: " + command);
			UGateKeeper.DEFAULT.xbeeSendData(
					UGateKeeper.DEFAULT.GATE_XBEE_ADDRESS, new int[]{command});
		}
	}
}
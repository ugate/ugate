package org.ugate.gui;

import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.ugate.IGateKeeperListener;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.PlateGroup;
import org.ugate.gui.components.UGateToggleSwitchPreferenceView;
import org.ugate.resources.RS;
import org.w3c.dom.Element;

/**
 * XBee GUI view responsible for communicating with local and remote XBees once a connection is established
 */
public class Controls extends StackPane {

	private static final Logger log = Logger.getLogger(Controls.class);
	public static final int HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT = 8;
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);
	protected static final String NAVIGATE_JS = "nav";
	private final ScrollPane helpTextPane;
	private final Label helpText;
	private int remoteNodeIndex = 1;
	private boolean propagateSettingsToAllRemoteNodes = false;
	private IntegerProperty commandProperty = new SimpleIntegerProperty();
	public final Service<Boolean> commandService;

	public Controls(final Stage stage) {
		super();
		// help view
		final DropShadow helpTextDropShadow = new DropShadow();
		final Timeline helpTextTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				helpTextDropShadow, Color.CYAN, Color.BLACK, HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT);
		helpTextPane = new ScrollPane();
		helpTextPane.setStyle("-fx-background-color: #ffffff;");
		helpTextPane.setPrefHeight(40d);
		helpTextPane.setPrefWidth(200d);
		helpTextPane.setEffect(helpTextDropShadow);
		helpText = new Label(GuiUtil.HELP_TEXT_DEFAULT);
		helpText.setWrapText(true);
		helpText.setPrefWidth(helpTextPane.getPrefWidth() - 35d);
		helpText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				helpTextTimeline.stop();
				if (newValue != null && newValue.length() > 0) {
					helpTextTimeline.play();
				}
			}
		});
		helpTextPane.setContent(helpText);
		
		// main menu bar and tabs
		final ToolBar mainBar = new ToolBar(createMainBarChildren());
		final TabPane mainView = new TabPane();
		mainView.setSide(Side.RIGHT);
		final Tab camTab = createTab(null, "Camera/Gate", CameraGateControl.class);
		final Tab sensorsTab = createTab(null, "Sensors", SensorControl.class);
		mainView.getTabs().addAll(camTab, sensorsTab);

		// add main view
		final VBox main = new VBox();
		main.setStyle("-fx-background-color: #000000;");
		HBox.setHgrow(main, Priority.ALWAYS);
		VBox.setVgrow(main, Priority.ALWAYS);
		main.getChildren().addAll(mainBar, mainView);
		getChildren().add(main);
		
		// TODO : move to central location?
		commandService = GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				if (commandProperty.get() == UGateUtil.CMD_SENSOR_GET_READINGS) {
					if (!UGateKeeper.DEFAULT.wirelessSendData(getRemoteNodeAddress(), 
							new int[] { UGateUtil.CMD_SENSOR_GET_READINGS })) {
						setHelpText(String.format(
								"Unable to get the sensor readings from node %1$s. See log for more details.",
								getRemoteNodeIndex()));
						return false;
					}
				} else if (commandProperty.get() == UGateUtil.CMD_SENSOR_SET_SETTINGS) {
					if (!UGateKeeper.DEFAULT.wirelessSendSettings(
							(isPropagateSettingsToAllRemoteNodes() ? null : getRemoteNodeAddress()))) {
						setHelpText(
								"Unable to send the settings to the remote node(s). See log for more details.");
						return false;
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * @return the children used for the main menu bar
	 */
	protected Node[] createMainBarChildren() {
		// add the actions
		final DropShadow ds = new DropShadow();
		final ImageView camTakeQvga = RS.imgView(RS.IMG_CAM_QVGA);
		GuiUtil.addHelpText(helpTextPane, camTakeQvga, "Takes a QVGA image at the current camera pan/tilt angle " + 
		"and transfers the image back to the host. An email with the attached image will also be sent when enabled");
		camTakeQvga.setCursor(Cursor.HAND);
		camTakeQvga.setEffect(ds);
		final ImageView camTakeVga = RS.imgView(RS.IMG_CAM_VGA);
		GuiUtil.addHelpText(helpTextPane, camTakeVga, "Takes a VGA image at the current camera pan/tilt angle " + 
				"and transfers the image back to the host. An email with the attached image will also be sent when enabled");
		camTakeVga.setCursor(Cursor.HAND);
		camTakeVga.setEffect(ds);
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		settingsSet.setCursor(Cursor.HAND);
		settingsSet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					setHelpText(GuiUtil.HELP_TEXT_DEFAULT);
					commandProperty.set(UGateUtil.CMD_SENSOR_SET_SETTINGS);
					commandService.start();
				}
			}
	    });
		GuiUtil.addHelpText(helpTextPane, settingsSet, "Sends the settings to the remote microcontroller node. " + 
				"Blinks when settings updates have been made, but have not yet been sent");
		final DropShadow settingsDS = new DropShadow();
		settingsSet.setEffect(settingsDS);
		final Timeline settingsSetTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				settingsDS, Color.RED, Color.BLACK, Timeline.INDEFINITE);
		// show a visual indication that the settings need updated
		UGateKeeper.DEFAULT.preferencesAddListener(new IGateKeeperListener() {
			@Override
			public void handle(final IGateKeeperListener.Event type, final String node,
					final String key, final String oldValue, final String newValue) {
				if (type == IGateKeeperListener.Event.PREFERENCES_SET) {
					settingsSetTimeline.play();
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_SUCCESS) {
					settingsSetTimeline.stop();
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_FAILED) {
					
				}
			}
		});
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		GuiUtil.addHelpText(helpTextPane, readingsGet, 
				"Gets the current sensor readings and updates the readings display with the values");
		readingsGet.setCursor(Cursor.HAND);
		readingsGet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					setHelpText(GuiUtil.HELP_TEXT_DEFAULT);
					commandProperty.set(UGateUtil.CMD_SENSOR_GET_READINGS);
					commandService.start();
				}
			}
	    });
		
		// add the readings view
		final ImageView sonarReadingLabel = RS.imgView(RS.IMG_SONAR);
		final Digits sonarReading = new Digits(String.format(SensorControl.FORMAT_SONAR, 0.0f),
				0.15f, SensorControl.COLOR_SONAR, null);
		final ImageView pirReadingLabel = RS.imgView(RS.IMG_PIR);
		final Digits pirReading = new Digits(String.format(SensorControl.FORMAT_PIR, 0.0f), 
				0.15f, SensorControl.COLOR_PIR, null);
		final ImageView mwReadingLabel = RS.imgView(RS.IMG_MICROWAVE);
		final Digits mwReading = new Digits(String.format(SensorControl.FORMAT_MW, 0), 0.15f, 
				SensorControl.COLOR_MW, null);
		final Group readingsGroup = createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 10,
				sonarReadingLabel, sonarReading, pirReadingLabel, pirReading, mwReadingLabel, mwReading);
		GuiUtil.addHelpText(helpTextPane, readingsGroup, "Current sensors readings display");
		
		
		// add the multi-alarm trip state
		final UGateToggleSwitchPreferenceView multiAlarmToggleSwitch = new UGateToggleSwitchPreferenceView(UGateUtil.SV_MULTI_ALARM_TRIP_STATE_KEY,
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_MICROWAVE_ALARM_ON, RS.IMG_MICROWAVE_ALARM_OFF, false));
		final Group multiAlarmGroup = createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 0,
				multiAlarmToggleSwitch);
		GuiUtil.addHelpText(helpTextPane, multiAlarmGroup, "Multi-alarm trip state. When any combination of sensors have been selected those " + 
				"selected sensors will ALL have to be tripped in order to cause an alarm. When NONE of the sensors have been selected ANY " + 
				"sensor trip will cause an alarm. Keep in mind that the sensors selected should be on or no alarm will be triggered.");
		
		// add the menu items
		return new Node[] { camTakeQvga, camTakeVga, settingsSet, readingsGet, 
				new Separator(Orientation.VERTICAL), readingsGroup, 
				new Separator(Orientation.VERTICAL), multiAlarmGroup,
				new Separator(Orientation.VERTICAL), helpTextPane};
	}
	
	/**
	 * Creates a readings display
	 * 
	 * @param padding the padding in the grid group
	 * @param gapBetweenChildren the vertical and/or horizontal gap between cells
	 * @param numItemsPerRow the number of items per row
	 * @param nodes the nodes to add to the display
	 * @return the readings display
	 */
	public static final Group createReadingsDisplay(final Insets padding, final double gapBetweenChildren, 
			final int numItemsPerRow, final Node... nodes) {
		final Label readingsHeader = new Label("Readings");
		readingsHeader.getStyleClass().add("gauge-header");
		final GridPane gridReadings = new GridPane();
		gridReadings.setPadding(padding);
		gridReadings.setHgap(gapBetweenChildren);
		gridReadings.setVgap(gapBetweenChildren);
		int col = -1, row = 0;
		for (final Node node : nodes) {
			node.getStyleClass().add("gauge");
			gridReadings.add(node, ++col, row);
			row = col == (numItemsPerRow - 1) ? row + 1 : row;
			col = col == (numItemsPerRow - 1) ? -1 : col;
		}
		final PlateGroup readingsGroup = new PlateGroup(gridReadings.widthProperty(), 
				gridReadings.heightProperty(), 
				gridReadings.paddingProperty());
		readingsGroup.getChildren().add(gridReadings);
		return readingsGroup;
	}
	
	/**
	 * Creates a tab
	 * 
	 * @param graphicFileName the optional graphic for the tab
	 * @param text the text for the tab
	 * @param cpc the class used for the tab content
	 * @return the tab
	 */
	protected <T extends ControlPane> Tab createTab(final String graphicFileName, final String text, 
			final Class<T> cpc) {
		final Tab tab = new Tab(text);
		tab.setClosable(false);
		try {
			tab.setContent((T) cpc.getConstructor(helpTextPane.getClass()).newInstance(helpTextPane));
		} catch (final Throwable t) {
			log.error("Unable to Instantiate " + cpc, t);
		}
		// TODO : dynamic tab content creation causes memory leaks
		/*
		//tab.setGraphic(RS.imgView(graphicFileName));
		tab.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
//			        final ParameterizedType pt = (ParameterizedType) cpc.getGenericSuperclass();
//			        final String parameterClassName = pt.getActualTypeArguments()[0].toString().split("\\s")[1];
//			        T cp = (T) Class.forName(parameterClassName).newInstance();
					try {
						tab.setContent((T) cpc.newInstance());
					} catch (final Throwable t) {
						log.error("Unable to Instantiate " + cpc, t);
					}
				} else {
					tab.setContent(null);
				}
			}
		});
		*/
		return tab;
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
	 * @return the current help text
	 */
	public String getHelpText() {
		return helpText.getText();
	}
	
	/**
	 * Sets the help text
	 * 
	 * @param text the help text to set
	 */
	public void setHelpText(final String text) {
		helpText.setText(text);
	}
	
	/**
	 * @return the remote node address of the device node for which the controls represent
	 */
	public String getRemoteNodeAddress() {
		return UGateUtil.SV_WIRELESS_ADDRESS_NODE_PREFIX_KEY + getRemoteNodeIndex();
	}

	/**
	 * @return the remote index of the device node for which the controls represent
	 */
	public int getRemoteNodeIndex() {
		return this.remoteNodeIndex;
	}

	/**
	 * Sets the remote index of the device node for which the controls represent
	 * 
	 * @param remoteNodeIndex the remote node index
	 */
	public void setRemoteNodeIndex(final int remoteNodeIndex) {
		this.remoteNodeIndex = remoteNodeIndex;
	}

	/**
	 * @return true when settings updates should be propagated to all the remote nodes when
	 * 		the user chooses to save the settings
	 */
	public boolean isPropagateSettingsToAllRemoteNodes() {
		return propagateSettingsToAllRemoteNodes;
	}

	/**
	 * @param propagateSettingsToAllRemoteNodes true when settings updates should be propagated 
	 * 		to all the remote nodes when the user chooses to save the settings
	 */
	public void setPropagateSettingsToAllRemoteNodes(final boolean propagateSettingsToAllRemoteNodes) {
		this.propagateSettingsToAllRemoteNodes = propagateSettingsToAllRemoteNodes;
	}
}
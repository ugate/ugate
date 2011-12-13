package org.ugate.gui;

import javafx.animation.Timeline;
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
import org.ugate.Command;
import org.ugate.IGateKeeperListener;
import org.ugate.Settings;
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
	private final Stage stage;
	private int remoteNodeIndex = 1;
	private boolean propagateSettingsToAllRemoteNodes = false;

	public Controls(final Stage stage) {
		super();
		this.stage = stage;
		// help view
		final DropShadow helpTextDropShadow = new DropShadow();
		final Timeline helpTextTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				helpTextDropShadow, Color.RED, Color.BLACK, HELP_TEXT_COLOR_CHANGE_CYCLE_COUNT);
		helpTextPane = new ScrollPane();
		helpTextPane.setStyle("-fx-background-color: #ffffff;");
		helpTextPane.setPrefHeight(40d);
		helpTextPane.setPrefWidth(200d);
		helpTextPane.setEffect(helpTextDropShadow);
		helpText = new Label(RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY));
		helpText.setWrapText(true);
		helpText.setPrefWidth(helpTextPane.getPrefWidth() - 35d);
		helpText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				helpTextTimeline.stop();
				if (newValue != null && newValue.length() > 0 && 
						!newValue.equals(RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY))) {
					helpTextTimeline.play();
				}
			}
		});
		helpTextPane.setContent(helpText);
		
		// main menu bar and tabs
		final ToolBar mainBar = new ToolBar(createMainBarChildren());
		final TabPane mainView = new TabPane();
		mainView.setSide(Side.RIGHT);
		final Tab camTab = createTab(null, RS.rbLabel("camgate"), CameraGateControl.class);
		final Tab sensorsTab = createTab(null, RS.rbLabel("sensors"), SensorControl.class);
		mainView.getTabs().addAll(camTab, sensorsTab);

		// add main view
		final VBox main = new VBox();
		main.setStyle("-fx-background-color: #000000;");
		HBox.setHgrow(main, Priority.ALWAYS);
		VBox.setVgrow(main, Priority.ALWAYS);
		main.getChildren().addAll(mainBar, mainView);
		getChildren().add(main);
		
		// TODO : move to central location?

	}
	
	/**
	 * @return the children used for the main menu bar
	 */
	protected Node[] createMainBarChildren() {
		// add the actions
		final DropShadow ds = new DropShadow();
		final ImageView camTakeQvga = RS.imgView(RS.IMG_CAM_QVGA);
		addHelpTextTrigger(camTakeQvga, RS.rbLabel("cam.take.qvga"));
		camTakeQvga.setCursor(Cursor.HAND);
		camTakeQvga.setEffect(ds);
		final ImageView camTakeVga = RS.imgView(RS.IMG_CAM_VGA);
		addHelpTextTrigger(camTakeVga, RS.rbLabel("cam.take.vga"));
		camTakeVga.setCursor(Cursor.HAND);
		camTakeVga.setEffect(ds);
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		settingsSet.setCursor(Cursor.HAND);
		settingsSet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createControlsService(Command.SENSOR_SET_SETTINGS).start();
				}
			}
	    });
		addHelpTextTrigger(settingsSet, RS.rbLabel("settings.send"));
		final DropShadow settingsDS = new DropShadow();
		settingsSet.setEffect(settingsDS);
		final Timeline settingsSetTimeline = GuiUtil.createDropShadowColorIndicatorTimline(
				settingsDS, Color.RED, Color.BLACK, Timeline.INDEFINITE);
		// show a visual indication that the settings need updated
		UGateKeeper.DEFAULT.preferencesAddListener(new IGateKeeperListener() {
			@Override
			public void handle(final IGateKeeperListener.Event type, final String node,
					final Settings key, final Command command, 
					final String oldValue, final String newValue) {
				if (type == IGateKeeperListener.Event.SETTINGS_SAVE_LOCAL) {
					if (key != null && key.canRemote) {
						settingsSetTimeline.play();
					}
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_SUCCESS) {
					settingsSetTimeline.stop();
				} else if (type == IGateKeeperListener.Event.SETTINGS_SEND_FAILED) {
				}
			}
		});
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		addHelpTextTrigger(readingsGet, RS.rbLabel("sensors.readings.get"));
		readingsGet.setCursor(Cursor.HAND);
		readingsGet.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					createControlsService(Command.SENSOR_GET_READINGS).start();
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
		addHelpTextTrigger(readingsGroup, "Current sensors readings display");
		
		
		// add the multi-alarm trip state
		final UGateToggleSwitchPreferenceView multiAlarmToggleSwitch = new UGateToggleSwitchPreferenceView(
				Settings.MULTI_ALARM_TRIP_STATE_KEY,
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_SONAR_ALARM_ON, RS.IMG_SONAR_ALARM_OFF, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_IR_ALARM_ON, RS.IMG_IR_ALARM_OFF, false),
				new UGateToggleSwitchPreferenceView.ToggleItem(RS.IMG_MICROWAVE_ALARM_ON, RS.IMG_MICROWAVE_ALARM_OFF, false));
		final Group multiAlarmGroup = createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 0,
				multiAlarmToggleSwitch);
		addHelpTextTrigger(multiAlarmGroup, RS.rbLabel("sensors.trip.multi"));
		
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
		final Label readingsHeader = new Label(RS.rbLabel("sensors.readings"));
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
			tab.setContent((T) cpc.getConstructor(Controls.class).newInstance(this));
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
		final Button reloadBtn = new Button(RS.rbLabel("reload"));
		reloadBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				webView.getEngine().reload();
			}
		});
		return reloadBtn;
	}
	
	/**
	 * Creates a service for the command
	 * 
	 * @param command the command
	 * @return the service
	 */
	public Service<Boolean> createControlsService(final Command command) {
		setHelpText(null);
		return GuiUtil.alertProgress(stage, new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				try {
					if (command == Command.SENSOR_GET_READINGS) {
						if (!UGateKeeper.DEFAULT.wirelessSendData(getRemoteNodeAddress(), 
								Command.SENSOR_GET_READINGS)) {
							setHelpText(String.format(RS.rbLabel("sensors.readings.failed"),
									(isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
										getRemoteNodeAddress())));
							return false;
						}
					} else if (command == Command.SENSOR_SET_SETTINGS) {
						if (!UGateKeeper.DEFAULT.wirelessSendSettings(
								(isPropagateSettingsToAllRemoteNodes() ? null : getRemoteNodeAddress()))) {
							setHelpText(String.format(RS.rbLabel("settings.send.failed"),
									(isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
										getRemoteNodeAddress())));
							return false;
						}
					} else if (command == Command.GATE_TOGGLE_OPEN_CLOSE) {
						if (!UGateKeeper.DEFAULT.wirelessSendData(getRemoteNodeAddress(), 
								Command.GATE_TOGGLE_OPEN_CLOSE)) {
							setHelpText(String.format(RS.rbLabel("gate.toggle.failed"),
									(isPropagateSettingsToAllRemoteNodes() ? RS.rbLabel("all") : 
										getRemoteNodeAddress())));
							return false;
						}
					} else {
						log.warn(String.format("%1$c is not a valid command for %2$s", 
								command, Controls.class.getName()));
						return false;
					}
				} catch (final Throwable t) {
					setHelpText(
							"An error occurred while executing command. See log for more details.");
					log.error("Unable to execute " + command, t);
					return false;
				}
				return true;
			}
		});
	}
	
	
	/**
	 * Adds the help text when the node is right clicked
	 * 
	 * @param node the node to trigger the text
	 * @param text the text to show
	 */
	public void addHelpTextTrigger(final Node node, final String text) {
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					helpTextPane.setVvalue(helpTextPane.getVmin());
					helpText.setText(text);
					event.consume();
				}
			}
		});
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
		helpText.setText(text == null || text.length() == 0 ? RS.rbLabel(UGateUtil.HELP_TEXT_DEFAULT_KEY) : text);
	}
	
	/**
	 * @return the remote node address of the device node for which the controls represent
	 */
	public String getRemoteNodeAddress() {
		return Settings.WIRELESS_ADDRESS_NODE_PREFIX_KEY.key + getRemoteNodeIndex();
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
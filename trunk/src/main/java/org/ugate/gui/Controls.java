package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
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
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import org.apache.log4j.Logger;
import org.ugate.gui.components.Digits;
import org.ugate.gui.components.PlateGroup;
import org.ugate.resources.RS;
import org.w3c.dom.Element;

/**
 * XBee GUI view responsible for communicating with local and remote XBees once a connection is established
 */
public class Controls extends VBox {

	private static final Logger log = Logger.getLogger(Controls.class);
	public static final double CHILD_SPACING = 10d;
	public static final double CHILD_PADDING = 5d;
	public static final Insets PADDING_INSETS = new Insets(CHILD_PADDING, CHILD_PADDING, 0, CHILD_PADDING);
	protected static final String NAVIGATE_JS = "nav";
	public final ScrollPane helpText;

	public Controls() {
		super();
		this.setStyle("-fx-background-color: #000000;");
		
		// help view
		helpText = new ScrollPane();
		helpText.setStyle("-fx-background-color: #ffffff;");
		helpText.setPrefHeight(48d);
		helpText.setPrefWidth(200d);
		final Label helpTextContent = new Label();
		helpTextContent.setWrapText(true);
		helpTextContent.setPrefWidth(helpText.getPrefWidth() - 20d);
		helpText.setContent(helpTextContent);
		
		final ToolBar mainBar = new ToolBar(createMainBarChildren());
		final TabPane mainView = new TabPane();
		mainView.setSide(Side.LEFT);
		final Tab camTab = createTab(null, "Camera/Gate", CameraGateControl.class);
		final Tab sensorsTab = createTab(null, "Sensors", SensorControl.class);
		mainView.getTabs().addAll(camTab, sensorsTab);

		HBox.setHgrow(this, Priority.ALWAYS);
		VBox.setVgrow(this, Priority.ALWAYS);
		
		getChildren().addAll(mainBar, mainView);
	}
	
	/**
	 * @return the children used for the main menu bar
	 */
	protected Node[] createMainBarChildren() {
		// add the actions
		final DropShadow ds = new DropShadow();
		final ImageView camTakeQvga = RS.imgView(RS.IMG_CAM_QVGA);
		camTakeQvga.setEffect(ds);
		final ImageView camTakeVga = RS.imgView(RS.IMG_CAM_VGA);
		camTakeVga.setEffect(ds);
		final ImageView settingsSet = RS.imgView(RS.IMG_SETTINGS_SET);
		settingsSet.setEffect(ds);
		final ImageView readingsGet = RS.imgView(RS.IMG_READINGS_GET);
		
		// add the readings view
		final ImageView sonarReadingLabel = RS.imgView(RS.IMG_SONAR);
		final Digits sonarReading = new Digits(String.format(SensorControl.FORMAT_SONAR, 7.5f),
				0.15f, SensorControl.COLOR_SONAR, null);
		final ImageView pirReadingLabel = RS.imgView(RS.IMG_PIR);
		final Digits pirReading = new Digits(String.format(SensorControl.FORMAT_PIR, 15.5f), 
				0.15f, SensorControl.COLOR_PIR, null);
		final ImageView mwReadingLabel = RS.imgView(RS.IMG_MICROWAVE);
		final Digits mwReading = new Digits(String.format(SensorControl.FORMAT_MW, 7.5f), 0.15f, 
				SensorControl.COLOR_MW, null);
		final Group readingsGroup = createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 10,
				sonarReadingLabel, sonarReading, pirReadingLabel, pirReading, mwReadingLabel, mwReading);
		
		// add the multi-alarm trip state
		final ImageView sonarMultiAlarmBtn = RS.imgView(RS.IMG_SONAR_ALARM_ON);
		final ImageView pirMultiAlarmBtn = RS.imgView(RS.IMG_IR_ALARM_ON);
		final ImageView mwMultiAlarmBtn = RS.imgView(RS.IMG_MICROWAVE_ALARM_ON);
		final Group multiAlarmGroup = createReadingsDisplay(PADDING_INSETS, CHILD_SPACING, 10,
				sonarMultiAlarmBtn, pirMultiAlarmBtn, mwMultiAlarmBtn);
		
		// add the menu items
		return new Node[] { camTakeQvga, camTakeVga, settingsSet, readingsGet, 
				new Separator(Orientation.VERTICAL), readingsGroup, 
				new Separator(Orientation.VERTICAL), multiAlarmGroup,
				new Separator(Orientation.VERTICAL), helpText};
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
			tab.setContent((T) cpc.getConstructor(helpText.getClass()).newInstance(helpText));
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
}
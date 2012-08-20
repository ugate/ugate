package org.ugate.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.gui.view.AccessSettings;
import org.ugate.gui.view.AlarmSettings;
import org.ugate.gui.view.PositionSettings;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEYS;
import org.w3c.dom.Element;

/**
 * Camera/Sensor controls
 */
public class Controls extends TabPane {

	private static final Logger log = LoggerFactory.getLogger(Controls.class);
	protected static final String NAVIGATE_JS = "nav";

	/**
	 * Constructor
	 * 
	 * @param controlBar the control bar
	 */
	public Controls(final ControlBar controlBar) {
		super();
		
		// add the main tabs
		setSide(Side.RIGHT);
		final Tab remoteAccessTab = createTab(null, RS.rbLabel(KEYS.LABEL_REMOTE_ACCESS), AccessSettings.class, controlBar);
		final Tab alarmSettingsTab = createTab(null, RS.rbLabel(KEYS.LABEL_ALARM_SETTINGS), AlarmSettings.class, controlBar);
		final Tab positioningTab = createTab(null, RS.rbLabel(KEYS.LABEL_POS_SETTINGS), PositionSettings.class, controlBar);
		getTabs().addAll(remoteAccessTab, alarmSettingsTab, positioningTab);
	}
	
	/**
	 * Creates a tab
	 * 
	 * @param graphicFileName the optional graphic for the tab
	 * @param text the text for the tab
	 * @param cpc the class used for the tab content
	 * @param controlBar the {@linkplain ControlBar} to pass to the constructor
	 * @return the tab
	 */
	protected <T extends ControlPane> Tab createTab(final String graphicFileName, final String text, 
			final Class<T> cpc, final ControlBar controlBar) {
		final Tab tab = new Tab(text);
		tab.setClosable(false);
		try {
			tab.setContent((T) cpc.getConstructor(ControlBar.class).newInstance(controlBar));
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
		final Button reloadBtn = new Button(RS.rbLabel(KEYS.RELOAD));
		reloadBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				webView.getEngine().reload();
			}
		});
		return reloadBtn;
	}
}
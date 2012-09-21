package org.ugate.gui.view;

import org.ugate.gui.ControlBar;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

/**
 * Web browser UI builder to build web browser content
 */
public class WebBuilder extends BorderPane {

	private final WebView webView;

	/**
	 * Constructor
	 * 
	 * @param controlBar
	 *            the {@linkplain ControlBar}
	 */
	public WebBuilder(final ControlBar controlBar) {
		webView = createWebView(null, null, null, false);
		setCenter(webView);
	}

	/**
	 * Creates a controls web view. When the view is loaded the images will be
	 * updated with the ones provided
	 * 
	 * @param fileName
	 *            the HTML file name to load
	 * @param navResultImgSrc
	 *            the navigation result image source
	 * @param navImgSrc
	 *            the navigation image source
	 * @param isCam
	 *            true if the web view is for the camera
	 * @return the web view
	 */
	protected WebView createWebView(final String fileName,
			final String navResultImgSrc, final String navImgSrc,
			final boolean isCam) {
		final WebView webView = new WebView();
		webView.getEngine().getLoadWorker().stateProperty()
				.addListener(new ChangeListener<State>() {
					@Override
					public void changed(
							ObservableValue<? extends State> observable,
							State oldValue, State newValue) {
						if (newValue == State.SUCCEEDED
								&& webView.getEngine().getDocument() != null) {
//							final Element navImg = webView.getEngine()
//									.getDocument().getElementById("navImg");
//							/*
//							 * link =item.getAttributes().getNamedItem("src").
//							 * getTextContent();
//							 */
//							navImg.getAttributes().getNamedItem("src")
//									.setNodeValue(navImgSrc);
//							final Element navResultImg = webView.getEngine()
//									.getDocument()
//									.getElementById("navResultImg");
//							navResultImg.getAttributes().getNamedItem("src")
//									.setNodeValue(navResultImgSrc);
						}
					}
				});
		webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> event) {
//				if (event.getData().indexOf(NAVIGATE_JS) > -1) {
//
//				}
//				log.debug(event.getData());
			}
		});
		//webView.getEngine().load("http://codiqa.com/embed/editor");
		return webView;
	}
}

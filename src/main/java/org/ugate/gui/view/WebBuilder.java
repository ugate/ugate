package org.ugate.gui.view;

import java.io.StringWriter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ugate.gui.ControlBar;
import org.ugate.resources.RS;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.RemoteNodeType;

/**
 * Web browser UI builder to build web browser content
 */
public class WebBuilder extends BorderPane {

	private final ToolBar toolBar;
	private final WebView webView;
	private final ControlBar cb;
	private boolean isToolBarLoaded;

	/**
	 * Constructor
	 * 
	 * @param controlBar
	 *            the {@linkplain ControlBar}
	 */
	public WebBuilder(final ControlBar controlBar) {
		webView = createWebView(null, null, null, false);
		cb = controlBar;
		toolBar = new ToolBar();
		toolBar.setOrientation(Orientation.VERTICAL);
		setLeft(toolBar);
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
							getCurrentPageSource();
//							webView.getEngine().executeScript("complete()");
//					        try {
//						        final DocumentFragment frag =  webView.getEngine().getDocument().createDocumentFragment();
//						        frag.setNodeValue(getContent(true));
//								final HTMLElement html = (HTMLElement) webView.getEngine().getDocument().getElementsByTagName("body").item(0);
//								html.appendChild(frag);
//					        } catch (Throwable t) {
//					            t.printStackTrace();
//					        }
//							/*
//							 * link =item.getAttributes().getNamedItem("src").
//							 * getTextContent();
//							 */
//							body.getAttributes().getNamedItem("src")
//									.setNodeValue(navImgSrc);
//							final Element navResultImg = webView.getEngine()
//									.getDocument()
//									.getElementById("navResultImg");
//							navResultImg.getAttributes().getNamedItem("src")
//									.setNodeValue(navResultImgSrc);
						}
					}
				});
		webView.getEngine().getLoadWorker().exceptionProperty()
				.addListener(new ChangeListener<Throwable>() {
					@Override
					public void changed(
							final ObservableValue<? extends Throwable> observableValue,
							final Throwable oldThrowable, final Throwable newThrowable) {
						System.out.println("Load exception: " + newThrowable);
					}
				});
		webView.getEngine().setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
			@Override
			public WebEngine call(PopupFeatures popupFeatures) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		webView.getEngine().setPromptHandler(new Callback<PromptData, String>() {
			@Override
			public String call(final PromptData promptData) {
				//promptData.
				return null;
			}
		});
		webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> event) {
				return;
//				if (event.getData().indexOf(NAVIGATE_JS) > -1) {
//
//				}
//				log.debug(event.getData());
			}
		});
		return webView;
	}

	/**
	 * Loads the {@linkplain WebView} content
	 */
	public void load() {
		if (loadToolBar()) {
			webView.getEngine().load(
					"http://" + cb.getActor().getHost().getWebHostLocal() + ':'
							+ cb.getActor().getHost().getWebPortLocal());
		}
	}

	protected boolean loadToolBar() {
		if (!ServiceProvider.IMPL.getWebService().isRunning()) {
			toolBar.getItems().clear();
			toolBar.getItems().add(new Label("Web server must be started to view/edit the web view"));
			return false;
		}
		return true;
	}

	protected String getContent(final boolean justBodyContent) {
		String content = RS.getEscapedResource("index.html", null,
				cb.getRemoteNode(), RemoteNodeType.values());
		content = RS.getEscapedContent(content, null, cb.getActor(),
				ActorType.values());
		if (justBodyContent) {
			content = RS.getHtmlBodyContent(content);
		}
		return content;
	}
	
	protected String getCurrentPageSource() {
        try {
        	final DOMSource source = new DOMSource(webView.getEngine().getDocument());
            try (final StringWriter stringWriter = new StringWriter()) {
                final Result result = new StreamResult(stringWriter);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(source, result);
                return stringWriter.getBuffer().toString();
            }
        } catch (final Throwable t) {
            t.printStackTrace();
            return null;
        }
	}
}

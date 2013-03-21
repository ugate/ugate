package org.ugate.gui;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.slf4j.Logger;
import org.ugate.UGateEvent;
import org.ugate.UGateEvent.Type;
import org.ugate.UGateKeeper;
import org.ugate.UGateUtil;
import org.ugate.gui.components.AppFrame;
import org.ugate.gui.components.BeanPathAdapter;
import org.ugate.gui.components.DisplayShelf;
import org.ugate.gui.components.UGateStartupDialog;
import org.ugate.gui.view.EmailHostConnection;
import org.ugate.gui.view.HostConnection;
import org.ugate.gui.view.RemoteNodes;
import org.ugate.gui.view.SensorReadingHistory;
import org.ugate.gui.view.WebBuilder;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.ActorType;
import org.ugate.service.entity.EntityExtractor;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.RemoteNode;

/**
 * The main GUI application entry point
 */
public class UGateGUI extends Application {

	private static final Logger log = UGateUtil.getLogger(UGateGUI.class);

	public static final double APPLICATION_WIDTH = 1000d;
	public static final double APPLICATION_HEIGHT = 825d;

	private static final double TASKBAR_HEIGHT = 130d;
	private static final double TASKBAR_BUTTON_WIDTH = 100d;
	private static final double TASKBAR_BUTTON_HEIGHT = 100d;

	protected final HBox taskbar = new HBox(10d);
	protected final HBox connectionView = new HBox(10d);
	protected ControlBar controlBar;
	protected EmailHostConnection mailConnectionView;
	protected HostConnection wirelessConnectionView;
	protected SensorReadingHistory sensorReadingHistoryView;
	protected WebBuilder webSetupView;
	protected StackPane centerView;
	protected AppFrame applicationFrame;
	protected final IntegerProperty taskBarSelectProperty = new SimpleIntegerProperty(0);
	private String initErrorHeader;
	private static StringBuffer initErrors;
	private final BeanPathAdapter<Actor> actorPA;
	private final BeanPathAdapter<RemoteNode> remoteNodePA;

	/**
	 * Constructor
	 */
	public UGateGUI() {
		notifyPreloader(new ProgressNotification(0.01d));
		// TextAreaAppender.setTextArea(webSetupView);
		actorPA = new BeanPathAdapter<Actor>(ActorType.newDefaultActor());
		remoteNodePA = new BeanPathAdapter<RemoteNode>(
				RemoteNodeType
						.newDefaultRemoteNode(actorPA.getBean().getHost()));
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		try {
			Application.launch(UGateGUI.class, args);
		} catch (final Throwable t) {
			log.error("Unable to launch GUI", t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		log.debug("Initializing GUI...");
		notifyPreloader(new ProgressNotification(0.1d));
		logStartStop(true);
	}

	/**
	 * Adds initialization error message(s)
	 * 
	 * @param messages
	 *            the message(s) to add
	 */
	private void addInitError(final String... messages) {
		if (initErrors == null) {
			initErrors = new StringBuffer();
		}
		for (final String msg : messages) {
			initErrors.append(msg);
			initErrors.append('\n');
		}
	}

	/**
	 * Initializes the {@link ServiceProvider}
	 */
	private void initServices() {
		try {
			controlBar = new ControlBar(actorPA, remoteNodePA);
			final boolean srvcSuccess = ServiceProvider.IMPL
					.init(new EntityExtractor<Actor>() {

						@Override
						public Actor extract() {
							// dynamic extraction of current actor
							return actorPA.getBean();
						}
					});
			if (!srvcSuccess
					&& ServiceProvider.IMPL.getWirelessService()
							.isRequiresRestart()) {
				initErrorHeader = RS.rbLabel(KEY.APP_TITLE_ACTION_REQUIRED);
				addInitError(RS.rbLabel(KEY.APP_SERVICE_COM_RESTART_REQUIRED));
			}
			// Text.setFontSmoothingType(FontSmoothingType.LCD);
		} catch (final Throwable t) {
			log.error("Unable to start services", t);
			try {
				addInitError(RS.rbLabel(KEY.APP_SERVICE_INIT_ERROR), t.getMessage());
			} catch (final Throwable t2) {
				log.error("Unable notify user that services failed to start", t2);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		try {
			log.debug("Starting GUI...");
			RS.img(stage);
			notifyPreloader(new ProgressNotification(0.1d));

			stage.setTitle(RS.rbLabel(KEY.APP_TITLE));

			log.debug("Iniitializing Services...");
			notifyPreloader(new ProgressNotification(0.2d));
			initServices();

			if (initErrors != null && initErrors.length() > 0) {
				final TextArea errorDetails = TextAreaBuilder.create().text(initErrors.toString()).wrapText(true
						).focusTraversable(false).editable(false).opacity(0.7d).build();
				if (initErrorHeader == null) {
					initErrorHeader = RS.rbLabel(KEY.APP_TITLE_ERROR);
				}
				final GuiUtil.DialogService dialogService = GuiUtil.dialogService(stage, KEY.APP_TITLE, initErrorHeader, 
						KEY.CLOSE, 550d, 300d, new Service<Void>() {
					@Override
					protected Task<Void> createTask() {
						return new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								// if the dialog shouldn't be closed call super.cancel()
								return null;
							}
						};
					}
				}, null, errorDetails);
				dialogService.getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
					@Override
					public void handle(final WindowEvent event) {
						notifyPreloader(new ProgressNotification(1d));
						dialogService.getStage().removeEventHandler(WindowEvent.WINDOW_SHOWN, this);
					}
				});
				notifyPreloader(new ProgressNotification(0.5d));
				dialogService.start();
				notifyPreloader(new StateChangeNotification(
						StateChangeNotification.Type.BEFORE_START));
				notifyPreloader(new ProgressNotification(0.75d));
			} else {
				// start the main GUI
				primaryStageStart(stage);
			}
			
			log.debug("GUI started");
		} catch (final Throwable t) {
			log.error("Unable to start GUI", t);
			throw new RuntimeException("Unable to start GUI", t);
		}
	}

	/**
	 * Builds the application components on the primary {@link Stage}
	 * 
	 * @param stage
	 *            the primary {@link Stage}
	 */
	protected void primaryStageBuild(final Stage stage) {
		controlBar.setStage(stage);
		final BorderPane content = new BorderPane();
		content.setId("main-content");
		content.setEffect(new InnerShadow());

		taskbar.setId("taskbar");
		taskbar.setCache(true);
		taskbar.setPrefHeight(TASKBAR_HEIGHT);

		centerView = new StackPane();
		centerView.setId("center-view");

		final Controls controls = new Controls(controlBar);

		wirelessConnectionView = new HostConnection(controlBar);
		mailConnectionView = new EmailHostConnection(controlBar);

		connectionView.setId("connection-view");
		connectionView.getChildren().addAll(wirelessConnectionView, createSeparator(Orientation.VERTICAL), mailConnectionView);

		final VBox bottom = new VBox();
		bottom.setId("bottom-view");
		bottom.getChildren().addAll(taskbar, new RemoteNodes(controlBar, Orientation.HORIZONTAL));

		content.setCenter(centerView);
		content.setBottom(bottom);
		content.setTop(controlBar);
		
		sensorReadingHistoryView = new SensorReadingHistory(controlBar);
		webSetupView = new WebBuilder(controlBar);

		taskbar.getChildren().add(genTaskbarItem(RS.IMG_CONNECT, RS.rbLabel(KEY.APP_CONNECTION_DESC), 0, new Runnable() {
			@Override
			public void run() {
				changeCenterView(connectionView, 0);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_WIRELESS, RS.rbLabel(KEY.APP_CONTROLS_DESC), 1, new Runnable() {
			@Override
			public void run() {
				changeCenterView(controls, 1);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_PICS, RS.rbLabel(KEY.APP_CAPTURE_DESC), 2, new Runnable() {
			@Override
			public void run() {
				changeCenterView(new DisplayShelf(new File(controlBar.getRemoteNode().getWorkingDir()), 350, 350, 0.25, 45, 80, 
						DisplayShelf.TOOLBAR_POSITION_TOP, RS.rbLabel(KEY.LABEL_DISPLAYSHELF_FULLSIZE_DESC)), 2);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_GRAPH, RS.rbLabel(KEY.LABEL_GRAPH_DESC), 3, new Runnable() {
			@Override
			public void run() {
				changeCenterView(sensorReadingHistoryView, 3);
			}
		}));
		taskbar.getChildren().add(genTaskbarItem(RS.IMG_WEB, RS.rbLabel(KEY.APP_WEB_TOOL_DESC), 4, new Runnable() {
			@Override
			public void run() {
				webSetupView.load();
				changeCenterView(webSetupView, 4);
			}
		}));
		final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		final double appWidth = Math.min(bounds.getWidth(), APPLICATION_WIDTH);
		final double appHeight = Math.min(bounds.getHeight(), APPLICATION_HEIGHT);
		applicationFrame = new AppFrame(stage, content, appWidth, appHeight, appWidth + 10d, 
				appHeight + 10d, false, new String[] { RS.path(RS.CSS_MAIN), RS.path(RS.CSS_DISPLAY_SHELF) }, 
				controlBar.createTitleBarItems());
		changeCenterView(connectionView, 0);
	}
	/**
	 * Starts the primary {@linkplain Stage} but does not call {@linkplain Stage#show()}
	 * 
	 * @param stage the primary {@linkplain Stage}
	 */
	protected void primaryStageStart(final Stage stage) {
		Platform.runLater(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				notifyPreloader(new ProgressNotification(0.5d));
				primaryStageBuild(stage);
				notifyPreloader(new ProgressNotification(0.75d));
				SystemTray.initSystemTray(stage);
				return null;
			}
		});
		UGateStartupDialog.DFLT.start(new UGateStartupDialog.StartupHandler() {
			@Override
			public void handle(final Stage stage, final Actor actor) {
				primaryStageShow(stage, actor);
			}
			@Override
			public ControlBar getControlBar() {
				return controlBar;
			}
		}, this, stage);
		notifyPreloader(new StateChangeNotification(
				StateChangeNotification.Type.BEFORE_START));
	}

	/**
	 * Shows the primary {@linkplain Stage} and shows the
	 * {@linkplain SystemTray}
	 * 
	 * @param stage
	 *            the primary {@linkplain Stage}
	 * @param authActor
	 *            the authorized {@linkplain Actor}
	 */
	protected void primaryStageShow(final Stage stage, final Actor authActor) {
		notifyPreloader(new ProgressNotification(0.85d));
		log.info("Showing Main GUI");
		if (authActor == null) {
			throw new IllegalArgumentException(
					"Cannot show application without an authenticated user");
		}
		stage.addEventFilter(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
			@Override
			public void handle(final WindowEvent event) {
				notifyPreloader(new ProgressNotification(1d));
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						actorPA.setBean(authActor);
						remoteNodePA.setBean(authActor.getHost().getRemoteNodes()
								.iterator().next());
						UGateKeeper.DEFAULT.notifyListeners(new UGateEvent<>(this,
								Type.APP_DATA_LOADED, false));
					}
				});
				stage.removeEventHandler(WindowEvent.WINDOW_SHOWN, this);
			}
		});
		stage.show();
		notifyPreloader(new ProgressNotification(0.9d));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() throws Exception {
		log.info("Exiting application...");
		try {
			ServiceProvider.IMPL.disconnect();
		} catch (final Throwable t) {
			log.error(
					"Unable to disconnect from "
							+ ServiceProvider.class.getName(), t);
		}
		try {
			SystemTray.exit();
			logStartStop(false);
		} catch (final Throwable t) {
			log.error("Unable to exit the system tray", t);
		}
		Platform.exit();
		// System.exit(0);
	}

	private static void logStartStop(final boolean start) {
		final String msg = start ? "Application Starting" : "Application Exited";
		UGateUtil.PLAIN_LOGGER
				.info("=============================================" + msg
						+ "=============================================");
	}

	private Separator createSeparator(final Orientation orientation) {
		Separator topSeparator = new Separator();
		topSeparator.setOrientation(orientation);
		topSeparator.setHalignment(HPos.CENTER);
		topSeparator.setValignment(VPos.CENTER);
		return topSeparator;
	}

	protected ImageView genTaskbarItem(final String iconName, final String helpText, final int index, final Runnable action) {
		return genFisheye(iconName, TASKBAR_BUTTON_WIDTH, TASKBAR_BUTTON_HEIGHT, helpText, index, taskBarSelectProperty, action);
	}

	public ImageView genFisheye(final String iconName, final double width, final double height, final String helpText, final int index, final IntegerProperty selectProperty, final Runnable action) {
		final ImageView node = RS.imgView(iconName);
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		node.setFitWidth(width);
		node.setFitHeight(height);

		final DropShadow effect = DropShadowBuilder.create().color(selectProperty != null && selectProperty.get() == index ? GuiUtil.COLOR_SELECTED : Color.TRANSPARENT).build();
		final Reflection effect2 = new Reflection();
		effect.setInput(effect2);
		node.setEffect(effect);
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					action.run();
				} else {
					controlBar.setHelpText(helpText);
				}
			}
		});
		node.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (selectProperty == null || selectProperty.get() != index) {
					effect.setColor(GuiUtil.COLOR_SELECTING);
				}
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (selectProperty == null || selectProperty.get() != index) {
					effect.setColor(Color.TRANSPARENT);
				} else if (selectProperty.get() == index) {
					effect.setColor(GuiUtil.COLOR_SELECTED);
				}
			}
		});
		if (selectProperty != null) {
			selectProperty.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
					if (newValue.intValue() == index) {
						effect.setColor(GuiUtil.COLOR_SELECTED);
					} else {
						effect.setColor(Color.TRANSPARENT);
					}
				}
			});
		}
		return node;
	}

	/**
	 * Changes the center view above the task bar menu
	 * 
	 * @param node
	 *            the node to change the center view to
	 * @param index
	 *            the index of the task bar item
	 */
	protected void changeCenterView(final Node node, final int index) {
		centerView.getChildren().setAll(node);
		taskBarSelectProperty.set(index);
	}
}

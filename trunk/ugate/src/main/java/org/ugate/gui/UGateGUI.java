package org.ugate.gui;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.builders.ColorAdjustBuilder;
import javafx.builders.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Separator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.apache.log4j.Logger;

import com.javafx.preview.control.TextArea;

/**
 * The main GUI application entry point
 */
public class UGateGUI extends Application {

	private static final Logger log = Logger.getLogger(UGateGUI.class);
	
    private static final double TASKBAR_BUTTON_SCALE = 1.3;
    private static final double TASKBAR_BUTTON_DURATION = 300;
    private static final double TASKBAR_BUTTON_WIDTH = 100;
    private static final double TASKBAR_BUTTON_HEIGHT = 100;

    public static final AudioClip mediaPlayerConfirm = createResourceAudioClip("x_confirm.wav");
    public static final AudioClip mediaPlayerDoorBell = createResourceAudioClip("x_doorbell.wav");
    public static final AudioClip mediaPlayerCam = createResourceAudioClip("x_cam.wav");
    public static final AudioClip mediaPlayerComplete = createResourceAudioClip("x_complete.wav");
    public static final AudioClip mediaPlayerError = createResourceAudioClip("x_error.wav");
    public static final AudioClip mediaPlayerBlip = createResourceAudioClip("x_blip.wav");
	private final StackPane centerView = new StackPane();
	private final HBox taskbar = new HBox(10);
	private final HBox connectionView = new HBox(10);
	private final TextArea loggingView = new TextArea();
	private MailConnectionView mailConnectionView;
	private XBeeConnectionView xbeeConnectionView;
	private XBeeRxTxView xbeeRxTxView;
	private boolean xbeeConnected = false;
	private boolean mailConnected = false;
	
	public UGateGUI() {
		TextAreaAppender.setTextArea(loggingView);
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Set up a simple configuration that logs on the console.
			//BasicConfigurator.configure();
			//PropertyConfigurator.configure(UGateGUI.class.getResource("log4j.xml").getPath());
			Application.launch(UGateGUI.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	    //public final AudioClip mediaPlayerBeep = new AudioClip(UGateGUI.class.getResource("x_beep.wav").getPath());
	    xbeeConnectionView = new XBeeConnectionView() {

			@Override
			public void handleStatusChange(Boolean on) {
				xbeeConnected = on;
				if (!xbeeConnected) {
					changeCenterView(connectionView, false);
				}
			}
		};
		mailConnectionView = new MailConnectionView() {

			@Override
			public void handleStatusChange(Boolean on) {
				mailConnected = on;
				if (!mailConnected) {
					changeCenterView(connectionView, false);
				}
			}
		};
	    xbeeRxTxView = new XBeeRxTxView();

		log.debug("Creating GUI...");
		BorderPane root = new BorderPane();
		
	    Scene scene = new Scene(root, 720, 550, Color.LIGHTGRAY);
	    scene.getStylesheets().add("/org/ugate/gui/main.css");
	    primaryStage.setTitle("UGate Application Interface");
	    
        taskbar.setAlignment(Pos.CENTER);
        taskbar.setPadding(new Insets(10, 10, 50, 10));
        taskbar.setPrefHeight(100);

        centerView.setStyle("-fx-background-color: #ffffff;");
        centerView.setPadding(new Insets(10, 10, 70, 10));
	    connectionView.setAlignment(Pos.CENTER);
	    changeCenterView(connectionView, false);
	    
	    connectionView.getChildren().addAll(xbeeConnectionView, createSeparator(Orientation.VERTICAL), 
	    		mailConnectionView);

	    root.setCenter(centerView);
	    root.setBottom(taskbar);

        taskbar.getChildren().add(createConnectionStatusView(createTaskbarButton("power-red.png", new Runnable() {

            public void run() {
            	changeCenterView(connectionView, true);
            }
        })));
        taskbar.getChildren().add(createTaskbarButton("wireless-globe.png", new Runnable() {

            public void run() {
            	changeCenterView(xbeeRxTxView, true);
            }
        }));
        taskbar.getChildren().add(createTaskbarButton("graph.png", new Runnable() {

            public void run() {
                NumberAxis xAxis = new NumberAxis();
                NumberAxis yAxis = new NumberAxis();
                LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
                chart.setTitle("XBee Notifications");
                xAxis.setLabel("Hour");
                yAxis.setLabel("Occurrences");
                XYChart.Series<Number, Number> sonarTrippedSeries = new XYChart.Series<Number, Number>();
                sonarTrippedSeries.setName("Tripped Image Snapshot");
                Random random = new Random();
                for (int i = 0; i < 10 + random.nextInt(20); i++) {
                    sonarTrippedSeries.getData().add(new XYChart.Data<Number, Number>(10 * i + 10, random.nextDouble() * 150));
                }
                chart.getData().add(sonarTrippedSeries);
                XYChart.Series<Number, Number> manualImageSeries = new XYChart.Series<Number, Number>();
                manualImageSeries.setName("Manually Initiated Image Snapshots");
                for (int i = 0; i < 10 + random.nextInt(20); i++) {
                	manualImageSeries.getData().add(new XYChart.Data<Number, Number>(10 * i + 10, random.nextDouble() * 150));
                }
                chart.getData().add(manualImageSeries);
                changeCenterView(chart, true);
            }
        }));
        taskbar.getChildren().add(createTaskbarButton("logs.png", new Runnable() {

            public void run() {
            	loggingView.setEditable(false);
                changeCenterView(loggingView, true);
            }
        }));
	    
	    primaryStage.addEventHandler(WindowEvent.ANY, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if (event.getEventType() == WindowEvent.WINDOW_SHOWN) {
					// load the COM ports
					xbeeConnectionView.loadComPorts();
					Platform.runLater(new Runnable() {
						public void run() {
							// attempt connections
							xbeeConnectionView.getStatusHandler().handle(null);
							mailConnectionView.getStatusHandler().handle(null);
						}
					});
				} else if (event.getEventType() == WindowEvent.WINDOW_HIDING) {
					xbeeConnectionView.disconnect();
					mailConnectionView.disconnect();
				}
			}
		});
	    primaryStage.setScene(scene);
		primaryStage.setVisible(true);
		log.debug("GUI created");
	}
	
	private Separator createSeparator(Orientation orientation) {
	    Separator topSeparator = new Separator();
	    topSeparator.setOrientation(orientation);
	    topSeparator.setHalignment(HPos.CENTER);
	    topSeparator.setValignment(VPos.CENTER);
	    return topSeparator;
	}

	/*
	private void setupSWT() {
		//ugateGUI = new UGateGUI();
		ugateGUI.tabFolder.setEnabled(false);
		ugateGUI.shell.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent event) {

			}
		});
		ugateGUI.shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				if (xbee != null && xbee.isConnected()) {
					xbee.close();
				}
				mailProvider.disconnect();
			}
		});
		ugateGUI.lblComPort.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ugateGUI.comPortCombo.setItems(getAvailablePorts());
			}
		});
		ugateGUI.comPortCombo.setItems(getAvailablePorts());
		ugateGUI.baudRateCombo.setItems(new String[] { "1200", "2400", "4800", "9600", "19200", "38400", "57600", "115200", "230400" });
		ugateGUI.activityCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ugateGUI.refreshRecentTableViewerData();
			}
		});
		ugateGUI.btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent event) {
				if (ugateGUI.comPortCombo.getText().length() > 0 && ugateGUI.baudRateCombo.getText().length() > 0) {
					try {
						if (xbee.isConnected()) {
							xbee.close();
						}
						xbee.open(ugateGUI.comPortCombo.getText().toString(), Integer.parseInt(ugateGUI.baudRateCombo.getText().toString()));
						xbee.addPacketListener(UGateMain.getInstance());
						log.info("Connected");
						ugateGUI.tabFolder.setEnabled(true);
					} catch (XBeeException e) {
						log.info("Unable to connect to local XBee", e);
						ugateGUI.tabFolder.setEnabled(false);
					}
				}
			}
		});
		ugateGUI.btnGetRemoteAddy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent event) {
				if (xbee != null && xbee.isConnected()) {
					// getAddress(false);
					getAddress(true);
				}
			}
		});
		ugateGUI.btnSendText.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent event) {
			}

			@Override
			public void mouseDown(MouseEvent event) {
				if (ugateGUI.testText.getText().length() > 0) {
					sendData(ARDUINO_GATE, ugateGUI.testText.getText());// sendData(new int[] {'H', 'i'});
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}
		});
		ugateGUI.imagePathText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				// serialService.writeSerial(ugate.loadedImageCache);
				ugateGUI.loadedImageCache = null;
			}
		});
		// open is blocking
		executor.execute(new Runnable(){
			public void run() {
				ugateGUI.open(new Runnable() {
					public void run() {
						TextAreaAppender.setTextArea(ugateGUI.logText);
					}
				});
			}
		});
		mailProvider = new MailProvider();
		mailProvider.connectGmail(username, password, false);
	}*/
	
	private Node createConnectionStatusView(Node connectionButton) {
		final VBox node = new VBox();
		final HBox statusNode = new HBox(10);
		statusNode.setAlignment(Pos.CENTER);
		statusNode.getChildren().add(xbeeConnectionView.statusIcon);
		statusNode.getChildren().add(mailConnectionView.statusIcon);
		node.getChildren().add(connectionButton);
		node.getChildren().add(statusNode);
		return node;
	}
	
    private Node createTaskbarButton(String iconName, final Runnable action) {
        final ImageView node = new ImageView(new Image(getClass().getResource(iconName).toString()));
        node.setSmooth(true);
        node.setFitWidth(TASKBAR_BUTTON_WIDTH);
        node.setFitHeight(TASKBAR_BUTTON_HEIGHT);

        final ScaleTransition animationGrow = new ScaleTransition(Duration.valueOf(TASKBAR_BUTTON_DURATION), node);
        animationGrow.setToX(TASKBAR_BUTTON_SCALE);
        animationGrow.setToY(TASKBAR_BUTTON_SCALE);

        final ScaleTransition animationShrink = new ScaleTransition(Duration.valueOf(TASKBAR_BUTTON_DURATION), node);
        animationShrink.setToX(1);
        animationShrink.setToY(1);

        final Reflection effect = new Reflection();
        node.setEffect(effect);
   
        final ColorAdjust effectPressed = new ColorAdjustBuilder().hue(20).brightness(-0.5).build();

        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
        	@Override
            public void handle(MouseEvent event) {
                effect.setInput(effectPressed);
                new TimelineBuilder().keyFrames(new KeyFrame(Duration.valueOf(300), new EventHandler<ActionEvent>() {
                	@Override
                    public void handle(ActionEvent event) {
                        effect.setInput(null);
                    }
                })).build().play();
                action.run();
            }
        });
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
        	@Override
            public void handle(MouseEvent event) {
                node.toFront();
                animationShrink.stop();
                animationGrow.playFromStart();
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
        	@Override
            public void handle(MouseEvent event) {
                animationGrow.stop();
                animationShrink.playFromStart();
            }
        });
        return node;
    }
    
    private void changeCenterView(Node node, boolean checkConnection) {
    	if (!checkConnection || (xbeeConnected && mailConnected)) {
            centerView.getChildren().clear();
            centerView.getChildren().add(node);
    	}
    }
    
    private static AudioClip createResourceAudioClip(String fileName) {
    	return new AudioClip(UGateGUI.class.getResource(fileName).getPath().replace("/C", "file"));
    }
}

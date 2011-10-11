package org.ugate.gui.components;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.ugate.resources.RS;

import com.sun.javafx.Utils;

/**
 * Application frame that takes place of the typical native window look and feel.
 * The content will be sized to fit in the frame.
 */
public class AppFrame extends StackPane {

	public static final double TOP_LEFT_WIDTH = 90;
	public static final double TOP_RIGHT_WIDTH = 140;
	public static final double TOP_BORDER_HEIGHT = 80;
	public static final double TOP_BORDER_CONTENT_ADJUSTMENT = 10;
	public static final double BOTTOM_LEFT_WIDTH = 70;
	public static final double BOTTOM_RIGHT_WIDTH = 70;
	public static final double BOTTOM_BORDER_HEIGHT = 40;
	public static final double BOTTOM_BORDER_CONTENT_ADJUSTMENT = 11;
	public static final double LEFT_BORDER_WIDTH = 1;
	public static final double RIGHT_BORDER_WIDTH = 1;
	public static final double TOP_MIN_MAX_CLOSE_ADJUSTMENT = 5;
	public static final double LOGO_X = 20;
	public static final double LOGO_Y = 0;
	private Rectangle2D backupWindowBounds;
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;
    private boolean isExiting = false;

	public AppFrame(final Stage stage, final Region content, final double sceneWidth, final double sceneHeight,
			final double minResizableWidth, final double minResizableHeight) {
		// stage/scene adjustment for transparency/size
		this.setMinSize(minResizableWidth, minResizableHeight);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(new Scene(this, sceneWidth, sceneHeight, Color.TRANSPARENT));
		setAlignment(Pos.TOP_LEFT);
	    
	    // logo
	    final ImageView logoView = RS.imgView(RS.IMG_LOGO_64);
	    logoView.setTranslateX(LOGO_X);
	    logoView.setTranslateY(LOGO_Y);
//	    final ScaleTransition logoViewEffect = new ScaleTransition(Duration.seconds(4), logoView);
//	    logoViewEffect.setByX(0.5f);
//	    logoViewEffect.setByY(0.5f);
//	    logoViewEffect.setCycleCount(1);
//	    logoView.setOnMouseEntered(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				if (logoViewEffect.getStatus() != javafx.animation.Animation.Status.RUNNING) {
//					logoViewEffect.stop();
//				}
//				logoViewEffect.setRate(1);
//				logoViewEffect.play();
//			}
//		});
//	    logoView.setOnMouseExited(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				if (logoViewEffect.getStatus() == javafx.animation.Animation.Status.RUNNING) {
//					logoViewEffect.stop();
//					logoViewEffect.setRate(-1);
//					logoViewEffect.play();
//				}
//			}
//		});
	    
	    // title bar
	    final HBox titleBar = new HBox();
	    //titleBar.setStyle("-fx-background-color: black;");
	    titleBar.setPrefHeight(TOP_BORDER_HEIGHT);
	    titleBar.setMaxHeight(TOP_BORDER_HEIGHT);
	    titleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                mouseDragOffsetX = event.getSceneX();
                mouseDragOffsetY = event.getSceneY();
            }
        });
	    titleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - mouseDragOffsetX);
                stage.setY(event.getScreenY() - mouseDragOffsetY);
            }
        });
        final Region titleBarLeft = new Region();
        titleBarLeft.setId("title-bar-left");
        titleBarLeft.setPrefWidth(TOP_LEFT_WIDTH);
        titleBarLeft.setMaxWidth(TOP_LEFT_WIDTH);
	    final Region titleBarCenter = new Region();
	    titleBarCenter.setId("title-bar");
        HBox.setHgrow(titleBarCenter, Priority.ALWAYS);
	    final HBox titleBarRight = newMinMaxClose(stage);
	    titleBarRight.setId("title-bar-right");
	    titleBarRight.setPrefWidth(TOP_RIGHT_WIDTH);
        titleBarRight.setMaxWidth(TOP_RIGHT_WIDTH);
	    //titleBarRight.getChildren().add(newMinMaxClose(stage, 0, TOP_BORDER_HEIGHT / 2 + TOP_MIN_MAX_CLOSE_Y));
        titleBar.getChildren().addAll(titleBarLeft, titleBarCenter, titleBarRight);
	    
	    final VBox leftBar = new VBox();
	    leftBar.setId("border-left");
	    leftBar.setPrefWidth(LEFT_BORDER_WIDTH);
	    leftBar.setMaxWidth(LEFT_BORDER_WIDTH);
	    leftBar.setTranslateX(1);
	    Bindings.bindBidirectional(leftBar.translateYProperty(), content.translateYProperty());
	    Bindings.bindBidirectional(leftBar.maxHeightProperty(), content.maxHeightProperty());
	    
	    final VBox rightBar = new VBox();
	    rightBar.setId("border-right");
	    rightBar.setPrefWidth(RIGHT_BORDER_WIDTH);
	    rightBar.setMaxWidth(RIGHT_BORDER_WIDTH);
	    Bindings.bindBidirectional(rightBar.translateYProperty(), content.translateYProperty());
	    Bindings.bindBidirectional(rightBar.maxHeightProperty(), content.maxHeightProperty());
	    
	    final HBox statusBar = new HBox();
	    //statusBar.setStyle("-fx-background-color: black;");
	    statusBar.setPrefHeight(BOTTOM_BORDER_HEIGHT);
	    statusBar.setMaxHeight(BOTTOM_BORDER_HEIGHT);
        final Region statusBarLeft = new Region();
        statusBarLeft.setId("status-bar-left");
        statusBarLeft.setPrefWidth(BOTTOM_LEFT_WIDTH);
        statusBarLeft.setMaxWidth(BOTTOM_LEFT_WIDTH);
	    final Region statusBarCenter = new Region();
	    statusBarCenter.setId("status-bar");
        HBox.setHgrow(statusBarCenter, Priority.ALWAYS);
	    final HBox statusBarRight = new HBox();
	    statusBarRight.setAlignment(Pos.BOTTOM_RIGHT);
	    statusBarRight.setId("status-bar-right");
	    statusBarRight.setPrefWidth(BOTTOM_RIGHT_WIDTH);
	    statusBarRight.setMaxWidth(BOTTOM_RIGHT_WIDTH);
	    final WindowReziseButton windowResizeButton = new WindowReziseButton(stage, this.getMinWidth(), this.getMinHeight());
	    HBox.setMargin(windowResizeButton, new Insets(BOTTOM_BORDER_HEIGHT - 11, 0, 0, 0));
	    statusBarRight.getChildren().addAll(windowResizeButton);
        statusBar.getChildren().addAll(statusBarLeft, statusBarCenter, statusBarRight);
	    
	    // content adjustment
	    stage.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				content.setMaxWidth(stage.getWidth() - content.getTranslateX() - RIGHT_BORDER_WIDTH);
				rightBar.setTranslateX(content.getMaxWidth() - RIGHT_BORDER_WIDTH);
			}
		});
	    stage.heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				statusBar.setTranslateY(stage.getHeight() - BOTTOM_BORDER_HEIGHT);
				content.setMaxHeight(stage.getHeight() - content.getTranslateY() - statusBar.getMaxHeight() + BOTTOM_BORDER_CONTENT_ADJUSTMENT);
			}
		});
	    content.setTranslateX(LEFT_BORDER_WIDTH);
	    content.setTranslateY(TOP_BORDER_HEIGHT - TOP_BORDER_CONTENT_ADJUSTMENT);
	    getChildren().addAll(content, leftBar, rightBar, titleBar, statusBar, logoView);
	}

	private HBox newMinMaxClose(final Stage stage) {
		final HBox box = new HBox(4);
		final java.awt.SystemTray st = java.awt.SystemTray.isSupported() ? java.awt.SystemTray.getSystemTray() : null;
		if (st != null && st.getTrayIcons().length == 0) {
			final String imageName = st.getTrayIconSize().width > 16 ? st.getTrayIconSize().width > 64 ? RS.IMG_LOGO_128 : RS.IMG_LOGO_64 : RS.IMG_LOGO_16;
			try {
				try {
					final java.awt.Image image = javax.imageio.ImageIO.read(RS.stream(imageName));
					final java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
					trayIcon.setToolTip("Hello");
					st.add(trayIcon);
				} catch (java.io.IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//final java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(imageFileName);
			} catch (java.awt.AWTException e) {
				// TODO : Log error?
				e.printStackTrace();
			}
		}
		final ImageView minBtn = newTitleBarButton(RS.IMG_SKIN_MIN, 0.9, 0);
		minBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (st != null && st.getTrayIcons().length > 0) {
					// Linux/Windows
					if (stage.isShowing()) {
						stage.hide();
					} else {
						stage.show();
					}
					//st.getTrayIcons()[0].displayMessage("Hey", "Hello World", java.awt.TrayIcon.MessageType.INFO);
				} else {
					// Mac just minimize/restore
					stage.setIconified(!stage.isIconified());
				}
			}
		});
		final ImageView maxBtn = newTitleBarButton(RS.IMG_SKIN_MAX, 0.9, 0);
		final Image maxImage = maxBtn.getImage();
		final Image resImage = RS.img(RS.IMG_SKIN_RESTORE);
		maxBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
                final double stageY = Utils.isMac() ? stage.getY() - 22 : stage.getY(); // TODO Workaround for RT-13980
                final Screen screen = Screen.getScreensForRectangle(stage.getX(), stageY, 1, 1).get(0);
                Rectangle2D bounds = screen.getVisualBounds();
                if (bounds.getMinX() == stage.getX() && bounds.getMinY() == stageY &&
                        bounds.getWidth() == stage.getWidth() && bounds.getHeight() == stage.getHeight()) {
                    if (backupWindowBounds != null) {
                        stage.setX(backupWindowBounds.getMinX());
                        stage.setY(backupWindowBounds.getMinY());
                        stage.setWidth(backupWindowBounds.getWidth());
                        stage.setHeight(backupWindowBounds.getHeight());
                        maxBtn.setImage(maxImage);
                    }
                } else {
                    backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                    final double newStageY = Utils.isMac() ? screen.getVisualBounds().getMinY() + 22 : screen.getVisualBounds().getMinY(); // TODO Workaround for RT-13980
                    stage.setX(screen.getVisualBounds().getMinX());
                    stage.setY(newStageY);
                    stage.setWidth(screen.getVisualBounds().getWidth());
                    stage.setHeight(screen.getVisualBounds().getHeight());
                    maxBtn.setImage(resImage);
                }
			}
		});
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	        public void handle(final WindowEvent event) {
	            if (isExiting) {
	                stage.close();
	            }
	            event.consume();
	        }
	    });
		final ImageView closeBtn = newTitleBarButton(RS.IMG_SKIN_CLOSE, 0.7, 0);
		closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (isExiting) {
					return;
				}
				isExiting = true;
				if (st != null) {
					for (java.awt.TrayIcon trayIcon : st.getTrayIcons()) {
						st.remove(trayIcon);
					}
				}
				Platform.exit();
			}
		});
		box.getChildren().addAll(minBtn, maxBtn, closeBtn);
		return box;
	}
	
	private ImageView newTitleBarButton(final String imageName, final double enterBrightness, final double exitedBrightness) {
	    final ImageView btn = RS.imgView(imageName);
	    btn.setId("title-menu");
	    HBox.setMargin(btn, new Insets(TOP_BORDER_HEIGHT / 2 + TOP_MIN_MAX_CLOSE_ADJUSTMENT, 0, 0, 0));
        final ColorAdjust btnEffect = new ColorAdjust();
        //btnEffect.setContrast(enterBrightness);
        btnEffect.setBrightness(exitedBrightness);
        btn.setEffect(btnEffect);
	    btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
		        btnEffect.setBrightness(enterBrightness);
		        //btnEffect.setContrast(exitedBrightness);
			}
		});
	    btn.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
		        btnEffect.setBrightness(exitedBrightness);
		        //btnEffect.setContrast(enterBrightness);
			}
		});
	    return btn;
	}
	
	public class WindowReziseButton extends Region {
		private double dragOffsetX, dragOffsetY;
		
		public WindowReziseButton(final Stage stage, final double stageMinimumWidth, final double stageMinimumHeight) {
	        setId("window-resize-button");
	        setPrefSize(11, 11);
	        setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override public void handle(MouseEvent e) {
	                final double stageY = Utils.isMac() ? stage.getY() + 22 : stage.getY(); // TODO Workaround for RT-13980
	                dragOffsetX = (stage.getX() + stage.getWidth()) - e.getScreenX();
	                dragOffsetY = (stageY + stage.getHeight()) - e.getScreenY();
	                e.consume();
	            }
	        });
	        setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override public void handle(MouseEvent e) {
	                final double stageY = Utils.isMac() ? stage.getY() + 22 : stage.getY(); // TODO Workaround for RT-13980
	                final Screen screen = Screen.getScreensForRectangle(stage.getX(), stageY, 1, 1).get(0);
	                Rectangle2D visualBounds = screen.getVisualBounds();
	                if (Utils.isMac()) visualBounds = new Rectangle2D(visualBounds.getMinX(), visualBounds.getMinY() + 22,
	                        visualBounds.getWidth(), visualBounds.getHeight()); // TODO Workaround for RT-13980
	                double maxX = Math.min(visualBounds.getMaxX(), e.getScreenX() + dragOffsetX);
	                double maxY = Math.min(visualBounds.getMaxY(), e.getScreenY() - dragOffsetY);
	                stage.setWidth(Math.max(stageMinimumWidth, maxX - stage.getX()));
	                stage.setHeight(Math.max(stageMinimumHeight, maxY - stageY));
	                e.consume();
	            }
	        });
		}
	}
}

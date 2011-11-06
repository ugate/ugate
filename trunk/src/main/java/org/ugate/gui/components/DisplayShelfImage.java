package org.ugate.gui.components;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Popup;

/**
 * Image display that shows an image at a specified size with a tool bar with information about the image
 * and a button to view the image at its original dimensions.
 */
public class DisplayShelfImage extends Parent {

	public static final int TOOLBAR_POSITION_NONE = -1;
	public static final int TOOLBAR_POSITION_TOP = 0;
	public static final int TOOLBAR_POSITION_BOTTOM = 1;
    private PerspectiveTransform transform = new PerspectiveTransform();
    /** Angle Non-Observable Property */
    public final DoubleProperty angle;
    private final Label label;
    private final ToolBar toolBar;
    protected final Reflection reflection;
    protected final ImageView imageView;
    protected final boolean preserveRatio;
    protected final boolean smooth;
    protected final int toolBarPosition;
    private File file;

    /**
     * Constructs an image display
     * 
     * @param file the file for the image (when null image is used)
     * @param image the image (when null file is used)
     * @param reflectionSize the reflection size of the image
     * @param displayAngle the display angle of the image
     * @param toolBarPosition the tool bar position that displays information about the image (top, bottom, none, etc.)
     * @param fileImageWidth the width used for the image created from the file
     * @param fileImageHeight the height used for the image created from the file
     * @param fileImagePreserveRatio true to preserve the image ratio when creating an image from the file
     * @param fileImageSmooth true to smooth the image when creating an image from the file
     */
    public DisplayShelfImage(final File file, Image image, final double reflectionSize, final double displayAngle, 
    		final int toolBarPosition, final double fileImageWidth, final double fileImageHeight, 
    		final boolean fileImagePreserveRatio, final boolean fileImageSmooth) {
    	this.angle = new SimpleDoubleProperty(displayAngle);
    	this.toolBarPosition = toolBarPosition;
    	this.preserveRatio = fileImagePreserveRatio;
    	this.smooth = fileImageSmooth;
        // create content
    	this.imageView = new ImageView();
    	this.imageView.setCache(true);
    	this.imageView.setCacheHint(CacheHint.SCALE_AND_ROTATE);
    	if (image == null) {
    		this.file = file;
    		image = createImage(this.file, fileImageWidth, fileImageHeight, this.preserveRatio, this.smooth);
    	}
    	imageView.setImage(image);
        reflection = new Reflection();
        reflection.setFraction(reflectionSize);
        imageView.setEffect(reflection);
        setEffect(transform);
        getChildren().addAll(imageView);
        angle.set(displayAngle);
        angle.addListener(new InvalidationListener() {
        	@Override
            public void invalidated(Observable vm) {
                // calculate new transform
            	final double radiusH = imageView.getImage().getWidth() / 2;
            	final double back = imageView.getImage().getWidth() / 10;
                double lx = (radiusH - Math.sin(Math.toRadians(angle.get())) * radiusH - 1);
                double rx = (radiusH + Math.sin(Math.toRadians(angle.get())) * radiusH + 1);
                double uly = (-Math.cos(Math.toRadians(angle.get())) * back);
                double ury = -uly;
                transform.setUlx(lx);
                transform.setUly(uly);
                transform.setUrx(rx);
                transform.setUry(ury);
                transform.setLrx(rx);
                transform.setLry(imageView.getImage().getHeight() + uly);
                transform.setLlx(lx);
                transform.setLly(imageView.getImage().getHeight() + ury);
            }
        });
    	this.label = createLabel(this.file != null ? this.file.getName() : image.getWidth() + "x" + image.getHeight());
    	if (this.toolBarPosition != TOOLBAR_POSITION_NONE) {
    		this.toolBar = createToolBar();
    		getChildren().add(this.toolBar);
    	} else {
    		this.toolBar = null;
    	}
    }
    
    /**
     * Creates an image from a file
     * 
     * @param file the file reference for the image
     * @param width the width of the image
     * @param height the height of the image
     * @param preserveRatio true to preserve the ratio of the image
     * @param smooth true to smooth the image
     * @return the image
     */
    protected static Image createImage(final File file, final double width, final double height, 
    		boolean preserveRatio, boolean smooth) {
    	return new Image(file.getAbsolutePath(), width, height, preserveRatio, smooth, false);
    }
    
    /**
     * @return a tool bar that will display the image name (using the label) and a zoom button
     */
    protected ToolBar createToolBar() {
    	final ToolBar toolBar = new ToolBar();
    	toolBar.setOpacity(toolBarPosition == TOOLBAR_POSITION_TOP ? 0.7 : 0.9);
    	final Button zoomButton = createViewOriginalButton();
    	if (zoomButton != null) {
    		toolBar.getItems().addAll(zoomButton, label);
    	} else {
    		toolBar.getItems().add(label);
    	}
    	toolBar.setPrefWidth(imageView.getImage().getWidth());
    	toolBar.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateToolBarY(toolBar);
			}
		});
    	//Bindings.add(toolBar.widthProperty(), imageView.imageProperty());
    	imageView.imageProperty().addListener(new ChangeListener<Image>() {
			@Override
			public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
				toolBar.setPrefWidth(newValue.getWidth());
				updateToolBarY(toolBar);
			}
		});
    	return toolBar;
    }
    
    /**
     * Creates a button for viewing the image at its original size. The button will open a pop up
     * window at the center of the scene displaying the original image. It will auto hide when it 
     * looses focus.
     * 
     * @return the button
     */
    protected Button createViewOriginalButton() {
    	// TODO : handle image zoom when no file is specified
    	if (file != null) {
    		final Group btnGraphic = createButtonGraphic(0.2);
    		final Button zoomButton = new Button();
    		zoomButton.setTooltip(new Tooltip("View Original Image Size"));
    		zoomButton.setGraphic(btnGraphic);
    		zoomButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent e) {
    				final Image image = new Image(file.getAbsolutePath());
    				final VBox frame = new VBox(10);
    				final ImageView imgView = new ImageView(image);
    				frame.getStyleClass().add("displayshelfpopup");
    				VBox.setMargin(imgView, new Insets(10, 10, 0, 10));
    				final VBox info = new VBox();
    				VBox.setMargin(info, new Insets(0, 10, 0, 10));
    				final Text infoText = new Text(label.getText());
    				infoText.setWrappingWidth(imgView.getImage().getWidth());
    				info.setPrefHeight(imgView.getImage().getHeight() / 7);
    				info.getChildren().add(infoText);
    				frame.getChildren().addAll(imgView, info);
    				final Popup popup = new Popup();
    				popup.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
    					@Override
    					public void handle(MouseEvent event) {
    						popup.hide();
    					}
    				});
    				popup.setAutoHide(true);
    				popup.getContent().add(frame);
    				popup.setX(getScene().getWindow().getX() + getScene().getWindow().getWidth() / 2 - image.getWidth() / 2);
    				popup.setY(getScene().getWindow().getY() + getScene().getWindow().getHeight() / 2 - image.getHeight() / 2);
    				popup.show(getScene().getWindow());
    			}
    		});
    		return zoomButton;
    	}
    	return null;
    }
    
    protected Group createButtonGraphic(final double scale) {
		final Group btnGrp = new Group();
    	final Circle circle = new Circle(106.0 * scale, 74.0 * scale, 25.0 * scale);
    	circle.setFill(Color.rgb(0xcc, 0xff, 0x00, 1.0));
    	circle.setOpacity(0.9);
    	circle.setSmooth(true);
    	circle.setStroke(Color.rgb(0xa7, 0xd1, 0x00, 1.0));
    	circle.setStrokeWidth(2.0 * scale);
    	final Path path = new Path();
    	path.setStroke(null);
    	path.setOpacity(0.74);
    	path.setSmooth(true);
    	final MoveTo mt = new MoveTo(123.143 * scale, 61.088 * scale);
    	mt.setAbsolute(true);
    	path.getElements().add(mt);
    	final RadialGradient rg = new RadialGradient(0, 0, 118.0 * scale, 90.0 * scale, 53.625 * scale, false, CycleMethod.NO_CYCLE, 
    			new Stop(0.0 * scale, Color.rgb(0xFF, 0xFF, 0xFF, 1.0)),
    			new Stop(0.2033 * scale, Color.rgb(0xFE, 0xFF, 0xFD, 1.0)),
    			new Stop(0.2765 * scale, Color.rgb(0xFD, 0xFD, 0xF6, 1.0)),
    			new Stop(0.3286 * scale, Color.rgb(0xF9, 0xFB, 0xEB, 1.0)),
    			new Stop(0.3708 * scale, Color.rgb(0xF4, 0xF7, 0xDA, 1.0)),
    			new Stop(0.4065 * scale, Color.rgb(0xEE, 0xF2, 0xC4, 1.0)),
    			new Stop(0.4157 * scale, Color.rgb(0xEC, 0xF1, 0xBD, 1.0)),
    			new Stop(1.0 * scale, Color.rgb(0xCC, 0xFF, 0x00, 1.0)));
    	path.setFill(rg);
    	final CubicCurveTo cct1 = new CubicCurveTo(130.602 * scale, 70.889 * scale, 129.01 * scale, 
    			84.643 * scale, 119.59 * scale, 91.813 * scale);
    	cct1.setAbsolute(true);
    	path.getElements().add(cct1);
    	final CubicCurveTo cct2 = new CubicCurveTo(110.171 * scale, 98.981 * scale, 96.489 * scale, 
    			96.843 * scale, 89.032 * scale, 87.043 * scale);
    	cct2.setAbsolute(true);
    	path.getElements().add(cct2);
    	final CubicCurveTo cct3 = new CubicCurveTo(81.573 * scale, 77.24 * scale, 83.165 * scale, 
    			63.486 * scale, 92.584 * scale, 56.316 * scale);
    	cct3.setAbsolute(true);
    	path.getElements().add(cct3);
    	final CubicCurveTo cct4 = new CubicCurveTo(102.004 * scale, 49.149 * scale, 115.686 * scale, 
    			51.285 * scale, 123.143 * scale, 61.088 * scale);
    	cct4.setAbsolute(true);
    	path.getElements().add(cct4);
    	path.getElements().add(new ClosePath());
//    	final Ellipse ellipse = new Ellipse(96.5 * scale, 62.5 * scale, 8.294 * scale, 4.906 * scale);
//    	ellipse.setFill(Color.rgb(0xff, 0xff, 0xff, 1.0));
//    	ellipse.setSmooth(true);
//    	ellipse.getTransforms().add(Transform.affine(0.7958 * scale, -0.6055 * scale, 0.655 * scale, 
//    			0.7958 * scale, -18.1424 * scale, 71.1966 * scale));
    	btnGrp.getChildren().addAll(circle, path);//, ellipse);
    	return btnGrp;
    }
    
    /**
     * Updates the tool bars Y coordinate based upon the position supplied upon creation
     * 
     * @param toolBar the tool bar to update
     */
    protected void updateToolBarY(final ToolBar toolBar) {
    	if (toolBarPosition == TOOLBAR_POSITION_TOP) {
    		//toolBar.setTranslateY(toolBar.getHeight() * -1);
    	} else if (toolBarPosition == TOOLBAR_POSITION_BOTTOM) {
        	double toolBarY = imageView.getBoundsInLocal().getMaxY();
        	toolBarY -= (toolBarY * reflection.getFraction());// - toolBar.getHeight());
        	toolBar.setTranslateY(toolBarY);
    	}
    }
    
    /**
     * Creates a label for the image that will keep the width at a third of the image size
     * 
     * @param text the initial text of the label
     * @return the created label
     */
    protected Label createLabel(final String text) {
    	final Label txt = new Label();
    	txt.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				txt.setPrefWidth(imageView.getBoundsInLocal().getWidth() / 3);
			}
		});
    	txt.setText(text);
    	return txt;
    }
    
    /**
     * @return the file for the image (or null if a file is not used)
     */
    public File getFile() {
    	return file;
    }
    
    /**
     * Creates an image from a file and sets the content to the view. Also updates the image label with the file name
     * 
     * @param file the file to set
     */
    public void setFile(final File file) {
    	this.file = file;
    	this.label.setText(this.file.getName());
    	setImage(createImage(this.file, imageView.getImage().getWidth(), imageView.getImage().getHeight(), 
				this.preserveRatio, this.smooth));
    }
    
    /**
     * @return the label for the image
     */
    public Label getLabel() {
    	return label;
    }
    
    /**
     * @return the tool bar (null if position is set to none)
     */
    public ToolBar getToolBar() {
    	return toolBar;
    }
    
    /**
     * @return the image
     */
    public Image getImage() {
    	return imageView.getImage();
    }
    
    /**
     * @param image the image to set
     */
    public void setImage(final Image image) {
    	this.file = null;
    	imageView.setImage(image);
    }
}
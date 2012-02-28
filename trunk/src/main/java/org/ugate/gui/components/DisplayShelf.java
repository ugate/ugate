package org.ugate.gui.components;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Display for viewing images in a book shelf type manner.
 */
public class DisplayShelf extends Region {

	public static final int TOOLBAR_POSITION_NONE = DisplayShelfImage.TOOLBAR_POSITION_NONE;
	public static final int TOOLBAR_POSITION_TOP = DisplayShelfImage.TOOLBAR_POSITION_TOP;
	public static final int TOOLBAR_POSITION_BOTTOM = DisplayShelfImage.TOOLBAR_POSITION_BOTTOM;
    private static final Duration DURATION = Duration.millis(500);
    private static final Interpolator INTERPOLATOR = Interpolator.EASE_BOTH;
    public static final double IDEAL_SPACING = 50;
    private static final double LEFT_OFFSET = -110;
    private static final double RIGHT_OFFSET = 110;
    private static final double SCALE_SMALL = 0.7;
    private final IntegerProperty selectedImageIndexProperty = new SimpleIntegerProperty();
	private ObservableList<DisplayShelfImage> items = FXCollections.observableArrayList();
    private Group centered = new Group();
    private Group left = new Group();
    private Group center = new Group();
    private Group right = new Group();
    private int centerIndex = 0;
    private Timeline timeline;
    private ScrollBar scrollBar = new ScrollBar();
    private int imagesPerLoad = 5;
    private boolean localChange = false;
    private boolean flipOnNav = false;
    private File imageDirectory;
    private File[] files;
    public final double imageWidth;
    public final double imageHeight;
    public final double reflectionSize;
    public final double displayAngle;
    private double spacing;
    public final int toolBarPosition;
    public final String imageFullSizeToolTip;
    
    /**
     * Constructor using a file directory where all image files within the immediate directory will be displayed.
     * When the number of image files exceeds an internal threshold they will be loaded as the user navigates
     * to the end of the list (to increase performance).
     * 
     * @param imageDirectory
     * @param imageWidth the width of the images used in the display
     * @param imageHeight the height of the images used in the display
     * @param reflectionSize the size of reflection
     * @param displayAngle the angle of images that are not in the center viewing area
     * @param spacing the spacing between images
     * @param toolBarPosition the position of the tool bar that shows the image information
     * @param imageFullSizeToolTip the tool tip for viewing image full size
     */
    public DisplayShelf(final File imageDirectory, final double imageWidth, final double imageHeight, final double reflectionSize,
    		final double displayAngle, final double spacing, final int toolBarPosition, final String imageFullSizeToolTip) {
    	this.imageFullSizeToolTip = imageFullSizeToolTip;
    	this.imageDirectory = imageDirectory;
    	this.imageWidth = imageWidth;
    	this.imageHeight = imageHeight;
    	this.reflectionSize = reflectionSize;
    	this.displayAngle = displayAngle;
    	this.spacing = spacing;
    	this.toolBarPosition = toolBarPosition;
        init(null);
    }

    /**
     * Constructor using predefined images
     * 
     * @param images the images to display
     * @param imageWidth the width of the images used in the display
     * @param imageHeight the height of the images used in the display
     * @param reflectionSize the size of reflection
     * @param displayAngle the angle of images that are not in the center viewing area
     * @param spacing the spacing between images
     * @param toolBarPosition the position of the tool bar that shows the image information
     * @param imageFullSizeToolTip the tool tip for viewing image full size
     */
    public DisplayShelf(final Image[] images, final double imageWidth, final double imageHeight, final double reflectionSize, 
    		final double displayAngle, final double spacing, final int toolBarPosition, final String imageFullSizeToolTip) {
    	this.imageFullSizeToolTip = imageFullSizeToolTip;
    	this.imageDirectory = null;
    	this.imageWidth = imageWidth;
    	this.imageHeight = imageHeight;
    	this.reflectionSize = reflectionSize;
    	this.displayAngle = displayAngle;
    	this.spacing = spacing;
    	this.toolBarPosition = toolBarPosition;
    	init(images);
    }
    
    /**
     * Initializes the content of the component
     * 
     * @param images the images to add as image items for (null if an image directory has been defined)
     */
    protected void init(final Image[] images) {
    	setCache(true);
    	setCacheHint(CacheHint.SCALE_AND_ROTATE);
    	setMinSize(imageWidth * 2, imageHeight);
        getStyleClass().add("displayshelf");
        // create items
        if (images != null) {
        	setImageItems(images);
        } else {
        	updateFiles();
        	setImageItems(null);
        }
        // setup scroll bar
        items.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				scrollBar.setMax(items.size() - 1);
			}
		});
        scrollBar.setMax(items.size() - 1);
        scrollBar.setVisibleAmount(1);
        scrollBar.setUnitIncrement(1);
        scrollBar.setBlockIncrement(1);
        scrollBar.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (!localChange) {
                	shiftToCenter(items.get((int)scrollBar.getValue()));
                }
            }
        });
        // create content
        centered.getChildren().addAll(left, right, center);
        getChildren().addAll(centered, scrollBar);
        // listen for keyboard events
        setFocusTraversable(true);
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.LEFT) {
                    shift(1);
                    localChange = true;
                    scrollBar.setValue(centerIndex);
                    localChange = false;
                } else if (ke.getCode() == KeyCode.RIGHT) {
                    shift(-1);
                    localChange = true;
                    scrollBar.setValue(centerIndex);
                    localChange = false;
                }
            }
        });
        // update
        update(null);
    }
    
    /**
     * Creates the image items from the image files (up to max) or the passed images themselves.
     * 
     * @param files the files to add image items for (null if images are supplied)
     * @param images the images to add as image items for (null if files are supplied)
     */
    protected void setImageItems(final Image[] images) {
        items.clear();
        int itemCount = images == null ? files.length : images.length;
        if (itemCount > imagesPerLoad) {
        	itemCount = imagesPerLoad;
        }
        for (int i=0; i<itemCount; i++) {
        	addItem(images == null ? files[i] : null, images != null ? images[i] : null, i);
        }
    }
    
    /**
     * Adds a image item
     * 
     * @param file the file to add (if image is null) or as a reference (if image is not null)
     * @param image the image to add (null if using the file to create the image)
     * @param index the index to add the image item at
     * @return the created image item
     */
    protected DisplayShelfImage addItem(final File file, final Image image, final int index) {
        final DisplayShelfImage item = image != null ?  
			new DisplayShelfImage(null, image, reflectionSize, displayAngle, 
					toolBarPosition, imageWidth, imageHeight, false, false, imageFullSizeToolTip) :
        		new DisplayShelfImage(file, null, reflectionSize, displayAngle, 
        				toolBarPosition, imageWidth, imageHeight, false, false, imageFullSizeToolTip);
        items.add(item);
        item.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                localChange = true;
                scrollBar.setValue(index);
                localChange = false;
                shiftToCenter(item);
            }
        });
        return item;
    }
    
    /**
     * Updates the image files within the image directory (sorted by modified date- newest to oldest).
     * 
     * @return the image files
     */
    protected void updateFiles() {
    	//final MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
    	final FileFilter imageFilter = new FileFilter() {
        	@Override
            public boolean accept(final File file) {
                return !file.isDirectory() && file.canRead() && !file.isHidden() && isImage(file);
                	//mftm.getContentType(file).startsWith("image");
            }
        };imageDirectory.getAbsolutePath();
        files = imageDirectory.listFiles(imageFilter);
        Arrays.sort(files, new Comparator<File>() {
        	@Override
        	public int compare(final File o1, final File o2) {
        		if (o1.lastModified() > o2.lastModified()) {
        			return -1;
        		} else if (o1.lastModified() < o2.lastModified()) {
        			return +1;
        		} else {
        			return 0;
        		}
        	}
        });
    }
    
    /**
     * @param fileOrStream the {@linkplain File} or {@linkplain FileInputStream} to check
     * @return true when the file is an image
     */
    private static boolean isImage(final Object fileOrStream) {
    	final String imgFormatName = getImageFormatName(fileOrStream);
    	return imgFormatName != null && (imgFormatName.equals("JPEG") || 
    			imgFormatName.equals("PNG") || imgFormatName.equals("BMP") || 
    			imgFormatName.equals("WBMP") || imgFormatName.equals("GIF"));
    }
    
    /**
     * @param fileOrStream the {@linkplain File} or {@linkplain FileInputStream} to check
     * @return the name of the image type (null when not an image)
     */
    private static String getImageFormatName(final Object fileOrStream) {
    	ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(fileOrStream);
            // Find all image readers that recognize the image format
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                return null;
            }
            return iter.next().getFormatName().toUpperCase();
        } catch (final IOException e) {
        	// image cannot be read
        } finally {
        	if (iis != null) {
        		try {
					iis.close();
				} catch (final IOException e) {
					// cannot be closed
				}
        	}
        }
        return null;
    }
    
    /**
     * Sets the images viewed in the display and centers the first image (newest image when using an image directory)
     * 
     * @param imageDirectory the directory where the desired image files exist that will be added to 
     * 			the display (null if images are supplied)
     * @param images the images to add as image items for (null if an image directory is supplied)
     */
    public void setImages(final File imageDirectory, final Image[] images) {
    	if (imageDirectory != null) {
    		this.imageDirectory = imageDirectory;
    		refreshImages();
    	} else {
    		setImageItems(images);
    		update(null);
    	}
    }
    
    /**
     * Refreshes the images viewed in the display and centers the first image (newest image when using an image directory)
     */
    public void refreshImages() {
    	if (imageDirectory != null) {
    		updateFiles();
	    	setImageItems(null);
    	}
    	update(null);
    }
    
    /**
     * Gets a image display item at the specified index (if exists)
     * 
     * @param index the index
     * @return the image display item
     */
    public DisplayShelfImage getImageAt(final int index) {
    	return index >= 0 && index < items.size()? items.get(index) : null;
    }
    
    /**
     * @return the image display item that is currently selected
     */
    public DisplayShelfImage getSelectedImage() {
    	return getImageAt(centerIndex);
    }
    
    /**
     * Listens for changes to the image directory for changes to any of the images and updates the display accordingly
     */
    protected void listenForFileChanges() {
    	// http://fxexperience.com/2011/07/worker-threading-in-javafx-2-0/
    	/*final Service<File[]> imgDirServcice = new Service<File[]>() {
			@Override
			protected Task<File[]> createTask() {
		        return new Task<File[]>() {
					@Override
					protected File[] call() throws Exception {
				    	final MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
				        final FileFilter imageFilter = new FileFilter() {
				        	@Override
				            public boolean accept(final File file) {
				                return !file.isDirectory() && file.canRead() && !file.isHidden() && 
				                	mftm.getContentType(file).startsWith("image");
				            }
				        };
				        return imageDirectory.listFiles(imageFilter);
					}
				};
			}
		};
		imgDirServcice.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.SUCCEEDED) {
					update();
				}
			}
		});*/

    	/* JDK 7 can listen for file changes in a directory...
    	final Path path = file.toPath();
    	WatchService ws = path.getFileSystem().newWatchService();
    	path.register(ws, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_DELETE, StandardWatchEventKind.ENTRY_MODIFY);
        WatchKey watch = null;
        while (true) {
            try {
                watch = ws.take();
            } catch (InterruptedException ex) {
                // continue to watch
            }
            List<WatchEvent<?>> events = watch.pollEvents();
            watch.reset();
            for (WatchEvent<?> event : events) {
                Kind<Path> kind = (Kind<Path>) event.kind();
                Path context = (Path) event.context();
                if (kind.equals(StandardWatchEventKind.ENTRY_CREATE)) {
                    System.out.println("Created: " + context.getFileName());
                } else if (kind.equals(StandardWatchEventKind.ENTRY_DELETE)) {
                    System.out.println("Deleted: " + context.getFileName());
                } else if (kind.equals(StandardWatchEventKind.ENTRY_MODIFY)) {
                    System.out.println("Modified: " + context.getFileName());
                }
            }
        }*/
    }

    /**
     * {@inheritDoc}
     */
    @Override 
    protected void layoutChildren() {
        // keep centered centered
        centered.setLayoutY((getHeight() - imageHeight) / 2);
        centered.setLayoutX((getWidth() - imageWidth) / 2);
        // position scroll bar at bottom
        scrollBar.setLayoutX(10);
        scrollBar.setLayoutY(getHeight() - 25);
        scrollBar.resize(getWidth() - 20, 15);
    }

    /**
     * Updates the image items and corresponding animation positions. Each group will be updated with new members 
     * based upon the center index
     */
    protected void update(final IndexRange excludeInAnimation) {
        // reset the time line and clear the children
        if (timeline!=null) timeline.stop();
        timeline = new Timeline();
        left.getChildren().clear();
        center.getChildren().clear();
        right.getChildren().clear();
        if (items.size() <= 0) {
        	return;
        }
        final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
        // add left items
        for (int i = 0; i < centerIndex; i++) {
        	final DisplayShelfImage it = items.get(i);
        	left.getChildren().add(it);
            it.getToolBar().setVisible(false);
            double newX = -centerIndex * spacing + spacing * i + LEFT_OFFSET;
            updateItemPositon(keyFrames, it, newX, displayAngle, SCALE_SMALL, DURATION, excludeInAnimation, i);
        }
        // add center item
        center.getChildren().add(items.get(centerIndex));
        final DisplayShelfImage centerItem = items.get(centerIndex);
        centerItem.getToolBar().setVisible(true);
        updateItemPositon(keyFrames, centerItem, 0, 90.0, 1.0, DURATION, excludeInAnimation, centerIndex);
        // add right items
        final int rightCount = items.size() - centerIndex;
        for (int i = items.size() - 1; i > centerIndex; i--) {
            final DisplayShelfImage it = items.get(i);
            right.getChildren().add(it);
            it.getToolBar().setVisible(false);
            final double newX = rightCount * spacing - spacing * (items.size() - i) + RIGHT_OFFSET;
            final double newAngle = (displayAngle * - 1.0) + (getFlipOnNav() ? 0 : 180.0);
            updateItemPositon(keyFrames, it, newX, newAngle, SCALE_SMALL, DURATION, excludeInAnimation, i);
        }
        // play animation
        timeline.play();
    }
    
    /**
     * Updates the items position
     * 
     * @param keyFrames the key frames to add to
     * @param item the image item to position
     * @param newX the new X coordinate
     * @param newAngle the new angle
     * @param newScale the new scale
     * @param duration the new duration
     * @param excludeInAnimation the range of indexes to exclude from animation
     * @param index the index of the item
     */
    protected void updateItemPositon(final ObservableList<KeyFrame> keyFrames, final DisplayShelfImage item, double newX, 
    		final double newAngle, final double newScale, final Duration duration, final IndexRange excludeInAnimation, final int index) {
        if (excludeInAnimation != null && index >= excludeInAnimation.getStart() && 
        		index <= excludeInAnimation.getEnd()) {
        	item.setTranslateX(newX);
        	item.scaleXProperty().set(newScale);
        	item.scaleYProperty().set(newScale);
        	item.angle.set(newAngle);
        } else {
            keyFrames.add(new KeyFrame(duration,
                    new KeyValue(item.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(item.scaleXProperty(), newScale, INTERPOLATOR),
                    new KeyValue(item.scaleYProperty(), newScale, INTERPOLATOR),
                    new KeyValue(item.angle, newAngle, INTERPOLATOR)));
        }
    }

    /**
     * Shifts an image item to the center viewing area
     * 
     * @param item the item to center in the view
     */
    private void shiftToCenter(final DisplayShelfImage item) {
        if (center.getChildren().get(0) == item) {
            return;
        }
        for (int i = 0; i < left.getChildren().size(); i++) {
            if (left.getChildren().get(i) == item) {
                int shiftAmount = left.getChildren().size() - i;
                // positive shift indicates navigation to the left
                shift(shiftAmount);
                return;
            }
        }
        for (int i = 0; i < right.getChildren().size(); i++) {
            if (right.getChildren().get(i) == item) {
                int shiftAmount = -(right.getChildren().size() - i);
                // negative shift indicates navigation to the right
                shift(shiftAmount);
                return;
            }
        }
    }

    /**
     * Shifts the image items (negative shift amount) or right (positive shift amount)
     * 
     * @param shiftAmount the amount of the shift
     */
    public void shift(final int shiftAmount) {
        if (centerIndex <= 0 && shiftAmount > 0) return;
        if (centerIndex >= items.size() - 1 && shiftAmount < 0) return;
        centerIndex -= shiftAmount;
        IndexRange newItemRange = null;
        if (files != null && centerIndex < (files.length - 1) && centerIndex > (items.size() - 3)) {
        	final int startIndex = items.size();
        	for (int i=startIndex; i<(startIndex + imagesPerLoad); i++) {
        		if (i >= files.length) {
        			break;
        		}
        		addItem(files[i], null, items.size() - 1);
        	}
        	if (startIndex < (items.size() - 1)) {
        		newItemRange = new IndexRange(startIndex, items.size() - 1);
        	}
        }
        update(newItemRange);
        selectedImageIndexProperty.set(centerIndex);
    }
    
    /**
     * @return true to flip the image while transitioning between images
     */
    public boolean getFlipOnNav() {
    	return flipOnNav;
    }
    /**
     * @param flipOnNav true to flip the image while transitioning between images
     */
    public void setFlipOnNav(boolean flipOnNav) {
    	this.flipOnNav = flipOnNav;
    }
    
    /**
     * @return the selected image index property
     */
    public IntegerProperty getSelectedImageIndexProperty() {
		return selectedImageIndexProperty;
	}

    /**
     * @return the spacing between images
     */
	public double getSpacing() {
		return spacing;
	}

	/**
	 * @param spacing the spacing between images
	 */
	public void setSpacing(double spacing) {
		this.spacing = spacing;
	}

	/**
	 * @return the number of images to between navigation (files or urls)
	 */
	public int getImagesPerLoad() {
		return imagesPerLoad;
	}

	/**
	 * The number of images to between navigation (files or urls)
	 * 
	 * @param imagesPerLoad the number of images per load
	 */
	public void setImagesPerLoad(int imagesPerLoad) {
		this.imagesPerLoad = imagesPerLoad;
	}
}
package org.ugate.gui.components;

import java.text.DecimalFormat;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

/**
 * Needle driven gauge control
 */
public class Gauge extends Group {

	private final HandType handType;
	private final double outerRimRadius;
	private final double gaugeCenterRadius;
	private final int numOfMajorTickMarks;
	private final int numOfMinorTickMarks;
	private final double majorTickMarkWidth;
	private final double majorTickMarkHeight;
	private final double minorTickMarkWidth;
	private final double minorTickMarkHeight;
	private final double handWidth;
	private final double handHeight;
	private final double minAngle;
	private final double maxAngle;
	private final DecimalFormat anglePrecision;
	public final DoubleProperty angleProperty;
	public final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
	public final BooleanProperty showTickMarks;
	
	public Gauge() {
		this(HandType.NEEDLE, 0, 0, 0, 0);
	}
	
	public Gauge(final HandType handType, final double outerRimRadius, final double gaugeCenterRadius,
			final double minAngle, final double maxAngle) {
		this(handType, outerRimRadius, gaugeCenterRadius, 0, maxAngle, outerRimRadius, 0, minAngle, maxAngle, null);
	}
	
	public Gauge(final HandType handType, final double outerRimRadius, final double gaugeCenterRadius, final int numOfMajorTickMarks, 
			final double majorTickMarkWidth, final double majorTickMarkHeight, final double handHeightFactor,
			final double minAngle, final double maxAngle, final DecimalFormat anglePrecision) {
		this.handType = handType;
		this.outerRimRadius = outerRimRadius <= 0 ? 140d : outerRimRadius;
		this.gaugeCenterRadius = gaugeCenterRadius <= 0 ? 130d : gaugeCenterRadius;
		this.numOfMajorTickMarks = numOfMajorTickMarks <= 0 ? 12 : numOfMajorTickMarks;
		this.numOfMinorTickMarks = this.numOfMajorTickMarks * 10;
		this.majorTickMarkWidth = majorTickMarkWidth <= 0 ? 10d : majorTickMarkWidth;
		this.majorTickMarkHeight = majorTickMarkHeight <= 0 ? 2d : majorTickMarkHeight;
		this.minorTickMarkWidth = this.majorTickMarkWidth / 2d;
		this.minorTickMarkHeight = this.minorTickMarkWidth;
		this.handWidth = this.gaugeCenterRadius - this.minorTickMarkWidth;
		this.handHeight = this.majorTickMarkHeight * (handHeightFactor <= 0 ? 7d : handHeightFactor); 
		this.minAngle = minAngle <= 0 ? 0d : minAngle;
		this.maxAngle = maxAngle > 360 ? 360d : maxAngle;
		this.anglePrecision = anglePrecision == null ?  new DecimalFormat("#.##") : anglePrecision;
		this.angleProperty =  new SimpleDoubleProperty(this.minAngle);
		this.showTickMarks = new SimpleBooleanProperty(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void layoutChildren() {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		final Group gaugeParent = new Group();
		gaugeParent.setCache(true);
		gaugeParent.setCacheHint(CacheHint.SPEED);
		final Shape ourterRim = createOuterRim(outerRimRadius, minAngle, maxAngle);
		final Shape gaugeCenter = createGaugeCenter(outerRimRadius, gaugeCenterRadius, minAngle, maxAngle);
		gaugeParent.getChildren().addAll(ourterRim, gaugeCenter);
		// add minor tick marks
		addTickMarks(gaugeParent, gaugeCenter, numOfMinorTickMarks, Color.web("#AAAAAA"), minorTickMarkWidth, 
				minorTickMarkHeight, majorTickMarkWidth, majorTickMarkHeight, false);
		// add major tick marks
		addTickMarks(gaugeParent, gaugeCenter, numOfMajorTickMarks, Color.web("#CCCCCC"), majorTickMarkWidth, 
				majorTickMarkHeight, majorTickMarkWidth, majorTickMarkHeight, false);
		
		// create hand
		final Group hand = new Group();
		final ColorAdjust handColorAdj = new ColorAdjust();
		handColorAdj.setBrightness(0);
		final DropShadow handDropShadow = new DropShadow();
		handDropShadow.setOffsetX(4);
		handDropShadow.setOffsetY(4);
		handDropShadow.setRadius(7);
		handDropShadow.setColor(Color.web("#000000"));
		handDropShadow.setInput(handColorAdj);
		hand.setEffect(handDropShadow);
		final Group handBase = new Group();
		final Circle handDial = new Circle(outerRimRadius, outerRimRadius, handHeight / 1.3);
		handDial.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.web("#777777")), new Stop(0.9499, Color.rgb(20, 20, 20)),
				new Stop(0.95, Color.rgb(20, 20, 20)), new Stop(0.975, Color.rgb(20, 20, 20)),
				new Stop(1, Color.rgb(84, 84, 84, 0.0))));
		final double hx = handDial.getCenterX() - (handWidth / 1.2);
		final double hy = handDial.getCenterY() - (handHeight / 2);
		//final Rectangle hrec = new Rectangle(hx, hy, handWidth, handHeight);
		final Shape handShape = createHand(hx, hy, handWidth, handHeight, (majorTickMarkHeight * 4));
		final Rotate handShapeRotate = new Rotate(this.angleProperty.get(), handDial.getCenterX(), handDial.getCenterY());
		Bindings.bindBidirectional(handShapeRotate.angleProperty(), this.angleProperty);
		handShape.getTransforms().addAll(handShapeRotate);
		handShape.setFill(Color.web("#FF0000"));
		
		final Light.Distant handBaseLight = new Light.Distant();
		handBaseLight.setAzimuth(225);
		final Lighting handBaseLighting = new Lighting();
		handBaseLighting.setLight(handBaseLight);
		handBase.setEffect(handBaseLighting);
		
		handBase.getChildren().addAll(handShape, handDial);
		hand.getChildren().add(handBase);
		gaugeParent.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_ENTERED && event.getTarget() == hand) {
					handColorAdj.setBrightness(0.3);
				} else if (event.isPrimaryButtonDown() && (event.getEventType() == MouseEvent.MOUSE_DRAGGED || 
						(event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() != hand))) {
					handColorAdj.setBrightness(0.3);
					moveHand(handDial.getCenterX() - event.getX(), handDial.getCenterY() - event.getY());
				} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {// || (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET)) {
					handColorAdj.setBrightness(0);
				}
			}
		});
		gaugeParent.getChildren().addAll(hand, createHighlights(outerRimRadius));

		final Label val = new Label("0");
		val.setStyle("-fx-text-fill: white;");
		angleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				valueProperty.set(newValue.doubleValue());
				val.setText(String.valueOf(valueProperty.get()));
			}
		});
		getChildren().addAll(gaugeParent, val);
	}
	
	/**
	 * Creates the outer rim of the gauge
	 * 
	 * @param radius the radius of the gauge center
	 * @param minAngle the minimum angle
	 * @param maxAngle the maximum angle 
	 * @return the outer rim of the gauge
	 */
	protected Shape createOuterRim(final double radius, final double minAngle, final double maxAngle) {
		final Shape og = new Circle(radius, radius, radius);
		og.setSmooth(true);
		// fill border rim
		og.fillProperty().set(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFFFFF")), new Stop(1, Color.web("#010101"))));
		return og;
	}
	
	/**
	 * Creates the center gauge shape (should be either a circle or arc)
	 * 
	 * @param outerRimRadius the outer rim radius
	 * @param radius the radius of the gauge center
	 * @param minAngle the minimum angle
	 * @param maxAngle the maximum angle 
	 * @return the gauge center
	 */
	protected Shape createGaugeCenter(final double outerRimRadius, final double radius, final double minAngle, final double maxAngle) {
		final Shape ig = new Circle(outerRimRadius, outerRimRadius, radius);
		ig.fillProperty().set(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.web("#777777")), new Stop(0.9499, Color.rgb(20, 20, 20)),
				new Stop(0.95, Color.rgb(20, 20, 20)), new Stop(0.975, Color.rgb(20, 20, 20)),
				new Stop(1, Color.rgb(84, 84, 84, 0.0))));
		return ig;
	}
	
	/**
	 * Creates highlights for the control
	 * 
	 * @param radius the radius of the highlights
	 * @return the group highlights
	 */
	protected Group createHighlights(final double radius) {
		final Group highlight = new Group();
		highlight.setCache(true);
		highlight.setCacheHint(CacheHint.SPEED);
		highlight.setOpacity(0.05);
		final Arc hArc1 = new Arc(radius, radius, radius / 1.1, radius / 1.1, 200, -130);
		hArc1.setFill(Color.WHITE);
		final Arc hArc2 = new Arc(radius, radius, radius / 1.1, radius / 1.1, 190, -122);
		hArc2.setFill(Color.WHITE);
		highlight.getChildren().addAll(hArc1, hArc2);
		final GaussianBlur highlightBlur = new GaussianBlur();
		highlightBlur.setRadius(2);
		highlight.setEffect(highlightBlur);
		return highlight;
	}
	
	/**
	 * Adds the tick marks to the gauge
	 * 
	 * @param parent the parent to add the tick marks to
	 * @param gaugeCenter the center shape of the gauge (either circle or arc)
	 * @param numOfMarks the number of marks to add
	 * @param fill the fill for the tick marks
	 * @param width the width of the tick marks
	 * @param height the height of the tick marks
	 * @param offestWidth the pivot offset width
	 * @param offsetHeight the pivot offset height
	 * @param addLabel true to add value labels to the tick marks
	 */
	protected void addTickMarks(final Group parent, final Shape gaugeCenter, final double numOfMarks, final Paint fill,
			final double width, final double height, final double offestWidth, final double offsetHeight, boolean addLabel) {
		final double rtbase = (gaugeCenter instanceof Circle ? 360 : ((Arc) gaugeCenter).getRadiusX()) / numOfMarks;
		double angle = 0;
		Shape tick;
		for (int i=0; i<numOfMarks; i++) {
			angle = rtbase * i;
			tick = createTickMark(gaugeCenter, fill, angle, width, height, offestWidth, offsetHeight);
			parent.getChildren().add(tick);
			if (addLabel) {
				// TODO : add tick mark label option
				final Label lbl = new Label(String.valueOf(angle));
				lbl.setStyle("-fx-text-fill: white;");
				lbl.setTranslateX(tick.getBoundsInParent().getMaxX() - (angle > 90D ? lbl.getMaxWidth() : 0));
				lbl.setTranslateY(tick.getBoundsInParent().getMaxY() - (angle > 90D ? lbl.getMaxHeight() : 0));
				parent.getChildren().add(lbl);
			}
        }
	}
	
	/**
	 * Creates a tick mark
	 * 
	 * @param gaugeCenter the center shape of the gauge (either circle or arc)
	 * @param numOfMarks the number of marks to add
	 * @param fill the fill for the tick marks
	 * @param width the width of the tick marks
	 * @param height the height of the tick marks
	 * @param offestWidth the pivot offset width
	 * @param offsetHeight the pivot offset height
	 * @return the tick mark
	 */
    protected Shape createTickMark(final Shape gaugeCenter, final Paint fill, final double angle, 
    		final double width, final double height, final double offestWidth, final double offsetHeight) {
		final double pivotX = gaugeCenter instanceof Circle ? ((Circle) gaugeCenter).getCenterX() : ((Arc) gaugeCenter).getCenterX();
		final double pivotY = gaugeCenter instanceof Circle ? ((Circle) gaugeCenter).getCenterY() : ((Arc) gaugeCenter).getCenterY();
		final double x = (pivotX - (width / 2)) / offestWidth;
		final double y = pivotY - (height / 2);
    	final Rectangle tm = new Rectangle(x, y, width, height);
    	tm.fillProperty().set(fill);
		tm.getTransforms().addAll(new Rotate(angle, pivotX, pivotY));
		return tm;
    }

    /**
     * Creates the indicator that will be used to point toward the selected numeric value/tick mark.
     * The {@code hand-type} (needle or clock) style will be used to determine how the hand will be drawn.
     * 
     * @param x the hands x coordinate
     * @param y the hands y coordinate
     * @return the created hand
     */
    protected Shape createHand(final double x, final double y, final double width, final double height, final double pointOffset) {
    	switch (handType) {
    	case RECTANGLE: return new Rectangle(x, y, width, height);
    	case CLOCK: return new Polygon(
    			x, y,
    			x - pointOffset, y + (height / 2),  
    			x, y + height, 
    			x + width, y + (height / 2) + (height / 4), 
    			x + width, y + (height / 4));
    	case NEEDLE: default: return new Polygon(
				x, y + (height / 2.5), 
				x, y + height - (height / 2.5), 
				x + width - pointOffset, y + height, 
				x + width, y + (height / 2), 
				x + width - pointOffset, y);
    	}
    }
    
    /**
     * Moves the hand angle based upon an x/y coordinate
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     */
    protected void moveHand(final double x, final double y) {
		double angle = Double.valueOf(anglePrecision.format(Math.toDegrees(Math.atan2(y, x))));
		angleProperty.set(angle);
    }
    
//    public void setValue(final double value) {
//    	this.value = value < 0.0D ? 0 : value > 180 ? 180 : value + 90;
//    	
//    }
    
    /**
     * Hand types
     */
    public enum HandType {
    	RECTANGLE, NEEDLE, CLOCK;
    }
}

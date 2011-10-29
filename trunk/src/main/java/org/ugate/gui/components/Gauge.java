package org.ugate.gui.components;

import java.text.DecimalFormat;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
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
import javafx.scene.shape.Line;
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
	private final double handPointDistance;
	private final double minAngle;
	private final double maxAngle;
	private final DecimalFormat anglePrecision;
	public final DoubleProperty angleProperty;
	public final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
	public final DoubleProperty minorTickMarkOpacityProperty;
	public final DoubleProperty majorTickMarkOpacityProperty;
	public final ObjectProperty<Paint> outerRimFillProperty;
	public final ObjectProperty<Paint> dialCenterFillProperty;
	public final ObjectProperty<Paint> minorTickMarkFillProperty;
	public final ObjectProperty<Paint> majorTickMarkFillProperty;
	public final ObjectProperty<Paint> centerGaugeFillProperty;
	public final ObjectProperty<Paint> handFillProperty;
	
	public Gauge() {
		this(HandType.NEEDLE, 0, 0, 0, 0);
	}
	
	public Gauge(final HandType handType) {
		this(handType, 0, 0, 0, 0);
	}
	
	public Gauge(final HandType handType, final double outerRimRadius, final double gaugeCenterRadius,
			final double minAngle, final double maxAngle) {
		this(handType, outerRimRadius, gaugeCenterRadius, 0, maxAngle, outerRimRadius, 0, 0, minAngle, maxAngle, null);
	}
	
	public Gauge(final HandType handType, final double outerRimRadius, final double gaugeCenterRadius, final int numOfMajorTickMarks, 
			final double majorTickMarkWidth, final double majorTickMarkHeight, final double handHeightFactor,
			final double handPointDistance,
			final double minAngle, final double maxAngle, final DecimalFormat anglePrecision) {
		this.handType = handType;
		this.outerRimRadius = outerRimRadius <= 0 ? 140d : outerRimRadius;
		this.gaugeCenterRadius = gaugeCenterRadius <= 0 ? 135d : gaugeCenterRadius;
		this.numOfMajorTickMarks = numOfMajorTickMarks <= 0 ? 12 : numOfMajorTickMarks;
		this.numOfMinorTickMarks = this.numOfMajorTickMarks * 10;
		this.majorTickMarkWidth = majorTickMarkWidth <= 0 ? 10d : majorTickMarkWidth;
		this.majorTickMarkHeight = majorTickMarkHeight <= 0 ? 2d : majorTickMarkHeight;
		this.minorTickMarkWidth = this.majorTickMarkWidth / 2d;
		this.minorTickMarkHeight = this.minorTickMarkWidth / 2d;
		this.handWidth = this.gaugeCenterRadius - this.minorTickMarkWidth;
		this.handHeight = this.majorTickMarkHeight * (handHeightFactor <= 0 ? 7d : handHeightFactor); 
		this.minAngle = minAngle <= 0 ? 0d : minAngle;
		this.maxAngle = maxAngle > 360 ? 360d : maxAngle;
		this.anglePrecision = anglePrecision == null ?  new DecimalFormat("#.##") : anglePrecision;
		this.angleProperty =  new SimpleDoubleProperty(this.minAngle);
		this.majorTickMarkOpacityProperty = new SimpleDoubleProperty(1d);
		this.minorTickMarkOpacityProperty = new SimpleDoubleProperty(1d);
		this.dialCenterFillProperty = new Line().fillProperty();
		this.dialCenterFillProperty.set(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.WHITE), new Stop(0.3, Color.LIGHTGRAY),
				new Stop(0.7, Color.DARKGRAY), new Stop(1, Color.WHITE.brighter())));
		this.minorTickMarkFillProperty = new Line().fillProperty();
		this.minorTickMarkFillProperty.set(Color.web("#AAAAAA"));
		this.majorTickMarkFillProperty = new Line().fillProperty();
		this.majorTickMarkFillProperty.set(Color.web("#CCCCCC"));
		this.outerRimFillProperty = new Line().fillProperty();
		this.outerRimFillProperty.set(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.web("#FFFFFF")), new Stop(1, Color.web("#010101"))));
		this.centerGaugeFillProperty = new Line().fillProperty();
		this.centerGaugeFillProperty.set(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.web("#777777")), new Stop(0.9499, Color.rgb(20, 20, 20)),
				new Stop(0.95, Color.rgb(20, 20, 20)), new Stop(0.975, Color.rgb(20, 20, 20)),
				new Stop(1, Color.rgb(84, 84, 84, 0.0))));
		this.handFillProperty = new Line().fillProperty();
		this.handFillProperty.set(Color.ORANGERED);
		this.handPointDistance = handPointDistance <= 0 ? (this.majorTickMarkHeight * 4) : handPointDistance;
		createChildren();
	}
	
	protected void createChildren() {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		// create basic gauge shapes
		final Shape ourterRim = createOuterRim(outerRimRadius, minAngle, maxAngle, outerRimFillProperty);
		final Shape gaugeCenter = createGaugeCenter(outerRimRadius, gaugeCenterRadius, minAngle, maxAngle,
				centerGaugeFillProperty);
		final Group gaugeParent = createGaugeParent(ourterRim, gaugeCenter);
		// add minor tick marks
		addTickMarks(gaugeParent, gaugeCenter, numOfMinorTickMarks, minorTickMarkFillProperty, minorTickMarkWidth, 
				minorTickMarkHeight, majorTickMarkWidth, majorTickMarkHeight, false, minorTickMarkOpacityProperty);
		// add major tick marks
		addTickMarks(gaugeParent, gaugeCenter, numOfMajorTickMarks, majorTickMarkFillProperty, majorTickMarkWidth, 
				majorTickMarkHeight, majorTickMarkWidth, majorTickMarkHeight, false, majorTickMarkOpacityProperty);
		
		// create hand
		final Group hand = createHand(gaugeParent, centerX(ourterRim), centerY(ourterRim), 
				handPointDistance, handFillProperty);
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
	 * {@inheritDoc}
	 */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
	}
	
	/**
	 * Creates the initial gauge parent group that will hold the rim and center gauge background
	 * 
	 * @return the gauge parent
	 */
	protected final Group createGaugeParent(final Shape ourterRim, final Shape gaugeCenter) {
		final Group gaugeParent = new Group();
		gaugeParent.setCache(true);
		gaugeParent.setCacheHint(CacheHint.SPEED);
		gaugeParent.getChildren().addAll(ourterRim, gaugeCenter);
		return gaugeParent;
	}
	
	/**
	 * Creates the outer rim of the gauge
	 * 
	 * @param radius the radius of the gauge center
	 * @param minAngle the minimum angle
	 * @param maxAngle the maximum angle 
	 * @param outerRimFillProperty the outer rim fill property to bind
	 * @return the outer rim of the gauge
	 */
	protected Shape createOuterRim(final double radius, final double minAngle, final double maxAngle, 
			final ObjectProperty<Paint> outerRimFillProperty) {
		final Shape rim = new Circle(radius, radius, radius);
		rim.setSmooth(true);
		Bindings.bindBidirectional(rim.fillProperty(), outerRimFillProperty);
		return rim;
	}
	
	/**
	 * Creates the center gauge shape (should be either a circle or arc)
	 * 
	 * @param outerRimRadius the outer rim radius
	 * @param radius the radius of the gauge center
	 * @param minAngle the minimum angle
	 * @param maxAngle the maximum angle
	 * @param centerGaugeFillProperty the center of the gauge fill property to bind to
	 * @return the gauge center
	 */
	protected Shape createGaugeCenter(final double outerRimRadius, final double radius, final double minAngle, 
			final double maxAngle, final ObjectProperty<Paint> centerGaugeFillProperty) {
		final Shape centerGauge = new Circle(outerRimRadius, outerRimRadius, radius);
		Bindings.bindBidirectional(centerGauge.fillProperty(), centerGaugeFillProperty);
		return centerGauge;
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
	 * Creates the hand
	 * 
	 * @param gaugeParent the gauge parent group used to discover mouse events for moving the hand
	 * @param centerX the center x coordinate of the gauge
	 * @param centerY the center y coordinate of the gauge
     * @param pointDistance the distance from the tip of the hand shape to the arm of the hand shape 
     * 		(the sharpness of the hand pointer)
     * @param handFillProperty the hand fill property to bind to
	 * @return the hand
	 */
	protected final Group createHand(final Group gaugeParent, final double centerX, final double centerY,
			final double pointDistance, final ObjectProperty<Paint> handFillProperty) {
		final Group hand = new Group();
		hand.setCache(true);
		hand.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		final ColorAdjust handColorAdj = new ColorAdjust();
		handColorAdj.setBrightness(0);
		final DropShadow handDropShadow = new DropShadow();
		handDropShadow.setOffsetX(4);
		handDropShadow.setOffsetY(4);
		handDropShadow.setRadius(7);
		handDropShadow.setColor(Color.web("#000000"));
		handDropShadow.setInput(handColorAdj);
		hand.setEffect(handDropShadow);
		gaugeParent.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_ENTERED && event.getTarget() == hand) {
					handColorAdj.setBrightness(0.2);
				} else if (event.isPrimaryButtonDown() && (event.getEventType() == MouseEvent.MOUSE_DRAGGED || 
						(event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() != hand))) {
					handColorAdj.setBrightness(0.2);
					moveHand(centerX - event.getX(), centerY - event.getY());
				} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {// || (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET)) {
					handColorAdj.setBrightness(0);
				}
			}
		});
		final Group handBase = new Group();
		final Polygon handDial = createDial(centerX, centerY, 24, 10, 10, 0);
		final double hx = centerX - (handWidth / 1.2);
		final double hy = centerY - (handHeight / 2);
		//final Rectangle hrec = new Rectangle(hx, hy, handWidth, handHeight);
		final Shape handShape = createHandShape(hx, hy, handWidth, handHeight, pointDistance);
		final Rotate handRotate = new Rotate(this.angleProperty.get(), centerX, centerY);
		Bindings.bindBidirectional(handRotate.angleProperty(), this.angleProperty);
		handShape.getTransforms().addAll(handRotate);
		Bindings.bindBidirectional(handShape.fillProperty(), handFillProperty);
		
		final Light.Distant handBaseLight = new Light.Distant();
		handBaseLight.setAzimuth(225);
		final Lighting handBaseLighting = new Lighting();
		handBaseLighting.setLight(handBaseLight);
		handBase.setEffect(handBaseLighting);
		
		handBase.getChildren().addAll(handShape, handDial);
		hand.getChildren().add(handBase);
		return hand;
	}
	
	/**
	 * Adds the tick marks to the gauge
	 * 
	 * @param parent the parent to add the tick marks to
	 * @param gaugeCenter the center shape of the gauge (either circle or arc)
	 * @param numOfMarks the number of marks to add
	 * @param tickMarkFillProperty the fill property to bind for the tick marks
	 * @param width the width of the tick marks
	 * @param height the height of the tick marks
	 * @param offestWidth the pivot offset width
	 * @param offsetHeight the pivot offset height
	 * @param tickMarkOpacityProperty tick mark opacity property to bind to the opacity of the tick mark
	 * @param addLabel true to add value labels to the tick marks
	 */
	protected void addTickMarks(final Group parent, final Shape gaugeCenter, final double numOfMarks, 
			final ObjectProperty<Paint> tickMarkFillProperty, final double width, final double height, 
			final double offestWidth, final double offsetHeight, boolean addLabel,
			final DoubleProperty tickMarkOpacityProperty) {
		final double rtbase = (gaugeCenter instanceof Circle ? 360 : ((Arc) gaugeCenter).getRadiusX()) / numOfMarks;
		double angle = 0;
		Shape tick;
		for (int i=0; i<numOfMarks; i++) {
			angle = rtbase * i;
			tick = createTickMark(gaugeCenter, tickMarkFillProperty, angle, width, height, offestWidth, offsetHeight,
					tickMarkOpacityProperty);
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
	 * @param tickMarkFillProperty the fill property to bind for the tick marks
	 * @param width the width of the tick marks
	 * @param height the height of the tick marks
	 * @param offestWidth the pivot offset width
	 * @param offsetHeight the pivot offset height
	 * @param tickMarkOpacityProperty tick mark opacity property to bind to the opacity of the tick mark
	 * @return the tick mark
	 */
    protected Shape createTickMark(final Shape gaugeCenter, final ObjectProperty<Paint> tickMarkFillProperty, 
    		final double angle, final double width, final double height, final double offestWidth, 
    		final double offsetHeight, final DoubleProperty tickMarkOpacityProperty) {
		final double pivotX = gaugeCenter instanceof Circle ? ((Circle) gaugeCenter).getCenterX() : ((Arc) gaugeCenter).getCenterX();
		final double pivotY = gaugeCenter instanceof Circle ? ((Circle) gaugeCenter).getCenterY() : ((Arc) gaugeCenter).getCenterY();
		final double x = (pivotX - (width / 2)) / offestWidth;
		final double y = pivotY - (height / 2);
    	final Rectangle tm = new Rectangle(x, y, width, height);
    	Bindings.bindBidirectional(tm.fillProperty(), tickMarkFillProperty);
		tm.getTransforms().addAll(new Rotate(angle, pivotX, pivotY));
		Bindings.bindBidirectional(tm.opacityProperty(), tickMarkOpacityProperty);
		return tm;
    }

    /**
     * Creates the indicator that will be used to point toward the selected numeric value/tick mark.
     * The {@code hand-type} (needle or clock) style will be used to determine how the hand will be drawn.
     * 
     * @param x the hands x coordinate
     * @param y the hands y coordinate
     * @param width the width of the hand shape
     * @param height the height of the hand shape
     * @param pointDistance the distance from the tip of the hand shape to the arm of the hand shape 
     * 		(the sharpness of the hand pointer)
     * @return the created hand
     */
    protected Shape createHandShape(final double x, final double y, final double width, final double height, 
    		final double pointDistance) {
    	switch (handType) {
    	case RECTANGLE: return new Rectangle(x, y, width, height);
    	case CLOCK: return new Polygon(
    			x, y,
    			x - pointDistance, y + (height / 2),  
    			x, y + height, 
    			x + width, y + (height / 2) + (height / 4), 
    			x + width, y + (height / 4));
    	case ROTARYDIAL: new Polygon(
    			x, y,
    			x - pointDistance, y + (height / 2),  
    			x, y + height, 
    			x + width, y + (height / 2) + (height / 4), 
    			x + width, y + (height / 4));
    	case NEEDLE: default: return new Polygon(
				x, y + (height / 2.5), 
				x, y + height - (height / 2.5), 
				x + width - pointDistance, y + height, 
				x + width, y + (height / 2), 
				x + width - pointDistance, y);
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
	 * Creates the dial shape that appears in the center of the gauge
	 * 
	 * @param x x coordinate of the center of the dial
	 * @param y y coordinate of the center of the dial
	 * @param numOfSides number of teeth on dial
	 * @param innerRadius inner radius of the dial teeth.
	 * @param outerRadius outer radius of the dial teeth
	 * @param beginAngle begin angle in degrees
	 */
	protected Polygon createDial(final double x, final double y, final int numOfSides, final double innerRadius, 
			final double outerRadius, final double beginAngle) {
		//final Circle handDial = new Circle(outerRimRadius, outerRimRadius, handHeight / 1.3);
		final double teethSlope = (Math.PI * 2) / numOfSides;
		final double teethQuarterSlope = teethSlope / 4;
		final double angle = (beginAngle / 180) * Math.PI;
		final double[] points = new double[numOfSides * 8];
		int p = -1;
		for (int sideCnt=1; sideCnt<=numOfSides; sideCnt++) {
			points[++p] = x + Math.cos(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 3)) * innerRadius;
			points[++p] = y - Math.sin(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 3)) * innerRadius;
			points[++p] = x + Math.cos(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 2)) * innerRadius;
			points[++p] = y - Math.sin(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 2)) * innerRadius;
			points[++p] = x + Math.cos(angle + (teethSlope * sideCnt) - teethQuarterSlope) * outerRadius;
			points[++p] = y - Math.sin(angle + (teethSlope * sideCnt) - teethQuarterSlope) * outerRadius;
			points[++p] = x + Math.cos(angle + (teethSlope * sideCnt)) * outerRadius;
			points[++p] = y - Math.sin(angle + (teethSlope * sideCnt)) * outerRadius;
		}
		final Polygon dial = new Polygon(points);
		dial.setCache(true);
		dial.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		Bindings.bindBidirectional(dial.fillProperty(), dialCenterFillProperty);
		return dial;
	}
	
	protected static final double centerX(final Shape shape) {
		return shape instanceof Circle ? ((Circle) shape).getCenterX() : ((Arc) shape).getCenterX();
	}
	
	protected static final double centerY(final Shape shape) {
		return shape instanceof Circle ? ((Circle) shape).getCenterY() : ((Arc) shape).getCenterY();
	}
    
    /**
     * Hand types
     */
    public enum HandType {
    	RECTANGLE, NEEDLE, CLOCK, ROTARYDIAL;
    }
}

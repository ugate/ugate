package org.ugate.gui.components;

import java.math.BigDecimal;

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
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;

/**
 * Gauge control
 */
public class Gauge extends Group {

	public static final double RADIUS_OUTER_BASE = 140d;
	public static final double RADIUS_INNER_BASE = 130d;
	protected static final double START_END_DISTANCE_THRSHOLD = 30d;
	private final HandType handType;
	private final int numOfMajorTickMarks;
	private final int numOfMinorTickMarks;
	private final double majorTickMarkWidth;
	private final double majorTickMarkHeight;
	private final double minorTickMarkWidth;
	private final double minorTickMarkHeight;
	private final double handWidth;
	private final double handHeight;
	private final double handPointDistance;
	private final int dialNumberOfSides;
	private final double dialCenterInnerRadius;
	private final double dialCenterOuterRadius;
	protected final double outerRadius;
	protected final double innerRadius;
	protected final double angleStart;
	protected final double angleLength;
	protected final double centerX;
	protected final double centerY;
	protected final int anglePrecision;
	public final DoubleProperty angleProperty;
	public final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
	public final DoubleProperty minorTickMarkOpacityProperty;
	public final DoubleProperty majorTickMarkOpacityProperty;
	public final ObjectProperty<Paint> outerRimFillProperty;
	public final ObjectProperty<Paint> outerRimArcFillProperty;
	public final ObjectProperty<Paint> dialCenterFillProperty;
	public final ObjectProperty<Paint> minorTickMarkFillProperty;
	public final ObjectProperty<Paint> majorTickMarkFillProperty;
	public final ObjectProperty<Paint> centerGaugeFillProperty;
	public final ObjectProperty<Paint> handFillProperty;
	public final DoubleProperty dialCenterOpacityProperty;
	
	public Gauge() {
		this(HandType.NEEDLE, 0, 0, 0);
	}
	
	public Gauge(final HandType handType) {
		this(handType, 0, 0, 0);
	}
	
	public Gauge(final HandType handType, final double scale,
			final double startAngle, final double endAngle) {
		this(handType, scale, 0, 0, 0, startAngle, endAngle, 0);
	}
	
	public Gauge(final HandType handType, final double scale, final int dialNumberOfSides, 
			final double dialCenterInnerRadius, final double dialCenterOuterRadius,
			final double startAngle, final double angleLength, final int anglePrecision) {
		this.handType = handType;
		this.outerRadius = RADIUS_OUTER_BASE * scale;
		this.innerRadius = RADIUS_INNER_BASE * scale;
		this.centerX = this.outerRadius / 2d;
		this.centerY = this.centerX;
		this.angleStart = startAngle == 0 && angleLength == 0 ? 0 : positiveAngle(startAngle);
		this.angleLength = startAngle == 0 && angleLength == 0 ? 360d : positiveAngle(angleLength);
		this.anglePrecision = Math.max(0, anglePrecision);
		this.numOfMajorTickMarks = (int)this.angleLength / 30; // TODO : scale the number of tick marks based upon the min/max value/angle
		this.numOfMinorTickMarks = this.numOfMajorTickMarks * 10;
		this.majorTickMarkWidth = 12d * scale;
		this.majorTickMarkHeight = 2d * scale;
		this.minorTickMarkWidth = this.majorTickMarkWidth / 2d;
		this.minorTickMarkHeight = this.majorTickMarkHeight;
		this.handWidth = this.innerRadius;// - this.minorTickMarkWidth;
		this.handHeight = this.majorTickMarkHeight * 10d;
		this.handPointDistance = this.majorTickMarkHeight * 4;
		this.dialNumberOfSides = dialNumberOfSides <= 0 ? 10 : dialNumberOfSides;
		this.dialCenterInnerRadius = dialCenterInnerRadius <= ((this.handHeight/1.5d) * scale) ? ((this.handHeight/1.5d) * scale) : dialCenterInnerRadius;
		this.dialCenterOuterRadius = dialCenterOuterRadius <= ((this.handHeight/1.4d) * scale) ? ((this.handHeight/1.4d) * scale) : dialCenterOuterRadius;
		this.angleProperty =  new SimpleDoubleProperty(Math.min(getViewingStartAngle(), getViewingEndAngle()));
		this.majorTickMarkOpacityProperty = new SimpleDoubleProperty(1d);
		this.minorTickMarkOpacityProperty = new SimpleDoubleProperty(1d);
		this.dialCenterFillProperty = new Line().fillProperty();
		this.dialCenterFillProperty.set(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.LIGHTCYAN), new Stop(0.3, Color.DARKGRAY),
				new Stop(0.7, Color.DARKGRAY), new Stop(1, Color.WHITE)));
		this.minorTickMarkFillProperty = new Line().fillProperty();
		this.minorTickMarkFillProperty.set(Color.LIGHTCYAN);
		this.majorTickMarkFillProperty = new Line().fillProperty();
		this.majorTickMarkFillProperty.set(Color.LIGHTCYAN);
		this.outerRimFillProperty = new Line().fillProperty();
		this.outerRimFillProperty.set(new RadialGradient(0, 0, this.centerX, this.centerY, 
				this.outerRadius, false, CycleMethod.REPEAT, 
				new Stop(0.25d, Color.LIGHTGRAY), new Stop(0.26d, Color.DARKGRAY), new Stop(0.27d, Color.LIGHTGRAY),
				new Stop(0.95d, Color.LIGHTGRAY), new Stop(0.97d, Color.DARKGRAY), new Stop(1d, Color.LIGHTGRAY)));
		this.centerGaugeFillProperty = new Line().fillProperty();
		this.centerGaugeFillProperty.set(new RadialGradient(0, 0, this.centerX, this.centerY, 
				this.innerRadius, false, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.LIGHTCYAN), new Stop(0.8d, Color.BLACK)));
		this.handFillProperty = new Line().fillProperty();
		this.handFillProperty.set(Color.ORANGERED);
		this.dialCenterOpacityProperty = new SimpleDoubleProperty(1);
		this.outerRimArcFillProperty = new Line().fillProperty();
		createChildren();
	}
	
	protected final void createChildren() {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		// create basic gauge shapes
		final Shape gaugeCenter = createGaugeCenter(outerRadius, innerRadius, centerGaugeFillProperty, outerRimFillProperty);
		final Group gaugeParent = createGaugeParent(gaugeCenter);

		// create angle/value label
		final Label val = new Label(String.valueOf(angleProperty.get()));
		val.setTranslateX(10);
		val.setStyle("-fx-text-fill: white;");
		angleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				valueProperty.set(anglePrecision == 0 ? newValue.intValue() : newValue.doubleValue());
				val.setText(String.valueOf(anglePrecision == 0 ? newValue.intValue() : newValue.doubleValue()));
			}
		});
		gaugeParent.getChildren().add(val);
		
		// add minor tick marks
		addTickMarks(gaugeParent, numOfMinorTickMarks, minorTickMarkFillProperty, minorTickMarkWidth, 
				minorTickMarkHeight, minorTickMarkWidth, minorTickMarkHeight, false, minorTickMarkOpacityProperty);
		// add major tick marks
		addTickMarks(gaugeParent, numOfMajorTickMarks, majorTickMarkFillProperty, majorTickMarkWidth, 
				majorTickMarkHeight, minorTickMarkWidth, minorTickMarkHeight, false, majorTickMarkOpacityProperty);
		
		// create hand
		final Group hand = createHand(gaugeParent, handPointDistance, handFillProperty);
		gaugeParent.getChildren().addAll(hand);//, createHighlights(outerRadius));
		hand.getRotate();
		
		getChildren().add(gaugeParent);
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
	 * @param gaugeCenter the center of the gauge display
	 * @return the gauge parent
	 */
	protected final Group createGaugeParent(final Shape gaugeCenter) {
		final Group gaugeParent = new Group();
		gaugeParent.setCache(true);
		gaugeParent.setCacheHint(CacheHint.SPEED);
		gaugeParent.getChildren().addAll(gaugeCenter);
		return gaugeParent;
	}
	
	/**
	 * Creates the center gauge shape (should be either a circle or arc)
	 * 
	 * @param outerRimRadius the outer rim radius
	 * @param radius the radius of the gauge center
	 * @param centerGaugeFillProperty the center of the gauge fill property to bind to
	 * @param strokeFillProperty the fill property to bind the stroke to
	 * @return the gauge center
	 */
	protected Shape createGaugeCenter(final double outerRimRadius, final double radius, 
			final ObjectProperty<Paint> centerGaugeFillProperty, final ObjectProperty<Paint> strokeFillProperty) {
		if (isCircular()) {
			final Circle ccg = new Circle(this.centerX, this.centerY, radius);
			addGaugeStroke(ccg, strokeFillProperty);
			Bindings.bindBidirectional(ccg.fillProperty(), centerGaugeFillProperty);
			return ccg;
		} else {
			final Arc acg = new Arc(this.centerX, this.centerY, radius, radius, 
					this.angleStart, this.angleLength);
			acg.setType(ArcType.ROUND);
			final Shape acf = new Circle(centerX, centerY, this.dialCenterOuterRadius * 4.5d);
			final Shape cg = Shape.union(acg, acf);
			cg.setEffect(createLighting());
			addGaugeStroke(cg, strokeFillProperty);
			Bindings.bindBidirectional(cg.fillProperty(), centerGaugeFillProperty);
			return cg;
		}
	}
	
	/**
	 * Adds the stroke to the the gauge to form a rim
	 * 
	 * @param cg the gauge shape
	 * @param strokeFillProperty the fill property to bind the stroke to
	 */
	protected void addGaugeStroke(final Shape cg, final ObjectProperty<Paint> strokeFillProperty) {
		cg.setStrokeType(StrokeType.OUTSIDE);
		cg.setStrokeLineCap(StrokeLineCap.ROUND);
		cg.setStrokeLineJoin(StrokeLineJoin.ROUND);
		cg.setStrokeWidth(outerRadius - innerRadius);
		cg.setStroke(Color.GREEN);
		Bindings.bindBidirectional(cg.strokeProperty(), strokeFillProperty);
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
		highlight.setOpacity(0.05d);
		final Arc hArc1 = new Arc(this.centerX, this.centerY, radius, radius, 
				this.angleStart, this.angleLength);
		hArc1.setFill(Color.RED);
		final Arc hArc2 = new Arc(this.centerX, this.centerY, radius, radius, 
				this.angleStart, this.angleLength);
		hArc2.setFill(Color.WHITE);
		highlight.getChildren().addAll(hArc1);//, hArc2);
		final GaussianBlur highlightBlur = new GaussianBlur();
		highlightBlur.setRadius(2);
		highlight.setEffect(highlightBlur);
		return highlight;
	}
	
	/**
	 * @return the lighting applied to the hand group
	 */
	protected Lighting createLighting() {
		final Light.Distant handBaseLight = new Light.Distant();
		handBaseLight.setAzimuth(270d);
		handBaseLight.setElevation(70d);
		final Lighting handBaseLighting = new Lighting();
		handBaseLighting.setLight(handBaseLight);
		return handBaseLighting;
	}
	
	/**
	 * Creates the hand
	 * 
	 * @param gaugeParent the gauge parent group used to discover mouse events for moving the hand
     * @param pointDistance the distance from the tip of the hand shape to the arm of the hand shape 
     * 		(the sharpness of the hand pointer)
     * @param handFillProperty the hand fill property to bind to
	 * @return the hand
	 */
	protected final Group createHand(final Group gaugeParent, final double pointDistance, 
			final ObjectProperty<Paint> handFillProperty) {
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
					handColorAdj.setBrightness(0.2d);
				} else if (event.isPrimaryButtonDown() && (event.getEventType() == MouseEvent.MOUSE_DRAGGED || 
						(event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() != hand))) {
					handColorAdj.setBrightness(0.2d);
					moveHand(centerX - event.getX(), centerY - event.getY());
				} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {// || (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET)) {
					handColorAdj.setBrightness(0d);
				}
			}
		});
		final Group handBase = new Group();
		final double hx = centerX - (handWidth / 1.2);
		final double hy = centerY - (handHeight / 2);
		
		final Shape handShape = createHandShape(handType, hx, hy, handWidth, handHeight, pointDistance,
				centerX, centerY, handType == HandType.ROTARYDIAL ? centerGaugeFillProperty : handFillProperty);
		final Rotate handRotate = new Rotate(this.angleProperty.get(), centerX, centerY);
		Bindings.bindBidirectional(handRotate.angleProperty(), this.angleProperty);
		if (handType == HandType.ROTARYDIAL) {
	    	final Group handShapeGroup = createRotaryDialHandShape(handShape, hx, hy, 
					handWidth, handHeight, pointDistance, centerX, centerY, handFillProperty);
			handShapeGroup.getTransforms().addAll(handRotate);
			handBase.getChildren().add(handShapeGroup);
		} else {
			handShape.getTransforms().addAll(handRotate);
			handBase.getChildren().add(handShape);
		}
		handBase.setEffect(createLighting());
		
		final Circle handDialCenter = new Circle(centerX, centerY, dialCenterOuterRadius);
		Bindings.bindBidirectional(handDialCenter.fillProperty(), dialCenterFillProperty);
		Bindings.bindBidirectional(handDialCenter.opacityProperty(), dialCenterOpacityProperty);
		handBase.getChildren().add(handDialCenter);
		hand.getChildren().add(handBase);
		return hand;
	}
	
	/**
	 * Adds the tick marks to the gauge
	 * 
	 * @param parent the parent to add the tick marks to
	 * @param numOfMarks the number of marks to add
	 * @param tickMarkFillProperty the fill property to bind for the tick marks
	 * @param width the width of the tick marks
	 * @param height the height of the tick marks
	 * @param offestWidth the pivot offset width
	 * @param offsetHeight the pivot offset height
	 * @param tickMarkOpacityProperty tick mark opacity property to bind to the opacity of the tick mark
	 * @param addLabel true to add value labels to the tick marks
	 */
	protected void addTickMarks(final Group parent, final double numOfMarks, 
			final ObjectProperty<Paint> tickMarkFillProperty, final double width, final double height, 
			final double offestWidth, final double offsetHeight, boolean addLabel,
			final DoubleProperty tickMarkOpacityProperty) {
		final double rtbase = angleLength / numOfMarks;
		double angle = 0;
		Shape tick;
		for (int i=0; i<=numOfMarks; i++) {
			angle = 180d - (rtbase * i) - angleStart + height / 2d;
			tick = createTickMark(tickMarkFillProperty, angle, width, height, offestWidth, offsetHeight,
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
	 * @param tickMarkFillProperty the fill property to bind for the tick mark
	 * @param angle the angle of the tick mark
	 * @param width the width of the tick mark
	 * @param height the height of the tick mark
	 * @param offestWidth the pivot offset width
	 * @param offsetHeight the pivot offset height
	 * @param tickMarkOpacityProperty tick mark opacity property to bind to the opacity of the tick mark
	 * @return the tick mark
	 */
    protected Shape createTickMark(final ObjectProperty<Paint> tickMarkFillProperty, 
    		final double angle, final double width, final double height, final double offestWidth, 
    		final double offsetHeight, final DoubleProperty tickMarkOpacityProperty) {
		final double x = (centerX + width - outerRadius) + offestWidth;
		final double y = (centerY - height / 2) + offsetHeight;
    	final Rectangle tm = new Rectangle(x, y, width, height);
    	Bindings.bindBidirectional(tm.fillProperty(), tickMarkFillProperty);
		tm.getTransforms().addAll(new Rotate(angle, centerX, centerY));
		Bindings.bindBidirectional(tm.opacityProperty(), tickMarkOpacityProperty);
		return tm;
    }

    /**
     * Creates the indicator that will be used to point toward the selected numeric value/tick mark.
     * The {@code #handType} will be used to determine how the hand will be drawn.
     * 
     * @param handType the hand type
     * @param x the hands x coordinate
     * @param y the hands y coordinate
     * @param width the width of the hand shape
     * @param height the height of the hand shape
     * @param pointDistance the distance from the tip of the hand shape to the arm of the hand shape 
     * 		(the sharpness of the hand pointer)
     * @param gaugeCenterX a reference point for the overall gauge center x coordinate
     * @param gaugeCenterY a reference point for the overall gauge center y coordinate
     * @param handFillProperty the hand shape fill property to bind to
     * @return the created hand
     */
    protected Shape createHandShape(final HandType handType, final double x, final double y, 
    		final double width, final double height, final double pointDistance, 
    		final double gaugeCenterX, final double gaugeCenterY,
    		final ObjectProperty<Paint> handFillProperty) {
    	Shape handShape;
    	switch (handType) {
    	case RECTANGLE: handShape = new Rectangle(x, y, width, height);
    		Bindings.bindBidirectional(handShape.fillProperty(), handFillProperty);
    		break;
    	case CLOCK: handShape = new Polygon(
    			x, y,
    			x - pointDistance, y + (height / 2),  
    			x, y + height, 
    			x + width, y + (height / 2) + (height / 4), 
    			x + width, y + (height / 4));
    		Bindings.bindBidirectional(handShape.fillProperty(), handFillProperty);
    		break;
    	case ROTARYDIAL: handShape = createDial(gaugeCenterX, gaugeCenterY, dialNumberOfSides, 
    			width - (pointDistance * 2) - ((dialCenterOuterRadius - dialCenterInnerRadius) * height), 
    			width - (pointDistance * 2), 0, handFillProperty);
    		break;
    	case NEEDLE: default: handShape = new Polygon(
				x, y + (height / 2.5), 
				x, y + height - (height / 2.5), 
				x + width - pointDistance, y + height, 
				x + width, y + (height / 2), 
				x + width - pointDistance, y);
    		Bindings.bindBidirectional(handShape.fillProperty(), handFillProperty);
    		break;
    	}
    	return handShape;
    	
    }
    
    /**
	 * Creates a rotary dial hand shape
	 * 
	 * @param dialHandShape
	 *            the dial hand shape from
	 *            {@linkplain #createHandShape(HandType, double, double, double, double, double, double, double, ObjectProperty)}
	 * @param x the x coordinate of the indicator point
	 * @param y the x coordinate of the indicator point
	 * @param width the width of the indicator point
	 * @param height the height of the indicator point
	 * @param pointDistance the point distance offset
	 * @param gaugeCenterX the center of the gauge x coordinate
	 * @param gaugeCenterY the center of the gauge y coordinate
	 * @param handFillProperty the fill property to bind to
	 * @return the rotary dial
	 */
    protected Group createRotaryDialHandShape(final Shape dialHandShape, 
    		final double x, final double y, final double width, final double height, 
    		final double pointDistance, final double gaugeCenterX, final double gaugeCenterY,
    		final ObjectProperty<Paint> handFillProperty) {
    	final Group handShapeGroup = new Group();
    	handShapeGroup.setCache(true);
    	handShapeGroup.setCacheHint(CacheHint.ROTATE);
		final Shape handIndicatorShape = new Polygon(
    			x, y,
    			x - pointDistance, y + (handHeight / 2),  
    			x, y + handHeight, 
    			x + (handWidth / 4), y + (handHeight / 2) + (handHeight / 4), 
    			x + (handWidth / 4), y + (handHeight / 4));
		handIndicatorShape.setEffect(createLighting());
    	Bindings.bindBidirectional(handIndicatorShape.fillProperty(), handFillProperty);
		handShapeGroup.getChildren().addAll(dialHandShape, handIndicatorShape);
		return handShapeGroup;
    }
    
	/**
	 * Creates a dial shape that appears in the center of the gauge
	 * 
	 * @param x x coordinate of the center of the dial
	 * @param y y coordinate of the center of the dial
	 * @param numOfSides number of teeth on dial
	 * @param innerRadius inner radius of the dial teeth.
	 * @param outerRadius outer radius of the dial teeth
	 * @param beginAngle begin angle in degrees
	 * @param dialFillProperty the dial fill property to bind to
	 * @return the dial
	 */
	protected Polygon createDial(final double x, final double y, final int numOfSides, final double innerRadius, 
			final double outerRadius, final double beginAngle, final ObjectProperty<Paint> dialFillProperty) {
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
		dial.setCacheHint(CacheHint.ROTATE);
		Bindings.bindBidirectional(dial.fillProperty(), dialFillProperty);
		return dial;
	}
	
    /**
     * Moves the hand angle based upon an x/y coordinate
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the coordinates are within the min/max angles and the hand has been moved
     */
    protected boolean moveHand(final double x, final double y) {
    	double viewingAngle = Math.toDegrees(Math.atan2(y, x));
    	// convert angle to positive quadrants 0 - 360 degrees
		if (viewingAngle < 0) {
			viewingAngle += 360d;
		}
		return projectHandAngle(viewingAngle);
    }
    
    /**
     * Sets the projected angle of the hand when the angle is within the min/max range
     *  
     * @param viewingAngle the angle in which a typical gauge is viewed where zero starts on the west horizontal plane
     * @return true if the angle is within the min/max angles and the angle property has been set
     */
    protected boolean projectHandAngle(final double viewingAngle) {
    	if (isCircular()) {
    		// angle will always be with in 360 range
    		angleProperty.set(scaleAngle(viewingAngle, BigDecimal.ROUND_HALF_UP));
    		return true;
    	}
    	double trigAngle = reverseAngle(viewingAngle);
    	double startAngle = getTrigStartAngle();
    	double endAngle = getTrigEndAngle();
//    	System.out.println(String.format("angleStart: %1$s, angleLength: %2$s, viewingAngle: %3$s, startAngle: %4$s, endAngle: %5$s, trigAngle: %6$s", 
//    			angleStart, angleLength, viewingAngle, startAngle, endAngle, trigAngle));
    	if ((startAngle <= endAngle && trigAngle >= startAngle && trigAngle <= endAngle) ||
    		(startAngle > endAngle && (trigAngle >= startAngle || trigAngle <= endAngle))) {
    		// update the viewing angle using the predefined precision
    		angleProperty.set(scaleAngle(viewingAngle, BigDecimal.ROUND_HALF_UP));
    		return true;
    	}
    	// determine if the angle is closer to the start or end angle (accounting for 0-360 and 360-0 transition)
    	double startMax = positiveAngle(startAngle - START_END_DISTANCE_THRSHOLD);
		double endMax = positiveAngle(endAngle - START_END_DISTANCE_THRSHOLD);
    	double closestAngle = closestAngle(trigAngle, startAngle, endAngle);
		// only make the move when the position is within the start angle threshold
		if (closestAngle == startAngle && Math.abs(trigAngle - startAngle) <= START_END_DISTANCE_THRSHOLD) {
			angleProperty.set(reverseAngle(startAngle));
			return true;
		}
		// only make the move when the position is within the end angle threshold
		if (closestAngle == endAngle && Math.abs(trigAngle - endAngle) <= START_END_DISTANCE_THRSHOLD) {
			angleProperty.set(reverseAngle(endAngle));
			return true;
		}
    	return false;
    }
    
    /**
     * Returns the closest angle to the supplied angle
     * 
     * @param angle the angle to check against
     * @param angles the angles to check
     * @return the angle that is closest to the angle checked against
     */
    protected static final double closestAngle(final double angle, final double... angles) {
    	Double closestValue = null;
    	double ca, la = -1;
    	for (double a : angles) {
    		ca = Math.abs(angle - a);
    		if (closestValue == null || ca < la) {
    			closestValue = a;
    		}
    		la = ca;
    	}
    	return closestValue;
    }
    
    /**
     * Scales an angle based upon the angle precision
     * 
     * @param angle the angle to scale
     * @param bigDecimalRoundingMode the rounding mode
     * @return the scaled angle
     */
    protected final double scaleAngle(final double angle, final int bigDecimalRoundingMode) {
    	return new BigDecimal(angle).setScale(anglePrecision, bigDecimalRoundingMode).doubleValue();
    }
    
    /**
     * Converts the angle to a positive angle value (when negative)
     * 
     * @param angle the angle to be converted
     * @return the positive angle
     */
    public static final double positiveAngle(final double angle) {
    	return angle < 0 ? 360d + angle : angle;
    }
    
    /**
     * Reverses an angle. For example, if an angle has a zero quadrant position on the <b>east</b> horizontal plane the return angle
     * will have a zero position on the <b>west</b> horizontal plane.
     * 
     * @param angle the angle to reverse
     * @return the reversed angle
     */
    public static final double reverseAngle(final double angle) {
    	return 180d - angle < 0 ? 180d - angle + 360d : 180d - angle;
    }
    
    /**
     * @return the start angle within the gauges range relative to the normal trigonometry angle where an 
     * 		angle of zero is in the east horizontal plane
     */
    public final double getTrigStartAngle() {
    	return angleStart;
    }
    
    /**
     * @return the end angle within the gauges range relative to the normal trigonometry angle where an 
     * 		angle of zero is in the east horizontal plane
     */
    public final double getTrigEndAngle() {
    	return (angleStart + angleLength) > 360d ? (angleStart + angleLength) - 360d : (angleStart + angleLength);
    }
    
    /**
     * @return gets the viewing start angle that is within the range of the gauge
     */
    public final double getViewingStartAngle() {
    	return reverseAngle(getTrigStartAngle());
    }
    
    /**
     * @return gets the viewing end angle that is within the range of the gauge
     */
    public double getViewingEndAngle() {
    	return reverseAngle(getTrigEndAngle());
    }
    
    /**
     * @return true when the gauge is circular, false when it is an arc
     */
    public boolean isCircular() {
    	return angleLength == 360;
    }
    
    /**
     * Hand types
     */
    public enum HandType {
    	RECTANGLE, NEEDLE, CLOCK, ROTARYDIAL;
    }
}

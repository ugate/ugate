package org.ugate.gui.components;

import java.math.BigDecimal;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
 * Gauge control TODO : add bindings/properties so the size can be dynamically changed
 */
public class Gauge extends Group {

	public static final double RADIUS_OUTER_BASE = 140d;
	public static final double RADIUS_INNER_BASE = 130d;
	protected static final double ANGLE_START_END_DISTANCE_THRSHOLD = 30d;
	public static final double[] INTENSITY_REGIONS_DEFAULT = new double[] { 50, 20, 30 };
	private final IndicatorType indicatorType;
	private final int numOfMajorTickMarks;
	private final int numOfMinorTickMarks;
	private final double majorTickMarkWidth;
	private final double majorTickMarkHeight;
	private final double minorTickMarkWidth;
	private final double minorTickMarkHeight;
	private final double indicatorWidth;
	private final double indicatorHeight;
	private final double indicatorPointDistance;
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
	public final ObjectProperty<Paint> indicatorFillProperty;
	public final DoubleProperty dialCenterOpacityProperty;
	public final ObjectProperty<IntensityIndicatorRegions> intensityIndicatorRegionsProperty;
	
	public Gauge() {
		this(IndicatorType.NEEDLE, 0, 0, 0);
	}
	
	public Gauge(final IndicatorType indicatorType) {
		this(indicatorType, 0, 0, 0);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale,
			final double startAngle, final double angleLength, final double... intensityRegions) {
		this(indicatorType, sizeScale, 0, 0, 0, startAngle, angleLength, 0, intensityRegions);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale, final int dialNumberOfSides, 
			final double dialCenterInnerRadius, final double dialCenterOuterRadius,
			final double startAngle, final double angleLength, final int anglePrecision,
			final double... intensityRegions) {
		this.indicatorType = indicatorType;
		this.outerRadius = RADIUS_OUTER_BASE * sizeScale;
		this.innerRadius = RADIUS_INNER_BASE * sizeScale;
		this.centerX = this.outerRadius / 2d;
		this.centerY = this.centerX;
		this.angleStart = startAngle == 0 && angleLength == 0 ? 0 : positiveAngle(startAngle);
		this.angleLength = startAngle == 0 && angleLength == 0 ? 360d : positiveAngle(angleLength);
		this.anglePrecision = Math.max(0, anglePrecision);
		this.numOfMajorTickMarks = (int)this.angleLength / 30;
		this.numOfMinorTickMarks = this.numOfMajorTickMarks * 10;
		this.majorTickMarkWidth = 12d * sizeScale;
		this.majorTickMarkHeight = 2d * sizeScale;
		this.minorTickMarkWidth = this.majorTickMarkWidth / 2d;
		this.minorTickMarkHeight = this.majorTickMarkHeight;
		this.indicatorWidth = this.innerRadius;// - this.minorTickMarkWidth;
		this.indicatorHeight = this.majorTickMarkHeight * 10d;
		this.indicatorPointDistance = this.majorTickMarkHeight * 4;
		this.dialNumberOfSides = dialNumberOfSides <= 0 ? 10 : dialNumberOfSides;
		this.dialCenterInnerRadius = dialCenterInnerRadius <= ((this.indicatorHeight/1.5d) * sizeScale) ? 
				((this.indicatorHeight/1.5d) * sizeScale) : dialCenterInnerRadius;
		this.dialCenterOuterRadius = dialCenterOuterRadius <= ((this.indicatorHeight/1.4d) * sizeScale) ? 
				((this.indicatorHeight/1.4d) * sizeScale) : dialCenterOuterRadius;
		final double[] irvs = intensityRegions == null || intensityRegions.length < IntensityIndicatorRegions.INTENSITY_REGION_CNT ? 
				INTENSITY_REGIONS_DEFAULT : intensityRegions;
		
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
				new Stop(0, Color.LIGHTCYAN.darker()), new Stop(0.8d, Color.BLACK)));
		this.indicatorFillProperty = new Line().fillProperty();
		this.indicatorFillProperty.set(Color.ORANGERED);
		this.dialCenterOpacityProperty = new SimpleDoubleProperty(1);
		this.outerRimArcFillProperty = new Line().fillProperty();
		this.intensityIndicatorRegionsProperty = new SimpleObjectProperty<Gauge.IntensityIndicatorRegions>();
		this.intensityIndicatorRegionsProperty.setValue(new GaugeIntensityIndicatorRegions(irvs[0], irvs[1], irvs[2]));
		createChildren();
	}
	
	/**
	 * Creates the required children
	 */
	protected final void createChildren() {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		// create basic gauge shapes
		final Shape gaugeCenter = createBackground(outerRadius, innerRadius, centerGaugeFillProperty, outerRimFillProperty);
		final Group gaugeParent = createParent(gaugeCenter);

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

		gaugeParent.getChildren().add(createIntensityIndicator(majorTickMarkWidth));
		
		// add minor tick marks
		addTickMarks(gaugeParent, numOfMinorTickMarks, minorTickMarkFillProperty, minorTickMarkWidth, 
				minorTickMarkHeight, minorTickMarkWidth, minorTickMarkHeight, false, minorTickMarkOpacityProperty);
		// add major tick marks
		addTickMarks(gaugeParent, numOfMajorTickMarks, majorTickMarkFillProperty, majorTickMarkWidth, 
				majorTickMarkHeight, minorTickMarkWidth, minorTickMarkHeight, false, majorTickMarkOpacityProperty);
		
		// create indicator/hand
		final Group indicator = createIndicator(gaugeParent, indicatorPointDistance, indicatorFillProperty);
		gaugeParent.getChildren().addAll(indicator);//, createHighlights(outerRadius));
		indicator.getRotate();
		
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
	protected final Group createParent(final Shape gaugeCenter) {
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
	 * @param rimStrokeFillProperty the fill property that forms the rim that will be bound to the stroke
	 * @return the gauge center
	 */
	protected Shape createBackground(final double outerRimRadius, final double radius, 
			final ObjectProperty<Paint> centerGaugeFillProperty, final ObjectProperty<Paint> rimStrokeFillProperty) {
		if (isCircular()) {
			final Circle ccg = new Circle(this.centerX, this.centerY, radius);
			addRimStroke(ccg, rimStrokeFillProperty);
			Bindings.bindBidirectional(ccg.fillProperty(), centerGaugeFillProperty);
			return ccg;
		} else {
			final Arc acg = new Arc(this.centerX, this.centerY, radius, radius, 
					this.angleStart, this.angleLength);
			acg.setType(ArcType.ROUND);
			final Shape acf = new Circle(centerX, centerY, this.dialCenterOuterRadius * 4.5d);
			final Shape cg = Shape.union(acg, acf);
			cg.setEffect(createLighting());
			addRimStroke(cg, rimStrokeFillProperty);
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
	protected void addRimStroke(final Shape cg, final ObjectProperty<Paint> strokeFillProperty) {
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
	 * @return the lighting applied to the gauge shape and indicator/hand group
	 */
	protected Lighting createLighting() {
		final Light.Distant handBaseLight = new Light.Distant();
		handBaseLight.setAzimuth(270d);
		handBaseLight.setElevation(70d);
		final Lighting handBaseLighting = new Lighting();
		handBaseLighting.setLight(handBaseLight);
		return handBaseLighting;
	}
	
	protected final Group createIntensityIndicator(final double height) {
		final Arc redArc = createIntensityIndicatorArc(intensityIndicatorRegionsProperty.get().getRedPercentage(),
				innerRadius, innerRadius, angleStart, Color.RED);
		final Arc yellowArc = createIntensityIndicatorArc(intensityIndicatorRegionsProperty.get().getYellowPercentage(),
				redArc.getRadiusX(), redArc.getRadiusY(), getTrigEndAngle(redArc.getStartAngle(), redArc.getLength()),
				Color.GOLD);
		final Arc greenArc = createIntensityIndicatorArc(intensityIndicatorRegionsProperty.get().getGreenPercentage(),
				yellowArc.getRadiusX(), yellowArc.getRadiusY(), getTrigEndAngle(yellowArc.getStartAngle(), yellowArc.getLength()),
				Color.GREEN);
		
		final Group intensityIndicator = new Group();
		final Arc subractionArc = new Arc(centerX, centerY, innerRadius - height, innerRadius - height, angleStart, angleLength);
		final Shape is = Shape.subtract(greenArc, subractionArc);
		setIntensityIndicatorFill(greenArc.getRadiusX(), greenArc.getRadiusY(), greenArc.getStartAngle(), is, 
				intensityIndicatorRegionsProperty.get());
		intensityIndicatorRegionsProperty.addListener(new ChangeListener<IntensityIndicatorRegions>() {
			@Override
			public void changed(ObservableValue<? extends IntensityIndicatorRegions> observable, 
					IntensityIndicatorRegions oldValue, IntensityIndicatorRegions newValue) {
				setIntensityIndicatorFill(greenArc.getRadiusX(), greenArc.getRadiusY(), greenArc.getStartAngle(), is, newValue);
			}
		});
		intensityIndicator.getChildren().addAll(greenArc, yellowArc, redArc);
		return intensityIndicator;
	}
	
	protected final Arc createIntensityIndicatorArc(final double intensityPercentage, 
			final double radiusX, final double radiusY, final double startAngle, final Color color) {
//		final double x = radiusX * Math.cos(angleStart);
//		final double y = radiusY * Math.sin(angleStart);
		final double arcAngleLength = (intensityPercentage * 0.01d) * angleLength;
		final Arc arc = new Arc(centerX, centerY, radiusX, radiusY, startAngle, arcAngleLength);
		arc.setType(ArcType.ROUND);
//		arc.setFill(new RadialGradient(0, 0, centerX + x, centerY + y, 
//				Math.max(arc.getRadiusX(), arc.getRadiusY()), false, CycleMethod.NO_CYCLE, 
//				new Stop(0, color), new Stop(0.99d, Color.TRANSPARENT)));
		arc.setFill(color);
		arc.setOpacity(0.5);
		return arc;
	}
	
	/**
	 * Sets the specified intensity indicator arcs fill based upon its radius and start angle using 
	 * the intensity region values
	 * 
	 * @param is the intensity indicator arc
	 * @param intensityRegions the intensity indicator regions
	 */
	protected <T extends IntensityIndicatorRegions> void setIntensityIndicatorFill(final double radiusX, 
			final double radiusY, final double startAngle, final Shape intensityIndicator, final T intensityRegions) {
		final double gx = radiusX * Math.cos(startAngle);
		final double gy = radiusY * Math.sin(startAngle);
		final double gYellow = intensityRegions.getYellowPercentage() * .01d;//intensityRegions[2] / (360d / 1.0d);
		final double gGreen = intensityRegions.getGreenPercentage() * .01d;//intensityRegions[1] / (360d / 1.0d);
		intensityIndicator.setFill(new RadialGradient(startAngle, 0, centerX + gx, centerY + gy, 
				Math.max(radiusX, radiusY) * 4.5d, false, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.RED), new Stop(gYellow, Color.GOLD), new Stop(gGreen, Color.GREEN)));
	}
	
	/**
	 * Creates the indicator/hand
	 * 
	 * @param gaugeParent the gauge parent group used to discover mouse events for moving the indicator/hand
     * @param pointDistance the distance from the tip of the hand shape to the arm of the hand shape 
     * 		(the sharpness of the hand pointer)
     * @param indicatorFillProperty the indicator/hand fill property to bind to
	 * @return the indicator/hand
	 */
	protected final Group createIndicator(final Group gaugeParent, final double pointDistance, 
			final ObjectProperty<Paint> indicatorFillProperty) {
		final Group indicator = new Group();
		indicator.setCache(true);
		indicator.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		final ColorAdjust handColorAdj = new ColorAdjust();
		handColorAdj.setBrightness(0);
		final DropShadow handDropShadow = new DropShadow();
		handDropShadow.setOffsetX(4);
		handDropShadow.setOffsetY(4);
		handDropShadow.setRadius(7);
		handDropShadow.setColor(Color.web("#000000"));
		handDropShadow.setInput(handColorAdj);
		indicator.setEffect(handDropShadow);
		gaugeParent.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_ENTERED && event.getTarget() == indicator) {
					handColorAdj.setBrightness(0.2d);
				} else if (event.isPrimaryButtonDown() && (event.getEventType() == MouseEvent.MOUSE_DRAGGED || 
						(event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() != indicator))) {
					handColorAdj.setBrightness(0.2d);
					moveIndicator(centerX - event.getX(), centerY - event.getY());
				} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {// || (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET)) {
					handColorAdj.setBrightness(0d);
				}
			}
		});
		final Group indicatorBase = new Group();
		final double ix = centerX - (indicatorWidth / 1.2);
		final double iy = centerY - (indicatorHeight / 2);
		
		final Shape indicatorShape = createIndicatorShape(indicatorType, ix, iy, indicatorWidth, indicatorHeight, pointDistance,
				centerX, centerY, indicatorType == IndicatorType.KNOB ? centerGaugeFillProperty : indicatorFillProperty);
		final Rotate indicatorRotate = new Rotate(this.angleProperty.get(), centerX, centerY);
		Bindings.bindBidirectional(indicatorRotate.angleProperty(), this.angleProperty);
		if (indicatorType == IndicatorType.KNOB) {
	    	final Group indicatorShapeGroup = createKnobRotaryDialIndicatorShape(indicatorShape, ix, iy, 
					indicatorWidth, indicatorHeight, pointDistance, centerX, centerY, indicatorFillProperty);
			indicatorShapeGroup.getTransforms().addAll(indicatorRotate);
			indicatorBase.getChildren().add(indicatorShapeGroup);
		} else {
			indicatorShape.getTransforms().addAll(indicatorRotate);
			indicatorBase.getChildren().add(indicatorShape);
		}
		indicatorBase.setEffect(createLighting());
		
		final Circle indicatorCenter = new Circle(centerX, centerY, dialCenterOuterRadius);
		Bindings.bindBidirectional(indicatorCenter.fillProperty(), dialCenterFillProperty);
		Bindings.bindBidirectional(indicatorCenter.opacityProperty(), dialCenterOpacityProperty);
		indicatorBase.getChildren().add(indicatorCenter);
		indicator.getChildren().add(indicatorBase);
		return indicator;
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
     * Creates the indicator/hand that will be used to point toward the selected numeric value/tick mark.
     * The {@code #indicatorType} will be used to determine how the indicator/hand will be drawn.
     * 
     * @param indicatorType the indicator/hand type
     * @param x the indicator/hand x coordinate
     * @param y the indicator/hand y coordinate
     * @param width the width of the indicator/hand shape
     * @param height the height of the indicator/hand shape
     * @param pointDistance the distance from the tip of the indicator/hand shape to the arm of the indicator/hand shape 
     * 		(the sharpness of the indicator/hand pointer)
     * @param gaugeCenterX a reference point for the overall gauge center x coordinate
     * @param gaugeCenterY a reference point for the overall gauge center y coordinate
     * @param indicatorFillProperty the indicator/hand shape fill property to bind to
     * @return the created indicator/hand
     */
    protected Shape createIndicatorShape(final IndicatorType indicatorType, final double x, final double y, 
    		final double width, final double height, final double pointDistance, 
    		final double gaugeCenterX, final double gaugeCenterY,
    		final ObjectProperty<Paint> indicatorFillProperty) {
    	Shape indicatorShape;
    	switch (indicatorType) {
    	case RECTANGLE: indicatorShape = new Rectangle(x, y, width, height);
    		Bindings.bindBidirectional(indicatorShape.fillProperty(), indicatorFillProperty);
    		break;
    	case CLOCK: indicatorShape = new Polygon(
    			x, y,
    			x - pointDistance, y + (height / 2),  
    			x, y + height, 
    			x + width, y + (height / 2) + (height / 4), 
    			x + width, y + (height / 4));
    		Bindings.bindBidirectional(indicatorShape.fillProperty(), indicatorFillProperty);
    		break;
    	case KNOB: indicatorShape = createSproket(gaugeCenterX, gaugeCenterY, dialNumberOfSides, 
    			width - (pointDistance * 2) - ((dialCenterOuterRadius - dialCenterInnerRadius) * height), 
    			width - (pointDistance * 2), 0, indicatorFillProperty);
    		break;
    	case NEEDLE: default: indicatorShape = new Polygon(
				x, y + (height / 2.5), 
				x, y + height - (height / 2.5), 
				x + width - pointDistance, y + height, 
				x + width, y + (height / 2), 
				x + width - pointDistance, y);
    		Bindings.bindBidirectional(indicatorShape.fillProperty(), indicatorFillProperty);
    		break;
    	}
    	return indicatorShape;
    	
    }
    
    /**
	 * Creates a rotary/knob style dial indicator/hand shape
	 * 
	 * @param dialInidcatorShape
	 *            the dial indicator/hand shape from
	 *            {@linkplain #createIndicatorShape(IndicatorType, double, double, double, double, double, double, double, ObjectProperty)}
	 * @param x the x coordinate of the indicator point
	 * @param y the x coordinate of the indicator point
	 * @param width the width of the indicator point
	 * @param height the height of the indicator point
	 * @param pointDistance the point distance offset
	 * @param gaugeCenterX the center of the gauge x coordinate
	 * @param gaugeCenterY the center of the gauge y coordinate
	 * @param indicatorFillProperty the fill property to bind to
	 * @return the rotary dial
	 */
    protected Group createKnobRotaryDialIndicatorShape(final Shape dialInidcatorShape, 
    		final double x, final double y, final double width, final double height, 
    		final double pointDistance, final double gaugeCenterX, final double gaugeCenterY,
    		final ObjectProperty<Paint> indicatorFillProperty) {
    	final Group handShapeGroup = new Group();
    	handShapeGroup.setCache(true);
    	handShapeGroup.setCacheHint(CacheHint.ROTATE);
		final Shape handIndicatorShape = new Polygon(
    			x, y,
    			x - pointDistance, y + (indicatorHeight / 2),  
    			x, y + indicatorHeight, 
    			x + (indicatorWidth / 4), y + (indicatorHeight / 2) + (indicatorHeight / 4), 
    			x + (indicatorWidth / 4), y + (indicatorHeight / 4));
		handIndicatorShape.setEffect(createLighting());
    	Bindings.bindBidirectional(handIndicatorShape.fillProperty(), indicatorFillProperty);
		handShapeGroup.getChildren().addAll(dialInidcatorShape, handIndicatorShape);
		return handShapeGroup;
    }
    
	/**
	 * Creates a dial shape (sprocket) that appears in the center of the gauge
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
	protected Polygon createSproket(final double x, final double y, final int numOfSides, final double innerRadius, 
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
     * Moves the indicator/hand angle based upon an x/y coordinate
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the coordinates are within the start/end angles and the indicator/hand has been moved
     */
    protected boolean moveIndicator(final double x, final double y) {
    	double viewingAngle = Math.toDegrees(Math.atan2(y, x));
    	// convert angle to positive quadrants 0 - 360 degrees
		if (viewingAngle < 0) {
			viewingAngle += 360d;
		}
		return moveIndicator(viewingAngle);
    }
    
    /**
     * Sets the projected angle of the hand when the angle is within a specified threshold range of the start/end range
     *  
     * @param viewingAngle the angle in which a typical gauge is viewed where zero starts on the west horizontal plane
     * @return true if the angle is within a specified threshold range of the start/end angles and the angle property has been set
     */
    protected boolean moveIndicator(final double viewingAngle) {
    	if (isCircular()) {
    		// angle will always be with in 360 range
    		angleProperty.set(scaleAngle(viewingAngle, BigDecimal.ROUND_HALF_UP));
    		return true;
    	}
    	double trigAngle = reverseAngle(viewingAngle);
    	double startAngle = getTrigStartAngle();
    	double endAngle = getTrigEndAngle();
//    	System.out.println(String.format("angleStart: %1$s, angleLength: %2$s, viewingAngle: %3$s, 
//    			startAngle: %4$s, endAngle: %5$s, trigAngle: %6$s", 
//    			angleStart, angleLength, viewingAngle, startAngle, endAngle, trigAngle));
    	if ((startAngle <= endAngle && trigAngle >= startAngle && trigAngle <= endAngle) ||
    		(startAngle > endAngle && (trigAngle >= startAngle || trigAngle <= endAngle))) {
    		// update the viewing angle using the predefined precision
    		angleProperty.set(scaleAngle(viewingAngle, BigDecimal.ROUND_HALF_UP));
    		return true;
    	}
    	double closestAngle = closestAngle(trigAngle, startAngle, endAngle);
		// move to the start position when the angle is within the start angle threshold
		if (closestAngle == startAngle && Math.abs(trigAngle - startAngle) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
			angleProperty.set(reverseAngle(startAngle));
			return true;
		}
		// move to the end position when the angle is within the end angle threshold
		if (closestAngle == endAngle && Math.abs(trigAngle - endAngle) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
			angleProperty.set(reverseAngle(endAngle));
			return true;
		}
		// handle special case where the angle is within a specified threshold of the start position of a 0/360 border (i.e. angleStart=0) 
		if (closestAngle == endAngle && Math.abs(trigAngle - positiveAngle(startAngle - ANGLE_START_END_DISTANCE_THRSHOLD)) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
			angleProperty.set(reverseAngle(startAngle));
			return true;
		}
		// handle special case where the angle is within a specified threshold of the end position of a 0/360 border (i.e. angleStart=180, angleLength=180) 
		if (closestAngle == startAngle && Math.abs(trigAngle - positiveAngle(360d - endAngle + ANGLE_START_END_DISTANCE_THRSHOLD)) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
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
     * Calculates the end angle of a given start angle and the angle length
     * 
     * @param startAngle the start angle
     * @param angleLength the angle length
     * @return the the end angle
     */
    public static final double getTrigEndAngle(final double startAngle, final double angleLength) {
    	return (startAngle + angleLength) > 360d ? (startAngle + angleLength) - 360d : (startAngle + angleLength);
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
    	return getTrigEndAngle(angleStart, angleLength);
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
     * Indicator/Hand types
     */
    public enum IndicatorType {
    	RECTANGLE, NEEDLE, CLOCK, KNOB;
    }
    
    public abstract class IntensityIndicatorRegions {
    	public static final int INTENSITY_REGION_CNT = 3;
    	/**
    	 * @return greenPercentage the percentage of green
    	 */
    	public abstract double getGreenPercentage();
    	/**
    	 * @return yellowPercentage the percentage of green
    	 */
    	public abstract double getYellowPercentage();
    	/**
    	 * @return redPercentage the percentage of green
    	 */
    	public abstract double getRedPercentage();
    }
    
    /**
     * Regions used as a visual aid to distinguish the intensity of a {@linkplain #Gauge}. percentages should always add up to one hundred
     */
    public class GaugeIntensityIndicatorRegions extends IntensityIndicatorRegions {
    	private final double greenPercentage;
    	private final double yellowPercentage;
    	private final double redPercentage;
		/**
		 * Creates intensity indicator regions. percentages should always add up to one hundred
		 * 
		 * @param greenPercentage the percentage of green 0-100
		 * @param yellowPercentage the percentage of yellow 0-100
		 * @param redPercentage the percentage of red 0-100
		 */
		public GaugeIntensityIndicatorRegions(final double greenPercentage, final double yellowPercentage, final double redPercentage) {
			super();
			if ((greenPercentage + yellowPercentage + redPercentage) != 100) {
				throw new IllegalArgumentException("The sum of all percentages must be 100");
			}
			this.greenPercentage = greenPercentage;
			this.yellowPercentage = yellowPercentage;
			this.redPercentage = redPercentage;
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public double getGreenPercentage() {
			return greenPercentage;
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public double getYellowPercentage() {
			return yellowPercentage;
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public double getRedPercentage() {
			return redPercentage;
		}
    }
}

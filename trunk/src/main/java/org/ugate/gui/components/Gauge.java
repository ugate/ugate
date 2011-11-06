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
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
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
	public static final double RADIUS_INNER_BASE = 132d;
	protected static final double ANGLE_START_END_DISTANCE_THRSHOLD = 30d;
	public static final IntensityIndicatorRegions INTENSITY_REGIONS_DEFAULT = 
		new Gauge.IntensityIndicatorRegions(50d, 33d, 17d);
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
	public final double outerRadius;
	public final double innerRadius;
	public final double angleStart;
	public final double angleLength;
	public final double centerX;
	public final double centerY;
	public final int anglePrecision;
	public final DoubleProperty angleProperty;
	public final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
	public final DoubleProperty minorTickMarkOpacityProperty;
	public final DoubleProperty majorTickMarkOpacityProperty;
	public final ObjectProperty<Paint> outerRimFillProperty;
	public final ObjectProperty<Paint> dialCenterFillProperty;
	public final ObjectProperty<Paint> minorTickMarkFillProperty;
	public final DoubleProperty minorTickRadiusProperty;
	public final ObjectProperty<Paint> majorTickMarkFillProperty;
	public final DoubleProperty majorTickRadiusProperty;
	public final ObjectProperty<Paint> centerGaugeFillProperty;
	public final ObjectProperty<Paint> indicatorFillProperty;
	public final DoubleProperty dialCenterOpacityProperty;
	public final ObjectProperty<IntensityIndicatorRegions> intensityIndicatorRegionsProperty;
	public final DoubleProperty lightingAzimuthProperty;
	public final DoubleProperty lightingElevationProperty;
	public final ObjectProperty<Paint> highlightFillProperty;
	
	public Gauge() {
		this(null, 0, 0, 0);
	}
	
	public Gauge(final IndicatorType indicatorType) {
		this(indicatorType, 0, 0, 0);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale) {
		this(indicatorType, sizeScale, 0, 0, 0, 0, 0, 0, null);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale,
			final double startAngle, final double angleLength) {
		this(indicatorType, sizeScale, 0, 0, 0, startAngle, angleLength, 0, null);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale, final int dialNumberOfSides, 
			final double dialCenterInnerRadius, final double dialCenterOuterRadius,
			final double startAngle, final double angleLength, final int anglePrecision,
			final IntensityIndicatorRegions intensityRegions) {
		this.indicatorType = indicatorType == null ? IndicatorType.NEEDLE : indicatorType;
		this.outerRadius = RADIUS_OUTER_BASE * sizeScale;
		this.innerRadius = RADIUS_INNER_BASE * sizeScale;
		this.centerX = 0;//this.outerRadius / 2d;
		this.centerY = 0;//this.centerX;
		this.angleStart = startAngle == 0 && angleLength == 0 ? 0 : positiveAngle(startAngle);
		this.angleLength = angleLength == 0 ? this.indicatorType == IndicatorType.KNOB ? 360d : 180d : positiveAngle(angleLength);
		this.anglePrecision = Math.max(0, anglePrecision);
		this.numOfMajorTickMarks = (int)this.angleLength / 30;
		this.numOfMinorTickMarks = this.numOfMajorTickMarks * 10;
		this.majorTickMarkWidth = 12d * sizeScale;
		this.majorTickMarkHeight = 2d * sizeScale;
		this.minorTickMarkWidth = this.majorTickMarkWidth / 2d;
		this.minorTickMarkHeight = this.majorTickMarkHeight;
		this.indicatorWidth = this.innerRadius;
		this.indicatorHeight = this.majorTickMarkHeight * 10d;
		this.indicatorPointDistance = this.majorTickMarkHeight * 4;
		this.dialNumberOfSides = dialNumberOfSides <= 0 ? 10 : dialNumberOfSides;
		this.dialCenterInnerRadius = dialCenterInnerRadius <= 0 ? this.innerRadius / 16.5d : dialCenterInnerRadius;
		this.dialCenterOuterRadius = dialCenterOuterRadius <= 0 ? this.innerRadius / 17d : dialCenterOuterRadius;
		
		this.angleProperty =  new SimpleDoubleProperty(Math.min(getViewingStartAngle(), getViewingEndAngle()));
		this.majorTickMarkOpacityProperty = new SimpleDoubleProperty(1d);
		this.minorTickMarkOpacityProperty = new SimpleDoubleProperty(1d);
		this.dialCenterFillProperty = new SimpleObjectProperty<Paint>(
				this.indicatorType == IndicatorType.KNOB ? Color.TRANSPARENT : Color.BLACK);
		this.minorTickMarkFillProperty = new SimpleObjectProperty<Paint>(Color.WHITE);
		this.majorTickMarkFillProperty = new SimpleObjectProperty<Paint>(Color.WHITE);
		this.minorTickRadiusProperty = new SimpleDoubleProperty();
		this.majorTickRadiusProperty = new SimpleDoubleProperty();
		this.outerRimFillProperty = new SimpleObjectProperty<Paint>(Color.BLACK);
		this.centerGaugeFillProperty = new SimpleObjectProperty<Paint>(
				this.indicatorType == IndicatorType.KNOB ? Color.BLACK :
					new RadialGradient(0, 0, this.centerX, this.centerY, 
							this.innerRadius, false, CycleMethod.NO_CYCLE, 
							new Stop(0, Color.LIGHTCYAN.darker().darker()), 
							new Stop(0.7d, Color.BLACK.darker())));
		this.indicatorFillProperty = new SimpleObjectProperty<Paint>(
				this.indicatorType == IndicatorType.KNOB ? new RadialGradient(0, 0, 0, 0, 
							Math.max(this.indicatorHeight, this.indicatorWidth), false, 
							CycleMethod.REPEAT, new Stop(0, Color.ORANGE), 
							new Stop(1, Color.DARKRED)) : Color.ORANGERED);
		this.dialCenterOpacityProperty = new SimpleDoubleProperty(1);
		this.intensityIndicatorRegionsProperty = new SimpleObjectProperty<Gauge.IntensityIndicatorRegions>(
				intensityRegions == null ? INTENSITY_REGIONS_DEFAULT : intensityRegions);
		this.lightingAzimuthProperty = new SimpleDoubleProperty(270d);
		this.lightingElevationProperty = new SimpleDoubleProperty(55d);
		this.highlightFillProperty = new SimpleObjectProperty<Paint>(new RadialGradient(0, 0, this.centerX, this.centerY, 
				this.innerRadius, false, CycleMethod.NO_CYCLE, new Stop(0.8, Color.WHITE),
				new Stop(1, Color.TRANSPARENT)));
		createChildren();
	}
	
	/**
	 * Creates the required children
	 */
	protected final void createChildren() {
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
		gaugeParent.getChildren().addAll(val, createIntensityIndicator(), createHighlight(0, 0));
		
		// add minor tick marks
		addTickMarks(gaugeParent, numOfMinorTickMarks, minorTickMarkFillProperty, minorTickMarkWidth, 
				minorTickMarkHeight, minorTickMarkWidth, minorTickMarkHeight, false, minorTickMarkOpacityProperty);
		// add major tick marks
		addTickMarks(gaugeParent, numOfMajorTickMarks, majorTickMarkFillProperty, majorTickMarkWidth, 
				majorTickMarkHeight, minorTickMarkWidth, minorTickMarkHeight, false, majorTickMarkOpacityProperty);
		
		// create indicator/hand
		final Group indicator = createIndicator(gaugeParent, indicatorPointDistance, indicatorFillProperty);
		gaugeParent.getChildren().addAll(indicator);
		
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
		final Effect effect = createBackgroundEffect();
		if (isCircular()) {
			final Circle ccg = new Circle(this.centerX, this.centerY, radius);
			ccg.setCache(true);
			ccg.setCacheHint(CacheHint.QUALITY);
			ccg.setEffect(effect);
			addRimStroke(ccg, rimStrokeFillProperty);
			Bindings.bindBidirectional(ccg.fillProperty(), centerGaugeFillProperty);
			return ccg;
		} else {
			final Arc acg = new Arc(this.centerX, this.centerY, radius, radius, 
					this.angleStart, this.angleLength);
			acg.setType(ArcType.ROUND);
			acg.setCache(true);
			acg.setCacheHint(CacheHint.QUALITY);
			final Shape acf = new Circle(centerX, centerY, this.dialCenterOuterRadius * 4.5d);
			acf.setCache(true);
			final Shape cg = Shape.union(acg, acf);
			cg.setCache(true);
			cg.setEffect(effect);
			addRimStroke(cg, rimStrokeFillProperty);
			Bindings.bindBidirectional(cg.fillProperty(), centerGaugeFillProperty);
			return cg;
		}
	}
	
	/**
	 * @return the effect applied to the background
	 */
	protected Effect createBackgroundEffect() {
		final DropShadow outerGlow = new DropShadow();
		outerGlow.setOffsetX(0);
		outerGlow.setOffsetY(0);
		outerGlow.setColor(Color.WHITE);
		outerGlow.setRadius(5);
		//outerglow.setInput(createLighting());
		return outerGlow;
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
		//cg.setStroke(Color.WHITE);
		Bindings.bindBidirectional(cg.strokeProperty(), strokeFillProperty);
	}
	
	/**
	 * Creates highlight for the control
	 * 
	 * @param width the width of the highlight
	 * @param height the height of the highlight
	 * @return the highlight
	 */
	protected Shape createHighlight(final double width, final double height) {
		final double highlightRadius = innerRadius;
		final double offsetFactor = innerRadius / 4d;
		final Arc highlight = new Arc(centerX, centerY, highlightRadius, highlightRadius, 
				positiveAngle(angleStart + offsetFactor), positiveAngle(angleLength - offsetFactor * 2d));
		highlight.setType(ArcType.CHORD);
		
		highlight.setCache(true);
		highlight.setCacheHint(CacheHint.SPEED);
		highlight.setOpacity(0.05d);
		Bindings.bindBidirectional(highlight.fillProperty(), highlightFillProperty);
		final GaussianBlur highlightBlur = new GaussianBlur();
		highlightBlur.setRadius(2d);
		highlight.setEffect(highlightBlur);
		return highlight;
	}
	
	/**
	 * @return the lighting applied to the gauge shape and indicator/hand group
	 */
	protected final Lighting createLighting() {
		final Light.Distant handBaseLight = new Light.Distant();
		Bindings.bindBidirectional(handBaseLight.azimuthProperty(), lightingAzimuthProperty);
		Bindings.bindBidirectional(handBaseLight.elevationProperty(), lightingElevationProperty);
		final Lighting handBaseLighting = new Lighting();
		handBaseLighting.setLight(handBaseLight);
		handBaseLighting.setSpecularConstant(0.7d);
		handBaseLighting.setSpecularExponent(33d);
		handBaseLighting.setDiffuseConstant(2d);
		handBaseLighting.setSurfaceScale(1.7d);
		return handBaseLighting;
	}
	
	/**
	 * Creates an intensity indicator that shows a transition from {@code color 1}, {@code color 2}, and {@code color 3}
	 * 
	 * @return the intensity indicator
	 */
	protected final Group createIntensityIndicator() {
		final Arc region1Arc = new Arc();
		final Arc region2Arc = new Arc();
		final Arc region3Arc = new Arc();
		region1Arc.setCache(true);
		region2Arc.setCache(true);
		region3Arc.setCache(true);
		updateIntensityIndicatorProperties(region1Arc, region2Arc, region3Arc, intensityIndicatorRegionsProperty.get());
		
		final Group intensityIndicator = new Group();
		intensityIndicator.setCache(true);
		intensityIndicator.setCacheHint(CacheHint.SPEED);
		intensityIndicatorRegionsProperty.addListener(new ChangeListener<IntensityIndicatorRegions>() {
			@Override
			public void changed(ObservableValue<? extends IntensityIndicatorRegions> observable, 
					IntensityIndicatorRegions oldValue, IntensityIndicatorRegions newValue) {
				updateIntensityIndicatorProperties(region1Arc, region2Arc, region3Arc, newValue);
			}
		});
		intensityIndicator.getChildren().addAll(region3Arc, region2Arc, region1Arc);
		return intensityIndicator;
	}
	
	/**
	 * Updates the specified intensity indicator arcs properties based upon its radius and start angle using 
	 * the intensity region values
	 * 
	 * @param color1Arc the color 1 arc
	 * @param color2Arc the color 2 arc
	 * @param color3Arc the color 3 arc
	 * @param intensityIndicatorRegions the intensity regions to use
	 */
	protected final void updateIntensityIndicatorProperties(final Arc color1Arc, final Arc color2Arc, final Arc color3Arc,
			final IntensityIndicatorRegions intensityIndicatorRegions) {
		updateIntensityIndicatorProperties(color1Arc, intensityIndicatorRegions.getColor3SpanPercentage(),
				innerRadius, innerRadius, angleStart, intensityIndicatorRegions.getColor1());
		updateIntensityIndicatorProperties(color2Arc, intensityIndicatorRegions.getColor2SpanPercentage(),
				color1Arc.getRadiusX(), color1Arc.getRadiusY(), getTrigEndAngle(color1Arc.getStartAngle(), color1Arc.getLength()),
				intensityIndicatorRegions.getColor2());
		updateIntensityIndicatorProperties(color3Arc, intensityIndicatorRegions.getColor1SpanPercentage(),
				color2Arc.getRadiusX(), color2Arc.getRadiusY(), getTrigEndAngle(color2Arc.getStartAngle(), color2Arc.getLength()),
				intensityIndicatorRegions.getColor3());
	}
	
	/**
	 * Updates the specified intensity indicator arcs properties based upon its radius and start angle using 
	 * the intensity region values
	 * 
	 * @param intensityIndicator the arc to update
	 * @param intensityPercentage the percentage of the gauge the arc will occupy
	 * @param radiusX the x radius of the arc
	 * @param radiusY the y radius of the arc
	 * @param startAngle the start angle of the arc
	 * @param color the color of the arc
	 */
	protected void updateIntensityIndicatorProperties(final Arc intensityIndicator, final double intensityPercentage, 
			final double radiusX, final double radiusY, final double startAngle, final Color color) {
//		final double x = radiusX * Math.cos(angleStart);
//		final double y = radiusY * Math.sin(angleStart);
		final double arcAngleLength = (intensityPercentage * 0.01d) * angleLength;
		intensityIndicator.setType(ArcType.ROUND);
		intensityIndicator.setCenterX(centerX);
		intensityIndicator.setCenterY(centerY);
		intensityIndicator.setRadiusX(radiusX);
		intensityIndicator.setRadiusY(radiusY);
		intensityIndicator.setStartAngle(startAngle);
		intensityIndicator.setLength(arcAngleLength);
		intensityIndicator.setFill(new RadialGradient(0, 0, centerX, centerY, 
				Math.max(intensityIndicator.getRadiusX(), intensityIndicator.getRadiusY()), false, CycleMethod.NO_CYCLE, 
				 new Stop(0.8d, Color.TRANSPARENT), new Stop(1, color)));
		intensityIndicator.setOpacity(0.9d);
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
		indicator.setCacheHint(CacheHint.ROTATE);
		final Glow movingEffect = new Glow();
		movingEffect.setLevel(0);
		final DropShadow handDropShadow = new DropShadow();
		handDropShadow.setOffsetX(4);
		handDropShadow.setOffsetY(4);
		handDropShadow.setRadius(7);
		handDropShadow.setColor(Color.BLACK);
		handDropShadow.setInput(movingEffect);
		indicator.setEffect(handDropShadow);
		gaugeParent.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && (event.getEventType() == MouseEvent.MOUSE_DRAGGED || 
						(event.getEventType() == MouseEvent.MOUSE_CLICKED))) {
					movingEffect.setLevel(indicatorType == IndicatorType.KNOB ? 0.2d : 1d);
					moveIndicator(centerX - event.getX(), centerY - event.getY());
				} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
					movingEffect.setLevel(0);
				}
			}
		});
		final Group indicatorBase = new Group();
		indicatorBase.setCache(true);
		indicatorBase.setCacheHint(CacheHint.ROTATE);
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
		indicatorCenter.setCache(true);
		indicatorCenter.setCacheHint(CacheHint.SPEED);
		Bindings.bindBidirectional(indicatorCenter.fillProperty(), dialCenterFillProperty);
		Bindings.bindBidirectional(indicatorCenter.opacityProperty(), dialCenterOpacityProperty);
		indicatorBase.getChildren().add(indicatorCenter);
		indicator.getChildren().add(indicatorBase);
		return indicator;
	}
	
	protected Effect createIndicatorEffect() {
		return null;
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
	 * @return the tick mark group
	 */
	protected Group addTickMarks(final Group parent, final int numOfMarks, 
			final ObjectProperty<Paint> tickMarkFillProperty, final double width, final double height, 
			final double offestWidth, final double offsetHeight, boolean addLabel,
			final DoubleProperty tickMarkOpacityProperty) {
		double angle = 0;
		final Group tickGroup = new Group();
		tickGroup.setCache(true);
		tickGroup.setCacheHint(CacheHint.ROTATE);
		Shape tick;
		for (int i=0; i<=numOfMarks; i++) {
			angle = tickMarkAngle(numOfMarks, i, height);
			tick = createTickMark(tickMarkFillProperty, angle, width, height, offestWidth, offsetHeight,
					tickMarkOpacityProperty);
			tickGroup.getChildren().add(tick);
			if (addLabel) {
				// TODO : add tick mark label option
				final Label lbl = new Label(String.valueOf(angle));
				lbl.setStyle("-fx-text-fill: white;");
				lbl.setTranslateX(tick.getBoundsInParent().getMaxX() - (angle > 90D ? lbl.getMaxWidth() : 0));
				lbl.setTranslateY(tick.getBoundsInParent().getMaxY() - (angle > 90D ? lbl.getMaxHeight() : 0));
				parent.getChildren().add(lbl);
			}
        }
		parent.getChildren().add(tickGroup);
		return tickGroup;
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
		final double x = tickMarkX(width) + offestWidth;
		final double y = tickMarkY(height) + offsetHeight;
    	final Rectangle tm = new Rectangle(x, y, width, height);
    	tm.setCache(true);
    	tm.setCacheHint(CacheHint.QUALITY);
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
    			x - pointDistance, y + (height / 2d),  
    			x, y + height, 
    			x + width, y + (height / 2d) + (height / 4d), 
    			x + width, y + (height / 4d));
    		Bindings.bindBidirectional(indicatorShape.fillProperty(), indicatorFillProperty);
    		break;
    	case KNOB: indicatorShape = createSproket(gaugeCenterX, gaugeCenterY, dialNumberOfSides, 
    			width - (pointDistance * 2d) - ((dialCenterOuterRadius - dialCenterInnerRadius) * height), 
    			width - (pointDistance * 2d), 0, indicatorFillProperty);
    		break;
    	case NEEDLE: default: indicatorShape = new Polygon(
				x, y + (height / 2.5d), 
				x, y + height - (height / 2.5d), 
				x + width - pointDistance, y + height, 
				x + width, y + (height / 2d), 
				x + width - pointDistance, y);
    		Bindings.bindBidirectional(indicatorShape.fillProperty(), indicatorFillProperty);
    		break;
    	}
    	indicatorShape.setCache(true);
    	indicatorShape.setCacheHint(CacheHint.QUALITY);
    	//indicatorShape.setEffect(createLighting());
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
    			x - pointDistance, y + (indicatorHeight / 2d),  
    			x, y + indicatorHeight, 
    			x + (indicatorWidth / 2d), y + (indicatorHeight / 2d) + (indicatorHeight / 4d), 
    			x + (indicatorWidth / 2d), y + (indicatorHeight / 4d));
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
		final double teethSlope = (Math.PI * 2d) / numOfSides;
		final double teethQuarterSlope = teethSlope / 4d;
		final double angle = (beginAngle / 180d) * Math.PI;
		final double[] points = new double[numOfSides * 8];
		int p = -1;
		for (int sideCnt=1; sideCnt<=numOfSides; sideCnt++) {
			points[++p] = x + Math.cos(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 3d)) * innerRadius;
			points[++p] = y - Math.sin(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 3d)) * innerRadius;
			points[++p] = x + Math.cos(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 2d)) * innerRadius;
			points[++p] = y - Math.sin(angle + (teethSlope * sideCnt) - (teethQuarterSlope * 2d)) * innerRadius;
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
     * Calculates the x coordinate of a tick mark
     * 
     * @param width the width of the tick mark
     * @return the x coordinate of the tick mark
     */
    protected final double tickMarkX(final double width) {
    	return centerX + width - outerRadius;
    }
    
    /**
     * Calculates the y coordinate of a tick mark
     * 
     * @param height the height of the tick mark
     * @return the y coordinate of the tick mark
     */
    protected final double tickMarkY(final double height) {
    	return centerY - height / 2d;
    }
    
    /**
     * Calculates the angle of a tick mark
     * 
     * @param numOfMarks the total number of tick marks
     * @param index the index of the tick mark relative to the total number of tick marks (zero based)
     * @param height the height of the tick mark
     * @return the angle of the tick mark
     */
    protected final double tickMarkAngle(final int numOfMarks, final int index, final double height) {
    	return 180d - ((angleLength / numOfMarks) * index) - angleStart + height / 2d;
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
     * Calculates the difference of two angles
     * 
     * @param angle1 angle one
     * @param angle2 angle two
     * @return the difference
     */
    public static final double differenceAngle(final double angle1, final double angle2) {
        return Math.abs((angle1 + 180d - angle2) % 360d - 180d);
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
    
    /**
     * Regions used as a visual aid to distinguish the intensity of a {@linkplain #Gauge}. percentages should always add up to one hundred
     */
    public static class IntensityIndicatorRegions {
    	public static final int INTENSITY_REGION_CNT = 3;
    	private final double color1SpanPercentage;
    	private final double color2SpanPercentage;
    	private final double color3SpanPercentage;
    	private final Color color1;
    	private final Color color2;
    	private final Color color3;
    	/**
		 * Creates intensity indicator regions. percentages should always add up to one hundred
		 * 
		 * @param color1SpanPercentage the span percentage of color 1 0-100
		 * @param color2SpanPercentage the span percentage of color 2 0-100
		 * @param color3SpanPercentage the span percentage of color 3 0-100
    	 */
    	public IntensityIndicatorRegions(final double color1SpanPercentage, final double color2SpanPercentage, 
				final double color3SpanPercentage) {
    		this(color1SpanPercentage, color2SpanPercentage, color3SpanPercentage, Color.RED, Color.GOLD, Color.GREEN);
    	}
		/**
		 * Creates intensity indicator regions. percentages should always add up to one hundred
		 * 
		 * @param color1SpanPercentage the span percentage of color 1 0-100
		 * @param color2SpanPercentage the span percentage of color 2 0-100
		 * @param color3SpanPercentage the span percentage of color 3 0-100
		 * @param color1 color 1
		 * @param color2 color 2
		 * @param color3 color 3
		 */
		public IntensityIndicatorRegions(final double color1SpanPercentage, final double color2SpanPercentage, 
				final double color3SpanPercentage, final Color color1, final Color color2, final Color color3) {
			super();
			if ((color1SpanPercentage + color2SpanPercentage + color3SpanPercentage) != 100d) {
				throw new IllegalArgumentException("The sum of all percentages must be 100");
			}
			this.color1SpanPercentage = color1SpanPercentage;
			this.color2SpanPercentage = color2SpanPercentage;
			this.color3SpanPercentage = color3SpanPercentage;
			this.color1 = color1;
			this.color2 = color2;
			this.color3 = color3;
		}
		/**
		 * @return gets the span percentage of color 1
		 */
		public double getColor1SpanPercentage() {
			return color1SpanPercentage;
		}
		/**
		 * @return gets the span percentage of color 2
		 */
		public double getColor2SpanPercentage() {
			return color2SpanPercentage;
		}
		/**
		 * @return gets the span percentage of color 3
		 */
		public double getColor3SpanPercentage() {
			return color3SpanPercentage;
		}
		/**
		 * @return color 1
		 */
		public Color getColor1() {
			return color1;
		}
		/**
		 * @return color 2
		 */
		public Color getColor2() {
			return color2;
		}
		/**
		 * @return color 3
		 */
		public Color getColor3() {
			return color3;
		}
    }
}

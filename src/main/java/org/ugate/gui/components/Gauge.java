package org.ugate.gui.components;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

/**
 * Gauge control // TODO : add additional documentation w/examples
 */
public class Gauge extends Group {

	public static final double RADIUS_OUTER_BASE = 140d;
	public static final double RADIUS_INNER_BASE = 130d;
	public static final double ANGLE_START_END_DISTANCE_THRSHOLD = 30d;
	public static final int MAJOR_TICK_MARK_DIVISOR_DEFAULT = 30;
	public static final double INTENSITY_PERCENTAGE_1 = 50d;
	public static final double INTENSITY_PERCENTAGE_2 = 33d;
	public static final double INTENSITY_PERCENTAGE_3 = 17d;
	public static final IntensityIndicatorRegions INTENSITY_REGIONS_DEFAULT;
	static {
		INTENSITY_REGIONS_DEFAULT = new Gauge.IntensityIndicatorRegions(
				INTENSITY_PERCENTAGE_1, INTENSITY_PERCENTAGE_2, INTENSITY_PERCENTAGE_3);
	}
	public static final Color[] DEFAULT_KNOB_SURFACE_COLORS;
	static {
		DEFAULT_KNOB_SURFACE_COLORS = Gauge.genFadedColors(
				0xA9A9A9, 16, 0xFFFFFF, 16, 0xA9A9A9, 16, 0xFFFFFF, 16, 0xA9A9A9, 16, 
				0xFFFFFF, 16, 0xA9A9A9, 16, 0xFFFFFF, 16, 0xA9A9A9, 16, 0xFFFFFF, 16, 
				0xA9A9A9);
	}
	public static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
	public static final String FONT_NAME = "Trebuchet MS";
	public final IndicatorType indicatorType;
	public final double sizeScale;
	public final int numOfMajorTickMarks;
	public final int numOfMinorTickMarksPerMajorTick;
	public final double majorTickMarkWidth;
	public final double majorTickMarkHeight;
	public final double minorTickMarkWidth;
	public final double minorTickMarkHeight;
	public final double tickValueScale;
	public final double tickValueZeroOffset;
	public final DecimalFormat tickValueFormat;
	public final double indicatorWidth;
	public final double indicatorHeight;
	public final double indicatorPointDistance;
	public final int dialNumberOfSides;
	public final double dialCenterInnerRadius;
	public final double dialCenterOuterRadius;
	public final double dialCenterBackgroundRadius;
	public final double outerRadius;
	public final double innerRadius;
	public final double angleStart;
	public final double angleLength;
	public final double centerX;
	public final double centerY;
	public final Font tickValueFont;
	public final Glow indicatorMoveEffect;
	public final DoubleProperty angleProperty;
	public final DoubleProperty tickValueProperty;
	public final ObjectProperty<Paint> tickValueDisplayFillProperty;
	public final ObjectProperty<Paint> tickValueDisplayTextFillProperty;
	public final ObjectProperty<Paint> outerRimFillProperty;
	public final ObjectProperty<Color> outerRimEffectFillProperty;
	public final ObjectProperty<Paint> dialCenterFillProperty;
	public final ObjectProperty<Paint> minorTickMarkFillProperty;
	public final ObjectProperty<Paint> majorTickMarkFillProperty;
	public final ObjectProperty<Paint> tickMarkLabelFillProperty;
	public final ObjectProperty<Paint> centerGaugeFillProperty;
	public final ObjectProperty<Paint> indicatorFillProperty;
	public final DoubleProperty indicatorOpacityProperty;
	public final DoubleProperty dialCenterOpacityProperty;
	public final ObjectProperty<IntensityIndicatorRegions> intensityIndicatorRegionsProperty;
	public final DoubleProperty lightingAzimuthProperty;
	public final DoubleProperty lightingElevationProperty;
	public final ObjectProperty<Paint> highlightFillProperty;
	public final BooleanProperty snapToTicksProperty;
	
	public Gauge() {
		this(null, 0, 0, 0);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale, final double tickValueScale,
			final int tickValueZeroOffset) {
		this(indicatorType, sizeScale, tickValueScale, tickValueZeroOffset, 0, 0, -1, -1);
	}
	
	public Gauge(final IndicatorType indicatorType, final double sizeScale, final double tickValueScale,
			final int tickValueZeroOffset, final double startAngle, final double angleLength, final int numberOfMajorTickMarks,
			final int numberOfMinorTickMarksPerMajorTick) {
		this(indicatorType, sizeScale, tickValueScale, tickValueZeroOffset, startAngle, angleLength, numberOfMajorTickMarks, 
				numberOfMinorTickMarksPerMajorTick, -1, 0, 0, null, null);
	}
	
	/**
	 * Full constructor {@see #Gauge}
	 * 
	 * @param indicatorType the indicator/hand type
	 * @param sizeScale the size scale factor that will be used to size the control without quality degradation
	 * @param tickValueScale the tick value scale that will be used as a multiplier when converting the angle to a tick mark value
	 * @param tickValueZeroOffset the number of major tick marks that will appear before the zero tick 
	 * @param startAngle the starting arc angle of the gauge
	 * @param angleLength the angle length of the gauge
	 * @param numberOfMajorTickMarks the number of major tick marks (influences the available tick values)
	 * @param numberOfMinorTickMarksPerMajorTick the number of minor tick marks to appear in between the major tick marks.
	 * 		Typically that number of minor tick marks can be calculated based upon the number of decimal places being used for
	 * 		{@code tickValueScale}.
	 * @param dialNumberOfSides the number of sides of the dial that appears in the pivot point of the indicator arm (default
	 * 		is transparent for knob indicator types).
	 * @param dialCenterInnerRadius the dial center inner radius
	 * @param dialCenterOuterRadius the dial center outer radius
	 * @param intensityRegions the intensity regions that will render a visual aid to indicate when a tick value is moderate, 
	 * 		medium, or intense.
	 * @param tickValueFont the font used for the tick value display and tick mark labels
	 */
	public Gauge(final IndicatorType indicatorType, final double sizeScale, final double tickValueScale, 
			final int tickValueZeroOffset, final double startAngle, final double angleLength, final int numberOfMajorTickMarks, 
			final int numberOfMinorTickMarksPerMajorTick, final int dialNumberOfSides, 
			final double dialCenterInnerRadius, final double dialCenterOuterRadius, final IntensityIndicatorRegions intensityRegions, 
			final Font tickValueFont) {
		this.indicatorType = indicatorType == null ? IndicatorType.NEEDLE : indicatorType;
		this.sizeScale = sizeScale == 0 ? 1d : sizeScale;
		this.outerRadius = RADIUS_OUTER_BASE * this.sizeScale;
		this.innerRadius = RADIUS_INNER_BASE * this.sizeScale;
		this.centerX = 0;//this.outerRadius / 2d;
		this.centerY = 0;//this.centerX;
		this.angleStart = startAngle == 0 && angleLength == 0 ? 0 : positiveAngle(startAngle);
		this.angleLength = angleLength == 0 ? this.indicatorType == IndicatorType.KNOB ? 360d : 180d : positiveAngle(angleLength);
		this.numOfMajorTickMarks = numberOfMajorTickMarks <= 0 ? 
				(int)this.angleLength / 30 : this.angleLength < 360d && numberOfMajorTickMarks > 1 ? 
						numberOfMajorTickMarks - 1 : numberOfMajorTickMarks;
		this.numOfMinorTickMarksPerMajorTick = numberOfMinorTickMarksPerMajorTick <= 0 ? 0 : numberOfMinorTickMarksPerMajorTick + 1;
		this.majorTickMarkWidth = 15d * this.sizeScale;
		this.majorTickMarkHeight = 2.5d * this.sizeScale;
		this.minorTickMarkWidth = this.majorTickMarkWidth / 2d;
		this.minorTickMarkHeight = this.majorTickMarkHeight;
		this.tickValueScale = tickValueScale == 0 ? 1d : tickValueScale;
		this.tickValueZeroOffset = tickValueZeroOffset;
		this.tickValueFormat = createTickValueFormat();
		this.indicatorWidth = this.innerRadius;
		this.indicatorHeight = 24d * this.sizeScale;
		this.indicatorPointDistance = 12d * this.sizeScale;
		this.dialNumberOfSides = dialNumberOfSides < 0 ? 24 : dialNumberOfSides;
		this.dialCenterInnerRadius = dialCenterInnerRadius <= 0 ? this.innerRadius / 16.5d : dialCenterInnerRadius;
		this.dialCenterOuterRadius = dialCenterOuterRadius <= 0 ? this.innerRadius / 17d : dialCenterOuterRadius;
		this.dialCenterBackgroundRadius = this.dialCenterOuterRadius * 4.5d;
		this.indicatorMoveEffect = new Glow(0);
		this.tickValueFont = tickValueFont == null ? Font.font(FONT_NAME, 17d * sizeScale) : tickValueFont;
		
		final double defaultAngle = getTickValue(getViewingStartAngle()) <= getTickValue(getViewingEndAngle()) ? 
				getViewingStartAngle() : getViewingEndAngle();
		this.angleProperty = new SimpleDoubleProperty(defaultAngle) {
			@Override
			public final void set(final double v) {
				final double nav = calibrateViewingAngle(v);
				if (nav >= 0) {
					super.set(nav);
					final double ntv = getTickValue(nav);
					if (tickValueProperty.get() != ntv) {
						tickValueProperty.set(ntv);
					}
				}
			}
		};
		this.tickValueProperty = new SimpleDoubleProperty() {
			@Override
			public final void set(final double v) {
				double ntv = Double.parseDouble(tickValueFormat.format(v));
				ntv = snapToTicksProperty.get() ? closestTickMarkValue(ntv) : ntv;
				super.set(ntv);
				final double nav = getViewingAngle(ntv);
				if (angleProperty.get() != nav) {
					angleProperty.set(nav);
				}
			}
		};
		this.dialCenterFillProperty = new SimpleObjectProperty<Paint>(
				this.indicatorType == IndicatorType.KNOB ? Color.TRANSPARENT : Color.BLACK);
		this.minorTickMarkFillProperty = new SimpleObjectProperty<Paint>(Color.GRAY.brighter());
		this.majorTickMarkFillProperty = new SimpleObjectProperty<Paint>(Color.WHITE);
		this.tickMarkLabelFillProperty = new SimpleObjectProperty<Paint>(Color.WHITE);
		this.tickValueDisplayFillProperty = new SimpleObjectProperty<Paint>(new LinearGradient(
				0, 0, 0, 1d, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.DARKGRAY.darker()), new Stop(0.4d, Color.BLACK)));
		this.tickValueDisplayTextFillProperty = new SimpleObjectProperty<Paint>(Color.WHITE);
		this.outerRimFillProperty = new SimpleObjectProperty<Paint>(Color.BLACK);
		this.outerRimEffectFillProperty = new SimpleObjectProperty<Color>(Color.LIGHTCYAN);
		this.centerGaugeFillProperty = new SimpleObjectProperty<Paint>(
				this.indicatorType == IndicatorType.KNOB ? Color.BLACK : 
					new RadialGradient(0, 0, this.centerX, this.centerY, 
							this.innerRadius, false, CycleMethod.NO_CYCLE, 
							new Stop(0, Color.WHITESMOKE), 
							new Stop(0.7d, Color.BLACK)));
		this.indicatorFillProperty = new SimpleObjectProperty<Paint>(
				this.indicatorType == IndicatorType.KNOB ? Color.BLACK : Color.ORANGERED);
		this.indicatorOpacityProperty = new SimpleDoubleProperty(1);
		this.dialCenterOpacityProperty = new SimpleDoubleProperty(1);
		this.intensityIndicatorRegionsProperty = new SimpleObjectProperty<Gauge.IntensityIndicatorRegions>(
				intensityRegions == null ? INTENSITY_REGIONS_DEFAULT : intensityRegions);
		this.lightingAzimuthProperty = new SimpleDoubleProperty(270d);
		this.lightingElevationProperty = new SimpleDoubleProperty(50d);
		this.highlightFillProperty = new SimpleObjectProperty<Paint>(Color.WHITE);
		this.snapToTicksProperty = new SimpleBooleanProperty(false);
		createChildren();
	}
	
	/**
	 * Creates the required children
	 */
	protected final void createChildren() {
		// create basic gauge shapes
		final Shape gaugeCenter = createBackground(outerRadius, innerRadius, centerGaugeFillProperty, 
				outerRimFillProperty, outerRimEffectFillProperty);
		final Group gaugeParent = createParent(gaugeCenter);

		// add tick marks
		addTickMarks(gaugeParent, true, false);
		// add display that will show the current tick value
		final double tickValueHeight = 20d * sizeScale;
		final Node tickValueDisplay = this.indicatorType == IndicatorType.KNOB ? null : 
			createTickValueDisplay(tickValueHeight, tickValueDisplayFillProperty, tickValueDisplayTextFillProperty);
		final Node tickValueDisplay2 = this.indicatorType == IndicatorType.KNOB ? null : 
			createTickValueDisplay2(tickValueHeight, tickValueDisplayFillProperty, tickValueDisplayTextFillProperty);
		
		// create intensity indicators that will indicate when values are moderate, medium, or intense
		final Group intensityIndicator = createIntensityIndicator();
		
		// create highlight
		final double highlightAngle = 270d; //isCircular() ? 270d : getGeometricCenterAngle();
		final Shape highlight = this.indicatorType == IndicatorType.KNOB ? null : createHighlight(0, 0, highlightAngle);
		
		// create indicator/hand
		final Group indicator = createIndicator(gaugeParent, indicatorPointDistance, 
				indicatorFillProperty, indicatorOpacityProperty);
		
		if (tickValueDisplay2 != null) {
			gaugeParent.getChildren().add(tickValueDisplay2);
		}
		if (tickValueDisplay != null) {
			gaugeParent.getChildren().add(tickValueDisplay);
		}
		if (intensityIndicator != null) {
			gaugeParent.getChildren().add(intensityIndicator);
		}
		if (highlight != null) {
			gaugeParent.getChildren().add(highlight);
		}
		if (indicator != null) {
			gaugeParent.getChildren().add(indicator);
		}
		getChildren().add(gaugeParent);
	}
	
	/**
	 * Creates a display that will show the current tick value
	 * 
	 * @param height the height of the display
	 * @param backgroundFillProperty the fill property to bind to the background shape
	 * @param textFillProperty the fill property to bind to the text
	 * @return the tick value display
	 */
	protected Node createTickValueDisplay(final double height,
			final ObjectProperty<Paint> backgroundFillProperty, 
			final ObjectProperty<Paint> textFillProperty) {
		final StackPane valContainer = new StackPane();
		final Text val = new Text(getTickValueLabel());
		val.setFont(tickValueFont);
		final DropShadow outerGlow = new DropShadow();
		outerGlow.setOffsetX(0);
		outerGlow.setOffsetY(0);
		outerGlow.setColor(Color.BLUE);
		outerGlow.setRadius(2d);
		val.setEffect(outerGlow);
		Bindings.bindBidirectional(val.fillProperty(), textFillProperty);
		//val.setStyle("-fx-text-fill: white;");
		angleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				val.setText(getTickValueLabel());//newValue.doubleValue()));
			}
		});
		final double rimThickness = outerRadius - innerRadius;
		Shape border;
		if (isCircular()) {
			final Rectangle rec = new Rectangle(outerRadius, height);
			rec.setArcHeight(5d);
			rec.setArcWidth(5d);
			rec.setTranslateY(5d);
			border = rec;
			val.setTranslateY(rec.getHeight() / 2d);
			valContainer.setTranslateX(centerX - rec.getWidth() / 2d);
			valContainer.setTranslateY(centerY + rec.getHeight());
		} else {
			// create the background border for the tick mark value
			final double width = outerRadius - rimThickness;
			final double offsetY = centerY + rimThickness;
			// TODO : replace square root with proper angle offset
			final double offsetX = centerX + dialCenterBackgroundRadius - Math.sqrt(offsetY);
			// rotate value text so that it is never upside down
			final double angle = -getGeometericStartAngle();
			if (Math.abs(angle) > 90d && Math.abs(angle) < 270d) {
				val.setRotate(180d);
			}
			final Circle dialCutout = new Circle(centerX, centerY, innerRadius);
			final Circle gaugeCutout = new Circle(centerX, centerY, dialCenterBackgroundRadius
					+ rimThickness);
			// create the border rectangle
			final Polygon rec = new Polygon(centerX, offsetY, centerX + width, offsetY,
					centerX + width, offsetY + height, centerX, offsetY + height);
			// carve out outer rim rectangle edge
			final Shape recArc = Shape.intersect(rec, dialCutout);
			// carve out inner dial rectangle edge
			border = Shape.subtract(recArc, gaugeCutout);

			border.setTranslateX(offsetX);
			border.setTranslateY(offsetY);
			val.setTranslateX(offsetX);
			val.setTranslateY(offsetY);
			valContainer.getTransforms().add(new Rotate(angle, centerX, centerY));
		}
		Bindings.bindBidirectional(border.fillProperty(), backgroundFillProperty);
		border.setStroke(Color.GRAY);
		border.setStrokeWidth(1d);
		valContainer.getChildren().addAll(border, val);
		return valContainer;
	}
	
	/**
	 * Creates a display that will show a 2nd current tick value
	 * 
	 * @param height the height of the display
	 * @param backgroundFillProperty the fill property to bind to the background shape
	 * @param textFillProperty the fill property to bind to the text
	 * @return the 2nd tick value display
	 */
	protected Node createTickValueDisplay2(final double height, 
			final ObjectProperty<Paint> backgroundFillProperty, 
			final ObjectProperty<Paint> textFillProperty) {
		if (isCircular()) {
			return null;
		}
		final StackPane valContainer = new StackPane();
		final double rimRadius = outerRadius - innerRadius;
		final Circle dialCutout = new Circle(centerX, centerY, innerRadius);
		final Circle gaugeCutout = new Circle(centerX, centerY, dialCenterBackgroundRadius
				+ rimRadius);
		// create the opposing background border
		final double offsetX = centerX - innerRadius;
		final double offsetY = centerY + rimRadius;
		final Polygon rec2 = new Polygon(centerX, offsetY, offsetX, offsetY,
				offsetX, offsetY + height, centerX, offsetY + height);
		final Shape recArc2 = Shape.intersect(rec2, dialCutout);
		final Shape border2 = Shape.subtract(recArc2, gaugeCutout);
		
		border2.setTranslateX(offsetX);
		border2.setTranslateY(offsetY);
		double angle2 = 180d - getGeometericEndAngle();
		
		valContainer.getTransforms().add(new Rotate(angle2, centerX, centerY));
		Bindings.bindBidirectional(border2.fillProperty(), backgroundFillProperty);
		border2.setStroke(Color.GRAY);
		border2.setStrokeWidth(1d);
		valContainer.getChildren().add(border2);
		return valContainer;
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
	 * @param backgroundEffectFillProperty the background rim effect color property to bind to
	 * @return the gauge center
	 */
	protected Shape createBackground(final double outerRimRadius, final double radius, 
			final ObjectProperty<Paint> centerGaugeFillProperty, 
			final ObjectProperty<Paint> rimStrokeFillProperty, 
			final ObjectProperty<Color> backgroundEffectFillProperty) {
		final Effect effect = createBackgroundEffect(backgroundEffectFillProperty);
		if (isCircular()) {
			final Circle ccg = new Circle(centerX, centerY, radius);
			ccg.setCache(true);
			ccg.setCacheHint(CacheHint.QUALITY);
			ccg.setEffect(effect);
			addRimStroke(ccg, rimStrokeFillProperty);
			Bindings.bindBidirectional(ccg.fillProperty(), centerGaugeFillProperty);
			return ccg;
		} else {
			final Arc acg = new Arc(centerX, centerY, radius, radius, 
					angleStart, angleLength);
			acg.setType(ArcType.ROUND);
			acg.setCache(true);
			acg.setCacheHint(CacheHint.QUALITY);
			final Shape acf = new Circle(centerX, centerY, dialCenterBackgroundRadius);
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
	protected Effect createBackgroundEffect(final ObjectProperty<Color> fillProperty) {
		final DropShadow outerGlow = new DropShadow();
		outerGlow.setOffsetX(0);
		outerGlow.setOffsetY(0);
		Bindings.bindBidirectional(outerGlow.colorProperty(), outerRimEffectFillProperty);
		outerGlow.setRadius(5);
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
		Bindings.bindBidirectional(cg.strokeProperty(), strokeFillProperty);
	}
	
	/**
	 * Creates highlight for the control
	 * 
	 * @param width the width of the highlight
	 * @param height the height of the highlight
	 * @param highlightAngle the angle that the highlight will appear
	 * @return the highlight
	 */
	protected Shape createHighlight(final double width, final double height, final double highlightAngle) {
		final double adjAngleLength = isCircular() ? 180d : angleLength;
		final double arcRadius = innerRadius - (innerRadius / 100d);
		final double radiusX = centerX + adjAngleLength / 2d;
		final double radiusY = centerY + adjAngleLength / 2d;
		final double cx = (radiusX + innerRadius / 2.5d) * Math.cos(Math.toRadians(highlightAngle));
		final double cy = (radiusY + innerRadius / 2.5d) * Math.sin(Math.toRadians(highlightAngle));
		final Ellipse shape1 = new Ellipse(cx, cy, radiusX, radiusY);
		shape1.setFill(Color.GREEN);
		final Arc shape2 = new Arc(centerX, centerY, arcRadius, arcRadius, angleStart, adjAngleLength);
		shape2.setType(ArcType.ROUND);
		shape2.setFill(Color.WHITE);
		
		final Shape highlight = Shape.intersect(shape1, shape2);
		
		highlight.setCache(true);
		highlight.setCacheHint(CacheHint.SPEED);
		highlight.setOpacity(0.07d);
		highlightFillProperty.set(new LinearGradient(cx, cy, centerX, centerY, 
				false, CycleMethod.NO_CYCLE, new Stop(0.8d, Color.WHITE),
				new Stop(1d, Color.TRANSPARENT)));
		Bindings.bindBidirectional(highlight.fillProperty(), highlightFillProperty);
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
		handBaseLighting.setSpecularExponent(20d);
		handBaseLighting.setDiffuseConstant(1.0d);
		handBaseLighting.setSurfaceScale(7d * sizeScale);
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
		region1Arc.setSmooth(false);
		region1Arc.setCache(true);
		region2Arc.setSmooth(false);
		region2Arc.setCache(true);
		region3Arc.setSmooth(false);
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
				outerRadius, outerRadius, angleStart, intensityIndicatorRegions.getColor1());
		updateIntensityIndicatorProperties(color2Arc, intensityIndicatorRegions.getColor2SpanPercentage(),
				color1Arc.getRadiusX(), color1Arc.getRadiusY(), getGeometericEndAngle(color1Arc.getStartAngle(), color1Arc.getLength()),
				intensityIndicatorRegions.getColor2());
		updateIntensityIndicatorProperties(color3Arc, intensityIndicatorRegions.getColor1SpanPercentage(),
				color2Arc.getRadiusX(), color2Arc.getRadiusY(), getGeometericEndAngle(color2Arc.getStartAngle(), color2Arc.getLength()),
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
		final double arcAngleLength = (intensityPercentage * 0.01d) * angleLength;
		intensityIndicator.setType(ArcType.ROUND);
		intensityIndicator.setCenterX(centerX);
		intensityIndicator.setCenterY(centerY);
		intensityIndicator.setRadiusX(radiusX);
		intensityIndicator.setRadiusY(radiusY);
		intensityIndicator.setStartAngle(startAngle);
		intensityIndicator.setLength(arcAngleLength);
		final double stopAmt = (1d - (outerRadius - innerRadius) / outerRadius);
		intensityIndicator.setFill(new RadialGradient(0, 0, centerX, centerY, 
				Math.max(intensityIndicator.getRadiusX(), intensityIndicator.getRadiusY()), false, CycleMethod.NO_CYCLE, 
				 new Stop(stopAmt, Color.TRANSPARENT), new Stop(1, color)));
		intensityIndicator.setOpacity(0.9d);
	}
	
	/**
	 * Creates the indicator/hand
	 * 
	 * @param gaugeParent the gauge parent group used to discover mouse events for moving the indicator/hand
     * @param pointDistance the distance from the tip of the hand shape to the arm of the hand shape 
     * 		(the sharpness of the hand pointer)
     * @param fillProperty the indicator/hand fill property to bind to
     * @param opacityProperty the opacity property to bind to
	 * @return the indicator/hand
	 */
	protected final Group createIndicator(final Group gaugeParent, final double pointDistance, 
			final ObjectProperty<Paint> fillProperty, final DoubleProperty opacityProperty) {
		final Group indicator = new Group();
		Bindings.bindBidirectional(indicator.opacityProperty(), opacityProperty);
		indicator.setCache(true);
		indicator.setCacheHint(CacheHint.ROTATE);
		final DropShadow handDropShadow = new DropShadow();
		handDropShadow.setOffsetX(4);
		handDropShadow.setOffsetY(4);
		handDropShadow.setRadius(7);
		handDropShadow.setColor(Color.BLACK);
		handDropShadow.setInput(getIndicatorMoveEffect());
		indicator.setEffect(handDropShadow);
		gaugeParent.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_DRAGGED || 
						event.getEventType() == MouseEvent.MOUSE_PRESSED) {
					if (event.isMetaDown() || event.isControlDown() || event.isAltDown() || 
							event.isShiftDown() || event.isShortcutDown() || !event.isPrimaryButtonDown()) {
						return;
					}
					getIndicatorMoveEffect().setLevel(indicatorType == IndicatorType.KNOB ? 0.2d : 0.7d);
					moveIndicator(centerX - event.getX(), centerY - event.getY());
				} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
					getIndicatorMoveEffect().setLevel(0);
				}
			}
		});
		final Group indicatorBase = new Group();
		indicatorBase.setCache(true);
		indicatorBase.setCacheHint(CacheHint.ROTATE);
		final double ix = centerX - (indicatorWidth / 1.2);
		final double iy = centerY - (indicatorHeight / 2);
		
		final Rotate indicatorRotate = new Rotate(this.angleProperty.get(), centerX, centerY);
		Bindings.bindBidirectional(indicatorRotate.angleProperty(), this.angleProperty);
		Shape indicatorShape;
		if (indicatorType == IndicatorType.KNOB) {
			indicatorShape = createIndicatorShape(indicatorType, ix, iy, indicatorWidth - pointDistance, 
					indicatorHeight - pointDistance, pointDistance,
					centerX, centerY, fillProperty);
			final double knobSurfaceRadius = indicatorWidth - pointDistance * 2d;
    		final Group knobSurface = createKnobSurface(0, centerX, centerY, 
    				knobSurfaceRadius, knobSurfaceRadius, DEFAULT_KNOB_SURFACE_COLORS);
	    	final Group indicatorShapeGroup = createKnob(indicatorShape, knobSurface);
			indicatorShapeGroup.getTransforms().addAll(indicatorRotate);
			indicatorBase.getChildren().add(indicatorShapeGroup);
		} else {
			indicatorShape = createIndicatorShape(indicatorType, ix, iy, indicatorWidth, 
					indicatorHeight, pointDistance, centerX, centerY, fillProperty);
			indicatorShape.getTransforms().addAll(indicatorRotate);
			indicatorBase.getChildren().add(indicatorShape);
		}
		final Lighting lighting = createLighting();
		if (indicatorType == IndicatorType.KNOB) {
			lighting.setSpecularConstant(0.4d);
			lighting.setSpecularExponent(40d);
			lighting.setDiffuseConstant(1.7d);
		}
		indicatorBase.setEffect(lighting);
		
		final Circle indicatorCenter = new Circle(centerX, centerY, dialCenterOuterRadius);
		indicatorCenter.setCache(true);
		indicatorCenter.setCacheHint(CacheHint.SPEED);
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
	 * @param addMajorTickLabel true to add the value label to the major tick marks
	 * @param addMinorTickLabel true to add the value label to the minor tick marks
	 * @return the tick mark group
	 */
	protected final Group addTickMarks(final Group parent, final boolean addMajorTickLabel, 
			final boolean addMinorTickLabel) {
		final Group tickGroup = new Group();
		tickGroup.setCache(true);
		tickGroup.setCacheHint(CacheHint.ROTATE);
		Shape tick;
		// all tick marks will have the same starting coordinates- only the angle will be adjusted
		final double tx = tickMarkDefaultX();
		final double ty = tickMarkDefaultY();
		final int numOfTotalMinorTicks = getNumberOfMinorTicks();
		int i;
		String label;
		double angle, labelAngle, labelRadius, tlx, tly;
		// add the minor tick marks
		for (i=0; i<=numOfTotalMinorTicks; i++) {
			angle = tickMarkAngle(numOfTotalMinorTicks, i);
			tick = createTickMark(tx, ty, minorTickMarkWidth, minorTickMarkHeight, 
					angle + minorTickMarkHeight / 2.5d, minorTickMarkFillProperty);
			tickGroup.getChildren().add(tick);
		}
		// add the major tick marks
		for (i=0; i<=numOfMajorTickMarks; i++) {
			angle = tickMarkAngle(numOfMajorTickMarks, i);
			tick = createTickMark(tx, ty, majorTickMarkWidth, majorTickMarkHeight, 
					angle + majorTickMarkHeight / 2.5d, majorTickMarkFillProperty);
			tickGroup.getChildren().add(tick);
			if (addMajorTickLabel && (i != numOfMajorTickMarks || !isCircular())) {
				labelAngle = positiveAngle(angle - (majorTickMarkHeight / 2d) -180d);
				labelRadius = indicatorType == IndicatorType.KNOB ? outerRadius : 
					innerRadius;
				tlx = (centerX + labelRadius) 
						* Math.cos(Math.toRadians(labelAngle));
				tly = (centerY + labelRadius) 
						* Math.sin(Math.toRadians(labelAngle));
				label = getTickValueLabel(angle);
				tickGroup.getChildren().add(createTickMarkLabel(tlx, tly, angle, labelAngle, label, tick,
						tickMarkLabelFillProperty));
			}
        }
		parent.getChildren().add(tickGroup);
		return tickGroup;
	}
	
	/**
	 * Create tick mark label
	 * 
	 * @param x the x coordinate of the label
	 * @param y the y coordinate of the label
	 * @param tickMarkAngle the tick mark angle
	 * @param labelAngle the label angle
	 * @param label the label
	 * @param tickMark the tick mark that the label is for
	 * @param tickMarkLabelFillProperty the tick mark label fill property to bind to
	 * @return the tick mark label
	 */
	protected Shape createTickMarkLabel(final double x, final double y, final double tickMarkAngle,
			final double labelAngle, final String label, final Shape tickMark,
			final ObjectProperty<Paint> tickMarkLabelFillProperty) {
		final double lx = indicatorType == IndicatorType.KNOB ? outerRadius + majorTickMarkHeight : 
			innerRadius - majorTickMarkWidth * (label.indexOf('.') > -1 ? 2d : 1.8d);
		final Text lbl = new Text(lx, 0, label);
		//final double widthOffset = (lbl.getBoundsInLocal().getWidth() / 2d);
		final double heightOffset = (lbl.getBoundsInLocal().getHeight() / 2d);
		lbl.setY(lbl.getY() + heightOffset);
		lbl.getTransforms().addAll(new Rotate(labelAngle, centerX, centerY),
				new Rotate(90d, lbl.getBoundsInParent().getMinX() + lbl.getBoundsInParent().getWidth() / 2d, 
						0));
		Bindings.bindBidirectional(lbl.fillProperty(), tickMarkLabelFillProperty);
		lbl.setFont(tickValueFont);
		lbl.setTextAlignment(TextAlignment.CENTER);
		return lbl;
	}
	
	/**
	 * Creates a tick mark
	 * 
	 * @param x the x coordinate of the tick mark
	 * @param y the y coordinate of the tick mark
	 * @param width the width of the tick mark
	 * @param height the height of the tick mark
	 * @param angle the angle of the tick mark
	 * @param tickMarkFillProperty the fill property to bind for the tick mark
	 * @return the tick mark
	 */
    protected Shape createTickMark(final double x, final double y, final double width, 
    		final double height, final double angle, final ObjectProperty<Paint> tickMarkFillProperty) {
    	final Rectangle tm = new Rectangle(x, y, width, height);
    	tm.setSmooth(false);
    	tm.setCache(true);
    	tm.setCacheHint(CacheHint.QUALITY);
    	Bindings.bindBidirectional(tm.fillProperty(), tickMarkFillProperty);
		tm.getTransforms().addAll(new Rotate(angle, centerX, centerY));
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
    	case KNOB: 
    		indicatorShape = new Polygon(
        			x, y,
        			x - pointDistance, y + (indicatorHeight / 2d),  
        			x, y + indicatorHeight, 
        			x + (indicatorWidth / 2d), y + (indicatorHeight / 2d) + (indicatorHeight / 4d), 
        			x + (indicatorWidth / 2d), y + (indicatorHeight / 4d));
    		Bindings.bindBidirectional(indicatorShape.fillProperty(), indicatorFillProperty);
    		indicatorShape.setStroke(Color.WHITESMOKE);
    		indicatorShape.setStrokeWidth(2d);
    		indicatorShape.setStrokeType(StrokeType.CENTERED);
    		indicatorShape.setEffect(createLighting());
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
	 * Creates a rotary/knob dial parent
	 * 
	 * @param indicatorShape the indicator/hand shape
	 * @param dialNode the center portion of the knob
	 * @return the completed knob
	 */
    protected Group createKnob(final Shape indicatorShape, final Node dialNode) {
    	final Group handShapeGroup = new Group();
    	handShapeGroup.setCache(true);
    	handShapeGroup.setCacheHint(CacheHint.ROTATE);
		handShapeGroup.getChildren().addAll(dialNode, indicatorShape);
		return handShapeGroup;
    }
    
    /**
     * Creates a brushed aluminum surface for the knob
     * 
     * @param startAngle the start angle
     * @param centerX the center x of the surface
     * @param centerY the center y of the surface
     * @param radiusX the x radius of the surface
     * @param radiusY the y radius of the surface
     * @param colors the colors to use
     * @return the knob surface
     */
    protected Group createKnobSurface(final double startAngle, final double centerX, 
    		final double centerY, final double radiusX, final double radiusY, 
    		final Color... colors) {
    	Group group = new Group();
    	double startRadians = startAngle * 2d * Math.PI;
    	//double stepRadians = (2d * Math.PI) / colors.length;
    	//double oldX = centerX + Math.cos(startRadians) * radiusX;
    	//double oldY = centerY + Math.sin(startRadians) * radiusY;
    	//double newX, newY, newRadians;
    	double angle = Math.toDegrees(startRadians), angleLength = 360d / colors.length;
    	//angleLength *= 2d;
    	for (double i=0; i<colors.length; i++) {
    		//newRadians = startRadians + stepRadians * (i + 2d);
    		//newX = centerX + Math.cos(newRadians) * radiusX;
    		//newY = centerY + Math.sin(newRadians) * radiusY;
    		//Polygon shape = new Polygon(oldX, oldY, centerX, centerY, newX, newY, oldX, oldY);
    		Arc shape = new Arc(centerX, centerY, radiusX, radiusY, angle, angleLength);
    		angle += angleLength;
    		shape.setType(ArcType.ROUND);
    		shape.setSmooth(false);
    		shape.setCache(true);
    		shape.setCacheHint(CacheHint.SPEED);
    		shape.setFill(colors[(int)i]);
    		shape.setStroke(colors[(int)i]);
    		shape.setStrokeWidth(angleLength);
    		shape.setStrokeType(StrokeType.INSIDE);
    		group.getChildren().add(shape);
    		//oldX = newX;
            //oldY = newY;
    	}
//    	Ellipse brush;
//    	double rX, rY;
//    	int i = 1;
//    	do {
//    		rX = radiusX - (i * 4);
//    		rY = radiusY - (i * 4);
//    		brush = new Ellipse(centerX, centerY, rX, rY);
//    		brush.setFill(Color.TRANSPARENT);
//    		brush.setStroke(Color.WHITESMOKE);
//    		brush.setStrokeWidth(0.1d);
//    		brush.setOpacity(0.5d);
//    		group.getChildren().add(brush);
//    		i++;
//    	} while (rX > 0 && rY > 0);
    	return group;
    }
    
    /**
     * Generates an array of colors based upon the specified color sequences where every other
     * value in {@code colors} represents a hexadecimal color (starting at index zero) and 
     * every other value in {@code colors} represents how many times to fade the preceding 
     * hexadecimal color (starting at index one).
     * <p>For example, {@code [0xFFFFFF, 20, 0xA9A9A9, 32, 0xFFFFFF]} would generate 20 different 
     * variations of white, 32 variations of gray, and end with white.
     * </p>
     * @param colors the colors to fade
     * @return the faded colors
     */
    protected static final Color[] genFadedColors(int... colors) {
        int cs = colors.length - 1;
        List<Color> transColors = new ArrayList<Color>();
        int hex, fadeCount;
        double r, g, b, rd, gd, bd, steps, ratio;
        for (int i=0; i<cs; i += 2) {
            hex = colors[i];
            fadeCount = colors[i + 2];
            r = hex >> 16;
        	g = hex >> 8 & 0xFF;
    		b = hex & 0xFF;
            transColors.add(Color.rgb((int)r, (int)g, (int)b));
            rd = (fadeCount >> 16) - r;
            gd = (fadeCount >> 8 & 0xFF) - g;
            bd = (fadeCount & 0xFF) - b;
            steps = colors[i + 1] + 1;
            for (double j=1; j<steps; j++) {
                ratio = j / steps;
                transColors.add(Color.rgb((int)(r + rd * ratio), (int)(g + gd * ratio), 
                		(int)(b + bd * ratio)));
            }
        }
        r = colors[cs] >> 16;
        g = colors[cs] >> 8 & 0xFF;
        b = colors[cs] & 0xFF;
        transColors.add(Color.rgb((int)r, (int)g, (int)b));
        return transColors.toArray(new Color[]{});
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
		final double angle = ((beginAngle) / 180d) * Math.PI;
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
     */
    protected void moveIndicator(final double x, final double y) {
		angleProperty.set(cartesianCoordinatesToViewingAngle(x, y));
    }

    /**
     * Calibrates a viewing angle to ensure that it is within range of the start/end angle
     * 
     * @param viewingAngle the viewing angle to calibrate
     * @return the calibrated viewing angle
     */
    protected final double calibrateViewingAngle(final double viewingAngle) {
    	if (isCircular()) {
    		// angle will always be with in 360 range
    		return viewingAngle;
    	} else {
	    	double trigAngle = flipAngleVertically(viewingAngle);
	    	double startAngle = getGeometericStartAngle();
	    	double endAngle = getGeometericEndAngle();
//	    	System.out.println(String.format("angleStart: %1$s, angleLength: %2$s, viewingAngle: %3$s, " +
//	    			"startAngle: %4$s, endAngle: %5$s, trigAngle: %6$s", 
//	    			angleStart, angleLength, viewingAngle, startAngle, endAngle, trigAngle));
	    	if ((startAngle <= endAngle && trigAngle >= startAngle && trigAngle <= endAngle) ||
	    		(startAngle > endAngle && (trigAngle >= startAngle || trigAngle <= endAngle))) {
	    		// update the angle property with the viewing angle using the predefined precision
	    		return viewingAngle;
	    	} else {
	        	double closestAngle = closestAngle(trigAngle, startAngle, endAngle);
	    		if (closestAngle == startAngle && Math.abs(trigAngle - startAngle) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
	    			// move to the start position when the angle is within the start angle threshold
	    			return flipAngleVertically(startAngle);
	    		} else if (closestAngle == endAngle && Math.abs(trigAngle - endAngle) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
	    			// move to the end position when the angle is within the end angle threshold
	    			return flipAngleVertically(endAngle);
	    		} else if (closestAngle == endAngle && Math.abs(trigAngle - 
	    				positiveAngle(startAngle - ANGLE_START_END_DISTANCE_THRSHOLD)) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
	    			// handle special case where the angle is within a specified threshold of the start position of a 0/360 border
	    			// (i.e. angleStart=0)
	    			return flipAngleVertically(startAngle);
	    		} else if (closestAngle == startAngle && Math.abs(trigAngle - 
	    				positiveAngle(360d - endAngle + ANGLE_START_END_DISTANCE_THRSHOLD)) <= ANGLE_START_END_DISTANCE_THRSHOLD) {
	    			// handle special case where the angle is within a specified threshold of the end position of a 0/360 border
	    			// (i.e. angleStart=180, angleLength=180)
	    			return flipAngleVertically(endAngle);
	    		}
	    	}
    	}
    	return -1d;
    }
    
    /**
     * Converts coordinates to a viewing angle
     *  
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the angle
     */
    protected static double cartesianCoordinatesToViewingAngle(final double x, final double y) {
    	double viewingAngle = Math.toDegrees(Math.atan2(y, x));
    	// convert angle to positive quadrants 0 - 360 degrees
		if (viewingAngle < 0) {
			viewingAngle += 360d;
		}
		return viewingAngle;
    }
    
    /**
     * Gets the distance between two points
     * 
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return the distance between the two points
     */
    protected static double cartesianCoordinatesDistance(double x1, double y1, double x2, double y2) {
    	return Math.sqrt(Math.pow(x1 - x2, 2d) + Math.pow(y1 - y2, 2d));
    }
    
    /**
     * Returns the closest angle to the supplied angle
     * 
     * @param angle the angle to check against
     * @param angles the angles to check
     * @return the angle that is closest to the angle checked against
     */
    protected static double closestAngle(final double angle, final double... angles) {
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
     * Gets the tick mark value closest to the supplied tick value
     * 
     * @param tickValue the tick value
     * @return the tick mark value closest to the tick mark
     */
    public double closestTickMarkValue(final double tickValue) {
    	// TODO : add logic to determine the closest tick label
    	final int totalTicks = numOfMajorTickMarks * (numOfMinorTickMarksPerMajorTick <= 0 ? 1 : numOfMinorTickMarksPerMajorTick);
    	final double minTickValue = getTickValue(getViewingEndAngle());
    	final double scaledTickValue = getScaledTickValue(tickValue);
    	double snappedValue = tickValue;
    	if ((snappedValue - minTickValue) % totalTicks != 0 ) {
            double temp = (snappedValue - minTickValue) / totalTicks;
            long whichTick = Math.round(temp);
//            // This is the fix for the bug #6401380
//            if (temp - (int)temp == .5 && scaledTickValue < lastValue) {
//              whichTick --;
//            }
            snappedValue = minTickValue + (whichTick * totalTicks);
        }
    	return snappedValue;
    }
    
    /**
     * Converts the angle to a positive angle value (when negative)
     * 
     * @param angle the angle to be converted
     * @return the positive angle
     */
    public static double positiveAngle(final double angle) {
    	return angle < 0 ? 360d + angle : angle;
    }
    
    /**
     * Flips an angle relative to the y axis. For example, if an angle has a zero quadrant 
     * position on the <b>east</b> horizontal plane the return angle will have a zero 
     * position on the <b>west</b> horizontal plane.
     * 
     * @param angle the angle to flip
     * @return the reversed angle
     */
    public double flipAngleVertically(final double angle) {
    	final double ra = 180d - angle;
    	return ra < 0 || (ra == 0 && getGeometericStartAngle() != 0) ? ra + 360d : ra;
    }
    
    /**
     * Calculates the difference of two angles
     * 
     * @param angle1 angle one
     * @param angle2 angle two
     * @return the difference
     */
    public static double differenceAngle(final double angle1, final double angle2) {
        return Math.abs((angle1 + 180d - angle2) % 360d - 180d);
    }
    
    /**
     * Calculates the end angle of a given start angle and the angle length
     * 
     * @param startAngle the start angle
     * @param angleLength the angle length
     * @return the the end angle
     */
    public static double getGeometericEndAngle(final double startAngle, final double angleLength) {
    	return (startAngle + angleLength) > 360d ? (startAngle + angleLength) - 360d : (startAngle + angleLength);
    }
    
    /**
     * Calculates the center angle of a given start angle and the angle length
     * 
     * @param startAngle the start angle
     * @param angleLength the angle length
     * @return the center angle
     */
    public static double getGeometricCenterAngle(final double startAngle, final double angleLength) {
    	return Math.abs((angleLength / 2d) + startAngle - 360d);
    }
    
    /**
     * @return the start angle within the gauges range relative to the normal trigonometry angle where an 
     * 		angle of zero is in the east horizontal plane
     */
    public double getGeometericStartAngle() {
    	return angleStart;
    }
    
    /**
     * @return the end angle within the gauges range relative to the normal trigonometry angle where an 
     * 		angle of zero is in the east horizontal plane
     */
    public double getGeometericEndAngle() {
    	return getGeometericEndAngle(angleStart, angleLength);
    }
    
    /**
     * @return the center angle within the gauges range
     */
    public double getGeometricCenterAngle() {
    	return Math.abs((angleLength / 2d) + angleStart - 360d);
    }
    
    /**
     * @return gets the viewing start angle that is within the range of the gauge
     */
    public double getViewingStartAngle() {
    	return flipAngleVertically(getGeometericStartAngle());
    }
    
    /**
     * @return gets the viewing end angle that is within the range of the gauge
     */
    public double getViewingEndAngle() {
    	return flipAngleVertically(getGeometericEndAngle());
    }
    
    /**
     * @return the current angle
     */
    public double getAngle() {
    	return angleProperty.get();
    }
    
    /**
     * @param angle the angle to set
     */
    public void setAngle(final double angle) {
    	angleProperty.set(angle);
    }
    
    /**
     * Calculates the default x coordinate of a tick mark
     * 
     * @return the x coordinate
     */
    protected double tickMarkDefaultX() {
    	return centerX - innerRadius;
    }
    
    /**
     * Calculates the default y coordinate of a tick mark
     * 
     * @return the y coordinate
     */
    protected double tickMarkDefaultY() {
    	return centerY;
    }
    
    /**
     * Calculates the angle of a tick mark
     * 
     * @param numOfMarks the total number of tick marks
     * @param index the index of the tick mark relative to the total number of tick marks (zero based)
     * @return the angle of the tick mark
     */
    protected double tickMarkAngle(final int numOfMarks, final int index) {
    	return 180d - ((angleLength / numOfMarks) * index) - angleStart;
    }
    
    /**
     * Gets the viewing angle of a tick value
     * 
     * @param tickValue the tick value to get the viewing angle for
     * @return the viewing angle
     */
    public double getViewingAngle(final double tickValue) {
    	return getNumberOfMajorTicks() * getScaledTickValue(tickValue) + getViewingEndAngle();
    }
    
    /**
     * Gets a scaled tick value for a given tick value
     * 
     * @param tickValue the tick value to scale
     * @return the scaled tick value
     */
    protected double getScaledTickValue(final double tickValue) {
    	double tickValueScaled = tickValue;
    	if (tickValueScale == 1) {
    		tickValueScaled /= tickValueScale;
    		tickValueScaled -= tickValueZeroOffset;
    	} else {
    		tickValueScaled -= tickValueZeroOffset;
    		tickValueScaled /= tickValueScale;
    	}
    	return tickValueScaled;
    }
    
    /**
     * Gets the tick value relative to the specified viewing angle
     * 
     * @param viewingAngle the viewing angle
     * @return the tick value
     */
    public double getTickValue(final double viewingAngle) {
    	final double viewingEndAngle = getViewingEndAngle();
    	final double numOfTicks = getNumberOfMajorTicks();
    	final double numOfTicksAtEndAngle = viewingEndAngle / numOfTicks;
    	final double numOfTicksAtAngle = (viewingAngle != viewingEndAngle && 
    			viewingAngle <= 180d && viewingEndAngle >= 180d ? 
    			360d + viewingAngle : viewingAngle) / numOfTicks;
    	final double tickValue = ((numOfTicksAtAngle - numOfTicksAtEndAngle) * tickValueScale);
    	return tickValue + tickValueZeroOffset;
    }
    
    /**
     * Gets the current tick value relative to the tick mark the indicator is pointing to.
     * <p>To listen for changes on the tick mark value use the {@linkplain #angleProperty} and call 
     * {@linkplain #getTickValue()}</p>
     * 
     * @return the current tick value
     */
    public double getTickValue() {
    	return tickValueProperty.get();
    }
    
    /**
     * Sets the tick value relative to the {@linkplain #getNumberOfMajorTicks()}. When the tick value is out 
     * of range the closest value will be set relative to the start/end angles within the 
     * {@linkplain #ANGLE_START_END_DISTANCE_THRSHOLD}. When the value is also outside the threshold range 
     * the value will be default to the starting tick value. <b>The actual tick value set will be adjusted to
     * conform to the {@linkplain #snapToTicksProperty} (when used).</b>
     * <p>To listen for changes on the tick mark value use the {@linkplain #angleProperty} and call 
     * {@linkplain #getTickValue()}</p>
     * 
     * @param tickValue the tick value to set
     */
    public void setTickValue(final double tickValue) {
    	tickValueProperty.set(tickValue);
    }
    
    /**
     * Gets the tick value {@linkplain #getTickValue(double)} for the supplied angle formated for display
     * 
     * @param viewingAngle the angle to get the tick value for
     * @return the formated tick value
     */
    public String getTickValueLabel(final double viewingAngle) {
    	double tickValue = getTickValue(viewingAngle);
    	return tickValueFormat.format(tickValue);
    }
    
    /**
     * Gets the current tick value {@linkplain #getTickValue()} formated for display
     * 
     * @return the formated tick value
     */
    public String getTickValueLabel() {
    	return getTickValueLabel(angleProperty.get());
    }
    
    /**
     * @return a tick value format that will be used for tick value labels
     */
    protected final DecimalFormat createTickValueFormat() {
    	String format = String.valueOf(tickValueScale).replaceAll("[\\d\\-]", "#");
    	return new DecimalFormat(format);
    }
    
    /**
     * @return the total number of major tick marks
     */
    public final double getNumberOfMajorTicks() {
    	return angleLength / numOfMajorTickMarks;
    }
    
    /**
     * @return the total number of minor tick marks
     */
    public final int getNumberOfMinorTicks() {
    	return numOfMinorTickMarksPerMajorTick <= 1 ? -1 : numOfMajorTickMarks * numOfMinorTickMarksPerMajorTick;
    }
    
    /**
     * @return true when the gauge is circular, false when it is an arc
     */
    public final boolean isCircular() {
    	return angleLength == 360d;
    }
    
    /**
     * @return the effect applied to the indicator when it is being moved
     */
    protected Glow getIndicatorMoveEffect() {
		return indicatorMoveEffect;
	}

	/**
     * Indicator/Hand types
     */
    public enum IndicatorType {
    	RECTANGLE, NEEDLE, CLOCK, KNOB;
    }
    
    /**
     * Sets the intensity regions {@linkplain 
     * IntensityIndicatorRegions#IntensityIndicatorRegions(double, double, double)}
     * 
	 * @param color1SpanPercentage the span percentage of color 1
	 * @param color2SpanPercentage the span percentage of color 2
	 * @param color3SpanPercentage the span percentage of color 3
     */
    public void setIntensity(final double color1SpanPercentage, final double color2SpanPercentage, 
			final double color3SpanPercentage) {
		intensityIndicatorRegionsProperty.set(new IntensityIndicatorRegions(
				color1SpanPercentage, color2SpanPercentage, color3SpanPercentage));
    }
    
    
    /**
     * Sets the intensity regions {@linkplain 
     * IntensityIndicatorRegions#IntensityIndicatorRegions(double, double, double, Color, Color, Color)}
     * 
	 * @param color1 color 1
	 * @param color2 color 2
	 * @param color3 color 3
     */
    public void setIntensity(final Color color1, final Color color2, final Color color3) {
		intensityIndicatorRegionsProperty.set(new Gauge.IntensityIndicatorRegions(
				INTENSITY_PERCENTAGE_1, INTENSITY_PERCENTAGE_2, INTENSITY_PERCENTAGE_3, 
				color1, color2, color3));
    }
    
    /**
     * Sets the intensity regions {@linkplain 
     * IntensityIndicatorRegions#IntensityIndicatorRegions(double, double, double, Color, Color, Color)}
     * 
	 * @param color1SpanPercentage the span percentage of color 1
	 * @param color2SpanPercentage the span percentage of color 2
	 * @param color3SpanPercentage the span percentage of color 3
	 * @param color1 color 1
	 * @param color2 color 2
	 * @param color3 color 3
     */
    public void setIntensity(final double color1SpanPercentage, final double color2SpanPercentage, 
			final double color3SpanPercentage, final Color color1, final Color color2, final Color color3) {
		intensityIndicatorRegionsProperty.set(new Gauge.IntensityIndicatorRegions(
				color1SpanPercentage, color2SpanPercentage, color3SpanPercentage, 
				color1, color2, color3));
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
    		this(color1SpanPercentage, color2SpanPercentage, color3SpanPercentage, Color.RED, Color.YELLOW, Color.GREEN.brighter());
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
			final double sum = Math.round(color1SpanPercentage + color2SpanPercentage + color3SpanPercentage);
			if (sum != 100) {
				throw new IllegalArgumentException(String.format(
						"The sum of color percentages: %s + %s + %s = %s must be 100", color1SpanPercentage, 
						color2SpanPercentage, color3SpanPercentage, sum));
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

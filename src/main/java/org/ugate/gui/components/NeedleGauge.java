package org.ugate.gui.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Light.Point;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

/**
 * Needle driven gauge control
 */
public class NeedleGauge extends Group {

	private final double gaugeAngle = 360;
	private final int numOfMarks = 12;
	private final double outerBorderRadius = 140;
	private final double innerBorderRadius = 130;
	private final int numOfMinorMarks = numOfMarks * 10;
	private final double majorMarkWidth = 10;
	private final double majorMarkHeight = 2;
	private final double minorMarkWidth = majorMarkWidth / 2;
	private final double minorMarkHeight = majorMarkHeight;
	private final double handWidth = innerBorderRadius - minorMarkWidth;
	private final double handHeight = majorMarkHeight * 2;
	public final DoubleProperty handAngleProperty = new SimpleDoubleProperty(0);

	public NeedleGauge() {
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		final Group og = new Group();
		og.setCache(true);
		og.setCacheHint(CacheHint.SPEED);
		final Shape c1 = createOuterGauge(outerBorderRadius, gaugeAngle);
		final Shape c2 = createInnerGauge(outerBorderRadius, innerBorderRadius, gaugeAngle);
		og.getChildren().addAll(c1, c2);
		// add minor tick marks
		addTickMarks(og, c2, numOfMinorMarks, Color.web("#AAAAAA"), minorMarkWidth, minorMarkHeight, majorMarkWidth, majorMarkHeight);
		// add major tick marks
		addTickMarks(og, c2, numOfMarks, Color.web("#CCCCCC"), majorMarkWidth, majorMarkHeight, majorMarkWidth, majorMarkHeight);
		// 
		final Group hand = new Group();
		final DropShadow handDropShadow = new DropShadow();
		handDropShadow.setOffsetX(4);
		handDropShadow.setOffsetY(4);
		handDropShadow.setRadius(6);
		handDropShadow.setColor(Color.web("#000000"));
		hand.setEffect(handDropShadow);
		final Group handBase = new Group();
		final Circle hc1 = new Circle(outerBorderRadius, outerBorderRadius, 8);
		hc1.setFill(Color.web("#FF0000"));
		
		final Rectangle hrec = new Rectangle(hc1.getCenterX() - (handWidth / 1.2), hc1.getCenterY() - (handHeight / 2), handWidth, handHeight);
		final Rotate hrecRotate = new Rotate(this.handAngleProperty.get(), hc1.getCenterX(), hc1.getCenterY());
		Bindings.bindBidirectional(hrecRotate.angleProperty(), this.handAngleProperty);
		hrec.getTransforms().addAll(hrecRotate);
		hrec.setFill(Color.web("#FF0000"));
		
		final Light.Distant handBaseLight = new Light.Distant();
		handBaseLight.setAzimuth(225);
		final Lighting handBaseLighting = new Lighting();
		handBaseLighting.setLight(handBaseLight);
		handBase.setEffect(handBaseLighting);
		
		handBase.getChildren().addAll(hc1, hrec);
		hand.getChildren().add(handBase);
		og.getChildren().addAll(hand);
		
		// highlight
		final Group highlight = new Group();
		highlight.setCache(true);
		highlight.setCacheHint(CacheHint.SPEED);
		highlight.setOpacity(0.05);
		final Arc hArc1 = new Arc(outerBorderRadius, outerBorderRadius, outerBorderRadius / 1.1, outerBorderRadius / 1.1, 200, -130);
		hArc1.setFill(Color.WHITE);
		final Arc hArc2 = new Arc(outerBorderRadius, outerBorderRadius, outerBorderRadius / 1.1, outerBorderRadius / 1.1, 190, -122);
		hArc2.setFill(Color.WHITE);
		highlight.getChildren().addAll(hArc1, hArc2);
		final GaussianBlur highlightBlur = new GaussianBlur();
		highlightBlur.setRadius(2);
		highlight.setEffect(highlightBlur);
		og.getChildren().addAll(highlight);
		
		getChildren().addAll(og);
	}
	
	protected Shape createOuterGauge(final double radius, final double angle) {
		final Shape og = new Circle(radius, radius, radius);
		// fill border rim
		og.fillProperty().set(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#FFFFFF")), new Stop(1, Color.web("#010101"))));
		return og;
	}
	
	protected Shape createInnerGauge(final double outerRadius, final double radius, final double angle) {
		final Shape ig = new Circle(outerRadius, outerRadius, radius);
		ig.fillProperty().set(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
				new Stop(0, Color.web("#777777")), new Stop(0.9499, Color.rgb(20, 20, 20)),
				new Stop(0.95, Color.rgb(20, 20, 20)), new Stop(0.975, Color.rgb(20, 20, 20)),
				new Stop(1, Color.rgb(84, 84, 84, 0.0))));
		return ig;
	}
	
	protected void addTickMarks(final Group parent, final Shape reference, final double numOfMarks, final Paint fill,
			final double width, final double height, final double offestWidth, final double offsetHeight) {
		final double rtbase = (reference instanceof Circle ? 360 : ((Arc) reference).getRadiusX()) / numOfMarks;
		for (int i=0; i<numOfMarks; i++) {
			parent.getChildren().add(createTickMark(reference, fill, rtbase * i, width, height, offestWidth, offsetHeight));
        }
	}
	
    protected Shape createTickMark(final Shape reference, final Paint fill, final double angle, 
    		final double width, final double height, final double offestWidth, final double offsetHeight) {
		final double pivotX = reference instanceof Circle ? ((Circle) reference).getCenterX() : ((Arc) reference).getCenterX();
		final double pivotY = reference instanceof Circle ? ((Circle) reference).getCenterY() : ((Arc) reference).getCenterY();
		final double x = (pivotX - (width / 2)) / offestWidth;
		final double y = pivotY - (height / 2);
    	final Rectangle tm = new Rectangle(x, y, width, height);
    	//final Line tm = new Line(x, y, width, height);
    	tm.fillProperty().set(fill);
		tm.getTransforms().addAll(new Rotate(angle, pivotX, pivotY));
		return tm;
    }
    
    protected Point calculateRotationPosition(final Shape reference) {
    	final Point xy = new Point();
    	xy.setX(reference instanceof Circle ? ((Circle) reference).getCenterX() : ((Arc) reference).getCenterX());
    	xy.setY(reference instanceof Circle ? ((Circle) reference).getCenterY() : ((Arc) reference).getCenterY());
    	return xy;
    }
    
//    public void setValue(final double value) {
//    	this.value = value < 0.0D ? 0 : value > 180 ? 180 : value + 90;
//    	
//    }
}

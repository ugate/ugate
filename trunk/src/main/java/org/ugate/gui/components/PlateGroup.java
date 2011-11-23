package org.ugate.gui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class PlateGroup extends Group {
	
	public static final double BOLT_RADIUS = 4d;
	public static final double BOLT_OFFSET = 10d;
	
	public PlateGroup(final ReadOnlyDoubleProperty widthProperty, final ReadOnlyDoubleProperty heightProperty,
			final ObjectProperty<Insets> paddingProperty) {
		final Rectangle mainRec = new Rectangle();
		mainRec.setFill(new LinearGradient(0, 0, 0, 1d, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITESMOKE),
				new Stop(0.01d, Color.BLACK), new Stop(0.99d, Color.BLACK), new Stop(1d, Color.WHITESMOKE)));
		widthProperty.addListener(new InvalidationListener() {
			@Override
			public void invalidated(final Observable observable) {
				mainRec.setWidth(widthProperty.doubleValue());
			}
		});
		heightProperty.addListener(new InvalidationListener() {
			@Override
			public void invalidated(final Observable observable) {
				mainRec.setHeight(heightProperty.doubleValue() + paddingProperty.get().getTop());
			}
		});
		mainRec.setArcHeight(10d);
		mainRec.setArcWidth(10d);
		Shape mainShape = mainRec;
		mainShape.setEffect(createLighting());
		getChildren().add(mainShape);
	}
	
	private Lighting createLighting() {
		final Light.Distant light = new Light.Distant();
		final Lighting lighting = new Lighting();
		lighting.setLight(light);
		return lighting;
	}
}

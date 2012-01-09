package org.ugate.gui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.ugate.gui.GuiUtil;
import org.ugate.resources.RS;

import com.sun.javafx.collections.NonIterableChange;

/**
 * Text field with a label and context menu
 */
public class TextFieldMenu extends VBox {
	
	private final ToggleGroup toggleGroup = new ToggleGroup();
	private final ContextMenu menu = new ContextMenu();
	private final Label label = new Label();
	private final TextField textField = new TextField();
	private ImageView openButton;

	/**
	 * Constructor
	 */
	public TextFieldMenu(final String text) {
		super(5);
		label.setText(text);
		addMenuItems();
	}
	
	/**
	 * Adds the menu item controls
	 */
	protected void addMenuItems() {
		menu.setId("choice-box-popup-menu");
		menu.getItems().addListener(new ListChangeListener<MenuItem>() {
			@Override
			public void onChanged(final Change<? extends MenuItem> c) {
				if (!(c instanceof NonIterableChange) && c.getRemovedSize() > 0 && textField.getText() != null && 
						!textField.getText().isEmpty()) {
					for (final MenuItem mi : c.getRemoved()) {
						if (mi.getText() != null && mi.getText().equals(textField.getText())) {
							textField.setText("");
						}
					}
				}
			}
		});
		openButton = RS.imgView(RS.IMG_CORNER_RESIZE);
		openButton.setCursor(Cursor.HAND);
		openButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					toggleMenu();
				}
			}
		});
		textField.getStyleClass().setAll("text-field-fx");
		textField.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					toggleMenu();
				}
			}
		});
		final StackPane sp = new StackPane();
		sp.setAlignment(Pos.BOTTOM_RIGHT);
		sp.getChildren().addAll(textField, openButton);
	    getChildren().addAll(label, sp);
	}
	
	/**
	 * Toggles the menu
	 */
	protected void toggleMenu() {
		if (menu.isShowing()) {
			menu.hide();
		} else {
			menu.show(textField, Side.BOTTOM, 0.0d, 0.0d);
		}
	}
	
	/**
	 * Adds items to the menu
	 * 
	 * @param itemContents the menu item content(s)
	 */
	public void addMenuItems(final Object... itemContents) {
		final List<MenuItem> its = new ArrayList<MenuItem>();
		for (final Object ic : itemContents) {
			final RadioMenuItem mi = new RadioMenuItem(ic == null ? "" : ic.toString());
			mi.setId("choice-box-menu-item");
			mi.setToggleGroup(toggleGroup);
			mi.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(final ActionEvent event) {
					textField.setText(((MenuItem)event.getSource()).getText());
				}
			});
			its.add(mi);
		}
		menu.getItems().addAll(its);
	}
}

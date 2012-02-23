package org.ugate.gui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.ugate.gui.GuiUtil;

import com.sun.javafx.collections.NonIterableChange;

/**
 * Text field with a label and context menu
 */
public class TextFieldMenu extends VBox {
	
	public static final double MENU_BUTTON_WIDTH = 12d;
	public static final double MENU_BUTTON_HEIGHT = 8d;
	private final ToggleGroup toggleGroup = new ToggleGroup();
	private final ContextMenu menu = new ContextMenu();
	private final Label label;
	private final TextField textField = new TextField();
	private final Region menuNewItemButton = new Region();
	private final Region menuRemoveItemButton = new Region();
	private final Region menuButton = new Region();
	private final ReadOnlyStringWrapper selectionWrapper = new ReadOnlyStringWrapper();
	
	/**
	 * Constructor
	 * 
	 * @param labelText the label for the text field
	 * @param promptText the prompt text
	 */
	public TextFieldMenu(final String labelText, final String promptText) {
		super(5d);
		if (labelText != null && !labelText.isEmpty()) {
			label = new Label(labelText);
			getChildren().add(label);
		} else {
			label = null;
		}
		textField.setPromptText(promptText);
		addMenuItems();
	}
	
	/**
	 * Adds the menu item controls
	 */
	protected void addMenuItems() {
//		setStyle("-fx-background-color: gray;");
//		menu.setId("choice-box-popup-menu");
		menu.getItems().addListener(new ListChangeListener<MenuItem>() {
			@Override
			public void onChanged(final Change<? extends MenuItem> c) {
				if (!(c instanceof NonIterableChange) && c.getRemovedSize() > 0 && textField.getText() != null && 
						!textField.getText().isEmpty()) {
					for (final MenuItem mi : c.getRemoved()) {
						if (mi.getText() != null && mi.getText().equals(textField.getText())) {
							setSelectionText(null);
						}
					}
				}
			}
		});
		//textField.getStyleClass().setAll("text-field-fx");
		textField.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					toggleMenu();
				}
			}
		});
		menuButton.getStyleClass().setAll(new String[] { "menu-down-arrow" });
		menuButton.setMaxSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
		menuButton.setCursor(Cursor.HAND);
		menuButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					toggleMenu();
				}
			}
		});
		// new entry has been confirmed by clicking on the new choice button
		menuNewItemButton.getStyleClass().setAll(new String[] { "text-field-menu-add-choice" });
		menuNewItemButton.setMaxSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT / 2d);
		menuNewItemButton.setCursor(Cursor.HAND);
		menuNewItemButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					addMenuItems(textField.getText());
					select(textField.getText());
				}
			}
		});
		// remove selected item
		menuRemoveItemButton.getStyleClass().setAll(new String[] { "text-field-menu-remove-choice" });
		menuRemoveItemButton.setMaxSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT / 2d);
		menuRemoveItemButton.setCursor(Cursor.HAND);
		menuRemoveItemButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					selectOrRemove(textField.getText(), true);
				}
			}
		});
		final GridPane editMenu = new GridPane();
		editMenu.setAlignment(Pos.BOTTOM_RIGHT);
		editMenu.setPadding(new Insets(7d, 0, 0, 0));
		editMenu.setHgap(2d);
		editMenu.setVgap(5d);
		editMenu.add(menuNewItemButton, 0, 0);
		editMenu.add(menuRemoveItemButton, 0, 1);
		editMenu.setMaxSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
		GridPane.setVgrow(menuNewItemButton, Priority.ALWAYS);
		GridPane.setVgrow(menuRemoveItemButton, Priority.NEVER);
		final StackPane textStack = new StackPane();
		textStack.setAlignment(Pos.BOTTOM_RIGHT);
		textStack.getChildren().addAll(textField, menuButton);
		final HBox textBox = new HBox(2d);
		textBox.getChildren().addAll(textStack, editMenu);
	    getChildren().addAll(textBox);
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
	 * @param selectFirstItem true to select the first ite
	 * @param itemContents the menu item content(s)
	 */
	public void addMenuItems(final Object... itemContents) {
		if (itemContents == null || itemContents.length == 0) {
			return;
		}
		final RadioMenuItem[] existingMenuItems = menu.getItems() != null ? 
				menu.getItems().toArray(new RadioMenuItem[]{}) : new RadioMenuItem[]{};
		final List<MenuItem> its = new ArrayList<MenuItem>();
		boolean duplicate = false;
		for (final Object ic : itemContents) {
			duplicate = false;
			final RadioMenuItem mi = new RadioMenuItem(ic == null ? "" : ic.toString());
			for (final RadioMenuItem ei : existingMenuItems) {
				if (ei.getText().equals(mi.getText())) {
					duplicate = true;
					break;
				}
			}
			if (!duplicate) {
				mi.setId("choice-box-menu-item");
				mi.setToggleGroup(toggleGroup);
				mi.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(final ActionEvent event) {
						setSelectionText(((MenuItem)event.getSource()).getText());
					}
				});
				its.add(mi);
			}
		}
		menu.getItems().addAll(its);
	}
	
	/**
	 * Selects the menu item that matches the supplied content. Deselects all when null or no match 
	 * exists in the menu items.
	 * 
	 * @param itemContent the item content to select from the menu items
	 */
	public void select(final Object itemContent) {
		selectOrRemove(itemContent, false);
	}
	
	/**
	 * Selects the menu item that matches the supplied content. Deselects all when null or no match 
	 * exists in the menu items.
	 * 
	 * @param itemContent the item content to select from the menu items
	 * @param remove true to remove false to select
	 */
	protected void selectOrRemove(final Object itemContent, final boolean remove) {
		final RadioMenuItem[] existingMenuItems = menu.getItems() != null ? 
				menu.getItems().toArray(new RadioMenuItem[]{}) : new RadioMenuItem[]{};
		String meuContent;
		RadioMenuItem removeItem = null;
		for (final RadioMenuItem ei : existingMenuItems) {
			meuContent = itemContent != null ? itemContent.toString() : null;
			if (meuContent != null && !meuContent.isEmpty() && meuContent.equals(ei.getText())) {
				if (remove) {
					removeItem = ei;
				} else {
					ei.setSelected(true);
					setSelectionText(ei.getText());
				}
			} else {
				ei.setSelected(false);
			}
		}
		if (removeItem != null) {
			menu.getItems().remove(removeItem);
			if (itemContent.equals(removeItem.getText())) {
				setSelectionText(null);
			}
		}
	}
	
	/**
	 * Sets the selection text on the text input
	 * 
	 * @param selectionText the selection text
	 */
	protected void setSelectionText(final String selectionText) {
		// no binding because updates will occur when typing
		textField.setText(selectionText);
		selectionWrapper.set(selectionText);
	}
	
	/**
	 * @return the selection property
	 */
	public ReadOnlyStringProperty selectionProperty() {
		return selectionWrapper.getReadOnlyProperty();
	}
}

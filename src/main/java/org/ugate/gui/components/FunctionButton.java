package org.ugate.gui.components;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import org.ugate.gui.GuiUtil;

/**
 * A small button used to for common {@linkplain Function}s
 */
public class FunctionButton extends Button {
	
	private static final double BUTTON_SIZE = 12d;

	/**
	 * Constructor
	 * 
	 * @param function
	 *            the {@linkplain Function} of the {@linkplain FunctionButton}
	 * @param runnable
	 *            the {@linkplain Runnable} to execute for the
	 *            {@linkplain FunctionButton}
	 */
	public FunctionButton(final Function function, final Runnable runnable) {
		getStyleClass().add("button-mini");
		setCursor(Cursor.HAND);
		setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
		addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (GuiUtil.isPrimaryPress(event)) {
					runnable.run();
				}
			}
		});
		final Region graphic = new Region();
		graphic.getStyleClass().setAll(new String[] { function.getCssClass() });
		setGraphic(graphic);
	}

	/**
	 * The function to perform on a {@linkplain FunctionButton}
	 */
	public enum Function {
		ADD("text-field-menu-add-choice"), 
		REMOVE("text-field-menu-remove-choice");
		
		private final String cssClass;

		/**
		 * Constructor
		 * 
		 * @param cssClass
		 *            the {@linkplain #getCssClass()}
		 */
		private Function(final String cssClass) {
			this.cssClass = cssClass;
		}

		/**
		 * @return the CSS class for the {@linkplain Function}
		 */
		public String getCssClass() {
			return cssClass;
		}
	}
}

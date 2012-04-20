package org.ugate.gui.components;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Log4j TextArea appender for JavaFX
 */
public class TextAreaAppender<E> { //extends WriterAppender<E> {

	private static volatile TextArea textArea = null;
	
	/** Set the target JTextArea for the logging information to appear. */
	public static void setTextArea(final TextArea textArea) {
		TextAreaAppender.textArea = textArea;
	}
	/**
	 * Format and then append the loggingEvent to the stored
	 * JTextArea.
	 */
//	public void append(final LoggingEvent loggingEvent) {
//		final String message = this.layout.format(loggingEvent);
//
//		// Append formatted message to text area using the Swing Thread.
//		try {
//			Platform.runLater(new Runnable() {
//				public void run() {
//					try {
//						if (textArea != null) {
//							if (textArea.getText().length() == 0) {
//								textArea.setText(message);
//							} else {
//								textArea.selectEnd();
//								textArea.insertText(textArea.getText().length(), message);
//							}
//						}
//					} catch (final Throwable t) {
//						System.out.println("Unable to append log to GUI: " + t.getMessage());
//					}
//				}
//			});
//		} catch (final IllegalStateException e) {
//			// ignore case when the platform hasn't yet been iniitialized
//		}
//	}
}

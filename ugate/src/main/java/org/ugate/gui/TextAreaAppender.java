package org.ugate.gui;

import javafx.application.Platform;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.javafx.preview.control.TextArea;

/**
 * Log4j TextArea appender for JavaFX
 */
public class TextAreaAppender extends WriterAppender {

	static volatile private TextArea textArea = null;
	
	/** Set the target JTextArea for the logging information to appear. */
	static public void setTextArea(TextArea textArea) {
		TextAreaAppender.textArea = textArea;
	}
	/**
	 * Format and then append the loggingEvent to the stored
	 * JTextArea.
	 */
	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);

		// Append formatted message to text area using the Swing Thread.
		Platform.runLater(new Runnable() {
			public void run() {
				try {
					if (textArea != null) {
						if (textArea.getText().length() == 0) {
							textArea.setText(message);
						} else {
							textArea.clearSelection();
							textArea.insertText(textArea.getText().length(), message);
						}
					}
				} catch (Throwable t) {
					System.out.println("Unable to append log to GUI: " + t.getMessage());
				}
			}
		});
	}
}

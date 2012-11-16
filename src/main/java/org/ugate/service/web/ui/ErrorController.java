package org.ugate.service.web.ui;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;

/**
 * Error {@link BaseController}
 */
public class ErrorController extends BaseController {

	private Integer errorCode;
	private String errorMessage;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RequiredValues processContext(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final WebContext ctx) throws Throwable {
		ctx.setVariable("errorCode", errorCode == null ? res.getStatus()
				: errorCode);
		ctx.setVariable("errorMessage", errorMessage == null ? ctx
				.getVariables().get("errorCode") : errorMessage);
		return new RequiredValues(null, RS.rbLabel(KEY.ERROR), null);
	}

	/**
	 * Processes an error
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * @param res
	 *            the {@link HttpServletResponse}
	 * @param servletContext
	 *            the {@link ServletContext}
	 * @param code
	 *            the HTTP status code
	 * @param message
	 *            the error message
	 * @throws IOException
	 *             the {@link IOException}
	 */
	public void processError(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final int code, final String message,
			final TemplateEngine templateEngine) throws IOException {
		this.errorCode = code;
		this.errorMessage = message;
		try {
			super.process(req, res, servletContext, templateEngine);
		} catch (final Throwable t) {
			if (t instanceof IOException) {
				throw (IOException) t;
			} else {
				throw new IOException("Unable to write to "
						+ BaseController.class.getSimpleName(), t);
			}
		}
	}
}

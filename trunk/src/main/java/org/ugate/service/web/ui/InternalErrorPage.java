package org.ugate.service.web.ui;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR} {@linkplain BasePage}
 */
public class InternalErrorPage extends BasePage {
	private static final long serialVersionUID = -7188150858059168625L;
	private static final Logger log = LoggerFactory.getLogger(InternalErrorPage.class);

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            the {@link PageParameters}
	 */
	public InternalErrorPage(final PageParameters parameters) {
		super(parameters);
		String msg = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
				+ " Internal Server Error";
		try {
			final StringValue sv = parameters.get(WicketApplication.SA_LAST_ERROR_MSG);
			if (sv.isNull() || sv.isEmpty()) {
				final Serializable s = getSession().getAttribute(WicketApplication.SA_LAST_ERROR_MSG);
				if (s != null) {
					msg = s.toString();
					getSession().removeAttribute(WicketApplication.SA_LAST_ERROR_MSG);
				}
			} else {
				msg = sv.toString();
			}
		} catch (final Throwable t) {
			log.warn(t.getMessage());
		}
		log.info(msg);
		add(new Label("errorContent", msg));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "UGate Mobile Error";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHeader() {
		return "UGate Mobile Error";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setHeaders(final WebResponse response) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}

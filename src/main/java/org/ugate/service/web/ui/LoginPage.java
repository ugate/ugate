package org.ugate.service.web.ui;

import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LoginPage extends BasePage {

	private static final long serialVersionUID = 6889937658802143321L;

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            the {@linkplain PageParameters}
	 */
	public LoginPage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "UGate Mobile";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHeader() {
		return "Login";
	}

}

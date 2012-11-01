package org.ugate.service.web.ui;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;
import org.ugate.resources.RS;
import org.ugate.resources.RS.KEY;

public class ErrorController extends BaseController {

	@Override
	public RequiredValues processContext(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final WebContext ctx) throws Throwable {
		// TODO Auto-generated method stub
		return new RequiredValues(RS.rbLabel(KEY.ERROR), null);
	}

}

package org.ugate.service.web.ui;

import java.util.ArrayList;
import java.util.Map;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RangeTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.ugate.Command;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.IModelType;
import org.ugate.service.entity.jpa.Actor;

/**
 * Base {@linkplain WebPage} for the application
 */
public abstract class BasePage extends WebPage {

	private static final long serialVersionUID = 6087480925969429202L;
	protected static final String LABEL_POSTFIX = "_LABEL";

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            the {@linkplain PageParameters}
	 */
	public BasePage(final PageParameters parameters) {
		super(parameters);
		//((WebRequest) RequestCycle.get().getRequest()).get
		add(new Label("title", getTitle()));
		add(new Label("home", "Home"));
		add(new Label("logInOut", "Login"));
		add(new Label("footer", "UGate Mobile"));
		add(new Label("header", getHeader()));
	}

	/**
	 * Finds an {@linkplain Actor} based upon the
	 * {@linkplain Actor#getUsername()} passed in the
	 * {@linkplain PageParameters}
	 * 
	 * @return the {@linkplain Actor} or null when none can be found
	 */
	protected Actor findActor(final PageParameters parameters) {
		final String username = "ugate.relay@gmail.com";//parameters.get(ActorType.USERNAME.name()).toString();
		if (username != null) {
			return ServiceProvider.IMPL.getCredentialService().getActor(username);
		}
		return null;
	}

	/**
	 * Adds a {@linkplain DropDownChoice} to the supplied
	 * {@linkplain MarkupContainer} that executes a {@linkplain Command} when a
	 * selection is made
	 * 
	 * @param parent
	 *            the {@linkplain MarkupContainer} to add the
	 *            {@linkplain DropDownChoice} to
	 * @param command
	 *            the {@linkplain Command} to to execute when changes are made
	 *            to the {@linkplain DropDownChoice}
	 * @param initialValue
	 *            the index of the initial value in the {@linkplain Map}
	 * @param label
	 *            the text for the {@linkplain Label}
	 * @param options
	 *            the key={@linkplain IChoiceRenderer#getIdValue(Object, int)},
	 *            value=the {@linkplain IChoiceRenderer#getDisplayValue(Object)}
	 * @return the {@linkplain DropDownChoice}
	 */
	protected <T extends org.ugate.service.entity.Model> DropDownChoice<Integer> addSelect(
			final MarkupContainer parent, final Command command,
			final int initialValue, final String label,
			final Map<Integer, String> options) {
		final int val = options.containsKey(initialValue) ? initialValue : 0;
		final DropDownChoice<Integer> ddc = new DropDownChoice<Integer>(
				command.name(), new Model<Integer>(val),
				new ArrayList<Integer>(options.keySet()),
				new IChoiceRenderer<Integer>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(final Integer value) {
						return options.get(value);
					}

					@Override
					public String getIdValue(final Integer value,
							final int index) {
						return options.get(value);
					}
				});
		ddc.setOutputMarkupId(true);
		parent.add(new Label(command.name() + LABEL_POSTFIX, label));
		parent.add(ddc);
		return ddc;
	}

	/**
	 * Adds a {@linkplain DropDownChoice} to the supplied
	 * {@linkplain MarkupContainer} binding it's value to a
	 * {@linkplain org.ugate.service.entity.Model} for a {@linkplain IModelType}
	 * 
	 * @param parent
	 *            the {@linkplain MarkupContainer} to add the
	 *            {@linkplain DropDownChoice} to
	 * @param type
	 *            the {@linkplain IModelType} to bind to the
	 *            {@linkplain DropDownChoice}
	 * @param model
	 *            the {@linkplain org.ugate.service.entity.Model} to bind to the
	 *            {@linkplain DropDownChoice}
	 * @param label
	 *            the text for the {@linkplain Label}
	 * @param options
	 *            the key={@linkplain IChoiceRenderer#getIdValue(Object, int)},
	 *            value=the {@linkplain IChoiceRenderer#getDisplayValue(Object)}
	 * @return the {@linkplain DropDownChoice}
	 */
	protected <T extends org.ugate.service.entity.Model> DropDownChoice<Integer> addSelect(
			final MarkupContainer parent, final IModelType<T> type,
			final T model, 
			final String label, final Map<Integer, String> options) {
		final DropDownChoice<Integer> ddc = new DropDownChoice<Integer>(
				type.name(), new PropertyModel<Integer>(model, type.getKey()),
				new ArrayList<Integer>(options.keySet()),
				new IChoiceRenderer<Integer>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(final Integer value) {
						return options.get(value);
					}

					@Override
					public String getIdValue(final Integer value, final int index) {
						return options.get(value);
					}
				});
		ddc.setOutputMarkupId(true);
		parent.add(new Label(type.name() + LABEL_POSTFIX, label));
		parent.add(ddc);
		return ddc;
	}

	/**
	 * Adds a {@linkplain RangeTextField} to the supplied
	 * {@linkplain MarkupContainer} binding it's value to a
	 * {@linkplain org.ugate.service.entity.Model} for a {@linkplain IModelType}
	 * 
	 * @param parent
	 *            the {@linkplain MarkupContainer} to add the
	 *            {@linkplain RangeTextField} to
	 * @param type
	 *            the {@linkplain IModelType} to bind to the
	 *            {@linkplain RangeTextField}
	 * @param model
	 *            the {@linkplain org.ugate.service.entity.Model} to bind to the
	 * @param minimum
	 *            the {@linkplain RangeTextField#setMinimum(Number)}
	 * @param maximum
	 *            the {@linkplain RangeTextField#setMaximum(Number)}
	 *            {@linkplain RangeTextField}
	 * @param label
	 *            the text for the {@linkplain Label}
	 * @return the {@linkplain RangeTextField}
	 */
	protected <T extends org.ugate.service.entity.Model> RangeTextField<Integer> addRange(
			final MarkupContainer parent, final IModelType<T> type,
			final T model, final Integer minimum, final Integer maximum,
			final String label) {
		final RangeTextField<Integer> tf = new RangeTextField<Integer>(
				type.name(), new PropertyModel<Integer>(model, type.getKey()),
				Integer.class);
		if (maximum != null) {
			tf.setMinimum(minimum);
		}
		if (maximum != null) {
			tf.setMaximum(maximum);
		}
		tf.setOutputMarkupId(true);
		parent.add(new Label(type.name() + LABEL_POSTFIX, label));
		parent.add(tf);
		return tf;
	}

	/**
	 * Adds a {@link #getSession()} attribute using the
	 * {@link Throwable#getMessage()} as the error message that will be shown on
	 * the error page then throws a {@link RuntimeException} to trigger the
	 * error page.
	 * 
	 * @param t
	 *            the {@link Throwable} that contains a user readable
	 *            {@link Throwable#getMessage()}
	 * @throws RuntimeException
	 *             the {@link RuntimeException} that will trigger the error page
	 */
	protected void internalError(final Throwable t) throws RuntimeException {
		// parameter option
		final PageParameters pp = new PageParameters();
		pp.add(WicketApplication.SA_LAST_ERROR_MSG, t.getMessage());
		setResponsePage(InternalErrorPage.class, pp);
		// session option
//		getSession().setAttribute(WicketApplication.SA_LAST_ERROR_MSG, 
//		t.getMessage());
//		if (t instanceof RuntimeException) {
//			throw (RuntimeException) t;
//		} else {
//			throw new RuntimeException(t.getMessage(), t);
//		}
	}

	/**
	 * @return the title of the {@linkplain WebPage}
	 */
	protected abstract String getTitle();

	/**
	 * @return the header of the {@linkplain WebPage} shown at the top of the
	 *         page
	 */
	protected abstract String getHeader();
}

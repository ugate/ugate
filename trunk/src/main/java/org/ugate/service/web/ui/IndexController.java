package org.ugate.service.web.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.WebContext;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeReadingType;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.ValueType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * Index controller for the {@link RemoteNode}s landing page
 */
public class IndexController extends BaseController {

	private static final Logger log = LoggerFactory
			.getLogger(IndexController.class);
	public static final String VAR_REMOTE_NODES_NAME = "remoteNodes";
	public static final String VAR_REMOTE_NODE_READING_NAME = "rnr";
	public static final String VAR_REMOTE_NODE_NAME = "rn";
	public static final String VAR_COMMAND_NAME = "command";
	public static final String VAR_ACTION_NAME = "action";
	public static final String VAR_ACTION_CONNECT_NAME = "connect";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RequiredValues processContext(final HttpServletRequest req,
			final HttpServletResponse res, final ServletContext servletContext,
			final WebContext ctx) throws Throwable {
		final Actor actor = findActor(req);
		if (actor == null) {
			req.authenticate(res);
		}
		final Integer rnId = getParameter(req, RemoteNodeType.ID.getKey(), Integer.class);
		RemoteNode rn = null;
		if (rnId != null) {
			rn = getRemoteNode(actor, rnId);
			if (rn != null) {
				ctx.setVariable(VAR_REMOTE_NODE_NAME, rn);
				addRemoteNodeReadingVars(rn, ctx);
				addRemoteNodeVars(rn, ctx);
			}
		}
		if (rn == null) {
			rn = new RemoteNode();
			rn.setAddress("");
			ctx.setVariable(VAR_REMOTE_NODE_NAME, rn);
		}
		ctx.setVariable(VAR_REMOTE_NODES_NAME, actor.getHost().getRemoteNodes());
		return null;
	}

	protected void addRemoteNodeReadingVars(final RemoteNode rn, final WebContext ctx) {
		final RemoteNodeReading rnr = getLastRemoteNodeReading(rn);
		final List<ValueType<RemoteNodeReading, Object>> cmds = new ArrayList<>();
		for (final RemoteNodeReadingType rnrt : RemoteNodeReadingType.values()) {
			try {
				cmds.add(rnrt.newValueType(rnr));
			} catch (final Throwable t) {
				log.error(
						String.format(
								"Unable to generate new value for %1$s at address %2$s for %3$s %4$s",
								rn.getClass(), rn.getAddress(),
								RemoteNodeReadingType.class.getSimpleName(),
								rnrt), t);
			}
		}
		// add the commands as a variable
		ctx.setVariable(RemoteNodeReadingType.class.getSimpleName(), cmds);
		ctx.setVariable(VAR_REMOTE_NODE_READING_NAME, getLastRemoteNodeReading(rn));
	}

	/**
	 * Calls {@link WebContext#setVariable(String, Object)} for each
	 * {@link RemoteNodeType.Type} name with a list of
	 * {@link RemoteNodeType.Value}(s) in that {@link RemoteNodeType.Type}.
	 * Also, adds the latest {@link RemoteNodeReading}.
	 * 
	 * @param rn
	 *            the {@link RemoteNode} to add values for
	 * @param ctx
	 *            the {@link WebContext} to add values to
	 */
	protected void addRemoteNodeVars(final RemoteNode rn, final WebContext ctx) {
		final Map<RemoteNodeType.Type, List<ValueType<RemoteNode, Object>>> vm = new HashMap<>();
		for (final RemoteNodeType.Type type : RemoteNodeType.Type.values()) {
			vm.put(type, new ArrayList<ValueType<RemoteNode, Object>>());
		}
//		rn.setConnected(ServiceProvider.IMPL.getWirelessService()
//				.testRemoteConnection(rn, 0));
		for (final RemoteNodeType rnt : RemoteNodeType.values()) {
			if (rnt.getType() != null) {
				try {
					vm.get(rnt.getType()).add(rnt.newValueType(rn));
				} catch (final Throwable t) {
					log.error(
							String.format(
									"Unable to generate new value for %1$s at address %2$s and group %3$s",
									rn.getClass(), rn.getAddress(), rnt
											.getType().name()), t);
				}
			}
		}
		// add the group of values as a variable
		for (final Map.Entry<RemoteNodeType.Type, List<ValueType<RemoteNode, Object>>> grp : vm.entrySet()) {
			ctx.setVariable(grp.getKey().name(), grp.getValue());
		}
	}

	/**
	 * Gets the last {@linkplain RemoteNodeReading}
	 * 
	 * @param rn
	 *            the {@linkplain RemoteNode}
	 * @return the {@linkplain RemoteNodeReading} or null when none exists
	 */
	private RemoteNodeReading getLastRemoteNodeReading(final RemoteNode rn) {
		final List<RemoteNodeReading> rnrs = ServiceProvider.IMPL
				.getRemoteNodeService().findReadingsById(rn, 0, 1);
		if (rnrs != null && !rnrs.isEmpty()) {
			return rnrs.get(0);
		}
		return null;
	}

	/**
	 * Gets the {@linkplain RemoteNode} for a given {@linkplain Actor} and
	 * {@linkplain RemoteNode#getId()}
	 * 
	 * @param actor
	 *            the {@linkplain Actor} to get the {@linkplain RemoteNode} from
	 * @param remoteNodeId
	 *            the {@linkplain RemoteNode#getId()} to find in the
	 *            {@linkplain Actor}
	 * @return the {@linkplain RemoteNode} or null when none can be found
	 */
	private RemoteNode getRemoteNode(final Actor actor, final int remoteNodeId) {
		if (remoteNodeId >= 0) {
			for (final RemoteNode rn : actor.getHost().getRemoteNodes()) {
				if (rn.getId() == remoteNodeId) {
					return rn;
				}
			}
		}
		return null;
	}
}

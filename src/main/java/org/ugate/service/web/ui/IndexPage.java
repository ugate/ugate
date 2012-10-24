package org.ugate.service.web.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.ugate.Command;
import org.ugate.service.ServiceProvider;
import org.ugate.service.entity.RemoteNodeType;
import org.ugate.service.entity.jpa.Actor;
import org.ugate.service.entity.jpa.RemoteNode;
import org.ugate.service.entity.jpa.RemoteNodeReading;

/**
 * Landing {@linkplain BasePage}
 */
public class IndexPage extends BasePage {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public IndexPage() {
		this(new PageParameters());
	}

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            the {@linkplain PageParameters}
	 */
	public IndexPage(final PageParameters parameters) {
		super(parameters);
		final Actor actor = findActor(parameters);
		add(new Label("deviceAddy", "Device Address"));
		final RemoteNode rn = getRemoteNode(actor, parameters.get("remoteNodeId").toString());
		final List<RemoteNode> rns = rn != null ? Arrays.asList(new RemoteNode[]{rn}) : 
			Arrays.asList(actor.getHost().getRemoteNodes().toArray(
					new RemoteNode[]{}));
        final Loop loop = new Loop("remoteNodesLI", rns.size()) {
			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(final LoopItem item) {
				// for each remote node add a link that will either take the
				// user to the details (when there is not remote node id IS
				// passed in the parameters) or take the user to the list of
				// available remote nodes to choose from (when the remote node
				// id is NOT passed in the parameters)
				final RemoteNode rni = rns.get(item.getIndex());
				final PageParameters pp = new PageParameters(rn == null ? parameters : null);
				if (rn == null) {
					pp.add("remoteNodeId", rni.getId());
				}
				final Link<Void> link = new Link<Void>("remoteNodeItem") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(IndexPage.class, pp);
					}
        		};
        		link.add(new Label("remoteNodeItemAddy", rni.getAddress()).setMarkupId("remoteNodeAddy" + item.getIndex()));
        		item.add(link);
				final boolean isConnected = ServiceProvider.IMPL.getWirelessService().testRemoteConnection(rni);
        		final Label cnt = new Label("remoteNodeConnect", isConnected ? "Connected" : "Connect");
        		if (!isConnected) {
        			cnt.add(new AttributeModifier("data-icon", "alert"));
        			cnt.add(new AttributeModifier("data-theme", "e"));
        		}
        		item.add(cnt);
            }
        };
        add(loop);
        if (rn != null) {
        	final Fragment frag = new Fragment("detail", "rnDetail", null);
        	addFormValues(parameters, frag, rn);
        	add(frag);
        } else {
        	add(new Fragment("detail", "rnNoDetail", null));
        }
	}

	private void addFormValues(final PageParameters parameters, final Fragment frag, final RemoteNode rn) {
		final RemoteNodeReading rnr = getLastRemoteNodeReading(rn);
		final Map<Integer, String> camRes = new HashMap<Integer, String>(2);
		camRes.put(0, "QVGA");
		camRes.put(1, "VGA");
		final Map<Integer, String> onOff = new HashMap<Integer, String>(2);
		onOff.put(0, "Off");
		onOff.put(1, "On");
		final Map<Integer, String> openClose = new HashMap<Integer, String>(2);
		openClose.put(0, "Close");
		openClose.put(1, "Open");
		final HiddenField<String> cmd = new HiddenField<>("command", new Model<String>());
		final StatelessForm<String> cmdForm = new StatelessForm<String>("rnCommandForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				super.onSubmit();
				final String cmdStr = cmd.getModelObject();
				try {
					final Command command = cmdStr != null ? Command.valueOf(cmdStr) : null;
					if (command != null) {
						ServiceProvider.IMPL.getWirelessService().sendData(rn,
								command, true);
					}
				} catch (final Throwable t) {
					internalError(new RuntimeException(String.format(
							"Unable to execute command %1$s, ERROR: %2$s ",
							cmdStr, t.getMessage())));
				}
			}
		};
		cmdForm.add(cmd);
		frag.add(cmdForm);
		frag.add(new Label("actionsCommands", "Actions/Commands"));
		addSelect(frag, Command.GATE_TOGGLE_OPEN_CLOSE, rnr != null ? rnr.getGateState() : 0, 
				"Gate State:", openClose);
		final StatelessForm<RemoteNode> sForm = new StatelessForm<RemoteNode>(
				"rnSettingsForm", new Model<RemoteNode>(rn)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				super.onSubmit();
				try {
					ServiceProvider.IMPL.getRemoteNodeService().merge(
							getModel().getObject());
				} catch (final Throwable t) {
					internalError(new RuntimeException(String.format(
							"Unable to save settings, ERROR: %1$s ",
							t.getMessage())));
				}
			}
		};
		sForm.add(new Label("generalSettings", "General Settings"));
		sForm.add(new Label("alarmState", "Alarm State"));
		addRange(sForm, RemoteNodeType.MULTI_ALARM_TRIP_STATE, rn, 0, 15, "Multi-Alarm Trip State:");
		addSelect(sForm, RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_ON, rn, "Universal Remote", onOff);
		addRange(sForm, RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_1, rn, 0, 9, "Universal Remote Key 1:");
		addRange(sForm, RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_2, rn, 0, 9, "Universal Remote Key 2:");
		addRange(sForm, RemoteNodeType.UNIVERSAL_REMOTE_ACCESS_CODE_3, rn, 0, 9, "Universal Remote Key 3:");
		sForm.add(new Label("notifications", "Notifications"));
		addSelect(sForm, RemoteNodeType.DEVICE_SOUNDS_ON, rn, "Host Sound Alarm:", onOff);
		addSelect(sForm, RemoteNodeType.MAIL_ALERT_ON, rn, "Email Notifications:", onOff);
		sForm.add(new Label("other", "Other"));
		addSelect(sForm, RemoteNodeType.CAM_RESOLUTION, rn, "Camera Resolution:", camRes);
		addSelect(sForm, RemoteNodeType.DEVICE_AUTO_SYNCHRONIZE, rn, "Auto Synchronize:", onOff);
		sForm.add(new Label("gate", "Gate"));
		addSelect(sForm, RemoteNodeType.GATE_ACCESS_ON, rn, "Gate Accessibility:", onOff);
		sForm.add(new Label("alarmThresholds", "Alarm Thresholds"));
		sForm.add(new Label("sonar", "Sonar"));
		addRange(sForm, RemoteNodeType.SONAR_DISTANCE_THRES_FEET, rn, 0, 10, "Sonar Distance Threshold (feet):");
		addRange(sForm, RemoteNodeType.SONAR_DISTANCE_THRES_INCHES, rn, 0, 11, "Sonar Distance Threshold (inches):");
		addRange(sForm, RemoteNodeType.SONAR_DELAY_BTWN_TRIPS, rn, 0, 60, "Sonar Delay Between Trips (minutes):");
		sForm.add(new Label("pir", "PIR"));
		addRange(sForm, RemoteNodeType.PIR_DELAY_BTWN_TRIPS, rn, 0, 60, "PIR Delay Between Trips (minutes):");
		sForm.add(new Label("microwave", "Micorwave"));
		addRange(sForm, RemoteNodeType.MW_SPEED_THRES_CYCLES_PER_SEC, rn, 2, 50, "Microwave Distance Threshold (cycles/second):");
		addRange(sForm, RemoteNodeType.MW_DELAY_BTWN_TRIPS, rn, 0, 60, "Micorwave Delay Between Trips (minutes):");
		sForm.add(new Label("laser", "Laser"));
		addRange(sForm, RemoteNodeType.LASER_DISTANCE_THRES_FEET, rn, 0, 32, "Laser Distance Threshold (feet):");
		addRange(sForm, RemoteNodeType.LASER_DISTANCE_THRES_INCHES, rn, 0, 11, "Laser Distance Threshold (inches):");
		addRange(sForm, RemoteNodeType.LASER_DELAY_BTWN_TRIPS, rn, 0, 60, "Laser Delay Between Trips (minutes):");
		sForm.add(new Label("positioning", "Positioning"));
		sForm.add(new Label("camera", "Camera"));
		addRange(sForm, RemoteNodeType.CAM_ANGLE_PAN, rn, 0, 181, "Camera Pan Angle:");
		addRange(sForm, RemoteNodeType.CAM_ANGLE_TILT, rn, 0, 181, "Camera Tilt Angle:");
		addRange(sForm, RemoteNodeType.CAM_SONAR_TRIP_ANGLE_PRIORITY, rn, 1, 4, "Sonar Alarm Camera Position Priority:");
		addRange(sForm, RemoteNodeType.CAM_SONAR_TRIP_ANGLE_PAN, rn, 0, 181, "Camera Pan Angle On Sonar Alarm:");
		addRange(sForm, RemoteNodeType.CAM_SONAR_TRIP_ANGLE_TILT, rn, 0, 181, "Camera Tilt Angle On Sonar Alarm:");
		addRange(sForm, RemoteNodeType.CAM_PIR_TRIP_ANGLE_PRIORITY, rn, 1, 4, "PIR Alarm Camera Position Priority:");
		addRange(sForm, RemoteNodeType.CAM_PIR_TRIP_ANGLE_PAN, rn, 0, 181, "Camera Pan Angle On PIR Alarm:");
		addRange(sForm, RemoteNodeType.CAM_PIR_TRIP_ANGLE_TILT, rn, 0, 181, "Camera Tilt Angle On PIR Alarm:");
		addRange(sForm, RemoteNodeType.CAM_MW_TRIP_ANGLE_PRIORITY, rn, 1, 4, "Microwave Alarm Camera Position Priority:");
		addRange(sForm, RemoteNodeType.CAM_MW_TRIP_ANGLE_PAN, rn, 0, 181, "Camera Pan Angle On Microwave Alarm:");
		addRange(sForm, RemoteNodeType.CAM_MW_TRIP_ANGLE_TILT, rn, 0, 181, "Camera Tilt Angle On Microwave Alarm:");
		addRange(sForm, RemoteNodeType.CAM_LASER_TRIP_ANGLE_PRIORITY, rn, 1, 4, "Laser Alarm Camera Position Priority:");
		addRange(sForm, RemoteNodeType.CAM_LASER_TRIP_ANGLE_PAN, rn, 0, 181, "Camera Pan Angle On Laser Alarm:");
		addRange(sForm, RemoteNodeType.CAM_LASER_TRIP_ANGLE_TILT, rn, 0, 181, "Camera Tilt Angle On Laser Alarm:");
		sForm.add(new Label("sonarPir", "Sonar/PIR"));
		addRange(sForm, RemoteNodeType.SONAR_PIR_ANGLE_PAN, rn, 0, 180, "Sonar/PIR Pan Angle:");
		addRange(sForm, RemoteNodeType.SONAR_PIR_ANGLE_TILT, rn, 0, 180, "Sonar/PIR Tilt Angle:");
		sForm.add(new Label("microwave2", "Micorwave"));
		addRange(sForm, RemoteNodeType.MW_ANGLE_PAN, rn, 0, 181, "Microwave Pan Angle:");
		frag.add(sForm);
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
	private RemoteNode getRemoteNode(final Actor actor, final String remoteNodeId) {
		if (remoteNodeId != null) {
			for (final RemoteNode rn : actor.getHost().getRemoteNodes()) {
				if (String.valueOf(rn.getId()).equals(remoteNodeId)) {
					return rn;
				}
			}
		}
		return null;
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
		return "Remote Node";
	}
}

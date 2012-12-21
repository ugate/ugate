package org.ugate.test;
import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.Test;

/**
 * Runs an Ant build as a {@link Test}
 */
public class AntBuild {

	@Test
	public void antRun() {
		final String buildFileStr = System.getProperty("buildFile");
		System.out.println("Executing Ant Build for buildFile=" + buildFileStr
				+ ", user.dir=" + System.getProperty("user.dir"));

		// add console logger so ant echo will show up in test
		final DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);

		// execute ant build
		final File buildFile = new File(buildFileStr);
		final Project p = new Project();
		p.addBuildListener(consoleLogger);
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		final ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget(p.getDefaultTarget());
	}
}

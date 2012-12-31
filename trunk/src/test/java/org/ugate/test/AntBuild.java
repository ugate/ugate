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
	
//	public static void main(final String[] args) {
//		System.setProperty("buildFile", "C:\\eclipse-juno-workspace\\ugate\\src\\test\\resources\\test.xml");
//		System.setProperty("buildTargets", "check-bitness");
//		new AntBuild().antRun();
//	}

	@Test
	public void antRun() {
		final String buildFileStr = System.getProperty("buildFile");
		final String buildTargetStr = System.getProperty("buildTargets");
		System.out.println("Executing Ant Build for buildFile=" + buildFileStr
				+ ", buildTargets=" + buildTargetStr + ", user.dir="
				+ System.getProperty("user.dir"));

		// add console logger so ant echo will show up in test
		final DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);

		// execute ant build
		final File buildFile = new File(buildFileStr);
		final Project p = new Project();
		//p.setCoreLoader(getClass().getClassLoader());
		p.addBuildListener(consoleLogger);
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		final ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		final String[] buildTargets = buildTargetStr == null
				|| buildTargetStr.isEmpty() ? new String[] { p
				.getDefaultTarget() } : buildTargetStr.split(",");
		for (final String t : buildTargets) {
			if (!t.trim().isEmpty()) {
				System.out.println();
				System.out.println("Executing Target: " + t);
				p.executeTarget(t.trim());
			}
		}
	}
}

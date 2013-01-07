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

	/**
	 * The Ant build file name that will be
	 * {@link Project#executeTarget(String)} (in order)
	 */
	public static final String BUILD_FILE_NAME = "buildFile";

	/**
	 * The Ant target(s) to run in the {@link #BUILD_FILE_NAME}
	 */
	public static final String BUILD_TARGETS = "buildTargets";

	/**
	 * The Ant build class path
	 */
	public static final String BUILD_CLASSPATH = "buildClassPath";

	/**
	 * True for verbose logging while building
	 */
	public static final String BUILD_VERBOSE = "buildVerbose";

	/**
	 * Runs
	 */
	@Test
	public void antRun() {
		final String buildFileStr = System.getProperty(BUILD_FILE_NAME);
		final String buildTargetStr = System.getProperty(BUILD_TARGETS);
		String buildClasspathStr = System.getProperty(BUILD_CLASSPATH);
		String buildVerboseStr = System.getProperty(BUILD_VERBOSE);
		if (buildClasspathStr == null) {
			buildClasspathStr = ".";
		}
		if (buildVerboseStr == null) {
			buildVerboseStr = String.valueOf(false);
		}
		System.out
				.println(String
						.format("Executing Ant Build for %1$s=%2$s, %3$s=%4$s, %5$s=%6$s, %7$s=%8$s, user.dir=%7$s",
								BUILD_FILE_NAME, buildFileStr, BUILD_TARGETS,
								buildTargetStr, BUILD_CLASSPATH,
								buildClasspathStr, BUILD_VERBOSE,
								buildVerboseStr, System.getProperty("user.dir")));

		// add console logger so ant echo will show up in test
		final DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);

		// execute ant build
		final File buildFile = new File(buildFileStr);
		final Project p = new Project();
		// p.setCoreLoader(getClass().getClassLoader());
		p.addBuildListener(consoleLogger);
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.setProperty(BUILD_CLASSPATH, buildClasspathStr);
		p.setProperty(BUILD_VERBOSE, buildVerboseStr);
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
				if (Boolean.parseBoolean(buildVerboseStr)) {
					System.out.println("Executing Target: " + t);
				}
				p.executeTarget(t.trim());
			}
		}
	}
}

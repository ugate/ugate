package org.ugate.resources;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.RemoteSettings;
import org.ugate.UGateUtil;

public class RS {

	private static final Logger log = LoggerFactory.getLogger(RS.class);
	public static final String PACKAGE_CHECK_EXTENSION = ".jar";
	public static final String RXTX_JAR_FILE_NAME = "RXTXcomm.jar";
	public static final String RXTX_DLL_PARALLEL_FILE_NAME = "rxtxParallel.dll";
	public static final String RXTX_DLL_SERIAL_FILE_NAME = "rxtxSerial.dll";
	public static final String RXTX_MAC_SERIAL_FILE_NAME = "librxtxSerial.jnilib";
	public static final String RXTX_LINUX_PARALLEL_FILE_NAME = "librxtxParallel.so";
	public static final String RXTX_LINUX_SERIAL_FILE_NAME = "librxtxSerial.so";
	public static final String WIRELESS_PREFERENCE_FILE_EXTENSION = ".properties";
	public static final String WIRELESS_HOST_SETTINGS_FILE = "host";
	private static final String WIRELESS_PREFERENCE_FILE_PREFIX = "remote-node-";
	public static final String WIRELESS_PREFERENCE_HISTORY_FILE = "history";
	public static final String HTML_SERVO_NAV = "nav.html";
	public static final String CSS_MAIN = "main.css";
	public static final String CSS_DISPLAY_SHELF = "displayshelf.css";
	public static final String IMG_LOGO_128 = "logo128x128.png";
	public static final String IMG_LOGO_64 = "logo64x64.png";
	public static final String IMG_LOGO_16 = "logo16x16.png";
	public static final String IMG_SKIN_MIN = "ugskin-btn-min32x10.png";
	public static final String IMG_SKIN_MAX = "ugskin-btn-max25x10.png";
	public static final String IMG_SKIN_RESTORE = "ugskin-btn-res25x10.png";
	public static final String IMG_SKIN_CLOSE = "ugskin-btn-close32x10.png";
	public static final String IMG_HELP = "help.png";
	public static final String IMG_CONNECT = "power-red.png";
	public static final String IMG_PICS = "pics.png";
	public static final String IMG_WIRELESS = "wireless-globe.png";
	public static final String IMG_WIRELESS_ICON = "wireless-icon.png";
	public static final String IMG_GRAPH = "graph.png";
	public static final String IMG_LOGS = "logs.png";
	public static final String IMG_CAM_VGA = "cam-vga.png";
	public static final String IMG_CAM_QVGA = "cam-qvga.png";
	public static final String IMG_CAM_TOGGLE_VGA = "cam-toggle-vga.png";
	public static final String IMG_CAM_TOGGLE_QVGA = "cam-toggle-qvga.png";
	public static final String IMG_SENSOR_ARM = "camera.png";
	public static final String IMG_EMAIL_NOTIFY_ON = "email-notify-on.png";
	public static final String IMG_EMAIL_NOTIFY_OFF = "email-notify-off.png";
	public static final String IMG_EMAIL_ICON = "email-icon.png";
	public static final String IMG_GATE_ON = "gate-on.png";
	public static final String IMG_GATE_OFF = "gate-off.png";
	public static final String IMG_GATE_CLOSED = "gate-closed.png";
	public static final String IMG_GATE_OPENED = "gate-opened.png";
	public static final String IMG_RULER = "ruler.png";
	public static final String IMG_STOPWATCH = "stopwatch.png";
	public static final String IMG_SONAR_ALARM_MULTI = "sonar-alarm-multi.png";
	public static final String IMG_SONAR_ALARM_ANY = "sonar-alarm-any.png";
	public static final String IMG_SONAR_ALARM_OFF = "sonar-alarm-off.png";
	public static final String IMG_PIR_ALARM_MULTI = "pir-alarm-multi.png";
	public static final String IMG_PIR_ALARM_ANY = "pir-alarm-any.png";
	public static final String IMG_PIR_ALARM_OFF = "pir-alarm-off.png";
	public static final String IMG_MICROWAVE_ALARM_MULTI = "mw-alarm-multi.png";
	public static final String IMG_MICROWAVE_ALARM_ANY = "mw-alarm-any.png";
	public static final String IMG_MICROWAVE_ALARM_OFF = "mw-alarm-off.png";
	public static final String IMG_LASER_ALARM_MULTI = "laser-alarm-multi.png";
	public static final String IMG_LASER_ALARM_ANY = "laser-alarm-any.png";
	public static final String IMG_LASER_ALARM_OFF = "laser-alarm-off.png";
	public static final String IMG_LASER_CALIBRATE = "laser-calibrate.png";
	public static final String IMG_SPEEDOMETER = "speedometer.png";
	public static final String IMG_PAN = "pan.png";
	public static final String IMG_READINGS_GET = "readings-get48x48.png";
	public static final String IMG_SETTINGS_SET = "settings-set48x48.png";
	public static final String IMG_SETTINGS_GET = "settings-get48x48.png";
	public static final String IMG_SONAR = "sonar32x32.png";
	public static final String IMG_PIR = "pir32x32.png";
	public static final String IMG_LASER = "laser32x32.png";
	public static final String IMG_MICROWAVE = "mw32x32.png";
	public static final String IMG_SOUND_ON = "sound-on.png";
	public static final String IMG_SOUND_OFF = "sound-off.png";
	public static final String IMG_UNIVERSAL_REMOTE_ON = "universal-remote-on.png";
	public static final String IMG_UNIVERSAL_REMOTE_OFF = "universal-remote-off.png";
	public static final String IMG_CORNER_RESIZE = "ugskin-resize11x11.png";
	private static final String RB_GUI = "LabelsBundle";
	public static final AudioClip mediaPlayerConfirm = RS.audioClip("x_confirm.wav");
	public static final AudioClip mediaPlayerDoorBell = RS.audioClip("x_doorbell.wav");
	public static final AudioClip mediaPlayerCam = RS.audioClip("x_cam.wav");
	public static final AudioClip mediaPlayerComplete = RS.audioClip("x_complete.wav");
	public static final AudioClip mediaPlayerError = RS.audioClip("x_error.wav");
	public static final AudioClip mediaPlayerBlip = RS.audioClip("x_blip.wav");
	private static final Map<String, Image> IMGS = new HashMap<String, Image>();
	
	private RS() {
	}
	
	/**
	 * Creates an image view from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @return the image view
	 */
	public static ImageView imgView(final String fileName) {
		return imgView(img(fileName));
	}
	
	/**
	 * Creates an image view from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @param key the preferences key to associate the image with
	 * @return the image view
	 */
	public static ImageView imgView(final String fileName, final String key) {
		return imgView(img(fileName), key);
	}
	
	/**
	 * Creates an image view
	 * 
	 * @param image the image
	 * @return the image view
	 */
	public static ImageView imgView(final Image image) {
		return imgView(image, null);
	}
	
	/**
	 * Creates an image view
	 * 
	 * @param image the image
	 * @param key the preferences key to associate the image with
	 * @return the image view
	 */
	public static ImageView imgView(final Image image, final String key) {
		final ImageView node = new ImageView(image);
		node.setSmooth(false);
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		return node;
	}
	
	/**
	 * Gets an image from a set of cached images
	 * 
	 * @param fileName the image file name
	 * @return the image
	 */
	public static Image img(final String fileName) {
		if (IMGS.containsKey(fileName)) {
			return IMGS.get(fileName);
		} else {
			final Image img = new Image(path(fileName));
			IMGS.put(fileName, img);
			return img;
		}
	}
	
	/**
	 * Gets a resource stream
	 * 
	 * @param fileName the resource file name
	 * @return the resource stream
	 */
	public static InputStream stream(final String fileName) {
		return RS.class.getResourceAsStream(fileName);
	}
	
	/**
	 * Gets a resource path
	 * 
	 * @param fileName the resource file name
	 * @return the resource path
	 */
	public static String path(final String fileName) {
		return RS.class.getResource(fileName).toExternalForm();
	}
	
	/**
	 * Creates an audio clip based on the file path
	 * 
	 * @param fileName
	 *            the file path with file name
	 * @return the audio clip
	 */
	public static AudioClip audioClip(final String fileName) {
		try {
			return new AudioClip(RS.class.getResource(fileName).toExternalForm());
//			return new AudioClip(RS.class.getResource(fileName).getPath()
//					.replace("/C", "file"));
		} catch (final Throwable t) {
			log.error("Unable to get audio clip for resource named: " + fileName);
		}
		return null;
	}

	/**
	 * Gets the image format name from a file
	 * 
	 * @param f
	 *            the file
	 * @return null if the format is not a known image format
	 */
	public static String imgFormatInFile(final File f) {
		return imgFormatName(f);
	}

	/**
	 * Gets the image format name from an input stream
	 * 
	 * @param is
	 *            the input stream
	 * @return null if the format is not a known image format
	 */
	public static String imgFormatFromStream(final InputStream is) {
		return imgFormatName(is);
	}

	/**
	 * Returns the format name of the image in the object
	 * 
	 * @param o
	 *            can be either a File or InputStream object
	 * @return null if the format is not a known image format
	 */
	private static String imgFormatName(final Object o) {
		try {
			// Create an image input stream on the image
			final ImageInputStream iis = ImageIO.createImageInputStream(o);

			// Find all image readers that recognize the image format
			final Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				// No readers found
				return null;
			}

			// Use the first reader
			final ImageReader reader = iter.next();

			// Close stream
			iis.close();

			// Return the format name
			return reader.getFormatName();
		} catch (final Exception e) {
		}
		// The image could not be read
		return null;
	}
	
	/**
	 * <p>Ensures that the needed RXTX is installed. If RXTX cannot be detected an 
	 * attempt will be made to install it.</p>
	 * <p><code>Ubuntu {@linkplain https://launchpad.net/ubuntu/+search?text=rxtx}, 
	 * Debian {@linkplain http://packages.debian.org/lenny/librxtx-java}, and 
	 * Fedora {@linkplain https://admin.fedoraproject.org/pkgdb/acls/name/rxtx?_csrf_token=f10460d886559c3a4e824f14b13302dff6cb1511}
	 * </code> all should have RXTX already installed 
	 * from the distributions</p>
	 * 
	 * @return true when RXTX was installed and the application needs to be restarted
	 */
	public static boolean initComm() {
		ManagementFactory.getRuntimeMXBean().getName();
		try {
			getSerialPorts();
			return false;
		} catch (final NoClassDefFoundError e) {
			log.info(String.format("RXTX not installed... attempting to install (from %1$s)", 
					e.getClass().getName()));
			// ClassNotFoundException
			installComm();
			return true;
		} catch (final UnsatisfiedLinkError e) {
			log.info(String.format("RXTX not installed... attempting to install (from %1$s)", 
					e.getClass().getName()));
			installComm();
			return true;
		}
	}
	
	/**
	 * Installs RXTX to the working JVM
	 */
	private static void installComm() {
		Path tempZip = null;
		FileSystem fs = null;
		FileSystem zipFs = null;
		try {
			if (!UGateUtil.isWindows() && !UGateUtil.isMac() && !UGateUtil.isLinux() && !UGateUtil.isSolaris()) {
				throw new UnsupportedOperationException(
						String.format("RXTX Install: %1$s is not a supported operating system", 
						UGateUtil.os()));
			}
			final String rxtxVersion = rbLabel("rxtx.version");
			final String rxtxFileName = rbLabel("rxtx.file.name");
			final String jvmPath = System.getProperties().getProperty("java.home");
			log.info(String.format("RXTX Install: Installing version %1$s from %2$s for JVM %3$s", 
					rxtxVersion, rxtxFileName, jvmPath));
			
			// copy the ZIP to a temporary file and create a ZIP file system to install RXTX
			final Path appPath = new File(applicationUri()).toPath();
			Path rxtxFilePath;
			if (Files.isDirectory(appPath)) {
				fs = FileSystems.getDefault();
				rxtxFilePath = fs.getPath(appPath.toAbsolutePath().toString(), rxtxFileName);
			} else {
				fs = FileSystems.newFileSystem(appPath, null);;
				rxtxFilePath = fs.getPath(rxtxFileName);
			}
			tempZip = Files.createTempFile(null, ".zip");
			Files.copy(rxtxFilePath, tempZip, StandardCopyOption.REPLACE_EXISTING);
			zipFs = FileSystems.newFileSystem(tempZip.toAbsolutePath(), null);
			final Path rxtxPath = zipFs.getPath("/");

			// TODO : verify Mac x64/Linux x64 works using embedded files... see http://blog.iharder.net/2009/08/18/rxtx-java-6-and-librxtxserial-jnilib-on-intel-mac-os-x/
			final String winArch = "Windows" + (System.getProperties().getProperty("os.arch") != "x86" ? "-x64" : "");
			final String linuxArch = "Linux" + (System.getProperties().getProperty("os.arch") != "x86" ? "-x64" : "");
			final String macArch = "Mac_OS_X" + (System.getProperties().getProperty("os.arch") != "x86" ? "-x64" : "");

			// install files
			Files.walkFileTree(rxtxPath, new java.nio.file.SimpleFileVisitor<Path>() {
				/** {@inheritDoc} */
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
					try {
						if ((!UGateUtil.isWindows() && dir.toAbsolutePath().toString().contains(winArch)) || 
								(!UGateUtil.isMac() && dir.toAbsolutePath().toString().contains(macArch)) || 
								(!UGateUtil.isLinux() && dir.toAbsolutePath().toString().contains(linuxArch)) || 
								(!UGateUtil.isSolaris() && dir.toAbsolutePath().toString().contains("Solaris"))) {
							return FileVisitResult.SKIP_SUBTREE;
						}
					} catch (final Throwable t) {
						log.error(String.format("RXTX Install: Error filtering RXTX directory %1$s", dir), t);
						return FileVisitResult.TERMINATE;
					}
					log.debug(String.format("RXTX Install: Searching directory: %1$s", dir));
					return FileVisitResult.CONTINUE;
				}

				/** {@inheritDoc} */
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
					Path newPath = null;
					try {
						if (UGateUtil.isWindows()) {
							if (file.getFileName().endsWith(RXTX_JAR_FILE_NAME)) {
								// copy JAR to install path
								newPath = Paths.get(jvmPath, "lib", "ext", file.getFileName().toString());
							} else if (file.getFileName().endsWith(RXTX_DLL_PARALLEL_FILE_NAME) 
									|| file.getFileName().endsWith(RXTX_DLL_SERIAL_FILE_NAME)) {
								// copy DLLs to the DLL directory
								newPath = Paths.get(jvmPath, "bin", file.getFileName().toString());
							}
						} else if (UGateUtil.isMac()) {
							if (file.getFileName().endsWith(RXTX_JAR_FILE_NAME) 
									|| file.getFileName().endsWith(RXTX_MAC_SERIAL_FILE_NAME)) {
								// copy JAR/JNI LIB to java extensions
								newPath = Paths.get("Library", "Java", "Extensions", file.getFileName().toString());
							}
						} else if (UGateUtil.isLinux()) {
							if (file.getFileName().endsWith(RXTX_JAR_FILE_NAME)) {
								// copy JAR to install path
								newPath = Paths.get(jvmPath, "lib", "ext", file.getFileName().toString());
							} else if ((file.getFileName().endsWith(RXTX_LINUX_PARALLEL_FILE_NAME) || 
									file.getFileName().endsWith(RXTX_LINUX_SERIAL_FILE_NAME)) && 
									UGateUtil.getBitness() == 32 && file.toAbsolutePath().toString().contains("i686")) {
								newPath = Paths.get(jvmPath, "lib", "i386", file.getFileName().toString());
							} else if (file.getFileName().endsWith(RXTX_LINUX_SERIAL_FILE_NAME) && 
									UGateUtil.getBitness() == 64 && file.toAbsolutePath().toString().contains("ia64")) {
								// TODO : should the 64 bit RXTX be in a different path other than i386 for Linux?
								newPath = Paths.get(jvmPath, "lib", "i386", file.getFileName().toString());
							}
						}
						if (newPath != null) {
							log.info(String.format("RXTX Install: Attempting to copy %1$s to %2$s", file, newPath));
							Files.copy(file, newPath, StandardCopyOption.REPLACE_EXISTING);
						}
					} catch (final Throwable t) {
						log.error(String.format("RXTX Install: Error installing the required file %1$s to %2$s", file, newPath), t);
						return FileVisitResult.TERMINATE;
					}
					return FileVisitResult.CONTINUE;
				}
			});
			Files.deleteIfExists(tempZip);
			//restartApplication();
		} catch (final Throwable t) {
			throw new IllegalStateException("RXTX Install: Unable to install the required files needed for " + 
					"Serial/Parallel communications! To install manually goto " + 
					"http://rxtx.qbang.org/wiki/index.php/Installation", t);
		} finally {
			closeFileSystem(fs);
			closeFileSystem(zipFs);
		}
	}

	/**
	 * Restarts the application
	 */
	protected static void restartApplication(final String... args) {
		// Sun property pointing the main class and its arguments. 
		// Might not be defined on non Hotspot VM implementations.
		final String mcmd = System.getProperty("sun.java.command");
		final StringBuilder cmd = new StringBuilder();
		final String[] mainCommand = mcmd != null && !mcmd.isEmpty() ? mcmd.split(" ") : new String[] { rbLabel("main.class") };
		log.info(String.format("Restarting application at entry point: %1$s", mainCommand[0]));
		cmd.append('"');
		cmd.append(Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString());
		cmd.append("\" ");
		for (final String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			if (!jvmArg.contains("-agentlib")) {
				cmd.append(jvmArg);
				cmd.append(' ');
			}
		}
		if (mainCommand[0].endsWith(".jar")) {
			// if it's a jar, add -jar mainJar
			cmd.append("-jar ");
			cmd.append(new File(mainCommand[0]).getPath());
		} else {
			// else it's a .class, add the class path and mainClass
			cmd.append("-cp \"").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append("\" ");
			cmd.append(mainCommand[0]);
		}
		// finally add program arguments
		for (int i = 1; i < mainCommand.length; i++) {
			cmd.append(' ');
			cmd.append(mainCommand[i]);
		}
		// execute the command in a shutdown hook, to be sure that all the
		// resources have been disposed before restarting the application
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//			}
//		});
		try {
			//final ProcessBuilder processBuilder
			Runtime.getRuntime().exec(cmd.toString());
		} catch (final IOException e) {
			log.error(String.format("Unable to restart the application at entry point: %1$s", cmd.toString()), e);
		}
		Platform.exit();
		//System.exit(0);
	}
	
	/**
	 * Attempts to close a {@linkplain FileSystem}
	 * 
	 * @param fs the {@linkplain FileSystem}
	 */
	private static void closeFileSystem(final FileSystem fs) {
		if (fs != null && fs != FileSystems.getDefault()) {
			try {
				fs.close();
			} catch (final Throwable t) {
			}
		}
	}
	
	/**
	 * This method is used to get a list of all the available Serial ports (note: only Serial ports are considered). 
	 * Any one of the elements contained in the returned {@link List} can be used as a parameter in 
	 * {@link #connect(String)} or {@link #connect(String, int)} to open a Serial connection.
	 * 
	 * @return A {@link List} containing {@link String}s showing all available Serial ports.
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getSerialPorts() {
		log.debug("Loading available COM ports");
		Enumeration<CommPortIdentifier> portList;
		List<String> ports = new ArrayList<String>();
		portList = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId;
		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				ports.add(portId.getName());
			}
		}
		if (log.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder("Found the following ports: ");
			for (int i = 0; i < ports.size(); i++) {
				sb.append(ports.get(i));
				sb.append(' ');
			}
			log.error(sb.toString());
		}
		return ports;
	}
	
	/**
	 * @return the {@linkplain URI} that the application is running in
	 */
	public static URI applicationUri() {
		try {
			final URI appUri = RS.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			log.debug("Application URI: " + appUri);
			return appUri;
		} catch (final Throwable e) {
			log.error("Unable to get application URI", e);
		}
		return null;
	}
	
	/**
	 * @return the {@linkplain FileSystems#getDefault()} when running within a directory or a 
	 * 		new {@linkplain FileSystems#newFileSystem(Path, ClassLoader)} when running within an archive
	 */
	public static FileSystem applicationFileSystem() {
		Path appPath = new File(applicationUri()).toPath();
		try {
			return Files.isDirectory(appPath) ? FileSystems.getDefault() : FileSystems.newFileSystem(appPath, null);
		} catch (final IOException e) {
			throw new RuntimeException(String.format("An %1$s occurred while trying to create a new %2$s", 
					e.getClass().getName(), FileSystem.class.getName()), e);
		}
	}
	
	/**
	 * @param validate true to validate that the resource exists
	 * @param resourceName the name of the resource to get a {@linkplain Path} to
	 * @param closeFileSystem true to close the {@linkplain FileSystem}
	 * @return the {@linkplain Path} to the resource
	 */
	private static Path resourcePath(final boolean validate, final String resourceName, final boolean closeFileSystem) {
		try {
			final Path resourcePath = Paths.get(RS.class.getResource(resourceName).toURI());
			log.debug("Extracting application resource path: " + resourcePath);
			return resourcePath;
		} catch (final Throwable e) {
			if (e instanceof FileSystemNotFoundException) {
				// must be running from within a JAR- get the resource from within the archive
				FileSystem fs = null;
				try {
					final Path appPath = new File(applicationUri()).toPath();
					fs = FileSystems.newFileSystem(appPath, null);
					final Path resourcePath = fs.getPath(RS.class.getPackage().getName().replace(".", fs.getSeparator()), resourceName);
					if (resourcePath != null && validate && Files.notExists(resourcePath)) {
						return null;
					}
					log.debug(String.format("Extracting application resource from ZIP path %1$s in %2$s", resourcePath.toAbsolutePath(), 
							appPath.toAbsolutePath()));
					return resourcePath;
				} catch (final Throwable e2) {
					log.error("Unable to get application path (ZIP source)", e2);
				} finally {
					if (closeFileSystem) {
						try {
							fs.close();
						} catch (final Exception e2) {
						}
					}
				}
			} else {
				log.error("Unable to get application path", e);
			}
		}
		return null;
	}
	
	/**
	 * Gets the working directory {@linkplain Path}. When a valid node index is specified a directory is created within 
	 * the applications execution directory. When a valid file name is passed it will be appended to the path. When the
	 * node index and file name are null the path returned will be the path where the application is running.
	 * 
	 * @param subDirectory the sub directory to append to the working directory
	 * @param fileName the name of the file (optional)
	 * @return the {@linkplain Path} to the remote nodes working directory (or the application path when 
	 * 		the file name and node index are null)
	 */
	public static Path workingDirectoryPath(final Path subDirectory, final String fileName) {
		Path workingPath = null;
		try {
			Path appPath = new File(applicationUri()).toPath();
			if (Files.isDirectory(appPath)) {
				workingPath = Files.createDirectories(appPath.toAbsolutePath());
			} else {
				// must be running from within a JAR- get the working directory excluding the file name of the archive
				workingPath = appPath.getParent();
				workingPath = workingPath == null ? appPath.subpath(0, 1).toAbsolutePath() : 
					workingPath.toAbsolutePath();
				log.debug(String.format("Extracted working directory %1$s from application path %2$s", workingPath, appPath));
				appPath = workingPath;
			}
			if (subDirectory != null) {
				workingPath = workingPath.resolve(subDirectory);
				log.debug(String.format("Creating working directory %1$s in %2$s", workingPath, appPath));
				workingPath = Files.createDirectories(workingPath);
			}
			if (fileName != null && !fileName.isEmpty()) {
				workingPath = workingPath.resolve(fileName);
			}
			return workingPath;
		} catch (final Throwable t) {
			throw new RuntimeException(String.format(
					"Unable to initialize the working directory path. " + 
					"%1$s (with optional sub directory: %2$s) must be an accessible/writable directory (optional file name: %3$s)", 
					workingPath, subDirectory, fileName), t);
		}
	}
	
	/**
	 * @return the {@linkplain RS#WIRELESS_HOST_SETTINGS_FILE} {@linkplain Path}
	 */
	public static Path hostDefaultPropertiesPath() {
		return resourcePath(true, hostPropertiesFileName(), true);
	}
	
	/**
	 * @return the {@linkplain Path} to the {@linkplain RS#WIRELESS_HOST_SETTINGS_FILE}
	 */
	public static Path hostPropertiesFilePath() {
		return workingDirectoryPath(null, hostPropertiesFileName());
	}
	
	/**
	 * @return the file name of the {@linkplain RS#WIRELESS_HOST_SETTINGS_FILE}
	 */
	public static String hostPropertiesFileName() {
		return WIRELESS_HOST_SETTINGS_FILE + WIRELESS_PREFERENCE_FILE_EXTENSION;
	}
	
	/**
	 * @param nodeIndex the index of the remote node
	 * @return the file name of the remote node
	 */
	public static String remotePropertiesFileName(final int nodeIndex) {
		return WIRELESS_PREFERENCE_FILE_PREFIX + nodeIndex + WIRELESS_PREFERENCE_FILE_EXTENSION;
	}
	
	/**
	 * @return the {@linkplain Path} to the default remote node (index 
	 * 		{@linkplain RemoteSettings.WIRELESS_ADDRESS_START_INDEX})
	 */
	public static Path remoteDefaultPropertiesPath() {
		return resourcePath(true, remotePropertiesFileName(RemoteSettings.WIRELESS_ADDRESS_START_INDEX), true);
	}
	
	/**
	 * @param nodeIndex the index of the remote node
	 * @return the {@linkplain Path} to the remote node
	 */
	public static Path remotePropertiesFilePath(final int nodeIndex) {
		return workingDirectoryPath(null, remotePropertiesFileName(nodeIndex));
	}
	
	/**
	 * Gets the first available locale and the label resource bundles 
	 * value for the specified key
	 * 
	 * @param key the key of the resource bundle value
	 * @param formatArguments the {@linkplain String#format(Locale, String, Object...)} arguments
	 * @return the resource bundle value
	 */
	public static String rbLabel(final String key, final Object... formatArguments) {
		return rbLabel(Locale.getAvailableLocales()[0], key, formatArguments);
	}
	
	/**
	 * Gets the a label resource bundles value for the specified key
	 *  
	 * @param locale the locale of the resource bundle
	 * @param key the key of the resource bundle value
	 * @param formatArguments the {@linkplain String#format(Locale, String, Object...)} arguments
	 * @return the resource bundle value
	 */
	public static String rbLabel(final Locale locale, final String key, final Object... formatArguments) {
		return rbValue(RB_GUI, locale, key, formatArguments);
	}
	
	/**
	 * Gets the a resource bundles value for the specified key
	 *  
	 * @param rb the resource bundle name
	 * @param locale the locale of the resource bundle
	 * @param key the key of the resource bundle value
	 * @param formatArguments the {@linkplain String#format(Locale, String, Object...)} arguments
	 * @return the resource bundle value
	 */
	private static String rbValue(final String rb, final Locale locale, final String key, final Object... formatArguments) {
		final String rbStr = ResourceBundle.getBundle(rb, locale).getString(key);
		return formatArguments != null && formatArguments.length > 0 ? String.format(locale, rbStr, formatArguments) : rbStr;
	}
}

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
	public static final String IMG_SYNC_ON = "sync-on.png";
	public static final String IMG_SYNC_OFF = "sync-off.png";
	public static final String IMG_UNIVERSAL_REMOTE_ON = "universal-remote-on.png";
	public static final String IMG_UNIVERSAL_REMOTE_OFF = "universal-remote-off.png";
	public static final String IMG_CORNER_RESIZE = "ugskin-resize11x11.png";
	public static final String IMG_DB_SAVE = "db-save.png";
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
			final String rxtxVersion = rbLabel(KEYS.RXTX_VERSION);
			final String rxtxFileName = rbLabel(KEYS.RXTX_FILE_NAME);
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
			final boolean is64 = !System.getProperties().getProperty("os.arch").equalsIgnoreCase("x86");
			final String winArch32 = "Windows" + (is64 ? "-x64" : "");
			final String linuxArch32 = "Linux" + (is64 ? "-x64" : "");
			final String macArch32 = "Mac_OS_X" + (is64 ? "-x64" : "");
			final String winArch64 = "Windows-x64";
			final String linuxArch64 = "Linux-x64";
			final String macArch64 = "Mac_OS_X-x64";

			// install files
			Files.walkFileTree(rxtxPath, new java.nio.file.SimpleFileVisitor<Path>() {
				/** {@inheritDoc} */
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
					try {
						if (skipCommPath(dir, winArch32, winArch64, UGateUtil.isWindows()) || 
								skipCommPath(dir, macArch32, macArch64, UGateUtil.isMac()) || 
								skipCommPath(dir, linuxArch32, linuxArch64, UGateUtil.isLinux()) || 
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
	 * Determines if a {@linkplain Path} should be skipped for
	 * {@linkplain #installComm()}
	 * 
	 * @param dir
	 *            the {@linkplain Path}
	 * @param osArch32
	 *            the OS 32-bit architecture folder name
	 * @param osArch64
	 *            the OS 64-bit architecture folder name
	 * @param osSame
	 *            true when the OS being checked is the same as the OS
	 *            architecture folder names
	 * @return true to skip
	 */
	protected static boolean skipCommPath(final Path dir, final String osArch32, final String osArch64, final boolean osSame) {
		final boolean is64 = !System.getProperties().getProperty("os.arch").equalsIgnoreCase("x86");
		final String absp = dir.toAbsolutePath().toString();
		return ((!osSame && absp.contains(osArch32)) || 
				((osSame && !is64 && absp.contains(osArch64)) || 
						(osSame && is64 && absp.contains(osArch32 + '/'))));
	}
	/**
	 * Restarts the application
	 */
	protected static void restartApplication(final String... args) {
		// Sun property pointing the main class and its arguments. 
		// Might not be defined on non Hotspot VM implementations.
		final String mcmd = System.getProperty("sun.java.command");
		final StringBuilder cmd = new StringBuilder();
		final String[] mainCommand = mcmd != null && !mcmd.isEmpty() ? mcmd.split(" ") : new String[] { rbLabel(KEYS.MAIN_CLASS) };
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
	 * Gets the resource path
	 * 
	 * @param validate
	 *            true to validate that the resource exists
	 * @param resourceName
	 *            the name of the resource to get a {@linkplain Path} to
	 * @param closeFileSystem
	 *            true to close the {@linkplain FileSystem}
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
	 * @param nodeIndex the index of the remote node
	 * @return the {@linkplain Path} to the remote node
	 */
	public static Path remotePropertiesFilePath(final int nodeIndex) {
		return workingDirectoryPath(null, remotePropertiesFileName(nodeIndex));
	}
	
	/**
	 * Gets the first available locale and the label resource bundles value for
	 * the specified key
	 * 
	 * @param key
	 *            the {@linkplain KEYS} of the resource bundle value
	 * @param formatArguments
	 *            the {@linkplain String#format(Locale, String, Object...)}
	 *            arguments
	 * @return the resource bundle value
	 */
	public static String rbLabel(final KEYS key, final Object... formatArguments) {
		return rbLabel(Locale.getAvailableLocales()[0], key, formatArguments);
	}
	
	/**
	 * Gets the a label resource bundles value for the specified key
	 * 
	 * @param locale
	 *            the locale of the resource bundle
	 * @param key
	 *            the {@linkplain KEYS} of the resource bundle value
	 * @param formatArguments
	 *            the {@linkplain String#format(Locale, String, Object...)}
	 *            arguments
	 * @return the resource bundle value
	 */
	public static String rbLabel(final Locale locale, final KEYS key, final Object... formatArguments) {
		return rbValue(RB_GUI, locale, key, formatArguments);
	}
	
	/**
	 * Gets the a resource bundles value for the specified key
	 * 
	 * @param rb
	 *            the resource bundle name
	 * @param locale
	 *            the locale of the resource bundle
	 * @param key
	 *            the {@linkplain KEYS} of the resource bundle value
	 * @param formatArguments
	 *            the {@linkplain String#format(Locale, String, Object...)}
	 *            arguments
	 * @return the resource bundle value
	 */
	private static String rbValue(final String rb, final Locale locale, final KEYS key, final Object... formatArguments) {
		final String rbStr = ResourceBundle.getBundle(rb, locale).getString(key.getKey());
		return formatArguments != null && formatArguments.length > 0 ? String.format(locale, rbStr, formatArguments) : rbStr;
	}

	/**
	 * {@linkplain RS} bundle keys
	 */
	public enum KEYS {
		MAIN_CLASS("main.class"), RXTX_VERSION("rxtx.version"), RXTX_FILE_NAME(
				"rxtx.file.name"), APP_ID("app.id"), APP_VERSION("app.version"), APP_DESC(
				"app.desc"), APP_TITLE("app.title"), APP_TITLE_ACTION_REQUIRED(
				"app.title.action.required"), APP_TITLE_ERROR("app.title.error"), APP_SERVICE_COM_RESTART_REQUIRED(
				"app.service.com.restart.required"), APP_SERVICE_INIT_ERROR(
				"app.service.init.error"), APP_GATE_KEEPER_ERROR(
				"app.gatekeeper.init.error"), APP_CONNECTION_DESC(
				"app.connection.desc"), APP_CONTROLS_DESC("app.controls.desc"), APP_CAPTURE_DESC(
				"app.capture.desc"), APP_LOGS_DESC("app.logs.desc"), APP_DIALOG_SETUP(
				"app.dialog.setup"), APP_DIALOG_SETUP_ERROR(
				"app.dialog.setup.error"), APP_DIALOG_SETUP_ERROR_PWD_MISMATCH(
				"app.dialog.setup.error.password.mismatch"), APP_DIALOG_AUTH(
				"app.dialog.auth"), APP_DIALOG_AUTH_ERROR(
				"app.dialog.auth.error"), APP_DIALOG_USERNAME(
				"app.dialog.username"), APP_DIALOG_PWD("app.dialog.password"), APP_DIALOG_PWD_VERIFY(
				"app.dialog.password.verify"), APP_DIALOG_REQUIRED(
				"app.dialog.required"), APP_HELP_DEFAULT("help.text.default"), APP_WIN_SYSTRAY_MIN_INFO(
				"win.systray.minimize.info"), APP_WIN_SYSTRAY(
				"win.systray.tooltip"), LOADING("loading"), SELECT("select"), TODAY(
				"today"), RELOAD("reload"), CLOSE("close"), ALL("all"), UPDATE(
				"update"), SUBMIT("submit"), ERROR("error"), INVALID("invalid"), SENDING(
				"sending"), CAM_PAN("cam.pan"), CAM_PAN_DESC("cam.pan.desc"), CAM_TILT(
				"cam.tilt"), CAM_TILT_DESC("cam.pan.tilt.desc"), CAM_RES(
				"cam.resolution"), CAM_RES_DESC("cam.resolution.desc"), CAM_RES_VGA(
				"cam.resolution.vga"), CAM_RES_QVGA("cam.resolution.qvga"), CAM_TRIP_ANGLE_PRIORITY_DESC(
				"cam.trip.angle.priority.desc"), CAM_SONAR_TRIP_ANGLE_PRIORITY(
				"cam.sonar.trip.angle.priority"), CAM_PIR_TRIP_ANGLE_PRIORITY(
				"cam.pir.trip.angle.priority"), CAM_MW_TRIP_ANGLE_PRIORITY(
				"cam.mw.trip.angle.priority"), CAM_LASER_TRIP_ANGLE_PRIORITY(
				"cam.laser.trip.angle.priority"), CAM_PAN_SONAR("cam.pan.sonar"), CAM_TILT_SONAR(
				"cam.tilt.sonar"), CAM_PAN_SONAR_DESC("cam.pan.sonar.desc"), CAM_TILT_SONAR_DESC(
				"cam.tilt.sonar.desc"), CAM_PAN_PIR("cam.pan.pir"), CAM_TILT_PIR(
				"cam.tilt.pir"), CAM_PAN_PIR_DESC("cam.pan.pir.desc"), CAM_TILT_PIR_DESC(
				"cam.tilt.pir.desc"), CAM_PAN_MW("cam.pan.microwave"), CAM_TILT_MW(
				"cam.pan.microwave"), CAM_PAN_MW_DESC("cam.pan.microwave.desc"), CAM_TILT_MW_DESC(
				"cam.pan.microwave.desc"), CAM_PAN_LASER("cam.pan.laser"), CAM_TILT_LASER(
				"cam.tilt.laser"), CAM_PAN_LASER_DESC("cam.pan.laser.desc"), CAM_TILT_LASER_DESC(
				"cam.tilt.laser.desc"), CAM_ACTION_QVGA("cam.take.qvga"), CAM_ACTION_VGA(
				"cam.take.vga"), SETTINGS_SAVE("settings.save"), SETTINGS_SEND(
				"settings.send"), SETTINGS_RECEIVE("settings.receive"), SETTINGS_SEND_FAILED(
				"settings.send.failed"), SENSOR_READINGS_GET(
				"sensors.readings.get"), SENSOR_TRIP_MULTI("sensors.trip.multi"), SENSOR_READINGS(
				"sensors.readings"), SENSOR_READINGS_FAILED(
				"sensors.readings.failed"), GATE_CONFIG("gate.conf"), GATE_TOGGLE(
				"gate.toggle"), GATE_TOGGLE_FAILED("gate.toggle.failed"), GATE_TOGGLE_DESC(
				"gate.toggle.desc"), GATE_STATE("gate.state"), LABEL_GRAPH_DESC(
				"app.graph.desc"), LABEL_GRAPH_ALARM_NOTIFY(
				"graph.alarm.notify"), LABEL_GRAPH_AXIS_X("graph.axis.x"), LABEL_DISPLAYSHELF_FULLSIZE_DESC(
				"displayshelf.fullsize.tooltip"), LABEL_TOGGLE_SWITCH_ON(
				"toggleswitch.on"), LABEL_TOGGLE_SWITCH_OFF("toggleswitch.off"), SERVICE_TX_RESPONSE_INVALID(
				"service.tx.response.unrecognized"), SERVICE_TX_RESPONSE_SUCCESS(
				"service.tx.response.success"), SERVICE_TX_RESPONSE_ERROR(
				"service.tx.response.error"), SERVICE_RX_READINGS(
				"service.rx.readings"), SERVICE_RX_SETTINGS(
				"service.rx.settings"), SERVICE_RX_KEYCODES(
				"service.rx.keycodes"), SERVICE_RX_IMAGE_MULTPART(
				"service.rx.image.multipart"), SERVICE_RX_IMAGE_SUCCESS(
				"service.rx.image.success"), SERVICE_RX_IMAGE_LOST_PACKETS(
				"service.rx.image.lostpackets"), SERVICE_RX_IMAGE_LOST_PACKETS_RETRY(
				"service.rx.image.lostpackets.retry"), SERVICE_RX_IMAGE_TIMEOUT(
				"service.rx.image.timeout"), SERVICE_CMD_SOUNDS(
				"service.command.sounds"), SERVICE_CMD_SOUNDS_TOGGLE(
				"service.command.sounds.toggle"), SERVICE_CMD_FAILED(
				"service.command.failed"), SERVICE_WIRELESS_CONNECTION_REQUIRED(
				"service.wireless.connection.required"), SERVICE_WIRELESS_FAILED(
				"service.wireless.failed"), SERVICE_WIRELESS_ACK_SUCCESS(
				"service.wireless.ack.success"), SERVICE_WIRELESS_ACK_FAILED(
				"service.wireless.ack.failed"), SERVICE_WIRELESS_SENDING(
				"service.wireless.sending"), SERVICE_WIRELESS_SUCCESS(
				"service.wireless.success"), SERVICE_WIRELESS_TX_TIMEOUT(
				"service.wireless.tx.timeout"), SERVICE_WIRELESS_TX_FAILED(
				"service.wireless.tx.failed"), SERVICE_WIRELESS_TX_BATCH_FAILED(
				"service.wireless.tx.batch.failed"), SERVICE_WIRELESS_SETTINGS_FAILED(
				"service.wireless.settings.failed"), SERVICE_EMAIL_FAILED(
				"service.email.failed"), SERVICE_EMAIL_CMD_EXEC(
				"service.email.commandexec"), SERVICE_EMAIL_CMD_EXEC_FAILED(
				"service.email.commandexec.failed"), LABEL_GRAPH_AXIS_Y(
				"graph.axis.y"), LABEL_GRAPH_SERIES_ALARM("graph.series.alarm"), LABEL_GRAPH_SERIES_ACTIVITY_MANUAL(
				"graph.series.activity.manual"), MAIL_CONNECT_FAILED(
				"mail.connect.failed"), MAIL_CONNECT("mail.connect"), MAIL_CONNECTED(
				"mail.connected"), MAIL_CONNECTING("mail.connecting"), MAIL_DISCONNECTING(
				"mail.disconnecting"), MAIL_DISCONNECTED("mail.disconnected"), MAIL_CLOSED(
				"mail.closed"), MAIL_RECONNECT("mail.reconnect"), MAIL_SMTP_HOST(
				"mail.smtp.host"), MAIL_SMTP_HOST_DESC("mail.smtp.host.desc"), MAIL_SMTP_PORT(
				"mail.smtp.port"), MAIL_SMTP_PORT_DESC("mail.smtp.port.desc"), MAIL_IMAP_HOST(
				"mail.imap.host"), MAIL_IMAP_HOST_DESC("mail.imap.host.desc"), MAIL_IMAP_PORT(
				"mail.imap.port"), MAIL_IMAP_PORT_DESC("mail.imap.port.desc"), MAIL_USERNAME(
				"mail.username"), MAIL_USERNAME_DESC("mail.username.desc"), MAIL_PASSWORD(
				"mail.password"), MAIL_PASSWORD_DESC("mail.password.desc"), MAIL_FOLDER_NAME(
				"mail.folder"), MAIL_FOLDER_DESC("mail.folder.desc"), SONAR_THRESHOLD(
				"sonar.threshold"), SONAR_THRESHOLD_DESC("sonar.threshold.desc"), SONAR_PIR_PAN(
				"sonarpir.pan"), SONAR_PIR_PAN_DESC("sonarpir.pan.desc"), SONAR_PIR_TILT(
				"sonarpir.tilt"), SONAR_PIR_TILT_DESC("sonarpir.tilt.desc"), SONAR_ALARM_DELAY(
				"sonar.alarm.delay"), SONAR_ALARM_DELAY_DESC(
				"sonar.alarm.delay.desc"), PIR_ALARM_DELAY("pir.alarm.delay"), PIR_ALARM_DELAY_DESC(
				"pir.alarm.delay.desc"), MW_THRESHOLD("microwave.threshold"), MW_THRESHOLD_DESC(
				"microwave.threshold.desc"), MW_ALARM_DELAY(
				"microwave.alarm.delay"), MW_ALARM_DELAY_DESC(
				"microwave.alarm.delay.desc"), MW_PAN("microwave.pan"), MW_PAN_DESC(
				"microwave.pan.desc"), LASER_THRESHOLD("laser.threshold"), LASER_THRESHOLD_DESC(
				"laser.threshold.desc"), LASER_ALARM_DELAY("laser.alarm.delay"), LASER_ALARM_DELAY_DESC(
				"laser.alarm.delay.desc"), LASER_CALIBRATION(
				"laser.calibration"), LASER_CALIBRATION_DESC(
				"laser.calibration.desc"), LASER_CALIBRATION_SUCCESS(
				"laser.calibration.success"), LASER_CALIBRATION_FAILED(
				"laser.calibration.failed"), WIRELESS_TAB1("wireless.tab1"), WIRELESS_TAB2(
				"wireless.tab2"), WIRELESS_TAB3("wireless.tab3"), WIRELESS_NODE_REMOTE_ADDY(
				"wireless.node.remote"), WIRELESS_NODE_REMOTE_ADDY_DESC(
				"wireless.node.remote.desc"), WIRELESS_NODE_REMOTE_PROMPT(
				"wireless.node.remote.prompt"), WIRELESS_NODE_REMOTE_STATUS(
				"wireless.node.remote.status"), WIRELESS_NODE_REMOTE_CHANGING(
				"wireless.node.remote.changing"), WIRELESS_NODE_REMOTE_REMOVE(
				"wireless.node.remote.remove"), WIRELESS_NODE_REMOTE_REMOVE_DESC(
				"wireless.node.remote.remove.desc"), WIRELESS_NODE_REMOVE_FAILED(
				"wireless.node.remote.remove.failed"), WIRELESS_NODE_REMOTE_ADD(
				"wireless.node.remote.add"), WIRELESS_NODE_REMOTE_ADD_DESC(
				"wireless.node.remote.add.desc"), WIRELESS_NODE_ADD_FAILED(
				"wireless.node.remote.add.failed"), WIRELESS_NODE_REMOTE_SELECT_FAILED(
				"wireless.node.remote.select.failed"), WIRELESS_NODE_REMOTE_SAVED_LOCAL(
				"wireless.node.remote.local.saved"), WIRELESS_REMOTE_SYNC(
				"wireless.node.remote.sync"), WIRELESS_REMOTE_SYNC_DESC(
				"wireless.node.remote.sync.desc"), WIRELESS_REMOTE_SYNCD(
				"wireless.node.remote.syncd"), WIRELESS_REMOTE_OUT_OF_SYNC(
				"wireless.node.remote.outofsync"), WIRELESS_REMOTE_UNIVERSAL(
				"wireless.remote.universal"), WIRELESS_REMOTE_UNIVERSAL_DESC(
				"wireless.remote.universal.desc"), WIRELESS_PORT(
				"wireless.port"), WIRELESS_PORT_DESC("wireless.port.desc"), WIRELESS_SPEED(
				"wireless.speed"), WIRELESS_SPEED_DESC("wireless.speed.desc"), WIRELESS_ACCESS_KEY(
				"wireless.access.key"), WIRELESS_ACCESS_KEY_DESC(
				"wireless.access.key.desc"), WIRELESS_HOST_ADDY("wireless.host"), WIRELESS_HOST_ADDY_DESC(
				"wireless.host.desc"), WIRELESS_CONNECT("wireless.connect"), WIRELESS_CONNECTING(
				"wireless.connecting"), WIRELESS_RECONNECT("wireless.reconnect"), WIRELESS_DISCONNECTING(
				"wireless.disconnecting"), WIRELESS_SYNC(
				"wireless.synchronizing"), WIRELESS_WORKING_DIR(
				"wireless.workingdir"), WIRELESS_WORKING_DIR_DESC(
				"wireless.workingdir.desc"), MAIL_ALARM_NOTIFY(
				"mail.alarm.notify"), MAIL_ALARM_NOTIFY_DESC(
				"mail.alarm.notify.desc"), MAIL_ALARM_NOFITY_EMAILS(
				"mail.alarm.notify.emails"), MAIL_ALARM_NOTIFY_EMAILS_DESC(
				"mail.alarm.notify.emails.desc"), MAIL_ALARM_NOTIFY_EMAILS_REMOVE(
				"mail.alarm.notify.emails.remove"), MAIL_ALARM_NOTIFY_EMAILS_ADD(
				"mail.alarm.notify.emails.add"), MAIL_ALARM_NOTIFY_EMAILS_ADD_DESC(
				"mail.alarm.notify.emails.add.desc"), MAIL_ALARM_NOTIFY_EMAILS_ADD_FAILED(
				"mail.alarm.notify.emails.add.failed"), MAIL_ALARM_NOTIFY_EMAILS_REMOVE_FAILED(
				"mail.alarm.notify.emails.remove.failed");

		private final String key;

		/**
		 * Constructor
		 * 
		 * @param key
		 *            the key to the {@linkplain RS}
		 */
		private KEYS(final String key) {
			this.key = key;
		}

		/**
		 * @return the key to the {@linkplain RS}
		 */
		public String getKey() {
			return key;
		}
	}
}

package org.ugate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ugate.service.web.WebServer;

public class ServiceManager {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

	public static void startServices() {
		WebServer.start();
	}
	
	public static void stopServices() {
		WebServer.stop();
	}
}

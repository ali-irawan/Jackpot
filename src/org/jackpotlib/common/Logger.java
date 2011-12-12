package org.jackpotlib.common;

import net.rim.device.api.system.EventLogger;

/**
 * Class for logging
 * 
 * <code>
 *   Logger.register(GUID_LOG, "MYAPP", EventLogger.VIEWER_STRING);
 *   
 *   Logger.info(GUID_LOG,"Some info");
 * </code>
 * @author Ali Irawan
 * @version 1.0
 */
public final class Logger { 

	public static void register(long guid, String name, int viewerType) {
		EventLogger.register(guid, name, viewerType);
	}

	public static void info(long guid, String data) {
		EventLogger.logEvent(guid, data.getBytes(), EventLogger.INFORMATION);
	}

	public static void debug(long guid, String data) {
		EventLogger.logEvent(guid, data.getBytes(), EventLogger.DEBUG_INFO);
	}

	public static void warning(long guid, String data) {
		EventLogger.logEvent(guid, data.getBytes(), EventLogger.WARNING);
	}

	public static void error(long guid, String data) {
		EventLogger.logEvent(guid, data.getBytes(), EventLogger.ERROR);
	}

	public static void severeError(long guid, String data) {
		EventLogger.logEvent(guid, data.getBytes(), EventLogger.SEVERE_ERROR);
	}

	public static void clearLog() {
		EventLogger.clearLog();
	}

	public static void setMinimumLevel(int level) {
		EventLogger.setMinimumLevel(level);
	}

	public static int getMinimumLevel() {
		return EventLogger.getMinimumLevel();
	}
}

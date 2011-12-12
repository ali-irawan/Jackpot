package org.jackpotlib.common;
 
/**
 * Application utility class. Provide general function that used in most application
 * 
 * @author Ali Irawan
 * @version 1.0
 */
public class AppUtil {
	
	/**
	 * Create a GUID (Global unique identifier)
	 * <code>
	 * final static long GUID_BASE = AppUtil.createGUID("org.jackpotlib.someapp");
	 * </code>
	 * @param packageName package name
	 * @return unique ID for specified package
	 */ 
	public static long createGUID(String packageName) {
		long h1 = Math.abs(packageName.hashCode());
		return h1 << 32; 
	}
	
	/**
	 * Create a GUID (Global unique identifier) for Logging
	 * <code>
	 * final static long GUID_BASE = AppUtil.createGUID("org.jackpotlib.someapp");<br/>
	 * final static long GUID_LOG = AppUtil.createGUID(GUID_BASE);
	 * </code>
	 * @param guidBase 
	 * @return unique ID for logging specified package
	 */
	public static long createLogGUID(long guidBase){
		long h1 = Math.abs("log".hashCode());
		long h = guidBase | h1;
		return (h);
	}
	
	
}

package org.jackpotlib.common;

import java.util.Vector;

/**
 * Contain class for manipulating strings
 * 
 * @author Ali Irawan
 * @version 1.0
 */
public class StringUtil {

	/**
	 * Split string using separator specified
	 * 
	 * @param original original string
	 * @param separator separator used
	 * @return an array of string split based separator
	 */
	public static String[] split(String original, String separator) {
		Vector nodes = new Vector();

		// Parse nodes into vector
		int start = 0;
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(start, index));
			start = index + separator.length();
			index = original.indexOf(separator, start);
		}
		// Get the last node
		nodes.addElement(original.substring(start));

		// Create splitted string array
		String[] result = new String[nodes.size()];
		for (int loop = 0; loop < nodes.size(); loop++) {
			result[loop] = (String) nodes.elementAt(loop);
		}
		return result;
	}
}

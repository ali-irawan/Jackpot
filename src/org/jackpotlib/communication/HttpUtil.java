/**
 * 
 */
package org.jackpotlib.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.jackpotlib.encode.Encoder;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.system.DeviceInfo;

/**
 * Http utility class for create connection, send parameters, etc.
 * 
 * @author Ali Irawan
 * @version 1.0
 */
public class HttpUtil {

	private static final String BES_STRING = DeviceInfo.isSimulator() ? ""
			: ";deviceside=false";
	private static final String BIS_STRING = DeviceInfo.isSimulator() ? ""
			: ";deviceside=false;ConnectionType=mds-public";
	private static final String DIRECT_TCP_STRING = DeviceInfo.isSimulator() ? ""
			: ";deviceside=true";
	private static final String WIFI_STRING = DeviceInfo.isSimulator() ? ""
			: ";interface=wifi";

	private String _url;
	private int _connectionType;
	private Hashtable _headers;
	private Hashtable _params;
	private Hashtable _paramsEncode;
	private String _requestMethod;

	public static final int CONNECTION_BES = 1;
	public static final int CONNECTION_BIS = 2;
	public static final int CONNECTION_DIRECT_TCP = 3;
	public static final int CONNECTION_WIFI = 4;

	public static final String POST = "POST";
	public static final String GET = "GET";

	public static final int ENCODED_NONE = 0;
	public static final int ENCODED_BASE64 = 1;

	/**
	 * Create a HTTP connection using default connection Default connection type
	 * is using BIS-B connection, and GET method
	 * 
	 * @param url
	 *            HTTP URL to connect
	 */
	public HttpUtil(String url) {
		this._url = url;
		this._headers = new Hashtable();
		this._params = new Hashtable();
		this._paramsEncode = new Hashtable();
		this._requestMethod = HttpConnection.GET;
		this._connectionType = CONNECTION_BIS;
	}

	/**
	 * Set the HTTP URL
	 * 
	 * @param url
	 *            HTTP URL to connect
	 */
	public void setUrl(String url) {
		this._url = url;
	}

	/**
	 * Get the HTTP URL
	 * 
	 * @return the HTTP URL
	 */
	public String getUrl() {
		return _url;
	}

	/**
	 * Set the Connection Type such as CONNECTION_BES, CONNECTION_BIS,
	 * CONNECTION_DIRECT_TCP, CONNECTION_WIFI
	 * 
	 * @param connectionType
	 *            connection type to use
	 */
	public void setConnectionType(int connectionType) {
		_connectionType = connectionType;
	}

	/**
	 * Return the Connection Type
	 * 
	 * @return Get the current connection type
	 */
	public int getConnectionType() {
		return _connectionType;
	}

	/**
	 * Set the HTTP header value
	 * 
	 * @param key
	 *            HTTP header
	 * @param value
	 *            string value for the header
	 */
	public void setHeader(String key, String value) {
		_headers.put(key, value);
	}

	/**
	 * Get the HTTP header value
	 * 
	 * @return the HTTP header
	 */
	public Hashtable getHeader() {
		return _headers;
	}

	/**
	 * Set the parameter
	 * 
	 * @param key
	 *            key to set
	 * @param value
	 *            value to set
	 */
	public void setParameter(String key, String value) {
		_params.put(key, value);
		_paramsEncode.put(key, new Integer(ENCODED_NONE)); // No format, not
															// using any
															// encoding
	}

	/**
	 * Set the parameter with format
	 * 
	 * @param key
	 *            key to set
	 * @param value
	 *            value to set
	 * @param format
	 *            one of these values: HttpUtil.ENCODED_BASE64
	 */
	public void setParameter(String key, String value, int format) {
		_params.put(key, value);
		_paramsEncode.put(key, new Integer(format));
	}

	/**
	 * Get the parameter
	 * 
	 * @return parameters
	 */
	public Hashtable getParameter() {
		return _params;
	}

	/**
	 * Set the request method
	 * 
	 * @param requestMethod
	 *            method to set, one of HttpUtil.POST, HttpUtil.GET
	 */
	public void setRequestMethod(String requestMethod) {
		_requestMethod = requestMethod;
	}

	/**
	 * Get the request method
	 * 
	 * @return method to get, one of HttpUtil.POST, HttpUtil.GET
	 */
	public String getRequestMethod() {
		return _requestMethod;
	}

	/**
	 * Clear all headers data
	 */
	public void clearHeaders() {
		_headers.clear();
	}

	/**
	 * Clear all Parameters Data
	 */
	public void clearParameters() {
		_params.clear();
		_paramsEncode.clear();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String send() throws Exception {
		HttpConnection _conn = null;
		String result = "";

		// Initialize the connection
		_conn = (HttpConnection) Connector.open(this.constructUrl(_url));
		
		// Set default content type and user-agent
		_conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		_conn.setRequestProperty("User-Agent",
				"Profile/MIDP-2.0 Configuration/CLDC-1.0");

		// Set the request method and headers, if content-type and user agent
		// exist it will replaced
		{
			_conn.setRequestMethod(_requestMethod);
			Enumeration enum = _headers.keys();
			while (enum.hasMoreElements()) {
				String key = (String) enum.nextElement();
				_conn.setRequestProperty(key, (String) _headers.get(key));
			}
		}

		byte[] dataBytes = getParameterInString().getBytes();

		_conn.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_LENGTH,
				String.valueOf(dataBytes.length));

		OutputStream out = _conn.openOutputStream();
		out.write(dataBytes);
		out.flush();

		// Getting the response code will open the connection, send the request,
		// and read the HTTP response headers.
		// The headers are stored until requested.
		{
			int rc = _conn.getResponseCode();
			if (rc != HttpConnection.HTTP_OK) {
				throw new IOException("HTTP response code: " + rc);
			}
		}

		// get the data from the service
		{
			InputStream _inputStream = _conn.openInputStream();
			byte[] bytesTemp = IOUtilities.streamToBytes(_inputStream);
			result = new String(bytesTemp);

			// final ByteBuffer bb = new ByteBuffer(_inputStream);

			// run this in EDT... not BGT!
			// result = bb.getString();

			// close everything out
			{
				if (_inputStream != null)
					try {
						_inputStream.close();
					} catch (Exception e) {
					}

				if (out != null)
					try {
						out.close();
					} catch (Exception e) {
					}

				if (_conn != null)
					try {
						_conn.close();
					} catch (Exception e) {
					}

			}
		}
		return result;
	}

	/**
	 * Generate all parameters in string key1=value1&key2=value2&key3=value3&...
	 * 
	 * @return query string
	 */
	public String getParameterInString() {
		StringBuffer data = new StringBuffer();
		Enumeration enum = _params.keys();
		while (enum.hasMoreElements()) {
			String key = (String) enum.nextElement();
			String value = (String) _params.get(key);
			
			Integer format = (Integer)_paramsEncode.get(key);
			if(format.intValue()==ENCODED_BASE64){
				try {
					data.append(key).append("=").append(Encoder.encode64(value.getBytes()));
				} catch (IOException e) {
					// Could not encode
					e.printStackTrace();
					data.append(key).append("=").append(value);
				}
			}else{
				data.append(key).append("=").append(value);
			}
			if (enum.hasMoreElements())
				data.append("&");
		}
		return data.toString();
	}

	/**
	 * Construct connection URL, adding specific connection directives depends
	 * on current connection used
	 * 
	 * @return complete url string
	 */
	private String constructUrl(String url) {
		switch (_connectionType) {
		case CONNECTION_BES:
			return url + BES_STRING;
		case CONNECTION_BIS:
			return url + BIS_STRING;
		case CONNECTION_DIRECT_TCP:
			return url + DIRECT_TCP_STRING;
		case CONNECTION_WIFI:
			return url + WIFI_STRING;
		}
		return url;
	}
}

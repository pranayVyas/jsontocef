package org.hortonworks.com.jsontocef;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.jettison.json.*;

/**
 * This program converts JSON to CEF
 * 
 * @author pranayashokvyas {@code} https://github.com/pranayVyas/CefWriter
 * @version V1.1
 * @since V1.0 - removed cefheader file dependency and sensor type dependency
 */
public class CefWriter implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1545048293685129868L;

	/**
	 * Default Constructor class that validations json to cef mapping file and cef
	 * header file
	 * 
	 * @param mapPath
	 *            - This is the path for json to cef mapping file
	 * @param headerPath
	 *            - This is the path for cef headers file
	 * @throws -
	 *             IllegalArgumentException - thrown when parameters missing
	 * @author pranayashokvyas
	 * @see
	 */
	public CefWriter(Path mapPath) {
		// Constructor class for Initialization, validation and creates output file to
		// write ceF
		logger.info("Inside Constructor class");
		if (mapPath.toFile().exists()) {
			writeMapHash(mapPath);
			// writeHeaderHash(headerPath); This is no longer needed
		} else {
			logger.error("json to cef mapping file path and cef header file path missing");
			throw new IllegalArgumentException();
		}
	}

	private static final Logger logger = LogManager.getLogger("CefWriter");
	private static Map<String, String> mapHash = null;
	private static CefHeader cefHeaderObj;
	private static CefExtension cefExtensionObj;
	private static boolean validationFailed = false;
	private static BufferedWriter cefWriter = null;
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final Charset ISO = Charset.forName("ISO-8859-1");
	
	public static void main(String[] args) throws IOException {

		// Expected number of arguments
		if (args.length == 3) {
			Path p1 = Paths.get(args[0]);
			// Path p2 = Paths.get(args[1]); header file is no longer needed
			CefWriter cf = new CefWriter(p1);
			// sensorType = args[4];
			// logger.info(sensorType);
			Path inpJson = Paths.get(args[1]);
			if (inpJson.toFile().exists()) {
				// logger.info("Input file exists");
				Path outJson = Paths.get(args[2]);
				BufferedReader bfr = null;
				try {
					cefWriter = Files.newBufferedWriter(outJson);
					bfr = new BufferedReader(new FileReader(inpJson.toFile()));
					String line = bfr.readLine();
					while ((line != null) && (!line.startsWith("#"))) {
						String cef = cf.jsonToCef(line);

						if (!cef.isEmpty()) {
							cefWriter.write(cef);
						}
						line = bfr.readLine();
					}
				} catch (IOException e) {
					logger.error("Unable to write output cef file - " + outJson);
					logger.error(e.getMessage());
				} finally {
					cefWriter.close();
					bfr.close();
				}
			} else {
				logger.error("Input Json file does not exist - " + inpJson);
				printHelp();
			}
		} else {
			logger.info("Expected 3 arguments received " + args.length);
			printHelp();

		}
	}

	private static void printHelp() {

		logger.info("argument1: path to json to cef properties file");
		logger.info("argument2: path or array of json objects. multiline json is not supported");
		logger.info("argument3: output directory to write cef file");
	}

	/*
	 * private static void parseHeader() {
	 * 
	 * cefHeaderObj = new CefHeader(); String headerConcat = ""; for (Entry<String,
	 * String> entry : headerHash.entrySet()) { String key = entry.getKey(); String
	 * value = entry.getValue(); if (key.startsWith(sensorType)) { if
	 * (key.equalsIgnoreCase(sensorType + ".DeviceVendor")) {
	 * cefHeaderObj.setDeviceVendor(value); } else if
	 * (key.equalsIgnoreCase(sensorType + ".DeviceProduct")) {
	 * cefHeaderObj.setDeviceProduct(value); } else { headerConcat = headerConcat +
	 * value + "|"; } } } cefHeaderObj.setCefHeader(headerConcat); }
	 */

	private static void parseExtensions(JSONObject sourceJobj) {

		cefExtensionObj = new CefExtension();
		cefHeaderObj = new CefHeader();
		Iterator<?> keys = sourceJobj.keys();
		String spaceDelimited = "";
		
		while (keys.hasNext()) {
			String key = (String) keys.next();
			// additional headers from extension
			if (key.equalsIgnoreCase("deviceVendor")) {
				cefHeaderObj.setDeviceVendor(sourceJobj.optString(key));
			}
			if (key.equalsIgnoreCase("deviceProduct")) {
				cefHeaderObj.setDeviceProduct(sourceJobj.optString(key));
			}
			if (key.equalsIgnoreCase("deviceVersion")) {
				cefHeaderObj.setDeviceVersion(sourceJobj.optString(key));
			}
			if (key.equalsIgnoreCase("deviceEvent")) {
				cefHeaderObj.setDeviceEvent(sourceJobj.optString(key));
			}
			if (key.equalsIgnoreCase("name")) {
				cefHeaderObj.setDeviceName(sourceJobj.optString(key));
			}
			if (key.equalsIgnoreCase("Severity")) {
				logger.info(key + "=" + sourceJobj.optString(key) );
				cefHeaderObj.setSeverity(sourceJobj.optString(key));
			}

			String cefKey = "";
			try {

				if (mapHash.containsKey(key)) {
					if ((mapHash.get(key).isEmpty()) | (mapHash.get(key).equalsIgnoreCase(null))
							| (mapHash.get(key).equalsIgnoreCase(""))) {
						cefKey = key;
					} else {
						cefKey = mapHash.get(key);
					}
				} else {
					// logger.info("key is not there " + key);
					cefKey = key;
				}
			} catch (NullPointerException e) {
				e.getStackTrace();
			}
			spaceDelimited = spaceDelimited + cefKey + "=" + sourceJobj.optString(key) + "  ";
		}
		cefExtensionObj.setExtensionFields(spaceDelimited);

	}

	private static JSONObject validateJSON(String jsonString) {

		JSONObject eventsObj = null;
		try {
			eventsObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			logger.error("JSON Exception encountered. Reason : " + e.getMessage());
			validationFailed = true;
		}
		return eventsObj;
	}

	/**
	 * @param inpJson
	 *            - single line JSON input
	 * @param sensorType
	 *            - sensor type to match cef headers with
	 * @return
	 * @author pranayashokvyas
	 * @see T-Mobile
	 */
	public String jsonToCef(String inpJson) {

		// sensorType = sensor;
		logger.info("Begin Parsing arguments");
		validationFailed = false;
		JSONObject sourceJobj = null;
		String jsonString = inpJson;

		if (!validationFailed) {
			sourceJobj = validateJSON(jsonString);
		}

		/*
		 * if (!validationFailed) { parseHeader(); }
		 */

		String cef = "";

		if (!validationFailed) {
			parseExtensions(sourceJobj);

			// if ((deviceAction.length() != 0) | (deviceEvent.length() != 0)) {
			// additionConcat = deviceEvent + "|" + deviceAction + "| ";
			// cef = cefHeaderObj.getCefHeader() + additionConcat +
			// cefExtensionObj.getExtensionFields();
			// } else {
			// cef = cefHeaderObj.getCefHeader() + cefExtensionObj.getExtensionFields();
			// }
			cefHeaderObj.setCefHeader();
			cef = new String((cefHeaderObj.getCefHeader() + " " + cefExtensionObj.getExtensionFields()).getBytes(ISO), UTF_8);
			logger.info(cef);
		}
		if (validationFailed) {
			logger.error("Validation has failed");
			return null;
		} else {
			return cef;
		}
	}

	/*
	 * private void writeHeaderHash(Path headerPath) {
	 * logger.info("writing headerHash"); BufferedReader bufR = null; try { bufR =
	 * new BufferedReader(new FileReader(headerPath.toFile())); headerHash = new
	 * HashMap<String, String>(); String line = bufR.readLine(); while ((line !=
	 * null) && (!line.startsWith("#"))) { String[] prop = line.split("="); if
	 * (prop.length == 2) { headerHash.put(prop[0], prop[1]); } else {
	 * headerHash.put(prop[0], prop[0]); } line = bufR.readLine(); } } catch
	 * (Exception e) {
	 * logger.error("Exception encountered while reading cef header file. Reason: "
	 * + e.getMessage()); } finally { try { bufR.close(); } catch (IOException e) {
	 * logger.error("Unknown exception encountered. Reason: " + e.getMessage()); } }
	 * }
	 */

	private void writeMapHash(Path mapPath) {
		logger.info("writing mapHash");
		BufferedReader bufR = null;
		try {
			bufR = new BufferedReader(new FileReader(mapPath.toFile()));
			mapHash = new HashMap<String, String>();
			String line = bufR.readLine();
			while ((line != null) && (!line.startsWith("#"))) {
				String[] prop = line.split("=");
				if (prop.length == 2) {
					mapHash.put(prop[0], prop[1]);
				} else {
					mapHash.put(prop[0], prop[0]);
				}
				line = bufR.readLine();
			}
		} catch (Exception e) {
			logger.error("Exception encountered while reading json to cef mapping file. Reason: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				bufR.close();
			} catch (IOException e) {
				logger.error("Unknown exception encountered. Reason: " + e.getMessage());
			}
		}
	}
}

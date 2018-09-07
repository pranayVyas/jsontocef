# JSON TO CEF CONVERTOR

## Description
The program converts JSON to CEF. This was built to generate CEF events to be passed to arcsight from Metron.
Download the contents and perform mvn install.

## To run as standalone
java -classpath jsontocef-V1.1.jar org.hortonworks.com.jsontocef.CefWriter 

## To call from other class
- CefWriter cf = new CefWriter(Path json to cef property file,Path cef header file)
- String CEFDATA = cf.jsonToCef(String <jsondata>,String <sensorType>);	
  
## Parameters required are 
- argument1: path to json to cef properties file.
- argument2: path or array of json objects. multiline json is not supported.
- argument4: output directory to write cef file.

- The program can run as individual job or can be called. 

## CEF HEADERS
By default the program will look for below fields for CEF HEADERS
- CEF Version - hardcoded to be "CEF:0"
- deviceVendor
- deviceProduct
- deviceVersion
- deviceEvent
- Name
- severity

CEF:0|deviceVendor|deviceProduct|deviceVersion|deviceEvent|Name|severity| <extennsionfields>
If your JSON field names for headers are different than above, you can modify the code in CefWriter.java between lines 141-156

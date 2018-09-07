package org.hortonworks.com.jsontocef;

import java.util.Optional;

public class CefHeader {

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceVersion() {
		return Optional.ofNullable(deviceVersion).orElse("");
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getDeviceEvent() {
		return Optional.ofNullable(deviceEvent).orElse("");
	}

	public void setDeviceEvent(String DeviceEvent) {
		this.deviceEvent = DeviceEvent;
	}

	public String getSeverity() {
		return Optional.ofNullable(severity).orElse("");
	}

	public void setSeverity(String severity) {
	
		int sevInt = Integer.valueOf(severity);
		switch (sevInt) {
		case 2:
			this.severity =  "Low";
			break;
		case 5:
			this.severity =  "Medium";
			break;
		case 8:
			this.severity =  "High";
			break;
		case 10:
			this.severity =  "Very-High";
			break;
		default:
			this.severity =  severity;
		}
	}

	public String getInitHeader() {
		return Optional.ofNullable(initHeader).orElse("");
	}

	public String getDeviceVendor() {
		return Optional.ofNullable(deviceVendor).orElse("");
	}

	public void setDeviceVendor(String deviceVendor) {
		this.deviceVendor = deviceVendor;
	}

	public String getDeviceProduct() {
		return Optional.ofNullable(deviceProduct).orElse("");
	}

	public void setDeviceProduct(String deviceProduct) {
		this.deviceProduct = deviceProduct;
	}

	public String getCefHeader() {
		return Optional.ofNullable(cefHeader).orElse("");
	}

	public void setCefHeader() {
		this.cefHeader = initHeader + "|" + getDeviceVendor() + "|" + getDeviceProduct()  + "|" + getDeviceVersion() + "|" + getDeviceEvent() + "|" + getDeviceName() + "|" + getSeverity() + "|";		
	}

	private final String initHeader = "CEF:0";
	private String deviceVendor;
	private String deviceProduct;
	private String deviceVersion;
	private String deviceEvent;
	private String deviceName;
	private String severity;
	private String cefHeader;



}

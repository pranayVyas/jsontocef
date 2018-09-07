package org.hortonworks.com.jsontocef;

public class CefExtension {

	public String getExtensionFields() {
		return extensionFields;
	}

	public void setExtensionFields(String extensionFields) {
		this.extensionFields = extensionFields;
		
		/*int elements = concatFields.length;
		int i = 0;
		for (i=0; i > elements-1; i++) {
			this.extensionFields = concatFields[i] + " ";

		}*/		
	}
	private String extensionFields;	
}

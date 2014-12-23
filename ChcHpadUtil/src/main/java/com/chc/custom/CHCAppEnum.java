package com.chc.custom;

public enum CHCAppEnum {
	Market ( "header_cloudstorage.png" ),
	MedicalRecords ( "header_medical.png" ),
	CloudStorage ( Market.getHeaderFileNameNoPath() ),
	Share ( "header_share.png" ),
	Launcher ( "logo_launcher.png" );
	
	private CHCAppEnum(String headerFileNameNoPath) {
		this.headerFileNameNoPath_ = headerFileNameNoPath;
	}
	
	public String getHeaderFileNameNoPath() {
		return this.headerFileNameNoPath_;
	}
	
	private String headerFileNameNoPath_;
}
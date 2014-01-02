package com.bill99.golden.inf.hbase.service.jmx;

/**
 * @author jun.bao
 * @since 2013年12月19日
 */
public class Region {

	private String nameAsString;
	private Integer storefileSizeMB;
	private Integer storefiles;
	private Integer memStoreSizeMB;

	public String getNameAsString() {
		return nameAsString;
	}

	public void setNameAsString(String nameAsString) {
		this.nameAsString = nameAsString;
	}

	public Integer getStorefileSizeMB() {
		return storefileSizeMB;
	}

	public void setStorefileSizeMB(Integer storefileSizeMB) {
		this.storefileSizeMB = storefileSizeMB;
	}

	public Integer getStorefiles() {
		return storefiles;
	}

	public void setStorefiles(Integer storefiles) {
		this.storefiles = storefiles;
	}

	public Integer getMemStoreSizeMB() {
		return memStoreSizeMB;
	}

	public void setMemStoreSizeMB(Integer memStoreSizeMB) {
		this.memStoreSizeMB = memStoreSizeMB;
	}

}

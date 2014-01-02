package com.bill99.golden.inf.hbase.domain;

/**
 * @author jun.bao
 * @since 2013年12月13日
 */
public class Table {

	private String tableName;
	private String serverName;
	private int regionCount;
	private int fileSize;
	/**
	 * region split策略 0.94+ hbase 版本支持建表时指定
	 */
	private String splitPolicy;

	public Table() {
		super();
	}

	public Table(String tableName, String serverName, int fileSize) {
		this();
		this.tableName = tableName;
		this.serverName = serverName;
		this.fileSize = fileSize;
		this.regionCount = 1;
	}

	public void add(int fileSize) {
		this.fileSize += fileSize;
		this.regionCount += 1;
	}

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", serverName=" + serverName + ", regionCount=" + regionCount
				+ ", fileSize=" + fileSize + "]";
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getRegionCount() {
		return regionCount;
	}

	public void setRegionCount(int regionCount) {
		this.regionCount = regionCount;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getSplitPolicy() {
		return splitPolicy;
	}

	public void setSplitPolicy(String splitPolicy) {
		this.splitPolicy = splitPolicy;
	}

}

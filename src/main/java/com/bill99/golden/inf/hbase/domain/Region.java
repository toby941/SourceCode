package com.bill99.golden.inf.hbase.domain;

import org.apache.hadoop.hbase.HServerLoad.RegionLoad;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author jun.bao
 * @since 2013年12月13日
 */
public class Region {

	private String hostName;
	private int port;
	private String regionName;
	private int storefiles;
	private int size;
	private String tableName;
	private long requestCount;
	private long readRequestCount;
	private long writeRequestCount;

	public Region() {
		super();
	}

	public Region(String tableName, String hostName, int port, RegionLoad regionLoad) {
		this();
		this.tableName = tableName;
		this.hostName = hostName;
		this.regionName = Bytes.toStringBinary(regionLoad.getName());
		this.storefiles = regionLoad.getStorefiles();
		this.size = regionLoad.getStorefileSizeMB();
		this.requestCount = regionLoad.getRequestsCount();
		this.readRequestCount = regionLoad.getReadRequestsCount();
		this.writeRequestCount = regionLoad.getWriteRequestsCount();
		this.port = port;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getStorefiles() {
		return storefiles;
	}

	public void setStorefiles(int storefiles) {
		this.storefiles = storefiles;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "Region [hostName=" + hostName + ", port=" + port + ", regionName=" + regionName + ", storefiles="
				+ storefiles + ", size=" + size + ", tableName=" + tableName + "]";
	}

	public long getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(long requestCount) {
		this.requestCount = requestCount;
	}

	public long getReadRequestCount() {
		return readRequestCount;
	}

	public void setReadRequestCount(long readRequestCount) {
		this.readRequestCount = readRequestCount;
	}

	public long getWriteRequestCount() {
		return writeRequestCount;
	}

	public void setWriteRequestCount(long writeRequestCount) {
		this.writeRequestCount = writeRequestCount;
	}

}

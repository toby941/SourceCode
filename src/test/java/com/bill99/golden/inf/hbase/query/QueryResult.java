package com.bill99.golden.inf.hbase.query;

/**
 * @author jun.bao
 * @since 2014年1月2日
 */
public class QueryResult {
	private static final long serialVersionUID = 4243184424513123072L;

	public static final int SUCCESS = 200;

	public static final int FAILURE = 400;

	private String key = null;

	private Double value = null;

	private String dayRate = "";

	private String weekRate = "";

	private int responseCode = SUCCESS;

	public QueryResult(String key) {
		this.key = key;
	}

	public QueryResult(String key, Double value) {
		this.key = key;
		this.value = value;
	}

	public QueryResult(String key, String value) {
		this.key = key;
		this.value = Double.parseDouble(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getDayRate() {
		return dayRate;
	}

	public void setDayRate(String dayRate) {
		this.dayRate = dayRate;
	}

	public String getWeekRate() {
		return weekRate;
	}

	public void setWeekRate(String weekRate) {
		this.weekRate = weekRate;
	}

	public long getTime() {
		return Long.parseLong(key.substring(key.lastIndexOf("#") + 1));
	}

}

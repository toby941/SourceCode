package com.fc.service.token;

import java.util.Calendar;
import java.util.Date;

/**
 * 令牌对象
 * 
 * @author jun.bao
 * @since 2013年7月29日
 */
public class Token {

	/**
	 * 令牌名称
	 */
	private String name;
	/**
	 * 优先级
	 */
	private Integer priority;

	/**
	 * 出生时间
	 */
	private Date bornTime;

	/**
	 * 最后被访问时间
	 */
	private Date lastAssessTime;

	public Token() {
		super();
	}

	public Token(String name, Integer priority) {
		super();
		this.name = name;
		this.priority = priority;
		Date nowTime = Calendar.getInstance().getTime();
		bornTime = nowTime;
		lastAssessTime = nowTime;
	}

	/**
	 * 重置最后访问时间
	 */
	public void reset() {
		Date nowTime = Calendar.getInstance().getTime();
		lastAssessTime = nowTime;
	}

	public Date getBornTime() {
		return bornTime;
	}

	public Date getLastAssessTime() {
		return lastAssessTime;
	}

	public String getName() {
		return name;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setBornTime(Date bornTime) {
		this.bornTime = bornTime;
	}

	public void setLastAssessTime(Date lastAssessTime) {
		this.lastAssessTime = lastAssessTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * 判断该token持有时间是否超时
	 * 
	 * @param holdTimeout
	 * @return true-持有超时 false-没超时
	 */
	public boolean isHoldTimeOut(Integer holdTimeout) {
		Calendar now = Calendar.getInstance();
		return now.getTime().getTime() - lastAssessTime.getTime() > holdTimeout;
	}

}

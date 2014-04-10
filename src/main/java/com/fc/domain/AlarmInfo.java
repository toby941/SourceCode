package com.fc.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jun.bao
 * @since 2013年12月6日
 */
@XmlRootElement(name = "alarmInfo")
public class AlarmInfo {

	private String requestUri;
	private Integer priority;
	private Long time;
	private String contextName;
	private String exception;

	public AlarmInfo() {
		super();
	}

	public AlarmInfo(String url, int priority, String error, String contextName) {
		this();
		this.requestUri = url;
		this.priority = priority;
		time = new Date().getTime();
		this.contextName = contextName;
		exception = error;

	}

	@XmlElement
	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	@XmlElement
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@XmlElement
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@XmlElement
	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	@XmlElement
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
}

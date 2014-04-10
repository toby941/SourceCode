package com.fc.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fc.service.token.FlowControlArrayBlockingQueue;
import com.fc.service.token.Token;
import com.fc.service.token.task.TokenPoolExecutor;

/**
 * @author jun.bao
 * @since 2013年11月15日
 */
@XmlRootElement(name = "tokenQueueSnapshot")
public class TokenQueueSnapshot {
	private Integer tokenPoolId;
	private Integer priority;
	private Integer sumToken;
	private Integer useToken;
	private Integer currentPoolSize;
	private Integer maxPoolSize;
	private Integer taskWaitQueueSize;
	private Date bornTime;

	public TokenQueueSnapshot() {
		super();
	}

	public TokenQueueSnapshot(FlowControlArrayBlockingQueue<Token> queue, TokenPoolExecutor executor, Integer id,
			Integer priority) {
		super();
		this.tokenPoolId = id;
		this.priority = priority;
		this.sumToken = queue.getCapacity();
		this.useToken = queue.remainingCapacity();
		this.currentPoolSize = executor.getPoolSize();
		this.maxPoolSize = executor.getMaximumPoolSize();
		this.taskWaitQueueSize = executor.getQueue().size();
		this.bornTime = new Date();

	}

	@XmlElement
	public Integer getTokenPoolId() {
		return tokenPoolId;
	}

	public void setTokenPoolId(Integer tokenPoolId) {
		this.tokenPoolId = tokenPoolId;
	}

	@XmlElement
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@XmlElement
	public Integer getSumToken() {
		return sumToken;
	}

	public void setSumToken(Integer sumToken) {
		this.sumToken = sumToken;
	}

	@XmlElement
	public Integer getUseToken() {
		return useToken;
	}

	public void setUseToken(Integer useToken) {
		this.useToken = useToken;
	}

	@XmlElement
	public Integer getCurrentPoolSize() {
		return currentPoolSize;
	}

	public void setCurrentPoolSize(Integer currentPoolSize) {
		this.currentPoolSize = currentPoolSize;
	}

	@XmlElement
	public Integer getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	@XmlElement
	public Integer getTaskWaitQueueSize() {
		return taskWaitQueueSize;
	}

	public void setTaskWaitQueueSize(Integer taskWaitQueueSize) {
		this.taskWaitQueueSize = taskWaitQueueSize;
	}

	@XmlElement
	public Date getBornTime() {
		return bornTime;
	}

	public void setBornTime(Date bornTime) {
		this.bornTime = bornTime;
	}

	@Override
	public String toString() {
		return "TokenQueueSnapshot [tokenPoolId=" + tokenPoolId + ", priority=" + priority + ", sumToken=" + sumToken
				+ ", useToken=" + useToken + ", currentPoolSize=" + currentPoolSize + ", maxPoolSize=" + maxPoolSize
				+ ", taskWaitQueueSize=" + taskWaitQueueSize + ", bornTime=" + bornTime + "]";
	}
}

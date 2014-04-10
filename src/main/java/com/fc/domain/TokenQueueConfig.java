package com.fc.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fc.util.Constants;

/**
 * 按具体优先级维护token池中的各个队列参数信息
 * @author jun.bao
 * @since 2013年8月28日
 */
@XmlRootElement(name = "tokenQueueConfig")
public class TokenQueueConfig {

	private String id;
	private String tokenPoolId;
	/**
	 * 优先级匹配value，同一组内用：分割，组组之间用#分割
	 */
	private String value;
	private Date addTime;
	private Date updateTime;

	/**
	 * 判断同一优先级的两个新旧配置token 数量是否有变化
	 * @param anotherTokenQueueConfig
	 * @return
	 */
	public boolean isTokenCountChange(TokenQueueConfig anotherTokenQueueConfig) {

		Integer anotherTokenCount = anotherTokenQueueConfig.getTokenCount();
		Integer tokenCount = this.getTokenCount();
		return !tokenCount.equals(anotherTokenCount);
	}

	public boolean isThresholdChange(TokenQueueConfig anotherTokenQueueConfig) {
		return !this.getThreshold().equals(anotherTokenQueueConfig.getThreshold());
	}

	public boolean isMaxPoolSizeChange(TokenQueueConfig anotherTokenQueueConfig) {
		return !this.getMaxPoolSize().equals(anotherTokenQueueConfig.getMaxPoolSize());
	}

	public boolean isChange(TokenQueueConfig another) {
		return isThresholdChange(another) || isTokenCountChange(another) || isMaxPoolSizeChange(another);
	}

	/**
	 * 持有token超时时间
	 */
	private Integer holdTimeout;

	/**
	 * 队列对应优先级
	 */
	private Integer priority;

	/**
	 * 队列持有token数量
	 */
	private Integer tokenCount;

	/**
	 * 可外借令牌占比最大阈值 等于1代表不可外借
	 */
	private Float threshold;

	/**
	 * 线程池最大并发排队数 5<x<99
	 */
	private Integer maxPoolSize;

	@XmlElement
	public Integer getHoldTimeout() {
		return holdTimeout;
	}

	@XmlElement
	public Integer getPriority() {
		return priority;
	}

	@XmlElement
	public Integer getTokenCount() {
		if (tokenCount == null) {
			tokenCount = 0;
		}
		return tokenCount;
	}

	public void setHoldTimeout(Integer holdTimeout) {
		this.holdTimeout = holdTimeout;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setTokenCount(Integer tokenCount) {
		this.tokenCount = tokenCount;
	}

	@XmlElement
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement
	public String getTokenPoolId() {
		return tokenPoolId;
	}

	public void setTokenPoolId(String tokenPoolId) {
		this.tokenPoolId = tokenPoolId;
	}

	@XmlElement
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@XmlElement
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@XmlElement
	public Float getThreshold() {
		if (threshold == null) {
			threshold = Constants.DEFAULT_TOKEN_BORROW_THRESHOLD;
		}
		return threshold;
	}

	public void setThreshold(Float threshold) {
		this.threshold = threshold;
	}

	@XmlElement
	public Integer getMaxPoolSize() {
		if (maxPoolSize == null || maxPoolSize <= 0) {
			maxPoolSize = Constants.DEFAULT_MAX_TOKEN_TASK_POOL_SIZE;
		}
		return maxPoolSize;
	}

	public void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	@Override
	public String toString() {
		return "TokenQueueConfig [id=" + id + ", tokenPoolId=" + tokenPoolId + ", value=" + value + ", addTime="
				+ addTime + ", holdTimeout=" + holdTimeout + ", priority=" + priority + ", tokenCount=" + tokenCount
				+ ", threshold=" + threshold + ", maxPoolSize=" + maxPoolSize + "]";
	}

}

package com.fc.domain;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 令牌池配置信息
 * @author jun.bao
 * @since 2013年9月9日
 */
@XmlRootElement(name = "tokenPoolConfig")
public class TokenPoolConfig {

	private Integer id;
	private Integer contextId;
	/**
	 * 持有token超时时间 允许{@link TokenQueueConfig#getHoldTimeout()}覆盖
	 */
	private Integer holdTimeout;

	private String requestUrl;

	/**
	 * 请求token超时时间 允许{@link TokenQueueConfig#getRequestTimeout()}覆盖
	 */
	private Integer requestTimeout;
	private List<TokenQueueConfig> tokenQueueConfigs;

	/**
	 * 该token池token总数
	 */
	private Integer totalTokenCount;
	/**
	 * 请求提交数据格式 目前支持text/xml
	 */
	private String contentType;
	private String requestKey;
	private Date addTime;
	private Date updateTime;
	/**
	 * 限流接入部署方式-1：客户端部署 2：服务端部署
	 */
	private Integer deployType;

	@XmlElement
	public Integer getHoldTimeout() {
		return holdTimeout;
	}

	@XmlElement
	public String getRequestUrl() {
		return requestUrl;
	}

	@XmlElement
	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	@XmlElements({ @XmlElement(name = "tokenQueueConfig", type = TokenQueueConfig.class) })
	public List<TokenQueueConfig> getTokenQueueConfigs() {
		return tokenQueueConfigs;
	}

	@XmlElement
	public Integer getTotalTokenCount() {
		return totalTokenCount;
	}

	public void setHoldTimeout(Integer holdTimeout) {
		this.holdTimeout = holdTimeout;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public void setTokenQueueConfigs(List<TokenQueueConfig> tokenQueueConfigs) {
		this.tokenQueueConfigs = tokenQueueConfigs;
	}

	public void setTotalTokenCount(Integer totalTokenCount) {
		this.totalTokenCount = totalTokenCount;
	}

	@XmlElement
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement
	public Integer getContextId() {
		return contextId;
	}

	public void setContextId(Integer contextId) {
		this.contextId = contextId;
	}

	@XmlElement
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@XmlElement
	public String getRequestKey() {
		return requestKey;
	}

	public void setRequestKey(String requestKey) {
		this.requestKey = requestKey;
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
	public Integer getDeployType() {
		return deployType;
	}

	public void setDeployType(Integer deployType) {
		this.deployType = deployType;
	}

	public boolean isClientDeploy() {
		return deployType.equals(1);
	}

	@Override
	public String toString() {
		return "TokenPoolConfig [id=" + id + ", contextId=" + contextId + ", holdTimeout=" + holdTimeout
				+ ", requestUrl=" + requestUrl + ", requestTimeout=" + requestTimeout + ", tokenQueueConfigs="
				+ tokenQueueConfigs + ", totalTokenCount=" + totalTokenCount + ", contentType=" + contentType
				+ ", requestKey=" + requestKey + ", addTime=" + addTime + ", updateTime=" + updateTime
				+ ", deployType=" + deployType + "]";
	}

}
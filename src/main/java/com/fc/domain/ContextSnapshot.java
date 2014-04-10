package com.fc.domain;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jun.bao
 * @since 2013年11月15日
 */
@XmlRootElement(name = "contextSnapshot")
public class ContextSnapshot {

	private String contextName;
	private Date bornTime;
	private List<TokenPoolSnapshot> tokenPoolSnapshots;

	public ContextSnapshot() {
		super();
	}

	public ContextSnapshot(String name) {
		super();
		this.contextName = name;
		this.bornTime = new Date();
	}

	@XmlElement
	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	@XmlElement
	public Date getBornTime() {
		return bornTime;
	}

	public void setBornTime(Date bornTime) {
		this.bornTime = bornTime;
	}

	@XmlElements({ @XmlElement(name = "tokenPoolSnapshot", type = TokenPoolSnapshot.class) })
	public List<TokenPoolSnapshot> getTokenPoolSnapshots() {
		return tokenPoolSnapshots;
	}

	public void setTokenPoolSnapshots(List<TokenPoolSnapshot> tokenPoolSnapshots) {
		this.tokenPoolSnapshots = tokenPoolSnapshots;
	}

	@Override
	public String toString() {
		return "ContextSnapshot [contextName=" + contextName + ", bornTime=" + bornTime + ", tokenPoolSnapshots="
				+ tokenPoolSnapshots + "]";
	}

}

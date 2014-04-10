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
@XmlRootElement(name = "tokenPoolSnapshot")
public class TokenPoolSnapshot {

	private Integer id;
	private Integer sumToken = 0;
	private Integer useToken = 0;
	private Date bornTime;
	private List<TokenQueueSnapshot> tokenQueueSnapshots;

	public TokenPoolSnapshot() {
		super();
	}

	public TokenPoolSnapshot(Integer id, List<TokenQueueSnapshot> list) {
		super();
		this.bornTime = new Date();
		this.id = id;
		this.tokenQueueSnapshots = list;
	}

	@XmlElement
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	public Date getBornTime() {
		return bornTime;
	}

	public void setBornTime(Date bornTime) {
		this.bornTime = bornTime;
	}

	@XmlElements({ @XmlElement(name = "tokenQueueSnapshot", type = TokenQueueSnapshot.class) })
	public List<TokenQueueSnapshot> getTokenQueueSnapshots() {
		return tokenQueueSnapshots;
	}

	public void setTokenQueueSnapshots(List<TokenQueueSnapshot> tokenQueueSnapshots) {
		this.tokenQueueSnapshots = tokenQueueSnapshots;
	}

	@Override
	public String toString() {
		return "TokenPoolSnapshot [id=" + id + ", sumToken=" + sumToken + ", useToken=" + useToken + ", bornTime="
				+ bornTime + ", tokenQueueSnapshots=" + tokenQueueSnapshots + "]";
	}

}

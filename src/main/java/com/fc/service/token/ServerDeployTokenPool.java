package com.fc.service.token;

import java.util.concurrent.Future;

import com.fc.domain.TokenPoolConfig;
import com.fc.domain.TokenPoolSnapshot;
import com.fc.util.Config;
import com.fc.util.Constants;
import com.fc.util.JDKHttpClient;

/**
 * 服务端分配token，客户端直接请求 <br/>
 * 多节点客户端同时请求单点的服务端 压力？<br/>
 * 若部署多节点的服务端 token数量一致性保证
 * @author jun.bao
 * @since 2013年9月13日
 */
public class ServerDeployTokenPool implements TokenPool {

	private String contextName;
	private Integer requestTimeout;
	private String poolName;

	public ServerDeployTokenPool(String contextName, TokenPoolConfig tokenPoolConfig) {
		super();
		requestTimeout = tokenPoolConfig.getRequestTimeout();
		poolName = tokenPoolConfig.getRequestUrl();
		this.contextName = contextName;
	}

	@Override
	public String getToken(Integer priority) {

		String url = Config.getTokenUrl(contextName, poolName, priority, null);
		return JDKHttpClient.doGet(url, requestTimeout);
	}

	@Override
	public boolean releaseToken(String uuid) {
		String url = Config.getReleaseTokenUrl(contextName, poolName, uuid, null);
		JDKHttpClient.doGet(url);
		return true;
	}

	@Override
	public String getSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<String> submit(Integer priority) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reload(TokenPoolConfig tokenPoolConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getDeployType() {

		return Constants.DEPLOY_TYPE_SERVER;
	}

	@Override
	public void setUsed(boolean used) {
		// TODO Auto-generated method stub
	}

	@Override
	public TokenPoolSnapshot getTokenPoolSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}

}

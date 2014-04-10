package com.fc.service.token;

import java.util.concurrent.Future;

import com.fc.domain.TokenPoolConfig;
import com.fc.domain.TokenPoolSnapshot;

/**
 * 令牌获取 释放接口
 * @author jun.bao
 * @since 2013年9月13日
 */
public interface TokenPool {

	public Integer getDeployType();

	public String getToken(Integer priority);

	public boolean releaseToken(String uuid);

	public String getSnapshot();

	public Future<String> submit(Integer priority);

	public void clear();

	public void reload(TokenPoolConfig tokenPoolConfig);

	public void setUsed(boolean used);

	public TokenPoolSnapshot getTokenPoolSnapshot();

}

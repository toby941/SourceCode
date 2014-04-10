package com.fc.service.token.task;

import java.util.concurrent.Callable;

import com.fc.service.token.TokenPool;

/**
 * @author jun.bao
 * @since 2013年7月31日
 */
public class TokenCallable implements Callable<String> {

	private Integer priority;
	private TokenPool tokenPool;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public TokenCallable() {
		super();
	}

	public TokenCallable(int priority) {
		super();
		this.priority = priority;
	}

	public TokenPool getTokenPool() {
		return tokenPool;
	}

	public void setTokenPool(TokenPool tokenPool) {
		this.tokenPool = tokenPool;
	}

	/**
	 * 异步调度获取 token
	 * 
	 * @return token的 UUID
	 */
	@Override
	public String call() throws Exception {
		return tokenPool.getToken(priority);
	}
}

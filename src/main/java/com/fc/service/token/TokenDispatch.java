package com.fc.service.token;

/**
 * 令牌池不同优先级队列调度接口
 * 
 * @author jun.bao
 * @since 2013年8月8日
 */
public interface TokenDispatch {

	/**
	 * 令牌调度，将空闲令牌调度到资源紧张的令牌池
	 * 
	 * @param priority
	 */
	public void dispatch(Integer priority);
}

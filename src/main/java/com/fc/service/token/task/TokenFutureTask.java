package com.fc.service.token.task;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.bill99.fc.service.token.task.TokenCallable;

/**
 * 封装异步获取token请求的 {@link FutureTask}
 * 
 * @author jun.bao
 * @since 2013年7月31日
 */
public class TokenFutureTask extends FutureTask<String> {

	/**
	 * 请求对应优先级
	 */
	private Integer priority;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public TokenFutureTask(Callable<String> callable) {
		super(callable);
		TokenCallable tokenCallable = (TokenCallable) callable;
		priority = tokenCallable.getPriority();
	}

}

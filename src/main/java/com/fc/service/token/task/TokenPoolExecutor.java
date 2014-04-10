package com.fc.service.token.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jun.bao
 * @since 2013年7月31日
 */
public class TokenPoolExecutor extends ThreadPoolExecutor implements RejectedExecutionHandler {

	private static final Integer DEFAULT_CORE_POOL_SIZE = 2;

	public TokenPoolExecutor(long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Integer maxPoolSize) {
		super(DEFAULT_CORE_POOL_SIZE, maxPoolSize, keepAliveTime, unit, workQueue);
		this.setRejectedExecutionHandler(this);
	}

	@Override
	protected RunnableFuture<String> newTaskFor(Callable c) {
		TokenCallable tokenCallable = (TokenCallable) c;
		return new TokenFutureTask(tokenCallable);
	}

	@Override
	public void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor) {
		TokenCallable callable = (TokenCallable) paramRunnable;
		throw new RejectedExecutionException("task-queue is full can not add more, priority: " + callable.getPriority());

	}
}

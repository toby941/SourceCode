package com.fc.service.token.task;

import java.util.TimerTask;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.fc.service.token.TokenPoolManager;

/**
 * 定时重载限流配置 {@link TimerTask}
 * @author jun.bao
 * @since 2013年9月17日
 */
public class ReloadConfigTimerTask extends TimerTask {
	private final static Log log = LogFactory.getLog(ReloadConfigTimerTask.class);

	@Override
	public void run() {
		try {
			TokenPoolManager.getTokenPoolManager().reload();
		} catch (Exception e) {
			log.error("reload config error", e);
		}
	}

}

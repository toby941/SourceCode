package com.fc.service.token.task;

import java.util.TimerTask;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.fc.service.token.TokenPoolManager;

/**
 * 定时上传token使用快照
 * @author jun.bao
 * @since 2013年8月30日
 */
public class SnapshotTimerTask extends TimerTask {
	private final static Log log = LogFactory.getLog(SnapshotTimerTask.class);

	@Override
	public void run() {
		try {
			TokenPoolManager.getTokenPoolManager().sendSnapshot();
		} catch (Exception e) {
			log.error("send snapshot error", e);
		}
	}

}

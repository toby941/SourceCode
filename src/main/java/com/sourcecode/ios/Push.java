package com.sourcecode.ios;

import java.util.ArrayList;
import java.util.List;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.util.CollectionUtils;

public class Push {
	private static final Logger logger = Logger.getLogger(Push.class);

	public static void main(String[] args) throws JSONException {
		List<String> list = new ArrayList<String>();
		list.add("d8669c61a8802595c353beace351229e7db0596b1a1b6c987248184647310fbb");
		list.add("fa4a00d78b81e414f3056f8b79ee771653125ae28856a5422c225c6df9b59697");
		pushIoSMsg(list);

	}

	/**
	 * ios消息推送
	 * 
	 * @param corePushMsg
	 * @throws JSONException
	 */
	public static void pushIoSMsg(List<String> devices) throws JSONException {
		if (CollectionUtils.isEmpty(devices)) {
			return;
		}
		// d8669c61a8802595c353beace351229e7db0596b1a1b6c987248184647310fbb
		// fa4a00d78b81e414f3056f8b79ee771653125ae28856a5422c225c6df9b59697
		String[] tokens = new String[devices.size()];
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = devices.get(i);
		}
		/*
		 * /mnt/html/tomcat_project/private/${0}/ioskey/${1}.p12 see in
		 * /mnt/html/tomcat_project/private/1632/ioskey/com.q.airad.hysoka87.p12
		 * see in environment.properties
		 */
		String keyPath = "C:\\Users\\toby\\git\\SourceCode\\src\\main\\java\\com\\sourcecode\\ios\\pass.p12";
		String keyPassword = "";
		logger.error("pushIoSMsg key: " + keyPath);
		try {
			PushNotificationPayload payload = PushNotificationPayload.complex();
			payload.addAlert(StringUtils.EMPTY);
			// payload.addAlert("content");
			// payload.addCustomDictionary("url", "url");
			// payload.addCustomDictionary("id", "id");
			List<PushedNotification> notifications = javapns.Push.payload(payload, keyPath, keyPassword, true, tokens);
			boolean handleRresult = handlePushResult(notifications);
		} catch (CommunicationException e) {
			logger.error("pushIoSMsg CommunicationException ", e);
		} catch (KeystoreException e) {
			logger.error("pushIoSMsg KeystoreException", e);
		}

	}

	public static boolean handlePushResult(List<PushedNotification> notifications) {
		int successCount = 0;
		for (PushedNotification notification : notifications) {
			if (notification.isSuccessful()) {
				/* Apple accepted the notification and should deliver it */
				logger.error("Push notification sent successfully to: " + notification.getDevice().getToken());
				System.out.println("Push notification sent successfully to: " + notification.getDevice().getToken());
				successCount = successCount + 1;
				/* Still need to query the Feedback Service regularly */
			} else {
				String invalidToken = notification.getDevice().getToken();
				/* Add code here to remove invalidToken from your database */

				/* Find out more about what the problem was */
				Exception theProblem = notification.getException();
				logger.error("invalidToken: " + invalidToken, theProblem);
				System.out.println("invalidToken: " + invalidToken + "  exception" + ExceptionUtils.getFullStackTrace(theProblem));
				/*
				 * If the problem was an error-response packet returned by
				 * Apple, get it
				 */
				ResponsePacket theErrorResponse = notification.getResponse();
				if (theErrorResponse != null) {
					logger.error("theErrorResponse: " + theErrorResponse.getMessage());
					System.out.println("theErrorResponse: " + theErrorResponse.getMessage());
				}
			}
		}
		logger.error("successCount: " + successCount + "  notifications total: " + notifications.size());
		return successCount >= (notifications.size() / 2);
	}
}

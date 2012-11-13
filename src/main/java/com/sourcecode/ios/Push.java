package com.sourcecode.ios;

import java.util.List;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.util.CollectionUtils;

public class Push {
    private final Logger logger = Logger.getLogger(Push.class);

    /**
     * ios消息推送
     * 
     * @param corePushMsg
     * @throws JSONException
     */
    public void pushIoSMsg(List<String> devices) throws JSONException {
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        String[] tokens = new String[devices.size()];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = devices.get(i);
        }
        /*
         * /mnt/html/tomcat_project/private/${0}/ioskey/${1}.p12 see in
         * /mnt/html/tomcat_project/private/1632/ioskey/com.q.airad.hysoka87.p12 see in environment.properties
         */
        String keyPath = "";
        logger.error("pushIoSMsg key: " + keyPath);
        try {

            PushNotificationPayload payload = PushNotificationPayload.complex();
            payload.addAlert("content");
            payload.addCustomDictionary("url", "url");
            payload.addCustomDictionary("id", "id");
            List<PushedNotification> notifications =
                    javapns.Push.payload(payload, keyPath, "keyPassword", false, tokens);
            boolean handleRresult = handlePushResult(notifications);
        }
        catch (CommunicationException e) {
            logger.error("pushIoSMsg CommunicationException ", e);
        }
        catch (KeystoreException e) {
            logger.error("pushIoSMsg KeystoreException", e);
        }

    }

    public boolean handlePushResult(List<PushedNotification> notifications) {
        int successCount = 0;
        for (PushedNotification notification : notifications) {
            if (notification.isSuccessful()) {
                /* Apple accepted the notification and should deliver it */
                logger.error("Push notification sent successfully to: " + notification.getDevice().getToken());
                successCount = successCount + 1;
                /* Still need to query the Feedback Service regularly */
            }
            else {
                String invalidToken = notification.getDevice().getToken();
                /* Add code here to remove invalidToken from your database */

                /* Find out more about what the problem was */
                Exception theProblem = notification.getException();
                logger.error("invalidToken: " + invalidToken, theProblem);

                /*
                 * If the problem was an error-response packet returned by Apple, get it
                 */
                ResponsePacket theErrorResponse = notification.getResponse();
                if (theErrorResponse != null) {
                    logger.error("theErrorResponse: " + theErrorResponse.getMessage());
                }
            }
        }
        logger.error("successCount: " + successCount + "  notifications total: " + notifications.size());
        return successCount >= (notifications.size() / 2);
    }
}

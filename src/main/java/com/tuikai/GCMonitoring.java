package com.tuikai;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;

/**
 * 使用GarbageCollectionNotificationInfo(jdk7u40,jdk8以上提供)进行GC监听
 * @author baojun.bj
 *
 */
public class GCMonitoring {
	 
    private static final long JVM_START_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
 
 
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
 
    private static final long ONE_BYTE = 1024;
 
 
    public static void init() {
        //get all GarbageCollectorMXBeans
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        //register every GarbageCollectorMXBean
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println(
                    "register " + gcBean.getName() + " for " + Arrays.deepToString(gcBean.getMemoryPoolNames()));
 
            NotificationEmitter emitter = (NotificationEmitter) gcBean;
            //new listener
            NotificationListener listener = new NotificationListener() {
                //record total gc time spend
                long totalGcTimeSpend = 0;
 
                @Override
                public void handleNotification(Notification notification, Object handback) {
                    HandBack handBack = (HandBack) handback;
 
                    //get gc info
                    GarbageCollectionNotificationInfo info =
                            GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                    //output
                    String gcType = info.getGcAction();
                    if (gcType.length() > 7) {
                        gcType = gcType.substring(7);
                    }
                    //get a glance of gc
                    StringBuilder gcGlance = new StringBuilder();
                    gcGlance.append(gcType).append(": - ").append(info.getGcInfo().getId());
                    gcGlance.append(" (").append(info.getGcCause()).append(") ");
                    gcGlance.append("start: ")
                            .append(dateFormat.format(new Date(JVM_START_TIME + info.getGcInfo().getStartTime())));
                    gcGlance.append(", end: ")
                            .append(dateFormat.format(new Date(JVM_START_TIME + info.getGcInfo().getEndTime())));
 
                    System.out.println(gcGlance.toString());
 
                    //memory info
                    Map<String, MemoryUsage> beforeUsageMap = info.getGcInfo().getMemoryUsageBeforeGc();
                    Map<String, MemoryUsage> afterUsageMap = info.getGcInfo().getMemoryUsageAfterGc();
                    for (Map.Entry<String, MemoryUsage> entry : afterUsageMap.entrySet()) {
                        String name = entry.getKey();
                        MemoryUsage afterUsage = entry.getValue();
                        MemoryUsage beforeUsage = beforeUsageMap.get(name);
 
                        StringBuilder usage = new StringBuilder();
                        usage.append("\t[").append(name).append("] ");
                        usage.append("init:").append(afterUsage.getInit() / ONE_BYTE).append("K; ");
                        usage.append("used:").append(handBack
                                .handUsage(beforeUsage.getUsed(), afterUsage.getUsed(), beforeUsage.getMax()))
                                .append("; ");
                        usage.append("committed: ").append(handBack
                                .handUsage(beforeUsage.getCommitted(), afterUsage.getCommitted(),
                                        beforeUsage.getMax()));
 
                        System.out.println(usage.toString());
                    }
                    totalGcTimeSpend += info.getGcInfo().getDuration();
                    //summary
                    long percent =
                            (info.getGcInfo().getEndTime() - totalGcTimeSpend) * 1000L / info.getGcInfo().getEndTime();
                    StringBuilder summary = new StringBuilder();
                    summary.append("duration:").append(info.getGcInfo().getDuration()).append("ms");
                    summary.append(", throughput:").append((percent / 10)).append(".").append(percent % 10).append('%');
                    System.out.println(summary.toString());
                    System.out.println();
                }
            };
 
            //add the listener
            emitter.addNotificationListener(listener, new NotificationFilter() {
                private static final long serialVersionUID = 3763793138186359389L;
 
                @Override
                public boolean isNotificationEnabled(Notification notification) {
                    //filter GC notification
                    return notification.getType()
                            .equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION);
                }
            }, HandBack.getInstance());
        }
    }
 
 public static void main(String[] args) throws InterruptedException {
	 GCMonitoring.init();
	 Thread.sleep(1000L*10);
}
    private static class HandBack {
 
        private HandBack() {
        }
 
        private static HandBack instance = new HandBack();
 
        public static HandBack getInstance() {
            return instance;
        }
 
        public String handUsage(long before, long after, long max) {
            StringBuilder usage = new StringBuilder();
 
            if (max == -1) {
                usage.append("").append(before / ONE_BYTE).append("K -> ").append(after / ONE_BYTE).append("K)");
                return usage.toString();
            }
 
            long beforePercent = ((before * 1000L) / max);
            long afterPercent = ((after * 1000L) / max);
 
            usage.append(beforePercent / 10).append('.').append(beforePercent % 10).append("%(")
                    .append(before / ONE_BYTE).append("K) -> ").append(afterPercent / 10).append('.')
                    .append(afterPercent % 10).append("%(").append(after / ONE_BYTE).append("K)");
            return usage.toString();
 
        }
 
    }
 
}

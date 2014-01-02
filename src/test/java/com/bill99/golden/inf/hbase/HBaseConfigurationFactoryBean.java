package com.bill99.golden.inf.hbase;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.FactoryBean;

/**
 * Creates HBase Configuration for the given HBase master.
 * 
 * @author Vaibhav Puranik
 * @version $Id: HBaseConfigurationFactoryBean.java 3458 2009-07-24 21:29:59Z
 *          vaibhav $
 */
public class HBaseConfigurationFactoryBean implements FactoryBean {

    /**
     * You can set various hbase client properties. hbase.zookeeper.quorum must
     * be set.
     */
    private Properties hbaseProperties;

    public Object getObject() throws Exception {
        if (hbaseProperties != null) {
            Configuration config = HBaseConfiguration.create();
            String propertyName;
            for (Enumeration enm = hbaseProperties.propertyNames(); enm.hasMoreElements();) {
                propertyName = (String) enm.nextElement();
                config.set(propertyName, hbaseProperties.getProperty(propertyName));
            }
            return config;
        } else {
            throw new RuntimeException("hbase properties cannot be null");
        }
    }

    public Class getObjectType() {
        return HBaseConfiguration.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public Properties getHbaseProperties() {
        return hbaseProperties;
    }

    public void setHbaseProperties(Properties hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }
}

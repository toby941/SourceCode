package com.sourcecode.rpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import com.sourcecode.rpc.annotation.XmlRpcMethod;
import com.sourcecode.rpc.annotation.XmlRpcService;

/**
 * SpringPropertyHandlerMapping.java
 * 
 * @author baojun
 */
public class SpringPropertyHandlerMapping extends PropertyHandlerMapping {

    public void addHandler(boolean detectServersInAncestorContexts, final ApplicationContext context) throws XmlRpcException {
        Assert.notNull(context, "context must not be null!");
        String[] beanNames =
                (detectServersInAncestorContexts ? BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Object.class) : context
                        .getBeanNamesForType(Object.class));
        for (String beanName : beanNames) {
            registerPublicMethods(beanName, context);
        }
    }

    protected void registerPublicMethods(String beanName, final ApplicationContext context) throws XmlRpcException {
        Class<?> serviceType = context.getType(beanName);
        XmlRpcService service = AnnotationUtils.findAnnotation(serviceType, XmlRpcService.class);
        if (service == null && context instanceof ConfigurableApplicationContext && context.containsBeanDefinition(beanName)) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext) context;
            BeanDefinition bd = cac.getBeanFactory().getMergedBeanDefinition(beanName);
            if (bd instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
                if (abd.hasBeanClass()) {
                    Class<?> beanClass = abd.getBeanClass();
                    serviceType = beanClass;// 得到被代理对象
                    service = AnnotationUtils.findAnnotation(beanClass, XmlRpcService.class);
                }
            }
        }
        if (service != null) {
            Map map = new HashMap();
            Method[] methods = serviceType.getMethods();
            for (Method method : methods) {
                if (!isHandlerMethod(service.useMethodAnnotation(), method)) {
                    continue;
                }
                String serviceName = StringUtils.isEmpty(service.value()) ? beanName : service.value();
                String name = serviceName + "." + method.getName();
                Method[] mArray;
                Method[] oldMArray = (Method[]) map.get(name);
                if (oldMArray == null) {
                    mArray = new Method[] { method };
                } else {
                    mArray = new Method[oldMArray.length + 1];
                    System.arraycopy(oldMArray, 0, mArray, 0, oldMArray.length);
                    mArray[oldMArray.length] = method;
                }
                map.put(name, mArray);
            }
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Method[] mArray = (Method[]) entry.getValue();
                handlerMap.put(name, newXmlRpcHandler(context.getBean(beanName), mArray));
                System.out.println("add handler bean :" + beanName);
            }
        }
    }

    protected XmlRpcHandler newXmlRpcHandler(final Object bean, final Method[] pMethods) throws XmlRpcException {
        String[][] sig = getSignature(pMethods);
        String help = getMethodHelp(bean.getClass(), pMethods);
        if (sig == null || help == null) {
            return new SpringXmlRpcHandler(this, getTypeConverterFactory(), bean, pMethods);
        }
        return new SpringReflectiveXmlRpcMetaDataHandler(this, getTypeConverterFactory(), bean, pMethods, sig, help);
    }

    protected boolean isHandlerMethod(boolean useMethodAnnotation, Method method) {
        if (useMethodAnnotation) {
            XmlRpcMethod xmlRpcMethod = AnnotationUtils.findAnnotation(method, XmlRpcMethod.class);
            if (null == xmlRpcMethod) {
                return false;
            }
        }
        return super.isHandlerMethod(method);
    }
}

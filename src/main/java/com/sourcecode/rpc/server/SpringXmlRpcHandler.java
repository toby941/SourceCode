package com.sourcecode.rpc.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.TypeConverter;
import org.apache.xmlrpc.common.TypeConverterFactory;
import org.apache.xmlrpc.common.XmlRpcInvocationException;
import org.apache.xmlrpc.common.XmlRpcNotAuthorizedException;
import org.apache.xmlrpc.metadata.Util;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping.AuthenticationHandler;

/**
 * SpringXmlRpcHandler.java
 * 
 * @author baojun 2012-4-5
 */
public class SpringXmlRpcHandler implements XmlRpcHandler {

    private static class MethodData {

        final Method method;
        final TypeConverter[] typeConverters;

        @SuppressWarnings("unchecked")
        MethodData(Method pMethod, TypeConverterFactory pTypeConverterFactory) {
            method = pMethod;
            Class[] paramClasses = method.getParameterTypes();
            typeConverters = new TypeConverter[paramClasses.length];
            for (int i = 0; i < paramClasses.length; i++) {
                typeConverters[i] = pTypeConverterFactory.getTypeConverter(paramClasses[i]);
            }
        }
    }

    private final AbstractReflectiveHandlerMapping mapping;
    private final MethodData[] methods;
    private final Object bean;

    public SpringXmlRpcHandler(AbstractReflectiveHandlerMapping pMapping, TypeConverterFactory pTypeConverterFactory, Object bean, Method[] pMethods) {
        mapping = pMapping;
        this.bean = bean;
        methods = new MethodData[pMethods.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = new MethodData(pMethods[i], pTypeConverterFactory);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.xmlrpc.XmlRpcHandler#execute(org.apache.xmlrpc.XmlRpcRequest)
     */
    public Object execute(XmlRpcRequest request) throws XmlRpcException {
        AuthenticationHandler authHandler = mapping.getAuthenticationHandler();
        if (authHandler != null && !authHandler.isAuthorized(request)) {
            throw new XmlRpcNotAuthorizedException("Not authorized");
        }
        Object[] args = new Object[request.getParameterCount()];
        for (int j = 0; j < args.length; j++) {
            args[j] = request.getParameter(j);
        }
        for (int i = 0; i < methods.length; i++) {
            MethodData methodData = methods[i];
            TypeConverter[] converters = methodData.typeConverters;
            if (args.length == converters.length) {
                boolean matching = true;
                for (int j = 0; j < args.length; j++) {
                    if (!converters[j].isConvertable(args[j])) {
                        matching = false;
                        break;
                    }
                }
                if (matching) {
                    for (int j = 0; j < args.length; j++) {
                        args[j] = converters[j].convert(args[j]);
                    }
                    return invoke(bean, methodData.method, args);
                }
            }
        }
        throw new XmlRpcException("No method matching arguments: " + Util.getSignature(args));
    }

    private Object invoke(Object pInstance, Method pMethod, Object[] pArgs) throws XmlRpcException {
        try {
            return pMethod.invoke(pInstance, pArgs);
        } catch (IllegalAccessException e) {
            throw new XmlRpcException("Illegal access to method " + pMethod.getName() + " in class " + bean.getClass().getName(), e);
        } catch (IllegalArgumentException e) {
            throw new XmlRpcException("Illegal argument for method " + pMethod.getName() + " in class " + bean.getClass().getName(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof XmlRpcException) {
                throw (XmlRpcException) t;
            }
            throw new XmlRpcInvocationException("Failed to invoke method " + pMethod.getName() + " in class " + bean.getClass().getName() + ": "
                    + t.getMessage(), t);
        }
    }
}

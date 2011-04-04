package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.jboss.weld.environment.osgi.OSGiLite;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class DynamicServiceHandler implements InvocationHandler {

    private final String name;

    public DynamicServiceHandler(String name) {
        this.name = name;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceReference reference = OSGiLite.registry().getServiceReference(name);
        Object instanceToUse = OSGiLite.registry().getService(reference);
        try {
            return method.invoke(instanceToUse, args);
        } finally {
            OSGiLite.registry().ungetService(reference);
        }
    }
}

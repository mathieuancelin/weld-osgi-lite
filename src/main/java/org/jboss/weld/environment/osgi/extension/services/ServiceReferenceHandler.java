package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.jboss.weld.environment.osgi.OSGiLite;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceReferenceHandler implements InvocationHandler {

    private final ServiceReference ref;

    public ServiceReferenceHandler(ServiceReference ref) {
        this.ref = ref;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object instanceToUse = OSGiLite.registry().getService(ref);
        try {
            return method.invoke(instanceToUse, args);
        } finally {
            OSGiLite.registry().ungetService(ref);
        }
    }
}

package org.jboss.weld.environment.osgi.extension.services;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class DynamicServiceHandler implements InvocationHandler {

    private final String name;
    private final PojoServiceRegistry registry;

    public DynamicServiceHandler(String name, PojoServiceRegistry registry) {
        this.name = name;
        this.registry = registry;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceReference reference = registry.getServiceReference(name);
        Object instanceToUse = registry.getService(reference);
        try {
            return method.invoke(instanceToUse, args);
        } finally {
            registry.ungetService(reference);
        }
    }
}

package org.jboss.weld.environment.osgi.extension.services;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import org.jboss.weld.environment.osgi.WeldOSGiLite;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServiceImpl<T> implements Service<T> {

    private final Class serviceClass;
    private final Class declaringClass;
    private final String serviceName;
    private final PojoServiceRegistry registry;
    private T service;

    public ServiceImpl(Type t, Class declaring, PojoServiceRegistry registry) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = declaring;
        this.registry = registry;
    }

    public ServiceImpl(Type t, Bundle bundle, PojoServiceRegistry registry) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = null;
        this.registry = registry;
    }

    @Override
    public T get() {
        if (service == null) {
            try {
                populateService();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return service;
    }

    private void populateService() throws Exception {
        ServiceReference ref = registry.getServiceReference(serviceName);
        if (ref != null) {
            if (!serviceClass.isInterface()) {
                service = (T) registry.getService(ref);
            } else {
                service = (T) Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class[]{(Class) serviceClass},
                            new DynamicServiceHandler(serviceName, registry));
            }
        } else {
            throw new IllegalStateException("Can't load service from OSGi registry : " + serviceName);
        }
    }

}

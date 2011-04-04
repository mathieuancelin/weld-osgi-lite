package org.jboss.weld.environment.osgi.extension.services;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jboss.weld.environment.osgi.OSGiLite;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServicesImpl<T> implements Services<T> {

    private final Class serviceClass;
    private final Class declaringClass;
    private final String serviceName;

    private List<T> services = new ArrayList<T>();

    public ServicesImpl(Type t, Class declaring) {
        serviceClass = (Class) t;
        serviceName = serviceClass.getName();
        declaringClass = declaring;
    }

    @Override
    public Iterator<T> iterator() {
        try {
            populateServiceRef();
        } catch (Exception ex) {
            ex.printStackTrace();
            services = Collections.emptyList();
        }
        return services.iterator();
    }

    private void populateServiceRef() throws Exception {
        services.clear();
        
        ServiceReference[] refs = OSGiLite.registry().getServiceReferences(serviceName, null);
        if (refs != null) {
            for (ServiceReference ref : refs) {
                if (!serviceClass.isInterface()) {
                    services.add((T) OSGiLite.registry().getService(ref));
                } else {
                    services.add((T) Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class[]{(Class) serviceClass},
                                new ServiceReferenceHandler(ref)));
                }
            }
        } else {
            services = Collections.emptyList();
        }
    }
}

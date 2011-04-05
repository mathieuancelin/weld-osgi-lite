package org.jboss.weld.environment.osgi;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.weld.environment.osgi.api.extension.OSGiService;
import org.jboss.weld.environment.osgi.api.extension.Registrations;
import org.jboss.weld.environment.osgi.api.extension.Services;
import org.jboss.weld.environment.osgi.api.extension.Specification;
import org.jboss.weld.environment.osgi.api.extension.events.AbstractServiceEvent.TypedService;
import org.jboss.weld.environment.osgi.api.extension.events.ContainerInitialized;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceArrival;
import org.jboss.weld.environment.osgi.api.extension.events.ServiceDeparture;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class GreetingBean {

    @Inject @OSGiService GreetingService greetingService;
    @Inject Services<GreetingService> services;
    @Inject PojoServiceRegistry registry;
    @Inject Registrations<GreetingService> registrations;

    public void bindService(@Observes @Specification(GreetingService.class) ServiceArrival event) {
        TypedService<GreetingService> newService = event.type(GreetingService.class);
        System.out.println("A new language is available : " + newService.getService().languageName());
        newService.ungetService();
    }

    public void unbindService(@Observes @Specification(GreetingService.class) ServiceDeparture event) {
        System.out.println("A language is gone, available languages are : ");
        int i = 1;
        for (GreetingService service : services) {
            System.out.println(i + ": " + service.languageName());
            i++;
        }
    }

    public void onStartup(@Observes ContainerInitialized event) throws Exception {
        System.out.println("Greeting bean started ...");
        ServiceReference[] refs = registry.getServiceReferences(GreetingService.class.getName(), null);
        int size = 0;
        if (refs != null) {
            size = refs.length;
        }
        System.out.println("Service count : " + size);
    }

    public void callService(@Observes SayHelloEvent event) {
        for (GreetingService greet : services) {
            System.out.println(greet.sayHello(event.getName()));
        }
    }

    public Registrations<GreetingService> getRegistrations() {
        return registrations;
    }
}

package org.jboss.weld.environment.osgi;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.jboss.weld.environment.osgi.extension.services.RegistrationsHolder;
import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldOSGiLite {

    public static ThreadLocal<PojoServiceRegistry> current = new ThreadLocal<PojoServiceRegistry>();

    private PojoServiceRegistry registry;

    private Weld container;

    private AtomicBoolean started;

    private Activator activator;

    private WeldOSGiLite() {
        started = new AtomicBoolean(false);
    }

    public static WeldOSGiLite start() {
        final WeldOSGiLite weldLite = new WeldOSGiLite();
        weldLite.activator = new Activator();
        try {
            ServiceLoader<PojoServiceRegistryFactory> loader
                    = ServiceLoader.load(PojoServiceRegistryFactory.class);
            weldLite.registry = loader.iterator().next().newPojoServiceRegistry(new HashMap());
            current.set(weldLite.registry);
            final BundleContext context = weldLite.registry.getBundleContext();
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        weldLite.stop();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            weldLite.activator.start(context);
            weldLite.container = weldLite.activator.getContainer();
            weldLite.started.set(true);
            current.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weldLite;
    }

    public void stop() {
        synchronized (this) {
            if (started.get()) {
                started.set(false);
                try {
                    instance().select(RegistrationsHolder.class).get().clear();
                    activator.stop(registry.getBundleContext());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public BeanManager beanManager() {
        return container.getBeanManager();
    }

    public Event event() {
        return container.getInstance().select(Event.class).get();
    }

    public Instance<Object> instance() {
        return container.getInstance();
    }
}

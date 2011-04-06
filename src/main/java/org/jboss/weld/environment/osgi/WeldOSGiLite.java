package org.jboss.weld.environment.osgi;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Provider;
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

    private Map<Class<?>, Beantype<?>> types = new HashMap<Class<?>, Beantype<?>>();

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
                for (Beantype<?> type : types.values()) {
                    type.destroy();
                }
                try {
                    instance().select(RegistrationsHolder.class).get().clear();
                    activator.stop(registry.getBundleContext());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public <T> void registerNewType(Class<T> type) {
        if (!types.containsKey(type)) {
            types.put(type, new Beantype<T>(type, beanManager()));
        }
    }

    public <T> Provider<T> newTypeInstance(Class<T> type) {
        if (!types.containsKey(type)) {
            types.put(type, new Beantype<T>(type, beanManager()));
        }
        return (Provider<T>) types.get(type);
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

    private class Beantype<T> implements Provider<T> {

        private final Class<T> clazz;
        private final BeanManager manager;
        private final AnnotatedType annoted;
        private final InjectionTarget it;
        private final CreationalContext<?> cc;
        private Collection<T> instances = new ArrayList<T>();

        public Beantype(Class<T> clazz, BeanManager manager) {
            this.clazz = clazz;
            this.manager = manager;
            annoted = manager.createAnnotatedType(clazz);
            it = beanManager().createInjectionTarget(annoted);
            cc = beanManager().createCreationalContext(null);
        }

        public void destroy() {
            for (T instance : instances) {
                it.preDestroy(instance);
                it.dispose(instance);
            }
            cc.release();
        }

        @Override
        public T get() {
            T instance = (T) it.produce(cc);
            it.inject(instance, cc);
            it.postConstruct(instance);
            instances.add(instance);
            return instance;
        }
    }
}

package org.jboss.weld.environment.osgi;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
import java.util.HashMap;
import java.util.ServiceLoader;
import org.jboss.weld.bootstrap.api.Singleton;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiLite {

    private static Singleton<PojoServiceRegistry> registry;

    static {
        registry = SingletonProvider.instance().create(PojoServiceRegistry.class);
    }

    public static PojoServiceRegistry registry() {
        return registry.get();
    }

    public static Weld startWeldOSGiLite() {
        final Activator activator = new Activator();
        try {
            ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);
            PojoServiceRegistry pojoregistry = loader.iterator().next().newPojoServiceRegistry(new HashMap());
            registry.set(pojoregistry);
            final BundleContext context = pojoregistry.getBundleContext();
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        activator.stop(context);
                        registry.clear();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            activator.start(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activator.getContainer();
    }
}

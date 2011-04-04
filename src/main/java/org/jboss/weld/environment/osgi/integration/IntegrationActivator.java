package org.jboss.weld.environment.osgi.integration;

import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.osgi.framework.*;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:27
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationActivator implements BundleActivator, BundleListener {

    private Map<Long, Holder> managed;

    @Override
    public void start(BundleContext context) throws Exception {

        managed = new HashMap<Long, Holder>();

        for (Bundle bundle : context.getBundles()) {
            if (Bundle.ACTIVE == bundle.getState()) {
                startManagement(bundle);
            }
        }

        context.addBundleListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        for (Bundle bundle : context.getBundles()) {
            Holder holder = managed.get(bundle.getBundleId());
            if (holder != null) {
                stopManagement(holder.bundle);
            }
        }

        SingletonProvider.reset();
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                startManagement(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                stopManagement(event.getBundle());
                break;
        }
    }

    private void stopManagement(Bundle bundle) {
        Holder holder = managed.get(bundle.getBundleId());
        if (holder != null) {
            Collection<ServiceRegistration> regs = holder.registrations;
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {
                    // Ignore
                }
            }
            holder.container.shutdown();
            managed.remove(bundle.getBundleId());
        }
    }

    private void startManagement(Bundle bundle) {
        //System.out.println("Starting management for bundle " + bundle);
        Weld weld = new Weld(bundle);
        weld.initialize();

        if (weld.isStarted()) {
            
            Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

            BundleContext bundleContext = bundle.getBundleContext();
            try {
                regs.add(
                        bundleContext.registerService(Event.class.getName(),
                                              weld.getEvent(),
                                              null));

                regs.add(
                        bundleContext.registerService(BeanManager.class.getName(),
                                weld.getBeanManager(),
                                null));

                regs.add(
                        bundleContext.registerService(Instance.class.getName(),
                                weld.getInstance(),
                                null));
            } catch (Throwable t) {
                // Ignore
            }
            Holder holder = new Holder();
            holder.container = weld;
            holder.registrations = regs;
            holder.bundle = bundle;
            managed.put(bundle.getBundleId(), holder);
        }
    }

    public Weld getContainer() {
        return managed.values().iterator().next().container;
    }

    private static class Holder {
        Bundle bundle;
        Weld container;
        Collection<ServiceRegistration> registrations;
    }
}

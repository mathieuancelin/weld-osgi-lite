package org.jboss.weld.environment.osgi.integration;

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

    private Holder managed;

    @Override
    public void start(BundleContext context) throws Exception {
        managed = new Holder();
        Bundle bundle = context.getBundle();
        if (Bundle.ACTIVE == bundle.getState()) {
            startManagement(bundle);
        }
        context.addBundleListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stopManagement(context.getBundle());
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
        if (managed != null) {
            Collection<ServiceRegistration> regs = managed.registrations;
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {
                    // Ignore
                }
            }
            managed.container.shutdown();
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
            if (managed != null) {
                managed = new Holder();
            }
            managed.container = weld;
            managed.registrations = regs;
            managed.bundle = bundle;
        }
    }

    public Weld getContainer() {
        return managed.container;
    }

    private static class Holder {
        Bundle bundle;
        Weld container;
        Collection<ServiceRegistration> registrations;
    }
}

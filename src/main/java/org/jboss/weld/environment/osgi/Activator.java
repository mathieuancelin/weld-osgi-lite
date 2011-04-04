package org.jboss.weld.environment.osgi;

import org.jboss.weld.environment.osgi.extension.ExtensionActivator;
import org.jboss.weld.environment.osgi.integration.IntegrationActivator;
import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class Activator implements BundleActivator {

    private IntegrationActivator integration = new IntegrationActivator();
    private ExtensionActivator extension = new ExtensionActivator();

    @Override
    public void start(BundleContext context) throws Exception {
        integration.start(context);
        extension.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        extension.stop(context);
        integration.stop(context);
    }

    public Weld getContainer() {
        return integration.getContainer();
    }
}

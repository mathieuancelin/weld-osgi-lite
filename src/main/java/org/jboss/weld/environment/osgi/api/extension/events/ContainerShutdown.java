package org.jboss.weld.environment.osgi.api.extension.events;

import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ContainerShutdown {

    private BundleContext bundleContext;

    public ContainerShutdown(final BundleContext context) {
        this.bundleContext = context;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}

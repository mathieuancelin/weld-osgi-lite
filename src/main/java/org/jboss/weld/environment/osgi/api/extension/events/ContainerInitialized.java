package org.jboss.weld.environment.osgi.api.extension.events;

import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ContainerInitialized {

    private BundleContext bundleContext;

    public ContainerInitialized(final BundleContext context) {
        this.bundleContext = context;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}

package org.jboss.weld.environment.osgi.extension.services;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@ApplicationScoped
public class RegistryHolder {

    private PojoServiceRegistry registry;

    public PojoServiceRegistry getRegistry() {
        return registry;
    }

    void setRegistry(PojoServiceRegistry registry) {
        this.registry = registry;
    }
}

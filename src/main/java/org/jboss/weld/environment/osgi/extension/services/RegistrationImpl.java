package org.jboss.weld.environment.osgi.extension.services;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import org.jboss.weld.environment.osgi.api.extension.Service;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class RegistrationImpl<T> implements Registration<T> {

    private final ServiceRegistration reg;
    private final Class<T> contract;
    private final PojoServiceRegistry registry;
    private final RegistrationsHolder holder;

    public RegistrationImpl(Class<T> contract, 
            ServiceRegistration reg, PojoServiceRegistry registry, RegistrationsHolder holder) {
        this.reg = reg;
        this.contract = contract;
        this.registry = registry;
        this.holder = holder;
    }

    @Override
    public void unregister() {
        holder.removeRegistration(reg);
        reg.unregister();
    }

    @Override
    public <T> Service<T> getServiceReference() {
        return new ServiceImpl<T>(contract, contract, registry);
    }
}

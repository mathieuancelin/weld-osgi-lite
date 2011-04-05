/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.weld.environment.osgi.extension.services;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import org.jboss.weld.environment.osgi.api.extension.Registrations;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class RegistrationsImpl<T> implements Registrations<T> {

    private final Class<T> t;

    private List<Registration<T>> registrations = new ArrayList<Registration<T>>();

    @Inject
    private PojoServiceRegistry registry;

    @Inject
    private RegistrationsHolder holder;

    public RegistrationsImpl(Type t) {
        this.t = (Class<T>) t;
    }

    @Override
    public Iterator<Registration<T>> iterator() {
        populate();
        return registrations.iterator();
    }

    @Override
    public int size() {
        List<ServiceRegistration> regs = holder.getRegistrations();
        return regs.size();
    }

    private void populate() {
        registrations.clear();
        try {
            List<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                registrations.add(new RegistrationImpl<T>(t, reg, registry, holder));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

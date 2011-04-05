package org.jboss.weld.environment.osgi.extension.services;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import java.lang.reflect.ParameterizedType;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.jboss.weld.environment.osgi.WeldOSGiLite;

/**
 * Producers for Specific injected types;
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class ServicesProducer {

    @Produces
    public <T> ServicesImpl<T> getOSGiServices(RegistryHolder holder, InjectionPoint p) {
        if (holder.getRegistry() == null) {
            holder.setRegistry(WeldOSGiLite.current.get());
        }
        return new ServicesImpl<T>(((ParameterizedType)p.getType()).getActualTypeArguments()[0],
                p.getMember().getDeclaringClass(), holder.getRegistry());
    }

    @Produces
    public <T> ServiceImpl<T> getOSGiService(RegistryHolder holder, InjectionPoint p) {
        if (holder.getRegistry() == null) {
            holder.setRegistry(WeldOSGiLite.current.get());
        }
        return new ServiceImpl<T>(((ParameterizedType)p.getType()).getActualTypeArguments()[0],
                p.getMember().getDeclaringClass(), holder.getRegistry());
    }

    @Produces
    public PojoServiceRegistry getRegistry(RegistryHolder holder) {
        if (holder.getRegistry() == null) {
            holder.setRegistry(WeldOSGiLite.current.get());
        }
        return holder.getRegistry();
    }
}

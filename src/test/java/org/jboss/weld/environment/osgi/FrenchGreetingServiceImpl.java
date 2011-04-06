package org.jboss.weld.environment.osgi;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.weld.environment.osgi.api.extension.Publish;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Publish
@ApplicationScoped
public class FrenchGreetingServiceImpl implements GreetingService {

    @Override
    public String languageName() {
        return "French";
    }

    @Override
    public String sayHello(String name) {
        return "Bonjour " + name +  "!";
    }

    @Override
    public Object getInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

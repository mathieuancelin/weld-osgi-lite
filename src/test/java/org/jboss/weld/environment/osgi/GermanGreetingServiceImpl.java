package org.jboss.weld.environment.osgi;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class GermanGreetingServiceImpl implements GreetingService {

    @Override
    public String languageName() {
        return "German";
    }

    @Override
    public String sayHello(String name) {
        return "Hallo " + name +  "!";
    }

    @Override
    public Object getInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

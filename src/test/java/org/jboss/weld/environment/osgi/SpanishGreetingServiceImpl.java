package org.jboss.weld.environment.osgi;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class SpanishGreetingServiceImpl implements GreetingService {

    @Override
    public String languageName() {
        return "Spanish";
    }

    @Override
    public String sayHello(String name) {
        return "Hola " + name +  "!";
    }

    @Override
    public Object getInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

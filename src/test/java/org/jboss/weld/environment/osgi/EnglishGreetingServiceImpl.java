package org.jboss.weld.environment.osgi;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class EnglishGreetingServiceImpl implements GreetingService {

    @Override
    public String languageName() {
        return "English";
    }

    @Override
    public String sayHello(String name) {
        return "Hello " + name +  "!";
    }

    @Override
    public Object getInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

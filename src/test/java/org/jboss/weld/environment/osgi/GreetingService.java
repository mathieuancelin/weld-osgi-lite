package org.jboss.weld.environment.osgi;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface GreetingService {

    String languageName();
    String sayHello(String name);
}

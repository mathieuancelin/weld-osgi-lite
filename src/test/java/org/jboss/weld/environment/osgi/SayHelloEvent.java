package org.jboss.weld.environment.osgi;

/**
 *
 * @author mathieuancelin
 */
public class SayHelloEvent {

    final String name;

    public SayHelloEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

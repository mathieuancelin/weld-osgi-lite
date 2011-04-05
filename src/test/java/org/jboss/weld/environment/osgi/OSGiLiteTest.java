package org.jboss.weld.environment.osgi;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import javax.enterprise.event.Event;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiLiteTest {

    private WeldOSGiLite weld;
    private WeldOSGiLite weld2;

    @Before
    public void setUp() {
        weld = WeldOSGiLite.start();
        weld2 = WeldOSGiLite.start();
    }

    @Test
    public void dummyTest() throws Exception {
        Event<SayHelloEvent> eventManager = weld.event().select(SayHelloEvent.class);
        SayHelloEvent event = new SayHelloEvent("Mathieu");
        PojoServiceRegistry registry = weld.instance().select(PojoServiceRegistry.class).get();

        EnglishGreetingServiceImpl english = weld.instance().select(EnglishGreetingServiceImpl.class).get();
        GermanGreetingServiceImpl german = weld.instance().select(GermanGreetingServiceImpl.class).get();
        SpanishGreetingServiceImpl spanish = weld.instance().select(SpanishGreetingServiceImpl.class).get();

        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 1);
        eventManager.fire(event);
        ServiceRegistration englishReg = registry.registerService(GreetingService.class.getName(),
                english, null);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 2);
        eventManager.fire(event);
        ServiceRegistration germanReg = registry.registerService(GreetingService.class.getName(),
                german, null);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 3);
        eventManager.fire(event);
        ServiceRegistration spannishReg = registry.registerService(GreetingService.class.getName(),
                spanish, null);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 4);
        eventManager.fire(event);

        germanReg.unregister();
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 3);
        eventManager.fire(event);
        spannishReg.unregister();
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 2);
        eventManager.fire(event);
        englishReg.unregister();
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class.getName(), null).length, 1);
        eventManager.fire(event);
    }
}

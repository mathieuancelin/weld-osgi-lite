package org.jboss.weld.environment.osgi;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import javax.enterprise.event.Event;
import org.jboss.weld.environment.osgi.api.extension.Registration;
import org.jboss.weld.environment.osgi.api.extension.ServiceRegistry;
import org.junit.After;
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

    @Before
    public void setUp() {
        weld = WeldOSGiLite.start();
    }

    @After
    public void stop() {
        weld.stop();
    }

    @Test
    public void pojoRegistryTest() throws Exception {
        Event<SayHelloEvent> eventManager = weld.event().select(SayHelloEvent.class);
        SayHelloEvent event = new SayHelloEvent("Mathieu");
        PojoServiceRegistry registry = weld.instance().select(PojoServiceRegistry.class).get();

        EnglishGreetingServiceImpl english = weld.instance().select(EnglishGreetingServiceImpl.class).get();
        GermanGreetingServiceImpl german = weld.instance().select(GermanGreetingServiceImpl.class).get();
        SpanishGreetingServiceImpl spanish = weld.instance().select(SpanishGreetingServiceImpl.class).get();
        GreetingBean bean = weld.instance().select(GreetingBean.class).get();

//        Assert.assertEquals(bean.getRegistrations().size(), 1);
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

    @Test
    public void registryTest() throws Exception {
        Event<SayHelloEvent> eventManager = weld.event().select(SayHelloEvent.class);
        SayHelloEvent event = new SayHelloEvent("Mathieu");
        ServiceRegistry registry = weld.instance().select(ServiceRegistry.class).get();

        EnglishGreetingServiceImpl english = weld.instance().select(EnglishGreetingServiceImpl.class).get();
        GermanGreetingServiceImpl german = weld.instance().select(GermanGreetingServiceImpl.class).get();
        SpanishGreetingServiceImpl spanish = weld.instance().select(SpanishGreetingServiceImpl.class).get();
        GreetingBean bean = weld.instance().select(GreetingBean.class).get();

        //Assert.assertEquals(bean.getRegistrations().size(), 1);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 1);
        eventManager.fire(event);
        Registration<GreetingService> englishReg = registry.registerService(GreetingService.class, english);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 2);
        eventManager.fire(event);
        Registration<GreetingService> germanReg = registry.registerService(GreetingService.class, german);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 3);
        eventManager.fire(event);
        Registration<GreetingService> spannishReg = registry.registerService(GreetingService.class, spanish);
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 4);
        eventManager.fire(event);

        germanReg.unregister();
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 3);
        eventManager.fire(event);
        spannishReg.unregister();
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 2);
        eventManager.fire(event);
        englishReg.unregister();
        Assert.assertEquals(registry.getServiceReferences(GreetingService.class).size(), 1);
        eventManager.fire(event);
    }
}

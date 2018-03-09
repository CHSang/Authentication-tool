package vn.axonactive.authentication.domain.utils;

import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.faces.bean.ManagedBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UtilityTest {
    private List<String> getSupportedPackages() {
        String corePackage = "vn.axonactive.hrtool";
        return Collections.singletonList(corePackage);
    }

    private boolean isTestable(Class<?> clazz) {
        // what should be the condition here???
        return clazz.getName().endsWith("ApiUtils");
    }

    private void callPrivateConstructor(Class<?> clazz) {
        Object instance;
        try {
            Constructor<?> con = clazz.getDeclaredConstructor();
            boolean isManagedBean = Objects.nonNull(clazz.getAnnotation(ManagedBean.class));
            boolean isPrivateModifier = Modifier.isPrivate(con.getModifiers());
            if(!(isManagedBean || isPrivateModifier)) {
                Assert.fail(clazz + " must have private constructor");
            }
            con.setAccessible(true);
            instance = con.newInstance();
            Assert.assertNotNull(clazz + " must have private constructor", instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
            e) {
            Assert.fail();
        }
    }

    private Set<Class<?>> types() {
        Reflections reflections = new Reflections(this.getSupportedPackages(), new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class);
    }

    @Test
    public void should_have_private_constructor() {
        this.types()
            .stream()
            .filter(this::isTestable)
            .forEach(this::callPrivateConstructor);
    }

    @Test
    public void should_be_final() {
        this.types()
            .stream()
            .filter(this::isTestable)
            .forEach(aClass -> Assert.assertTrue(aClass + " must be final!!!", Modifier.isFinal(aClass.getModifiers())));
    }

}

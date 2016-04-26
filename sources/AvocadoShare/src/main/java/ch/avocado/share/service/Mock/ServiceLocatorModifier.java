package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class to modify
 */
public class ServiceLocatorModifier {
    public static <T> void setService(Class<T> clazz,  T locater) throws IllegalAccessException {
        final Field fields[] =  ServiceLocator.class.getDeclaredFields();
        Field servicesField = null;
        for (Field field : fields) {
            if ("services".equals(field.getName())) {
                servicesField = field;
                break;
            }
        }
        if(servicesField == null) {
            throw new RuntimeException("services field not found");
        }
        servicesField.setAccessible(true);
        Map<Type, Object> serviceMap = (Map<Type, Object>) Objects.requireNonNull(servicesField).get(ServiceLocator.class);
        serviceMap.put(clazz, locater);
    }

    /**
     * Restore original handler
     */
    public static void restore() {
        Method registerMethod;
        try {
            registerMethod = ServiceLocator.class.getDeclaredMethod("registerServices", null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("registerMethod not found");
        }
        registerMethod.setAccessible(true);
        try {
            registerMethod.invoke(ServiceLocator.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

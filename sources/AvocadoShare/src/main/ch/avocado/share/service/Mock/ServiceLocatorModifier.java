package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class to modify
 */
public class ServiceLocatorModifier {
    public static <T> void setService(Class<T> clazz,  T locater) throws Exception {
        final Field fields[] =  ServiceLocator.class.getDeclaredFields();
        Field servicesField = null;
        for (int i = 0; i < fields.length; ++i) {
            if ("services".equals(fields[i].getName())) {
                servicesField = fields[i];
                break;
            }
        }
        servicesField.setAccessible(true);
        Map<Type, Object> serviceMap = (Map<Type, Object>) Objects.requireNonNull(servicesField).get(ServiceLocator.class);
        serviceMap.put(clazz, locater);
    }
}

package AvocadoShare.Common;

import AvocadoShare.Model.Exceptions.ServiceNotFoundException;
import AvocadoShare.Service.*;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bergm on 15/03/2016.
 */
public class ServiceLocator {

    private static Map<Type, Object> services;

    static{
        services = new HashMap<>();
        registerServices();
    }

    public static <T> T GetService(Class<T> clazz) throws ServiceNotFoundException {
        if (!services.containsKey(clazz)) throw new ServiceNotFoundException(clazz.toString(), ServiceLocator.class.toString());

        return (T)services.get(clazz);
    }

    private static void registerServices() {
        services.put(IDatabaseConnectionHandler.class, null);
        services.put(IUserDataHandler.class, null);
        services.put(IFileStorageHandler.class, null);
        services.put(ISecurityHandler.class, null);
        services.put(IFileDataHandler.class, null);
    }
}

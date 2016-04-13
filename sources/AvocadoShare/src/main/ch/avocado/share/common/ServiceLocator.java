package ch.avocado.share.common;

import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.Impl.*;
import ch.avocado.share.service.Mock.*;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bergm on 15/03/2016.
 */
public class ServiceLocator {

    private static Map<Type, Object> services;

    static {
        services = new HashMap<>();
        registerServices();
    }

    /**
     * Returns the implementation of the service.
     * @param clazz The required Service class
     * @param <T> The type of the service
     * @return The implemented service
     * @throws ServiceNotFoundException If there is no service with this class.
     */
    public static <T> T getService(Class<T> clazz) throws ServiceNotFoundException {
        if (!services.containsKey(clazz))
            throw new ServiceNotFoundException(clazz.toString(), ServiceLocator.class.toString());

        return (T) services.get(clazz);
    }

    /**
     * Build the service map.
     */
    private static void registerServices() {
        services.put(ISecurityHandler.class, new SecurityHandler());
        services.put(IFileDataHandler.class, new FileDataHandler());
        services.put(IDatabaseConnectionHandler.class, new DatabaseConnectionHandler());
        services.put(IUserDataHandler.class, new UserDataHandler());
        services.put(IFileStorageHandler.class, new FileStorageHandlerMock());
        services.put(IMailingService.class, new MailingService());
        services.put(IGroupDataHandler.class, new GroupDataHandler());
        services.put(ICategoryDataHandler.class, new CategoryDataHandler());
        services.put(IModuleDataHandler.class, new ModuleDataHandler());
    }
}

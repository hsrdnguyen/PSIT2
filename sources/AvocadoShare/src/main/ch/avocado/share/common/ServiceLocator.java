package ch.avocado.share.common;

import ch.avocado.share.model.data.File;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.*;
import ch.avocado.share.service.Impl.DatabaseConnectionHandler;
import ch.avocado.share.service.Impl.FileStorageHandler;
import ch.avocado.share.service.Impl.GroupDataHandler;
import ch.avocado.share.service.Impl.MailingService;
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

    static{
        services = new HashMap<>();
        registerServices();
    }

    public static <T> T getService(Class<T> clazz) throws ServiceNotFoundException {
        if (!services.containsKey(clazz)) throw new ServiceNotFoundException(clazz.toString(), ServiceLocator.class.toString());

        return (T)services.get(clazz);
    }

    private static void registerServices() {
        services.put(IDatabaseConnectionHandler.class, new DatabaseConnectionHandler());
        //services.put(IDatabaseConnectionHandler.class, new DatabaseConnectionHandlerMock());
        services.put(IUserDataHandler.class, new UserDataHandlerMock());
        services.put(IFileStorageHandler.class, new FileStorageHandler());
        services.put(ISecurityHandler.class, new SecurityHandlerMock());
        services.put(IFileDataHandler.class, new FileDataHandlerMock());
        services.put(IMailingService.class, new MailingService());
        services.put(IGroupDataHandler.class, new GroupDataHandler());
    }
}

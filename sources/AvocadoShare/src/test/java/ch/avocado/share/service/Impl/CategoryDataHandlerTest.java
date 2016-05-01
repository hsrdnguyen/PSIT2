package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;


public class CategoryDataHandlerTest {


    private CategoryDataHandler categoryDataHandler;
    private File file;
    private User user;
    private Module module;

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        categoryDataHandler = new CategoryDataHandler();

        user = DummyFactory.newUser(0);
        ServiceLocator.getService(IUserDataHandler.class).addUser(user);

        module = DummyFactory.newModule(0, user);
        ServiceLocator.getService(IModuleDataHandler.class).addModule(module);

        file = DummyFactory.newFile(0, user, module);
        ServiceLocator.getService(IFileDataHandler.class).addFile(file);

    }

    @After
    public void tearDown() throws Exception {

        ServiceLocator.getService(IFileDataHandler.class).deleteFile(file);
        ServiceLocator.getService(IModuleDataHandler.class).deleteModule(module);
        ServiceLocator.getService(IUserDataHandler.class).deleteUser(user);

        ServiceLocatorModifier.restore();
    }

    @Ignore
    @Test
    public void testAddAccessObjectCategories() throws Exception {

    }

    @Ignore
    @Test
    public void testUpdateAccessObjectCategories() throws Exception {

    }

    @Ignore
    @Test
    public void testGetCategoryByName() throws Exception {

    }

    @Ignore
    @Test
    public void testHasCategoryAssignedObject() throws Exception {

    }

    @Ignore
    @Test
    public void testGetAccessObjectAssignedCategories() throws Exception {

    }
}
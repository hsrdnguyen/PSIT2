package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static ch.avocado.share.test.Asserts.assertCategoriesEquals;
import static org.junit.Assert.*;

public class ModuleDataHandlerTest {

    private User owner;
    private User ownerTwo;
    private IUserDataHandler userDataHandler;
    private ModuleDataHandler moduleDataHandler;
    private Stack<String> notDeletedModuleIds;

    public ModuleDataHandlerTest() throws ServiceNotFoundException {
        userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
    }

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        owner = DummyFactory.newUser(1);
        ownerTwo = DummyFactory.newUser(2);

        assertNotNull(userDataHandler.addUser(owner));
        assertNotNull(userDataHandler.addUser(ownerTwo));
        moduleDataHandler = new ModuleDataHandler();
        notDeletedModuleIds = new Stack<>();
    }

    @After
    public void tearDown() throws Exception {
        try {
            userDataHandler.deleteUser(owner);

            userDataHandler.deleteUser(ownerTwo);
            while (!notDeletedModuleIds.isEmpty()) {
                String id = notDeletedModuleIds.pop();
                Module moduleToDelete = moduleDataHandler.getModule(id);
                if (moduleToDelete != null) {
                    moduleDataHandler.deleteModule(moduleToDelete);
                }
            }
        } finally {
            ServiceLocatorModifier.restore();
        }
    }

    private List<Category> getTestCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("File"));
        categories.add(new Category("Something"));
        categories.add(new Category("Another"));
        return categories;
    }

    @Test(expected = NullPointerException.class)
    public void testAddNull() throws Exception {
        moduleDataHandler.addModule(null);
    }

    @Test
    public void testAddAndGetModule() throws Exception {
        String description, name;
        description = "Description";
        name = "testAddAndGetModule.module";
        List<Category> categories = getTestCategories();
        Module module = new Module(owner.getId(), description, name);
        module.setCategories(categories);
        String id = moduleDataHandler.addModule(module);
        assertNotNull(id);
        notDeletedModuleIds.push(id);
        assertNotNull(module.getId());
        assertEquals(id, module.getId());


        IFileDataHandler fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        File fileOne = DummyFactory.newFile(1, owner, module);
        File fileTwo = DummyFactory.newFile(2, owner, module);
        assertNotNull(fileDataHandler.addFile(fileOne));
        assertNotNull(fileDataHandler.addFile(fileTwo));

        Module fetchedModule = moduleDataHandler.getModule(id);
        assertEquals("Fetched name doesn't match", name, fetchedModule.getName());
        assertEquals("Fetched description doesn't match", description, fetchedModule.getDescription());
        assertEquals("Fetched ownerId doesn't match", owner.getId(), fetchedModule.getOwnerId());
        assertEquals(2, fetchedModule.getFileIds().size());
        assertTrue(fetchedModule.getFileIds().contains(fileOne.getId()));
        assertTrue(fetchedModule.getFileIds().contains(fileTwo.getId()));
        assertCategoriesEquals(categories, fetchedModule.getCategoryList());

        fileDataHandler.deleteFile(fileOne);
        fileDataHandler.deleteFile(fileTwo);

    }


    @Test(expected = NullPointerException.class)
    public void testDeleteNull() throws Exception {
        moduleDataHandler.deleteModule(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteModuleWithIdNull() throws Exception {
        String description, name;
        description = "Description";
        name = "testAddAndGetModule.module";
        Module module = new Module(owner.getId(), description, name);
        moduleDataHandler.deleteModule(module);
    }

    @Test
    public void testDeleteModule() throws Exception {
        String description, name;
        description = "Description";
        name = "<-- Unexisting Module -- >";
        Module module = new Module(owner.getId(), description, name);
        String id = moduleDataHandler.addModule(module);
        assertNotNull(id);
        notDeletedModuleIds.push(id);
        assertNotNull(module.getId());
        assertNotNull(moduleDataHandler.getModule(id));

        moduleDataHandler.deleteModule(module);
        try {
            moduleDataHandler.getModule(id);
            fail();
        } catch (ObjectNotFoundException ignored) {
        }
        notDeletedModuleIds.pop();
        try {
            moduleDataHandler.updateModule(module);
            fail();
        } catch (ObjectNotFoundException ignored) {
        }
    }

    @Test
    public void testGetModules() throws Exception {
        List<String> ids = new ArrayList<>();
        String description, name;
        description = "Description";
        name = "<-- Unexisting Module -- >";
        Module module = new Module(owner.getId(), description, name);
        String id = moduleDataHandler.addModule(module);
        notDeletedModuleIds.push(id);
        assertNotNull(id);
        ids.add(id);

        description = "Description";
        name = "<-- Unexisting Module Two -- >";
        module = new Module(owner.getId(), description, name);
        id = moduleDataHandler.addModule(module);
        assertNotNull(id);
        notDeletedModuleIds.push(id);
        ids.add(id);

        ids.add(owner.getId());

        List<Module> modules = moduleDataHandler.getModules(ids);
        assertEquals(2, modules.size());
    }

    @Test
    public void testUpdateModule() throws Exception {
        String description, name;
        description = "Description";
        name = "<-- Unexisting Module -- >";
        List<Category> categories = getTestCategories();
        Module module = new Module(owner.getId(), description, name);
        module.setCategories(categories);
        String id = moduleDataHandler.addModule(module);
        assertNotNull(id);
        notDeletedModuleIds.push(id);
        assertNotNull(module.getId());
        assertEquals(id, module.getId());

        Module updatedModule = moduleDataHandler.getModule(id);
        assertNotNull(updatedModule);

        name += " NEW";
        description += "NEW DESCRIPTION";
        categories.remove(0);
        categories.add(new Category("Neu"));
        updatedModule.setName(name);
        updatedModule.setDescription(description);
        updatedModule.setCategories(categories);
        updatedModule.setOwnerId(ownerTwo.getId());

        moduleDataHandler.updateModule(updatedModule);

        Module fetchedModule = moduleDataHandler.getModule(id);
        assertEquals("Fetched name doesn't match", name, fetchedModule.getName());
        assertEquals("Fetched description doesn't match", description, fetchedModule.getDescription());
        assertEquals("Fetched ownerId doesn't match", ownerTwo.getId(), fetchedModule.getOwnerId());
        assertCategoriesEquals(categories, fetchedModule.getCategoryList());
    }
}
package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.FileDataHandlerMock;
import ch.avocado.share.service.exceptions.DataHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 16.04.16.
 */
public class FileDataHandlerTest {

    private FileDataHandler fileDataHandler;
    private IUserDataHandler userDataHandler;
    private IModuleDataHandler moduleDataHandler;
    private Module module;
    private User user;

    private Stack<String> notDeletedIds;


    @Before
    public void setUp() throws Exception {
        fileDataHandler = new FileDataHandler();
        user = new User(UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "Description", new EmailAddress(false, "unexisting_email@zhaw.ch", new EmailAddressVerification(new Date())));
        userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        assertNotNull(userDataHandler.addUser(user));

        String module_name = "Unexisting Module!!!";
        module = new Module(user.getId(), "Description", module_name);
        moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);
        assertNotNull(moduleDataHandler.addModule(module));
        notDeletedIds = new Stack<>();
    }

    @After
    public void tearDown() throws Exception {
        moduleDataHandler.deleteModule(module);
        userDataHandler.deleteUser(user);
        while(!notDeletedIds.isEmpty()) {
            try {
                File file = fileDataHandler.getFile(notDeletedIds.pop());
                if (file != null) {
                    System.out.println("Deleting file: "+ file);
                    fileDataHandler.deleteFile(file);
                }
            } catch (DataHandlerException e ) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testAddAndGetFile() throws Exception {
        // String ownerId, String description, String title, String path, Date lastChanged, String extension, String moduleId
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";
        // add file
        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId());
        String id = fileDataHandler.addFile(file);
        assertNotNull("File could not be added", id);
        notDeletedIds.push(id);
        assertEquals("Id not set in file object after it was successfully added", id, file.getId());

        // get file
        File fetchedFile = fileDataHandler.getFile(id);
        assertNotNull("Could not fetch the added file", fetchedFile);

        assertNotNull("Fetch by title and module failed", fetchedFile);
        assertEquals("Id (", id, fetchedFile.getId());
        assertEquals("ModuleID", module.getId(), fetchedFile.getModuleId());
        assertEquals("Title", title, fetchedFile.getTitle());
        assertEquals("Description", description, fetchedFile.getDescription());
        assertEquals("Path", path, fetchedFile.getPath());
        assertEquals("Last changed", lastChanged, fetchedFile.getLastChanged());
        // TODO: store extension and uncomment test below
        // assertEquals("Extension", extension, file.getExtension());

    }

    @Test
    public void testDeleteFile() throws Exception {
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";
        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId());
        String id = fileDataHandler.addFile(file);
        assertNotNull(fileDataHandler.getFileByTitleAndModule(file.getTitle(), module.getId()));
        assertNotNull(id);
        assertNotNull(file.getId());

        assertTrue(fileDataHandler.deleteFile(file));
        assertNull(fileDataHandler.getFile(file.getId()));
        assertNull(fileDataHandler.getFileByTitleAndModule(file.getTitle(), module.getId()));
    }


    @Test
    public void testGetFiles() throws Exception {
        ArrayList<String> ids = new ArrayList<>();

        String id;
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";

        File fileOne = new File(user.getId(), description, title + "1", path, lastChanged, extension, module.getId());

        // Add first file
        id = fileDataHandler.addFile(fileOne);
        assertNotNull(id);
        assertNotNull(fileOne.getId());
        notDeletedIds.push(id);
        ids.add(id);

        // Add second file
        File fileTwo = new File(user.getId(), description, title + "2", path, lastChanged, extension, module.getId());
        id = fileDataHandler.addFile(fileTwo);
        assertNotNull(id);
        assertNotNull(fileOne.getId());
        notDeletedIds.push(id);
        ids.add(id);

        // Add a non-file id
        ids.add(user.getId());

        List<File> files = fileDataHandler.getFiles(ids);
        assertEquals("list should have two files", 2, files.size());
        assertFalse(files.contains(null));

        for(File file: files) {
            assertTrue(file.getId().equals(fileOne.getId()) || file.getId().equals(fileTwo.getId()));
        }
    }

    @Test
    public void testGetFileByTitleAndModule() throws Exception {
        String id;
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";

        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId());

        id = fileDataHandler.addFile(file);
        assertNotNull(id);
        notDeletedIds.push(id);
        assertNotNull(file.getId());

        File fetchedFile = fileDataHandler.getFileByTitleAndModule(file.getTitle(), module.getId());
        assertNotNull("Fetch by title and module failed", fetchedFile);
        assertEquals("Id (", id, fetchedFile.getId());
        assertEquals("ModuleID", module.getId(), fetchedFile.getModuleId());
        assertEquals("Title", title, fetchedFile.getTitle());
        assertEquals("Description", description, fetchedFile.getDescription());
        assertEquals("Path", path, fetchedFile.getPath());
        assertEquals("Last changed", lastChanged, fetchedFile.getLastChanged());
        // TODO: store extension and uncomment test below
        // assertEquals("Extension", extension, file.getExtension());

    }

    @Test
    public void testUpdateFile() throws Exception {
        
    }

    @Test
    public void testGrantAccess() throws Exception {

    }
}
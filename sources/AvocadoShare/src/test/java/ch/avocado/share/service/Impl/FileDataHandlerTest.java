package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.*;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.test.Asserts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static ch.avocado.share.test.Asserts.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link FileDataHandler}
 */
public class FileDataHandlerTest {

    private FileDataHandler fileDataHandler;
    private IUserDataHandler userDataHandler;
    private IModuleDataHandler moduleDataHandler;
    private Module module;
    private Module moduleTwo;
    private User user;
    private User userTwo;

    private Stack<String> notDeletedIds;

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionHandlerMock.use();
        fileDataHandler = new FileDataHandler();


        userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        String emailOne = "unexisting_email@zhaw.ch";
        String emailTwo = "unexsiting_email2@zhaw.ch";
        user = userDataHandler.getUserByEmailAddress(emailOne);
        userTwo = userDataHandler.getUserByEmailAddress(emailTwo);
        if (user != null) {
            userDataHandler.deleteUser(user);
        }
        if (userTwo != null) {
            userDataHandler.deleteUser(userTwo);
        }

        user = new User(UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "Description", new EmailAddress(false, emailOne, new EmailAddressVerification(new Date())));
        userTwo = new User(UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "Description", new EmailAddress(false, emailTwo, new EmailAddressVerification(new Date())));

        assertNotNull(userDataHandler.addUser(user));
        assertNotNull(userDataHandler.addUser(userTwo));


        String module_name = "Unexisting Module!!!";
        module = new Module(user.getId(), "Description", module_name);
        moduleTwo = new Module(user.getId(), "Description", module_name + "TWO!");
        moduleDataHandler = ServiceLocator.getService(IModuleDataHandler.class);

        deleteModules();

        assertNotNull(moduleDataHandler.addModule(module));
        assertNotNull(moduleDataHandler.addModule(moduleTwo));
        notDeletedIds = new Stack<>();
    }

    private void deleteModules() throws DataHandlerException {
        if (module != null && module.getId() != null) {
            if (module.getId() != null) {
                moduleDataHandler.deleteModule(module);
            }
        }
        if (moduleTwo != null) {
            if (moduleTwo.getId() != null) {
                moduleDataHandler.deleteModule(moduleTwo);
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        try {
            deleteModules();
            userDataHandler.deleteUser(user);
            userDataHandler.deleteUser(userTwo);
            while (!notDeletedIds.isEmpty()) {
                try {
                    File file = fileDataHandler.getFile(notDeletedIds.pop());
                    if (file != null) {
                        System.out.println("Deleting file: " + file);
                        fileDataHandler.deleteFile(file);
                    }
                } catch (DataHandlerException e) {
                    e.printStackTrace();
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

    @Test
    public void testAddAndGetFile() throws Exception {
        // String ownerId, String description, String title, String path, Date lastChanged, String extension, String moduleId
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";
        String mimeType = "image/jpeg";
        List<Category> categories = getTestCategories();

        // add file
        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId(), mimeType);
        file.setCategories(categories);
        String id = fileDataHandler.addFile(file);
        assertNotNull("File could not be added", id);
        notDeletedIds.push(id);
        assertEquals("Id not set in file object after it was successfully added", id, file.getId());

        // get file
        File fetchedFile = fileDataHandler.getFile(id);
        assertNotNull("Could not fetch the added file", fetchedFile);

        assertNotNull("Fetch by title and module failed", fetchedFile);
        assertFilesEqual(file, fetchedFile);
    }

    public static void assertFilesEqual(File expected, File actual) {
        assertNotNull(actual);
        assertNotNull(expected);
        assertEquals("Id ", expected.getId(), actual.getId());
        assertEquals("ModuleID", expected.getModuleId(), actual.getModuleId());
        assertEquals("OwnerId", expected.getOwnerId(), actual.getOwnerId());
        assertEquals("Title", expected.getTitle(), actual.getTitle());
        assertEquals("Description", expected.getDescription(), actual.getDescription());
        assertEquals("MimeType", expected.getMimeType(), actual.getMimeType());
        assertEquals("Path", expected.getPath(), actual.getPath());
        assertEquals("Last changed", expected.getLastChanged(), actual.getLastChanged());
        assertEquals("Extension", expected.getExtension(), actual.getExtension());
        assertCategoriesEquals(expected.getCategories(), actual.getCategories());
    }

    @Test
    public void testDeleteFile() throws Exception {
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";
        String mimeType = "image/jpeg";
        List<Category> categories = getTestCategories();
        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId(), mimeType);
        file.setCategories(categories);
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
        String mimeType = "image/jpeg";

        List<Category> categories = getTestCategories();
        File fileOne = new File(user.getId(), description, title + "1", path, lastChanged, extension, module.getId(), mimeType);
        fileOne.setCategories(categories);
        // Add first file
        id = fileDataHandler.addFile(fileOne);
        assertNotNull(id);
        assertNotNull(fileOne.getId());
        notDeletedIds.push(id);
        ids.add(id);

        // Add second file
        File fileTwo = new File(user.getId(), description, title + "2", path, lastChanged, extension, module.getId(), mimeType);
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

        for (File file : files) {
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
        String mimeType = "image/jpeg";
        List<Category> categories = getTestCategories();

        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId(), mimeType);
        file.setCategories(categories);

        id = fileDataHandler.addFile(file);
        assertNotNull(id);
        notDeletedIds.push(id);
        assertNotNull(file.getId());

        File fetchedFile = fileDataHandler.getFileByTitleAndModule(file.getTitle(), module.getId());
        assertNotNull("Fetch by title and module failed", fetchedFile);
        assertFilesEqual(file, fetchedFile);
    }

    @Test
    public void testUpdateFile() throws Exception {
        String description = "Description";
        String title = "Title";
        String path = "123456";
        Date lastChanged = new Date();
        String extension = ".jpg";
        String mimeType = "image/jpeg";
        List<Category> categories = getTestCategories();
        File file = new File(user.getId(), description, title, path, lastChanged, extension, module.getId(), mimeType);
        file.setCategories(categories);

        assertNotNull(fileDataHandler.addFile(file));
        notDeletedIds.push(file.getId());
        assertNotNull(fileDataHandler.getFile(file.getId()));


        description = description + " new";
        title = title + " new";
        path = path + " new";
        lastChanged = new Date();
        categories = getTestCategories();
        categories.add(new Category("Something new"));
        file.setOwnerId(userTwo.getId());
        file.setPath(path);
        file.setTitle(title);
        file.setDescription(description);
        file.setLastChanged(lastChanged);
        file.setPath(path);
        file.setExtension(".png");
        file.setMimeType("image/png");
        file.setModuleId(moduleTwo.getId());
        file.setCategories(categories);
        assertTrue(fileDataHandler.updateFile(file));

        File fetchedFile = fileDataHandler.getFile(file.getId());
        assertNotNull(fetchedFile);
        assertFilesEqual(file, fetchedFile);
    }
}
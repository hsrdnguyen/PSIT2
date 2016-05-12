package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.sql.RatingConstants;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.Module;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.IModuleDataHandler;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Lion on 06.05.2016.
 */
public class RatingDataHandlerTest {

    private RatingDataHandler ratingDataHandler;
    private IFileDataHandler fileDataHandler;
    private File file;
    private User user1;
    private User user2;
    private Module module;
    private Long fileId;
    private Long user1Id;
    private Long user2Id;

    @Before
    public void setUp() throws IllegalAccessException, DataHandlerException, ServiceNotFoundException {
        DatabaseConnectionHandlerMock.use();
        ratingDataHandler = new RatingDataHandler();

        user1 = DummyFactory.newUser(111);
        IUserDataHandler userDataHandler = ServiceLocator.getService(IUserDataHandler.class);
        user1Id = Long.parseLong(userDataHandler.addUser(user1));

        user2 = DummyFactory.newUser(222);
        user2Id = Long.parseLong(userDataHandler.addUser(user2));

        module = DummyFactory.newModule(0, user1);
        ServiceLocator.getService(IModuleDataHandler.class).addModule(module);

        file = DummyFactory.newFile(0, user1, module);
        fileDataHandler = ServiceLocator.getService(IFileDataHandler.class);
        fileId = Long.parseLong(fileDataHandler.addFile(file));

    }

    @After
    public void tearDown() throws DataHandlerException, ServiceNotFoundException, ObjectNotFoundException {

        ServiceLocator.getService(IFileDataHandler.class).deleteFile(file);
        ServiceLocator.getService(IModuleDataHandler.class).deleteModule(module);
        ServiceLocator.getService(IUserDataHandler.class).deleteUser(user1);
        ServiceLocator.getService(IUserDataHandler.class).deleteUser(user2);

        ServiceLocatorModifier.restore();
    }

    @Test
    public void testAddAndGetOneRatingsForFile() throws DataHandlerException{
        int ratingValue = 2;

        ratingDataHandler.addRating(fileId, user1Id, ratingValue);

        Rating rating = ratingDataHandler.getRatingForObject(fileId);

        //assertEquals("RatedObjectId" ,rating.getRatedObjectId(), fileId, 0.01f);
        assertEquals("NumberOfRatings" ,rating.getNumberOfRatings(), 1);
        assertEquals("RatingValue" ,rating.getRating(), ratingValue, 0.01f);
        assertTrue("HasUserRated", rating.hasUserRated(user1Id));
    }

    @Test
    public void testAddAndGetTwoRatingsForFile() throws DataHandlerException{
        int ratingValue1 = 0;
        int ratingValue2 = 3;

        float expectedRatingValue = (float)((ratingValue1 + ratingValue2) / 2.0f);

        ratingDataHandler.addRating(fileId, user1Id, ratingValue1);
        ratingDataHandler.addRating(fileId, user2Id, ratingValue2);

        Rating rating = ratingDataHandler.getRatingForObject(fileId);

        //assertEquals("RatedObjectId" ,rating.getRatedObjectId(), fileId, 0.01f);
        assertEquals("NumberOfRatings" ,rating.getNumberOfRatings(), 2);
        assertEquals("RatingValue" ,rating.getRating(), expectedRatingValue, 0.01f);
        assertTrue("HasUser1Rated", rating.hasUserRated(user1Id));
        assertTrue("HasUser2Rated", rating.hasUserRated(user2Id));
    }

    @Test
    public void testAddAndGetTwoRatingsForFile_WithGetFile() throws DataHandlerException, ObjectNotFoundException {
        int ratingValue1 = 0;
        int ratingValue2 = 3;

        float expectedRatingValue = (float)((ratingValue1 + ratingValue2) / 2.0f);

        ratingDataHandler.addRating(fileId, user1Id, ratingValue1);
        ratingDataHandler.addRating(fileId, user2Id, ratingValue2);

        File fileFromDb = fileDataHandler.getFile(fileId.toString());
        Rating rating = fileFromDb.getRating();

        //assertEquals("RatedObjectId" ,rating.getRatedObjectId(), fileId, 0.01f);
        assertEquals("NumberOfRatings" ,rating.getNumberOfRatings(), 2);
        assertEquals("RatingValue" ,rating.getRating(), expectedRatingValue, 0.01f);
        assertTrue("HasUser1Rated", rating.hasUserRated(user1Id));
        assertTrue("HasUser2Rated", rating.hasUserRated(user2Id));
    }

    @Test
    public void testAddAndGetRatingForUserAndFile() throws DataHandlerException, ObjectNotFoundException {
        int ratingValue = 2;

        ratingDataHandler.addRating(fileId, user1Id, ratingValue);

        Integer rating = ratingDataHandler.getRatingForUserAndObject(user1Id, fileId);
        assertEquals("Rating FromUser ForObject", rating, ratingValue, 0.01f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinValueForRating() throws DataHandlerException {
        int ratingValue = RatingConstants.MIN_RATING_VALUE -1;
        ratingDataHandler.addRating(fileId, user1Id, ratingValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxValueForRating() throws DataHandlerException {
        int ratingValue = RatingConstants.MAX_RATING_VALUE +1;
        ratingDataHandler.addRating(fileId, user1Id, ratingValue);
    }

    @Test
    public void testDeleteRating() throws Exception {
        ratingDataHandler.addRating(fileId, user1Id, 1);
        File fileFromDb = fileDataHandler.getFile(fileId.toString());
        Rating rating = fileFromDb.getRating();
        assertTrue(rating.hasUserRated(user1Id));
        assertEquals(rating.getRating(), 1, 0.1);
        ratingDataHandler.deleteRating(fileId, user1Id);
        fileFromDb = fileDataHandler.getFile(fileId.toString());
        rating = fileFromDb.getRating();
        assertFalse(rating.hasUserRated(user1Id));
    }

    @Test
    public void testPutRating() throws Exception {
        ratingDataHandler.putRating(fileId, user1Id, 1);
        File fileFromDb = fileDataHandler.getFile(fileId.toString());
        Rating rating = fileFromDb.getRating();
        assertTrue(rating.hasUserRated(user1Id));
        assertEquals(rating.getRating(), 1, 0.1);


        ratingDataHandler.putRating(fileId, user1Id, 2);
        fileFromDb = fileDataHandler.getFile(fileId.toString());
        rating = fileFromDb.getRating();
        assertTrue(rating.hasUserRated(user1Id));
        assertEquals(rating.getRating(), 2, 0.1);
    }
}

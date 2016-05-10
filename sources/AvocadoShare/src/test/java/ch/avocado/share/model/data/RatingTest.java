package ch.avocado.share.model.data;

import ch.avocado.share.common.constants.sql.RatingConstants;
import ch.avocado.share.test.DummyFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Tests for {@link Rating}
 */
public class RatingTest {

    private Rating rating;
    private long objectId;
    private Map<User, Integer> ratings;
    private float averageRating;

    private static int getRandomRating(Random random) {
        double rating = random.nextDouble() * (RatingConstants.MAX_RATING_VALUE - RatingConstants.MIN_RATING_VALUE) + RatingConstants.MIN_RATING_VALUE;
        return (int) Math.round(rating);
    }

    @Before
    public void setUp() {
        ratings = new HashMap<>();
        objectId = 1234;
        rating = new Rating(1234);
        averageRating = 0.0f;
        Random random = new Random();
        random.setSeed(1);
        for(int i = 0; i < 10; ++i) {
            User user = DummyFactory.newUser(i);
            user.setId("" + Math.abs(random.nextLong()));
            int ratingValue = getRandomRating(random);
            rating.addRating(ratingValue, Long.parseLong(user.getId()));
            averageRating += ratingValue;
        }
        averageRating /= 10.0;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeValue() throws Exception {
        new Rating(-1);
    }

    @Test
    public void testHasUserRated() {
        for(User user: ratings.keySet()) {
            assertTrue(rating.hasUserRated(Long.parseLong(user.getId())));
        }
        assertFalse(rating.hasUserRated(11111));
    }

    @Test
    public void testGetRatedObjectId() {
        assertEquals(objectId, rating.getRatedObjectId());
    }

    @Test
    public void testGetNumberOfRatings() {
        assertEquals(10, rating.getNumberOfRatings());
        assertFalse(rating.hasUserRated(1));
        rating.addRating(1, 2131231);
        assertEquals(11, rating.getNumberOfRatings());
    }

    @Test
    public void testGetRating() {
        assertEquals(averageRating, rating.getRating(), 0.01);

        rating = new Rating(3213);
        assertEquals(RatingConstants.MIN_RATING_VALUE, rating.getRating(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRatingWithTooLowRating() {
        rating.addRating(RatingConstants.MIN_RATING_VALUE - 1, 321321);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRatingWithTooHigh() {
        rating.addRating(RatingConstants.MAX_RATING_VALUE + 1, 321321);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRatingWithUserIdBelowZero() {
        rating.addRating(RatingConstants.MAX_RATING_VALUE, -1);
    }
}
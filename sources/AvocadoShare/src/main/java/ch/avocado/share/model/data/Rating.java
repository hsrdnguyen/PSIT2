package ch.avocado.share.model.data;

/**
 * Created by bergm on 15/03/2016.
 */
public class Rating {

    static private final int MIN_RATING_VALUE = 0;
    static private final int MAX_RATING_VALUE = 6;

    private User ratingUser;
    private int rating;

    public Rating(User ratingUser, int rating) {
        setRating(rating);
        setRatingUser(ratingUser);
    }

    /**
     * @return The user who rated
     */
    public User getRatingUser() {
        return ratingUser;
    }

    /**
     * @param ratingUser The user who rated
     */
    public void setRatingUser(User ratingUser) {
        if (ratingUser == null) throw new IllegalArgumentException("ratingUser is null");
        this.ratingUser = ratingUser;
    }

    /**
     * @return The rating value
     */
    public int getRating() {
        return rating;
    }

    /**
     * @param rating The rating value
     * The rating has to be between {@value MIN_RATING_VALUE} and {@value MAX_RATING_VALUE}.
     */
    public void setRating(int rating) {
        if(rating < MIN_RATING_VALUE || rating > MAX_RATING_VALUE) {
            throw new IllegalArgumentException("Rating not between " + MIN_RATING_VALUE + " and " + MAX_RATING_VALUE);
        }
        this.rating = rating;
    }
}

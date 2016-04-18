package ch.avocado.share.model.data;

/**
 * Created by bergm on 15/03/2016.
 */
public class RatingUser {

    //TODO: Diese Klasse wird eventuell gar nicht gebraucht...

    static protected final int MIN_RATING_VALUE = 0;
    static protected final int MAX_RATING_VALUE = 6;

    private int ratingUserId;
    private int ratedObjectId;
    private int rating;

    public RatingUser(int ratingUserId, int ratedObjectId, int rating) {
        setRating(rating);
        setRatingUserId(ratingUserId);
        setRatedObjectId(ratedObjectId);
    }

    /**
     * @return The user who rated
     */
    public int getRatingUserId() {
        return ratingUserId;
    }

    /**
     * @param ratingUserId The id of the user who rated
     */
    public void setRatingUserId(int ratingUserId) {
        this.ratingUserId = ratingUserId;
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

    public int getRatedObjectId(){return ratedObjectId;}

    public void setRatedObjectId(int ratedObjectId){
        this.ratedObjectId = ratedObjectId;
    }
}

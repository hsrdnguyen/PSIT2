package ch.avocado.share.model.data;

import ch.avocado.share.common.constants.sql.RatingConstants;

/**
 * Created by bergm on 15/03/2016.
 */
public class RatingUser {

    //TODO: Diese Klasse wird eventuell gar nicht gebraucht...
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
     * The rating has to be between the max and the min rating value.
     */
    public void setRating(int rating) {
        if(rating < RatingConstants.MIN_RATING_VALUE || rating > RatingConstants.MAX_RATING_VALUE) {
            throw new IllegalArgumentException("Rating not between " + RatingConstants.MIN_RATING_VALUE
                    + " and " + RatingConstants.MAX_RATING_VALUE);
        }
        this.rating = rating;
    }

    public int getRatedObjectId(){return ratedObjectId;}

    public void setRatedObjectId(int ratedObjectId){
        this.ratedObjectId = ratedObjectId;
    }
}

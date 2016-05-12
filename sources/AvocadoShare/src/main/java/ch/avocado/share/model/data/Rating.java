package ch.avocado.share.model.data;

import ch.avocado.share.common.constants.sql.RatingConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * The rating contains the users who have rated and the avarage rating.
 */
public class Rating {
    private static long DEFAULT_RATED_OBJECT_ID = -1;
    private static float DEFAULT_RATING = RatingConstants.MIN_RATING_VALUE;
    private List<Long> ratingUserIds;
    private long ratedObjectId;
    private float rating;

    /**
     * Create a new rating object with the associated object id.
     * @param ratedObjectId
     */
    public Rating(long ratedObjectId){
        if(ratedObjectId < 0) throw new IllegalArgumentException("ratedObjectId is below zero");
        ratingUserIds = new ArrayList<>();
        this.ratedObjectId = ratedObjectId;
    }

    /**
     * Create a new rating object without the associated object id.
     */
    public Rating(){
        ratingUserIds = new ArrayList<>();
        this.ratedObjectId = DEFAULT_RATED_OBJECT_ID;
    }

    public boolean hasUserRated(long ratingUserId){
        return ratingUserIds.contains(ratingUserId);
    }

    public int getNumberOfRatings(){
        return ratingUserIds.size();
    }

    public float getRating(){
        if(ratingUserIds.isEmpty()) {
            return DEFAULT_RATING;
        }
        return rating / ((float)ratingUserIds.size());
    }

    public void addRating(int rating, long ratingUserId){
        if(ratingUserId < 0) throw new IllegalArgumentException("ratingUserId is below zero");
        if(rating < RatingConstants.MIN_RATING_VALUE || rating > RatingConstants.MAX_RATING_VALUE) {
            throw new IllegalArgumentException("Rating not between " + RatingConstants.MIN_RATING_VALUE
                    + " and " + RatingConstants.MAX_RATING_VALUE);
        }
        this.rating += rating;
        ratingUserIds.add(ratingUserId);
    }
}

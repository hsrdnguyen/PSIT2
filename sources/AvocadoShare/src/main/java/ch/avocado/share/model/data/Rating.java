package ch.avocado.share.model.data;

import ch.avocado.share.common.constants.sql.RatingConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunzlio1 on 18.04.2016.
 */
public class Rating {
    private static long DEFAULT_RATED_OBJECT_ID = -1;
    private List<Long> ratingUserIds;
    private long ratedObjectId;
    private float rating;

    public Rating(long ratedObjectId){
        ratingUserIds = new ArrayList<>();
        this.ratedObjectId = ratedObjectId;
    }

    public Rating(){
        ratingUserIds = new ArrayList<>();
        this.ratedObjectId = DEFAULT_RATED_OBJECT_ID;
    }

    public void addRatingUserId(long ratingUserId){
        ratingUserIds.add(ratingUserId);
    }

    public boolean hasUserRated(long ratingUserId){
        return ratingUserIds.contains(ratingUserId);
    }

    public void setRatedObjectId(long ratedObjectId){
        this.ratedObjectId = ratedObjectId;
    }

    public long getRatedObjectId(){
        return ratedObjectId;
    }

    public int getNumberOfRatings(){
        return ratingUserIds.size();
    }

    public float getRating(){
        return rating / ((float)ratingUserIds.size());
    }

    public void addRating(int rating, long ratingUserId){
        if(rating < RatingConstants.MIN_RATING_VALUE || rating > RatingConstants.MAX_RATING_VALUE) {
            throw new IllegalArgumentException("Rating not between " + RatingConstants.MIN_RATING_VALUE
                    + " and " + RatingConstants.MAX_RATING_VALUE);
        }
        this.rating += rating;
        ratingUserIds.add(ratingUserId);
    }
}

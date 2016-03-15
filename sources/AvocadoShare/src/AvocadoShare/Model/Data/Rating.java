package AvocadoShare.Model.Data;

/**
 * Created by bergm on 15/03/2016.
 */
public class Rating {

    private User ratingUser;
    private int rating;

    public Rating(User ratingUser, int rating) {
        setRating(rating);
        setRatingUser(ratingUser);
    }

    public User getRatingUser() {
        return ratingUser;
    }

    public void setRatingUser(User ratingUser) {
        if (ratingUser == null) throw new IllegalArgumentException("ratingUser is null");
        this.ratingUser = ratingUser;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

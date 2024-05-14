package de.dasshorty.dcevents.rating.image.submit;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "image-rating-submit")
public class SubmitDto {

    @Id
    private String id; // user id
    private String imageId;
    private String ratingId;
    private Binary image;
    private int rating;
    private String messageId;
    private ArrayList<String> membersThatVoted = new ArrayList<>();

    public SubmitDto() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void generateImageId() {
        this.imageId = new ObjectId().toHexString();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public ArrayList<String> getMembersThatVoted() {
        return membersThatVoted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Binary getImage() {
        return image;
    }

    public void setImage(Binary image) {
        this.image = image;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }
}

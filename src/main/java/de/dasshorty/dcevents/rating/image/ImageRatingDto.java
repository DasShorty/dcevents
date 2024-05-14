package de.dasshorty.dcevents.rating.image;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "image-rating")
public class ImageRatingDto {

    @Id
    private String id;
    private String serverId; // id from guild
    private String votingChannelId;
    private String submitChannelId;

    private ImageRatingDto(String id) {
        this.id = id;
    }

    public static ImageRatingDto createDto() {
        return new ImageRatingDto(new ObjectId().toHexString());
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setVotingChannelId(String votingChannelId) {
        this.votingChannelId = votingChannelId;
    }

    public void setSubmitChannelId(String submitChannelId) {
        this.submitChannelId = submitChannelId;
    }

    public void setState(ImageRatingState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getServerId() {
        return serverId;
    }

    public String getVotingChannelId() {
        return votingChannelId;
    }

    public String getSubmitChannelId() {
        return submitChannelId;
    }

    public ImageRatingState getState() {
        return state;
    }

    private ImageRatingState state;

}

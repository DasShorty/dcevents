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
    private ImageRatingState state;

    private ImageRatingDto(String id) {
        this.id = id;
    }

    public static ImageRatingDto createDto() {
        return new ImageRatingDto(new ObjectId().toHexString());
    }

    public String getId() {
        return id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getVotingChannelId() {
        return votingChannelId;
    }

    public void setVotingChannelId(String votingChannelId) {
        this.votingChannelId = votingChannelId;
    }

    public String getSubmitChannelId() {
        return submitChannelId;
    }

    public void setSubmitChannelId(String submitChannelId) {
        this.submitChannelId = submitChannelId;
    }

    public ImageRatingState getState() {
        return state;
    }

    public void setState(ImageRatingState state) {
        this.state = state;
    }

}

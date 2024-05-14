package de.dasshorty.dcevents.rating.image;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageRepository extends MongoRepository<ImageRatingDto, String> {

    boolean existsByServerId(String serverId);
    Optional<ImageRatingDto> getBySubmitChannelId(String submitChannelId);

}

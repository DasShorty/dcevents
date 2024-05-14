package de.dasshorty.dcevents.rating.image.submit;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubmitRepository extends MongoRepository<SubmitDto, String> {

    List<SubmitDto> getAllByRatingId(String ratingId);
    Optional<SubmitDto> findByImageId(String imageId);
    Optional<SubmitDto> findByMessageId(String messageId);

}

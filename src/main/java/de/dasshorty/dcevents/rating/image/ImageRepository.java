package de.dasshorty.dcevents.rating.image;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<ImageRatingDto, String> {
}

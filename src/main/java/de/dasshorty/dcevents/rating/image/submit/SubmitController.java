package de.dasshorty.dcevents.rating.image.submit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/image/rating/")
public class SubmitController {

    private final SubmitRepository submitRepository;

    @Autowired
    public SubmitController(SubmitRepository submitRepository) {
        this.submitRepository = submitRepository;
    }

    @GetMapping("{imageId}")
    public ResponseEntity<?> getImage(@PathVariable("imageId") String imageID) {

        Optional<SubmitDto> byId = this.submitRepository.findByImageId(imageID);
        if (byId.isEmpty()) {
            return null;
        }

        SubmitDto dto = byId.get();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(dto.getImage().getData());
    }

}

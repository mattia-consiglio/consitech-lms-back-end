package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.VideoResolution;
import mattia.consiglio.consitech.lms.repositories.VideoResolutionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VideoResolutionsService {
    private final VideoResolutionsRepository videoResolutionsRepository;


    public List<VideoResolution> getVideoResolutions() {
        return videoResolutionsRepository.findAll();
    }

    public VideoResolution createVideoResolution(VideoResolution videoResolution) {
        return videoResolutionsRepository.save(videoResolution);
    }

}

package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "media_videos")
public class MediaVideo extends Media {
    private double duration;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "MediaVideo_VideoResolutions",
            joinColumns = {@JoinColumn(name = "media_video_id")},
            inverseJoinColumns = {@JoinColumn(name = "video_resolution_id")}
    )
    private List<VideoResolution> resolutions;
    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private MediaImage thumbnail;

    @JsonIgnore
    @OneToMany(mappedBy = "video")
    private List<Lesson> lessons = new ArrayList<>();

    private MediaVideo(Builder builder) {
        super(builder);
        this.duration = builder.duration;
    }

    public static class Builder extends Media.Builder<Builder> {
        private double duration;

        public Builder duration(double duration) {
            this.duration = duration;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public MediaVideo build() {
            return new MediaVideo(this);
        }
    }
}
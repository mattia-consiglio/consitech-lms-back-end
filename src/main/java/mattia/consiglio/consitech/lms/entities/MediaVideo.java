package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "media_videos")
public class MediaVideo extends Media {
    private double duration;
    private List<VideoResolutions> resolutions;
    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private MediaImage thumbnail;

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
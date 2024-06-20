package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.utils.View;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "media_images")
public class MediaImage extends Media {
    @JsonView(View.Public.class)
    private int width;

    @JsonView(View.Public.class)
    private int height;

    @JsonView(View.Public.class)
    private String avgColor;

    @JsonIgnore
    @OneToMany(mappedBy = "thumbnailImage")
    List<AbstractContent> contents = new ArrayList<>();

    private MediaImage(Builder builder) {
        super(builder);
        this.width = builder.width;
        this.height = builder.height;
        this.avgColor = builder.mainColor;
    }

    public static class Builder extends Media.Builder<Builder> {
        private int width;
        private int height;
        private String mainColor;


        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder avgColor(String mainColor) {
            this.mainColor = mainColor;
            return this;
        }


        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public MediaImage build() {
            return new MediaImage(this);
        }
    }
}

package mattia.consiglio.consitech.lms.entities;

import lombok.Getter;

@Getter
public enum VideoResolutions {
    FOUR_K("2160p", 3840, 2160, 22),
    TWO_K("1440p", 2560, 1440, 21),
    FHD("1080p", 1920, 1080, 20),
    HD("720p", 1280, 720, 19),
    SD("480p", 854, 480, 18),
    LD("360p", 640, 360, 17);

    private final String name;
    private final int width;
    private final int height;
    private final int quality;

    VideoResolutions(String name, int width, int height, int quality) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.quality = quality;
    }

}

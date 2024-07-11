package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.entities.MediaImage;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.repositories.AbstractContentRepository;
import mattia.consiglio.consitech.lms.repositories.MediaImageRepository;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MediaImageService {
    private final MediaImageRepository mediaImageRepository;
    private final MediaServiceUtils mediaServiceUtils;
    private final AbstractContentRepository abstractContentRepository;


    public MediaImage uploadImage(MediaImage media) {

        try (InputStream inputStream = new FileInputStream(mediaServiceUtils.getPath(media))) {
            BufferedImage image = ImageIO.read(inputStream);
            Color averageColor = getAverageColor(image);
            MediaImage mediaImage = new MediaImage.Builder()
                    .media(media)
                    .avgColor(colorToHex(averageColor))
                    .width(image.getWidth())
                    .height(image.getHeight())
                    .build();
            return mediaImageRepository.save(mediaImage);
        } catch (IOException e) {
            log.error("Error while reading image", e);
            throw new BadRequestException("Error while reading image");
        }
    }

    public void deleteImage(MediaImage media) {
        media.getContents().forEach(abstractContent -> {
            abstractContent.setThumbnailImage(null);
            abstractContentRepository.save(abstractContent);
        });

        if (media.getParentId() == null) {
            List<MediaImage> mediaList = mediaImageRepository.findByParentId(media.getId());
            mediaList.forEach((MediaImage m) -> {
                m.getContents().forEach(abstractContent -> {
                    abstractContent.setThumbnailImage(null);
                    abstractContentRepository.save(abstractContent);
                });
                mediaImageRepository.delete(m);
            });
            mediaServiceUtils.deleteFile(mediaServiceUtils.getMediaFile(media, true));
        }
        mediaImageRepository.delete(media);
    }


    private Color getAverageColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        long sumRed = 0;
        long sumGreen = 0;
        long sumBlue = 0;
        int validPixelCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                int brightness = (pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue()) / 3;

                if (brightness > 50 && brightness < 200) { // Soglie per escludere pixel troppo chiari o scuri
                    sumRed += pixelColor.getRed();
                    sumGreen += pixelColor.getGreen();
                    sumBlue += pixelColor.getBlue();
                    validPixelCount++;
                }
            }
        }

        if (validPixelCount == 0) {
            return new Color(0, 0, 0); // Nessun pixel valido trovato
        }

        int averageRed = (int) (sumRed / validPixelCount);
        int averageGreen = (int) (sumGreen / validPixelCount);
        int averageBlue = (int) (sumBlue / validPixelCount);

        return new Color(averageRed, averageGreen, averageBlue);
    }

    // Convert Color into hexadecimal string
    public static String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
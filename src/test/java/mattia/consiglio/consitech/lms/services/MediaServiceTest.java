package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.MediaImage;
import mattia.consiglio.consitech.lms.entities.enums.MediaType;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.repositories.MediaImageRepository;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MediaServiceTest {

    @InjectMocks
    private MediaService mediaService;

    @Mock
    private MediaServiceUtils mediaServiceUtils;

    @Mock
    private MediaImageService mediaImageService;

    @Mock
    private MediaVideoService mediaVideoService;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private MediaImageRepository mediaImageRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadMedia_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);
        assertThrows(BadRequestException.class, () -> mediaService.uploadMedia(emptyFile));
    }

    @Test
    void testUploadMedia_NullFilename() {
        MultipartFile fileWithNullName = new MockMultipartFile("file", null, "image/jpeg", "test content".getBytes());
        assertThrows(BadRequestException.class, () -> mediaService.uploadMedia(fileWithNullName));
    }

    @Test
    void testUploadMedia_InvalidFileExtension() {
        MultipartFile fileWithInvalidExtension = new MockMultipartFile("file", "test.invalid", "application/octet-stream", "test content".getBytes());
        when(mediaServiceUtils.isValidFileExtension(anyString())).thenReturn(false);
        assertThrows(BadRequestException.class, () -> mediaService.uploadMedia(fileWithInvalidExtension));
    }

    @Test
    void testUploadMedia_ValidImageFile() throws IOException, NoSuchAlgorithmException {
        MultipartFile validImageFile = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "test image content".getBytes());
        when(mediaServiceUtils.isValidFileExtension(anyString())).thenReturn(true);
        when(mediaService.calculateHash(any(MultipartFile.class))).thenReturn("hash123");
        when(mediaService.checkFileDifference(anyString(), anyString(), anyString())).thenReturn(new MediaService.MediaDifference(true, "test-image.jpg", null));
        when(mediaService.getMediaType(any(MultipartFile.class))).thenReturn(MediaType.IMAGE);
        when(mediaService.getHostUrl()).thenReturn("http://localhost:8080");

        MediaImage mediaImage = new MediaImage.Builder()
                .url("http://localhost:8080/media/test-image.jpg")
                .type(MediaType.IMAGE)
                .alt("test image")
                .hash("hash123")
                .uploadedAt(LocalDateTime.now())
                .filename("test-image.jpg")
                .build();
        when(mediaImageService.uploadImage(any(MediaImage.class))).thenReturn(mediaImage);

        Media result = mediaService.uploadMedia(validImageFile);

        assertNotNull(result);
        assertInstanceOf(MediaImage.class, result);
        assertEquals("http://localhost:8080/media/test-image.jpg", result.getUrl());
        assertEquals(MediaType.IMAGE, result.getType());
        assertEquals("test image", result.getAlt());
        assertEquals("hash123", result.getHash());
        assertEquals("test-image.jpg", result.getFilename());
    }


    @Test
    void testUploadMedia_InvalidMediaType() throws IOException, NoSuchAlgorithmException {
        MultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        when(mediaServiceUtils.isValidFileExtension(anyString())).thenReturn(true);
        when(mediaService.calculateHash(any())).thenReturn("hash789");
        when(mediaService.checkFileDifference(anyString(), anyString(), anyString())).thenReturn(new MediaService.MediaDifference(true, "test.txt", null));
        when(mediaService.getMediaType(any())).thenReturn(null);

        assertThrows(BadRequestException.class, () -> mediaService.uploadMedia(invalidFile));
    }
}

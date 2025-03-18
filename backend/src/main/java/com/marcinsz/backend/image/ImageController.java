package com.marcinsz.backend.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> uploadImage(@RequestPart MultipartFile file) throws IOException {
        return ResponseEntity.ok().body(imageService.uploadReceiptImage(file));
    }
}

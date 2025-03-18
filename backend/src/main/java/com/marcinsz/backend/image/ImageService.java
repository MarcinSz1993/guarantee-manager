package com.marcinsz.backend.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final Cloudinary cloudinary;

    public ImageResponse uploadReceiptImage(MultipartFile file) throws IOException {
        Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return ImageResponse.builder()
                .imageUrl(upload.get("url").toString())
                .build();

    }
}
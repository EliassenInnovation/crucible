package com.eliassen.crucible.ai.imagetools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageTool {
    public static String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream); // Use PNG to avoid quality loss
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static BufferedImage loadImage(String filePath) throws IOException {
        // Attempt to read the image from the specified file path
        return ImageIO.read(new File(filePath));
    }

    public static String loadImageBase64(String imagePath) throws IOException {
        return convertImageToBase64(loadImage(imagePath));
    }
}

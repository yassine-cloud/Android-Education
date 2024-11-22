package com.iset.education.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /**
     * Convert a file to a byte array
     * @param file supported pdf, jpg,png
     * @return byte array
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }

    public static void bytesToFile(byte[] data, File outputFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(data);
        fos.close();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}

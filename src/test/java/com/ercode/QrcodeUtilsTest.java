package com.ercode;

import com.viewol.ercode.QrcodeUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class QrcodeUtilsTest {
    private static String content = "abc";

    public static void main(String[] args) throws Exception {
        decodeQrcode();
    }

    public static void createQrcode() throws IOException {
        byte[] bytes = QrcodeUtils.createQrcode(content, 800, null);
        Path path = Files.createTempFile("qrcode_800_", ".jpg");
        System.out.println(path.toAbsolutePath());
        Files.write(path, bytes);

        bytes = QrcodeUtils.createQrcode(content, null);
        path = Files.createTempFile("qrcode_400_", ".jpg");
        System.out.println(path.toAbsolutePath());
        Files.write(path, bytes);
    }

    public static void createQrcodeWithLogo() throws Exception {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("logo.png")) {
            File logoFile = Files.createTempFile("logo_", ".jpg").toFile();
            FileUtils.copyInputStreamToFile(inputStream, logoFile);

            byte[] bytes = QrcodeUtils.createQrcode(content, 800, logoFile);
            Path path = Files.createTempFile("qrcode_with_logo_", ".jpg");
            System.out.println(path.toAbsolutePath());
            Files.write(path, bytes);
        }
    }

    public static void decodeQrcode() throws Exception {
        File file = new File("C:\\Users\\JHSS-J~1\\AppData\\Local\\Temp\\qrcode_800_7656320085295259116.jpg");
        String parseContent = QrcodeUtils.decodeQrcode(file);
        System.out.println(parseContent);
    }

}
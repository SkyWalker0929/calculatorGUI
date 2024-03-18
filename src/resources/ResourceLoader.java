package resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
    private static ResourceLoader loader;

    private ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        if(loader == null)
            loader = new ResourceLoader();
        return loader;
    }

    public String getFileFromPath(String relativePath, String tempFilename, String extension) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(relativePath);

        File tempFile;
        try {
            tempFile = File.createTempFile(tempFilename, extension);
            tempFile.deleteOnExit();

            try(FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int length;
                while((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempFile.getAbsolutePath();
    }
}

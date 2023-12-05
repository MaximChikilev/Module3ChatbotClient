package academy.prog;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Content {
    private String fileName;
    private String comment;
    private byte[] fileInByteRepresentation;

    public Content(String fileName, String comment) {
        this.fileName = fileName;
        this.comment = comment;
    }

    public void uploadContent(String pathToWorkFolder) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource((fileName)).getFile());
        this.fileInByteRepresentation = new byte[(int) file.length()];
        try (InputStream fis = new FileInputStream(file)) {
            while (fis.read(fileInByteRepresentation) != -1) {

            }
        }
    }

    public void downloadContent(String pathToWorkFolder) throws IOException {
        File file = new File(pathToWorkFolder+"x"+fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileInByteRepresentation);
        }
    }

    public String getFileName() {
        return fileName;
    }
}

package academy.prog;

import java.io.*;

public class Content {
    private String fileName;
    private String comment;
    private byte[] fileInByteRepresentation;

    public Content(String fileName, String comment) {
        this.fileName = fileName;
        this.comment = comment;
    }

    public void uploadContent(String pathToWorkFolder) throws IOException {
        File file = new File(pathToWorkFolder + fileName);
        this.fileInByteRepresentation = new byte[(int) file.length()];
        try (InputStream fis = new FileInputStream(file)) {
            while (fis.read(fileInByteRepresentation) != -1) {

            }
        }
    }

    public void downloadContent(String pathToWorkFolder) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(pathToWorkFolder + "x" + fileName)) {
            fos.write(fileInByteRepresentation);
        }
    }

    public String getFileName() {
        return fileName;
    }
}

package pe.com.amsac.tramite.api.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {

    private final byte[] fileByte;
    private final String name;
    private final String contentType;

    public CustomMultipartFile(byte[] fileByte, String name, String contentType) {
        this.fileByte = fileByte;
        this.name = name;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    @Override
    public String getOriginalFilename() {
        // TODO Auto-generated method stub
        return name;
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return fileByte == null || fileByte.length == 0;
    }

    @Override
    public long getSize() {
        // TODO Auto-generated method stub
        return fileByte.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        return fileByte;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // TODO Auto-generated method stub
        return new ByteArrayInputStream(fileByte);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(fileByte);
    }
}

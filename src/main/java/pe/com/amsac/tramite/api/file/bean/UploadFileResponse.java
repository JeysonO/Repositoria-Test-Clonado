package pe.com.amsac.tramite.api.file.bean;

import java.io.Serializable;

public class UploadFileResponse implements Serializable {
    private static final long serialVersionUID = 5841992979997170674L;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, String size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return this.fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}

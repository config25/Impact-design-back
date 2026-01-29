package qtedu.Impact_design.domain.model.media;

import java.io.InputStream;

public final class FileData {

    private final InputStream inputStream;
    private final MediaType contentType;
    private final String name;
    private final long size;

    private FileData(InputStream inputStream, MediaType contentType, String name, long size) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.name = name;
        this.size = size;
    }

    public static FileData of(InputStream inputStream, MediaType contentType, String fileName, long size) {
        return new FileData(inputStream, contentType, fileName, size);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }
}
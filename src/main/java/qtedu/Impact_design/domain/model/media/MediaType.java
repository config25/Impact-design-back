package qtedu.Impact_design.domain.model.media;

public enum MediaType {
    IMAGE_BASIC("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_JPG("image/jpg"),
    IMAGE_PNG("image/png"),
    IMAGE_HEIC("image/heic"),
    IMAGE_WEBP("image/webp"),
    VIDEO_BASIC("video/mp4"),
    TEXT_HTML("text/html"),
    APPLICATION_PDF("application/pdf"),
    AUDIO_MP3("audio/mp3"),
    VIDEO_MP4("video/mp4");

    private final String type;

    MediaType(String type) {
        this.type = type;
    }

    public String value() {
        return type;
    }

    public static MediaType fromType(String type) {
        for (MediaType mediaType : values()) {
            if (mediaType.type.equalsIgnoreCase(type)) {
                return mediaType;
            }
        }
        return null;
    }

}
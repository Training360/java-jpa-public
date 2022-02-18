package activitytracker;

public class Image {

    private long id;
    private String filename;
    private byte[] content;

    public Image(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }

    public Image(long id, String filename, byte[] content) {
        this(filename, content);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }
}

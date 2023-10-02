package unsw.blackout;

public class File {
    private final String name;
    private String content;
    private boolean isFinished = false;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public int getNumBytes() {
        return this.content.length();
    }

    public String getContent(int start, int end) {
        return this.content.substring(start, end);
    }

    public String getAllContent() {
        return this.content;
    }

    public String getName() {
        return this.name;
    }

    public void appendContent(String content) {
        this.content += content;
    }

    public void fileComplete() {
        this.isFinished = true;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public static File emptyFile() {
        return new File(null, null);
    }
}

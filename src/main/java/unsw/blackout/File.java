package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private final String name;
    private String content;

    private int completeBytes;

    public File(String name, String content, int completeBytes) {
        this.name = name;
        this.content = content;
        this.completeBytes = completeBytes;
    }

    public int getNumBytes() {
        return this.content.length();
    }

    public int getCompleteBytes() {
        return this.completeBytes;
    }

    public String getContent() {
        return this.content;
    }

    public String getContent(int start, int end) {
        return this.content.substring(start, end);
    }

    public String getName() {
        return this.name;
    }

    public void setContent(String content) {
        setContent(content, true);
    }

    public void setContent(String content, boolean changeCompleteBytes) {
        this.content = content;

        if (changeCompleteBytes)
            this.completeBytes = content.length();
    }

    public void appendContent(String content) {
        this.content += content;
    }

    public boolean isFinished() {
        return getNumBytes() == getCompleteBytes();
    }

    public FileInfoResponse getInfo() {
        return new FileInfoResponse(this.name, this.content, this.completeBytes, this.isFinished());
    }

    @Override
    public String toString() {
        return "File [completeBytes=" + completeBytes + ", content=" + content + ", name=" + name + "]";
    }
}

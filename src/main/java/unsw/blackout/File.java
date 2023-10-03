package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private final String name;
    private String content;
    private boolean isFinished = false;

    // The number of bytes the complete file has
    private int completeBytes;

    public File(String name, String content, int completeBytes) {
        this.name = name;
        this.content = content;
        this.completeBytes = completeBytes;
        isFinished = false;
    }

    public int getNumBytes() {
        return this.content.length();
    }

    public int getCompleteBytes() {
        return this.completeBytes;
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
        return getNumBytes() == getCompleteBytes();
    }

    public FileInfoResponse getInfo() {
        return new FileInfoResponse(this.name, this.content, this.completeBytes, this.isFinished);
    }
}

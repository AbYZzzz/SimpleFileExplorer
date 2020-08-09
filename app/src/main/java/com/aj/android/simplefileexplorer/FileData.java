package com.aj.android.simplefileexplorer;

class FileData {
    private String fileName;
    private String fileUri;

    public FileData(String fileName, String fileUri) {
        this.fileName = fileName;
        this.fileUri = fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUri() {
        return fileUri;
    }
}

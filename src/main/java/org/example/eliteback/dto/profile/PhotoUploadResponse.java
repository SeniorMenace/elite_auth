package org.example.eliteback.dto.profile;

import java.util.List;

public class PhotoUploadResponse {
    private String message;
    private List<String> urls;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<String> getUrls() { return urls; }
    public void setUrls(List<String> urls) { this.urls = urls; }
}

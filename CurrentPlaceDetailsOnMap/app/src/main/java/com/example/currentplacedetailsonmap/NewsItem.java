package com.example.currentplacedetailsonmap;

public class NewsItem {
    private String title;
        private String description;
        private String url;

        // Getters and setters
        public NewsItem() {
            // Default constructor
        }

    public NewsItem(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    // Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package msku.ceng.madlab.branchify_mobile_app.model;

public class Song {
    private String title;
    private String artist;
    private String duration;

    // A no-argument constructor is required for Firestore deserialization.
    public Song() {}

    public Song(String title, String artist, String duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDuration() { return duration; }

    // Setters are also required for Firestore deserialization.
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDuration(String duration) { this.duration = duration; }
}
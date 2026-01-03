package msku.ceng.madlab.branchify_mobile_app.model;

public class Song {
    private long id;
    private String title;
    private String artist;
    private String duration;

    // A no-argument constructor is required for Firestore deserialization.
    public Song() {}

    public Song(long id, String title, String artist, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDuration() { return duration; }

    // Setters are also required for Firestore deserialization.
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDuration(String duration) { this.duration = duration; }
}
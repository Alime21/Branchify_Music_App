package msku.ceng.madlab.branchify_mobile_app.model;

public class Song {
    private String title;
    private String artist;
    private String duration;

    public Song(String title, String artist, String duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDuration() { return duration; }
}

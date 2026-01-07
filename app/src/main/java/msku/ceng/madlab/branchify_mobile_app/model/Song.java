package msku.ceng.madlab.branchify_mobile_app.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Song {
    private long id;
    private String title;
    private String artist;
    private String duration;
    private String albumArtUri;
    private Date timestamp;


    // A no-argument constructor is required for Firestore deserialization.
    public Song() {}

    public Song(long id, String title, String artist, String duration, String albumArtUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumArtUri = albumArtUri;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDuration() { return duration; }
    public String getAlbumArtUri() { return albumArtUri; }
    @ServerTimestamp public Date getTimestamp() { return timestamp; }


    // Setters are also required for Firestore deserialization.
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAlbumArtUri(String albumArtUri) { this.albumArtUri = albumArtUri; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
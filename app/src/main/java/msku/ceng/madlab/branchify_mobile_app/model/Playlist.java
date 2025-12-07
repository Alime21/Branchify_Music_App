package msku.ceng.madlab.branchify_mobile_app.model;

public class Playlist {
    private String name;
    private String trackCount;

    public Playlist(String name, String trackCount) {
        this.name = name;
        this.trackCount = trackCount;
    }

    public String getName() {
        return name;
    }

    public String getTrackCount() {
        return trackCount;
    }
}

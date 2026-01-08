package msku.ceng.madlab.branchify_mobile_app.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String documentId;
    private String name;
    // Store as Object to accept both Long and String from Firestore
    private List<Object> songIdsInternal;

    // No-argument constructor for Firestore
    public Playlist() {
        this.songIdsInternal = new ArrayList<>();
    }

    public Playlist(String documentId, String name) {
        this.documentId = documentId;
        this.name = name;
        this.songIdsInternal = new ArrayList<>();
    }

    @Exclude // Exclude from Firestore serialization to avoid conflicts
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns songIds as List<String>, converting any Long/Integer values to String.
     * Use this method in your code to get the song IDs.
     */
    @Exclude
    public List<String> getSongIds() {
        List<String> result = new ArrayList<>();
        if (songIdsInternal != null) {
            for (Object id : songIdsInternal) {
                result.add(String.valueOf(id));
            }
        }
        return result;
    }

    /**
     * Getter for Firestore serialization/deserialization - maps to "songIds" field.
     */
    @PropertyName("songIds")
    public List<Object> getSongIdsForFirestore() {
        return songIdsInternal;
    }

    /**
     * Setter for Firestore deserialization - maps to "songIds" field and accepts any type.
     */
    @PropertyName("songIds")
    public void setSongIdsForFirestore(List<Object> songIds) {
        this.songIdsInternal = songIds != null ? songIds : new ArrayList<>();
    }

    /**
     * Convenience method to set songIds from a List<String>.
     */
    @Exclude
    public void setSongIds(List<String> songIds) {
        this.songIdsInternal = new ArrayList<>();
        if (songIds != null) {
            this.songIdsInternal.addAll(songIds);
        }
    }

    public String getTrackCount() {
        if (songIdsInternal == null) {
            return "0 tracks";
        }
        return songIdsInternal.size() + " tracks";
    }
}
package msku.ceng.madlab.branchify_mobile_app;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.model.Playlist;

/**
 * Unit tests for the Playlist model class.
 */
public class PlaylistTest {

    private Playlist playlist;

    @Before
    public void setUp() {
        playlist = new Playlist("doc123", "My Playlist");
    }

    @Test
    public void playlistConstructor_setsNameAndDocumentId() {
        assertEquals("doc123", playlist.getDocumentId());
        assertEquals("My Playlist", playlist.getName());
        assertNotNull(playlist.getSongIds());
        assertTrue(playlist.getSongIds().isEmpty());
    }

    @Test
    public void getTrackCount_returnsCorrectFormat() {
        // Empty playlist
        assertEquals("0 tracks", playlist.getTrackCount());
        
        // Add some song IDs
        List<String> songIds = Arrays.asList("1", "2", "3");
        playlist.setSongIds(songIds);
        
        assertEquals("3 tracks", playlist.getTrackCount());
    }

    @Test
    public void getSongIds_convertsLongToString() {
        // Simulate Firestore returning Long values
        List<Object> mixedIds = new ArrayList<>();
        mixedIds.add(100L);  // Long
        mixedIds.add("200"); // String
        mixedIds.add(300);   // Integer
        
        playlist.setSongIdsForFirestore(mixedIds);
        
        List<String> result = playlist.getSongIds();
        
        assertEquals(3, result.size());
        assertEquals("100", result.get(0));
        assertEquals("200", result.get(1));
        assertEquals("300", result.get(2));
    }
}

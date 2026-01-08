package msku.ceng.madlab.branchify_mobile_app;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import java.util.Date;

import msku.ceng.madlab.branchify_mobile_app.model.Song;

/**
 * Unit tests for the Song model class.
 */
public class SongTest {

    private Song song;

    @Before
    public void setUp() {
        song = new Song(1L, "Test Song", "Test Artist", "3:45", "content://album/art/1");
    }

    @Test
    public void songConstructor_setsAllFieldsCorrectly() {
        assertEquals(1L, song.getId());
        assertEquals("Test Song", song.getTitle());
        assertEquals("Test Artist", song.getArtist());
        assertEquals("3:45", song.getDuration());
        assertEquals("content://album/art/1", song.getAlbumArtUri());
    }

    @Test
    public void songSetters_updateFieldsCorrectly() {
        song.setId(2L);
        song.setTitle("New Title");
        song.setArtist("New Artist");
        song.setDuration("4:30");
        song.setAlbumArtUri("content://album/art/2");
        
        Date testDate = new Date();
        song.setTimestamp(testDate);

        assertEquals(2L, song.getId());
        assertEquals("New Title", song.getTitle());
        assertEquals("New Artist", song.getArtist());
        assertEquals("4:30", song.getDuration());
        assertEquals("content://album/art/2", song.getAlbumArtUri());
        assertEquals(testDate, song.getTimestamp());
    }

    @Test
    public void songNoArgConstructor_createsEmptySong() {
        Song emptySong = new Song();
        
        assertEquals(0L, emptySong.getId());
        assertNull(emptySong.getTitle());
        assertNull(emptySong.getArtist());
        assertNull(emptySong.getDuration());
        assertNull(emptySong.getAlbumArtUri());
        assertNull(emptySong.getTimestamp());
    }
}

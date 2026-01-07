package msku.ceng.madlab.branchify_mobile_app.model.data;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class FirestoreManager {

    private static final String TAG = "FirestoreManager";
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static final int HISTORY_LIMIT = 15;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "FATAL: Cannot get user ID, no user is signed in!");
            return null;
        }
        return user.getUid();
    }

    // --- Callbacks for asynchronous operations ---
    public interface OnFavoritesCompleteListener {
        void onComplete(List<Song> favoriteSongs);
        void onError(Exception e);
    }
    
    public interface OnHistoryCompleteListener {
        void onComplete(List<Song> historySongs);
        void onError(Exception e);
    }


    // --- Public Methods ---

    public void addFavorite(Song song) {
        String userId = getCurrentUserId();
        if (userId == null || song == null || song.getTitle() == null) {
            Log.w(TAG, "Cannot add favorite: User not logged in or song is invalid.");
            return;
        }

        Log.d(TAG, "Attempting to add favorite for user: " + userId);
        db.collection("users").document(userId)
          .collection("favorites").document(song.getTitle())
          .set(song)
          .addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Favorite added to Firestore: " + song.getTitle()))
          .addOnFailureListener(e -> Log.e(TAG, "ERROR: Failed to add favorite to Firestore.", e));
    }

    public void removeFavorite(Song song) {
        String userId = getCurrentUserId();
        if (userId == null || song == null || song.getTitle() == null) {
            Log.w(TAG, "Cannot remove favorite: User not logged in or song is invalid.");
            return;
        }
        
        Log.d(TAG, "Attempting to remove favorite for user: " + userId);
        db.collection("users").document(userId)
          .collection("favorites").document(song.getTitle())
          .delete()
          .addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Favorite removed from Firestore: " + song.getTitle()))
          .addOnFailureListener(e -> Log.e(TAG, "ERROR: Failed to remove favorite from Firestore.", e));
    }

    public void getFavorites(OnFavoritesCompleteListener listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            listener.onError(new Exception("User not logged in."));
            return;
        }

        db.collection("users").document(userId)
          .collection("favorites")
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              List<Song> favoritesList = new ArrayList<>();
              if (queryDocumentSnapshots != null) {
                  favoritesList = queryDocumentSnapshots.toObjects(Song.class);
              }
              listener.onComplete(favoritesList);
          })
          .addOnFailureListener(listener::onError);
    }
    
    public void addSongToHistory(Song song) {
        String userId = getCurrentUserId();
        if (userId == null || song == null || song.getTitle() == null) {
            Log.w(TAG, "Cannot add to history: User not logged in or song is invalid.");
            return;
        }
    
        CollectionReference historyRef = db.collection("users").document(userId).collection("history");
    
        // Check if the song already exists in the history
        historyRef
            .whereEqualTo("title", song.getTitle())
            .whereEqualTo("artist", song.getArtist())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                WriteBatch batch = db.batch();
    
                // Delete existing entries
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    batch.delete(doc.getReference());
                }
    
                // Add the new entry
                batch.set(historyRef.document(), song);
    
                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "SUCCESS: Song added/updated in history.");
                        trimHistory(userId);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "ERROR: Failed to update history.", e));
            });
    }

    private void trimHistory(String userId) {
        db.collection("users").document(userId)
                .collection("history")
                .orderBy("timestamp", Query.Direction.ASCENDING) // Oldest first
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int size = queryDocumentSnapshots.size();
                    if (size > HISTORY_LIMIT) {
                        int surplus = size - HISTORY_LIMIT;
                        WriteBatch batch = db.batch();
                        List<DocumentSnapshot> docsToDelete = queryDocumentSnapshots.getDocuments().subList(0, surplus);
                        for (DocumentSnapshot doc : docsToDelete) {
                            batch.delete(doc.getReference());
                        }
                        batch.commit().addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully trimmed history."));
                    }
                });
    }

    public void getHistory(OnHistoryCompleteListener listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            listener.onError(new Exception("User not logged in."));
            return;
        }

        db.collection("users").document(userId)
          .collection("history")
          .orderBy("timestamp", Query.Direction.DESCENDING)
          .limit(HISTORY_LIMIT)
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              List<Song> historyList = new ArrayList<>();
              if (queryDocumentSnapshots != null) {
                  historyList = queryDocumentSnapshots.toObjects(Song.class);
              }
              listener.onComplete(historyList);
          })
          .addOnFailureListener(listener::onError);
    }
}
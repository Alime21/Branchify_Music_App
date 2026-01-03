package msku.ceng.madlab.branchify_mobile_app.model.data;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class FirestoreManager {

    private static final String TAG = "FirestoreManager";
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // --- Callbacks for asynchronous operations ---
    public interface OnFavoritesCompleteListener {
        void onComplete(List<Song> favoriteSongs);
        void onError(Exception e);
    }

    public interface OnFavoriteCheckListener {
        void onResult(boolean isFavorite);
    }

    // --- Public Methods ---

    public void addFavorite(Song song) {
        String userId = getCurrentUserId();
        if (userId == null || song == null || song.getTitle() == null) {
            Log.w(TAG, "Cannot add favorite: User not logged in or song is invalid.");
            return;
        }

        // Use the song's title as the document ID for simplicity
        db.collection("users").document(userId)
          .collection("favorites").document(song.getTitle())
          .set(song) // set() creates or overwrites the document
          .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite added: " + song.getTitle()))
          .addOnFailureListener(e -> Log.e(TAG, "Error adding favorite", e));
    }

    public void removeFavorite(Song song) {
        String userId = getCurrentUserId();
        if (userId == null || song == null || song.getTitle() == null) {
            Log.w(TAG, "Cannot remove favorite: User not logged in or song is invalid.");
            return;
        }

        db.collection("users").document(userId)
          .collection("favorites").document(song.getTitle())
          .delete()
          .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite removed: " + song.getTitle()))
          .addOnFailureListener(e -> Log.e(TAG, "Error removing favorite", e));
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
}
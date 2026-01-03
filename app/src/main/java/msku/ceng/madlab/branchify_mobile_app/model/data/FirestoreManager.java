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
}
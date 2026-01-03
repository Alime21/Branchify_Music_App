package msku.ceng.madlab.branchify_mobile_app.model.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private final FirebaseAuth mAuth;

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signInAnonymously() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "No user logged in. Signing in anonymously...");
            mAuth.signInAnonymously()
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    Log.d(TAG, "Anonymous sign-in successful. UID: " + (user != null ? user.getUid() : "null"));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Anonymous sign-in failed.", e);
                });
        } else {
            Log.d(TAG, "User already logged in. UID: " + currentUser.getUid());
        }
    }
}
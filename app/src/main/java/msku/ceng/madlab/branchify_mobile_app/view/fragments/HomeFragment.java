package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;
import msku.ceng.madlab.branchify_mobile_app.view.activities.MainActivity;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.SearchResultsAdapter;

public class HomeFragment extends Fragment {

    private LinearLayout playlistsContainer;
    private GridLayout recentActivityGrid;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirestoreManager firestoreManager;
    private List<Song> recentSongs = new ArrayList<>();

    // Search related fields
    private EditText editTextSearch;
    private ImageView btnClearSearch;
    private RecyclerView recyclerSearchResults;
    private SearchResultsAdapter searchResultsAdapter;
    private List<Song> allSongs = new ArrayList<>();

    // Threading components for search
    private final ExecutorService searchExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firestoreManager = new FirestoreManager();

        playlistsContainer = view.findViewById(R.id.playlistsContainer);
        recentActivityGrid = view.findViewById(R.id.recentActivityGrid);

        // Initialize search components
        editTextSearch = view.findViewById(R.id.editTextSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults);

        setupSearch();

        // Load real data
        loadPlaylists();
        loadRecentActivity();
        loadAllSongs();

        // Find View All from ID on fragment_home.xml
        TextView btnViewAll = view.findViewById(R.id.textPlaylistsViewAll);
        btnViewAll.setOnClickListener(v -> {
            Fragment playlistsFragment = new PlaylistsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, playlistsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Recent activity View All
        TextView btnRecentViewAll = view.findViewById(R.id.textRecentViewAll);
        btnRecentViewAll.setOnClickListener(v -> {
            Fragment historyFragment = new HistoryFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, historyFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        ImageButton btnSettings = view.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Fragment settingsFragment = new SettingsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, settingsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void loadPlaylists() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid()).collection("playlists")
                .limit(5) // Only show first 5 playlists on home
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && getContext() != null) {
                        playlistsContainer.removeAllViews();
                        List<Playlist> playlists = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Playlist playlist = document.toObject(Playlist.class);
                            playlist.setDocumentId(document.getId());
                            playlists.add(playlist);
                        }

                        if (playlists.isEmpty()) {
                            addEmptyPlaylistCard();
                        } else {
                            for (Playlist playlist : playlists) {
                                addPlaylistCard(playlist);
                            }
                        }
                    }
                });
    }

    private void addPlaylistCard(Playlist playlist) {
        if (getContext() == null) return;

        // Use CardView for rounded corners
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(
                dpToPx(140), dpToPx(120));
        cardViewParams.setMarginEnd(dpToPx(16));
        cardView.setLayoutParams(cardViewParams);
        cardView.setRadius(dpToPx(16));
        cardView.setCardElevation(dpToPx(4));
        cardView.setPreventCornerOverlap(true);
        cardView.setUseCompatPadding(false);

        // Create a FrameLayout to overlay text on image
        FrameLayout card = new FrameLayout(getContext());
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        card.setLayoutParams(cardParams);

        // Album art ImageView
        ImageView albumArt = new ImageView(getContext());
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        albumArt.setLayoutParams(imageParams);
        albumArt.setScaleType(ImageView.ScaleType.CENTER_CROP);
        albumArt.setBackgroundResource(R.drawable.bg_gradient_card);
        card.addView(albumArt);

        // Semi-transparent overlay for text readability
        View overlay = new View(getContext());
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        overlay.setLayoutParams(overlayParams);
        overlay.setBackgroundColor(0x40000000); // 25% black overlay
        card.addView(overlay);

        // Text container at bottom
        LinearLayout textContainer = new LinearLayout(getContext());
        FrameLayout.LayoutParams textContainerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.BOTTOM);
        textContainer.setLayoutParams(textContainerParams);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        textContainer.setBackgroundColor(0x80000000); // 50% black for text area

        TextView textView = new TextView(getContext());
        textView.setText(playlist.getName());
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(12);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setMaxLines(1);
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textContainer.addView(textView);

        TextView trackCountView = new TextView(getContext());
        trackCountView.setText(playlist.getTrackCount());
        trackCountView.setTextColor(0xFFE0E0E0);
        trackCountView.setTextSize(10);
        textContainer.addView(trackCountView);

        card.addView(textContainer);
        cardView.addView(card);

        // Load album art from first song in playlist
        loadPlaylistArt(playlist, albumArt);

        cardView.setOnClickListener(v -> {
            PlaylistDetailFragment detailFragment = new PlaylistDetailFragment();
            Bundle args = new Bundle();
            args.putString("playlistId", playlist.getDocumentId());
            args.putString("playlistName", playlist.getName());
            detailFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        playlistsContainer.addView(cardView);
    }

    private void loadPlaylistArt(Playlist playlist, ImageView albumArt) {
        List<String> songIds = playlist.getSongIds();
        if (songIds == null || songIds.isEmpty()) return;

        // Get the first song ID to fetch its album art
        String firstSongId = songIds.get(0);
        db.collection("songs").document(firstSongId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (getContext() == null) return;
                    if (documentSnapshot.exists()) {
                        String artUri = documentSnapshot.getString("albumArtUri");
                        if (artUri != null && !artUri.isEmpty()) {
                            Glide.with(getContext())
                                    .load(Uri.parse(artUri))
                                    .centerCrop()
                                    .into(albumArt);
                        }
                    }
                });
    }

    private void addEmptyPlaylistCard() {
        if (getContext() == null) return;

        LinearLayout card = new LinearLayout(getContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                dpToPx(140), dpToPx(100));
        card.setLayoutParams(cardParams);
        card.setBackgroundResource(R.drawable.bg_gradient_card);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));

        TextView textView = new TextView(getContext());
        textView.setText("No playlists\nCreate one!");
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(12);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(textView);

        card.setOnClickListener(v -> {
            Fragment playlistsFragment = new PlaylistsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, playlistsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        playlistsContainer.addView(card);
    }

    private void loadRecentActivity() {
        firestoreManager.getHistory(new FirestoreManager.OnHistoryCompleteListener() {
            @Override
            public void onComplete(List<Song> historyList) {
                if (getContext() == null) return;
                recentActivityGrid.removeAllViews();
                recentSongs.clear();

                // Show max 4 recent songs
                int limit = Math.min(historyList.size(), 4);
                if (limit == 0) {
                    addEmptyRecentCard();
                    return;
                }

                for (int i = 0; i < limit; i++) {
                    Song song = historyList.get(i);
                    recentSongs.add(song);
                    addRecentSongCard(song, i);
                }
            }

            @Override
            public void onError(Exception e) {
                if (getContext() != null) {
                    addEmptyRecentCard();
                }
            }
        });
    }

    private void addRecentSongCard(Song song, int index) {
        if (getContext() == null) return;

        // Use CardView for rounded corners
        CardView cardView = new CardView(getContext());
        GridLayout.LayoutParams cardViewParams = new GridLayout.LayoutParams();
        cardViewParams.width = 0;
        cardViewParams.height = dpToPx(120);
        cardViewParams.columnSpec = GridLayout.spec(index % 2, 1f);
        cardViewParams.rowSpec = GridLayout.spec(index / 2);
        cardViewParams.setMargins(
                index % 2 == 0 ? 0 : dpToPx(8),
                0,
                index % 2 == 0 ? dpToPx(8) : 0,
                dpToPx(16)
        );
        cardView.setLayoutParams(cardViewParams);
        cardView.setRadius(dpToPx(16));
        cardView.setCardElevation(dpToPx(4));
        cardView.setPreventCornerOverlap(true);

        // Create a FrameLayout to overlay text on image
        FrameLayout card = new FrameLayout(getContext());
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        card.setLayoutParams(cardParams);

        // Album art ImageView
        ImageView albumArt = new ImageView(getContext());
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        albumArt.setLayoutParams(imageParams);
        albumArt.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load album art with Glide
        if (song.getAlbumArtUri() != null && !song.getAlbumArtUri().isEmpty()) {
            Glide.with(getContext())
                    .load(Uri.parse(song.getAlbumArtUri()))
                    .placeholder(R.drawable.bg_gradient_card)
                    .error(R.drawable.bg_gradient_card)
                    .centerCrop()
                    .into(albumArt);
        } else {
            albumArt.setBackgroundResource(R.drawable.bg_gradient_card);
        }
        card.addView(albumArt);

        // Semi-transparent overlay for text readability
        View overlay = new View(getContext());
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        overlay.setLayoutParams(overlayParams);
        overlay.setBackgroundColor(0x40000000); // 25% black overlay
        card.addView(overlay);

        // Text container at bottom with darker background
        LinearLayout textContainer = new LinearLayout(getContext());
        FrameLayout.LayoutParams textContainerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.BOTTOM);
        textContainer.setLayoutParams(textContainerParams);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        textContainer.setBackgroundColor(0x80000000); // 50% black for text area

        TextView titleView = new TextView(getContext());
        titleView.setText(song.getTitle());
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setMaxLines(1);
        titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textContainer.addView(titleView);

        TextView artistView = new TextView(getContext());
        artistView.setText(song.getArtist());
        artistView.setTextColor(0xFFE0E0E0);
        artistView.setTextSize(10);
        artistView.setMaxLines(1);
        artistView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textContainer.addView(artistView);

        card.addView(textContainer);
        cardView.addView(card);

        final int songIndex = index;
        cardView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).playSong(recentSongs, songIndex);
            }
        });

        recentActivityGrid.addView(cardView);
    }

    private void addEmptyRecentCard() {
        if (getContext() == null) return;

        LinearLayout card = new LinearLayout(getContext());
        GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
        cardParams.width = 0;
        cardParams.height = dpToPx(100);
        cardParams.columnSpec = GridLayout.spec(0, 2, 1f);
        cardParams.rowSpec = GridLayout.spec(0);
        card.setLayoutParams(cardParams);
        card.setBackgroundResource(R.drawable.bg_gradient_card);
        card.setGravity(android.view.Gravity.CENTER);

        TextView textView = new TextView(getContext());
        textView.setText("No recent activity");
        textView.setTextColor(0xFFFFFFFF);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(textView);

        recentActivityGrid.addView(card);
    }

    private void setupSearch() {
        // Setup RecyclerView for search results
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsAdapter = new SearchResultsAdapter((songs, position) -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).playSong(songs, position);
                clearSearch();
            }
        });
        recyclerSearchResults.setAdapter(searchResultsAdapter);

        // Text watcher for search input
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    hideSearchResults();
                } else {
                    filterSongs(query);
                }
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle keyboard search action
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = editTextSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    filterSongs(query);
                }
                return true;
            }
            return false;
        });

        // Clear search button
        btnClearSearch.setOnClickListener(v -> clearSearch());
    }

    private void loadAllSongs() {
        if (getContext() == null) return;

        // Try to get cached songs first
        List<Song> cachedSongs = ContentResolverHelper.getAllMusicCache();
        if (cachedSongs != null && !cachedSongs.isEmpty()) {
            allSongs = new ArrayList<>(cachedSongs);
        } else {
            // Load from device if cache is empty
            ContentResolverHelper contentResolverHelper = new ContentResolverHelper(getContext());
            allSongs = new ArrayList<>(contentResolverHelper.getAudioFiles());
        }
    }

    private void filterSongs(String query) {
        if (allSongs.isEmpty()) {
            loadAllSongs();
        }

        // Run filtering on a background thread to avoid blocking the UI
        searchExecutor.execute(() -> {
            Log.d(TAG, "Search thread started: " + Thread.currentThread().getName());
            
            String lowerQuery = query.toLowerCase();
            List<Song> filteredSongs = new ArrayList<>();

            // Perform filtering on background thread
            for (Song song : allSongs) {
                String title = song.getTitle() != null ? song.getTitle().toLowerCase() : "";
                String artist = song.getArtist() != null ? song.getArtist().toLowerCase() : "";

                if (title.contains(lowerQuery) || artist.contains(lowerQuery)) {
                    filteredSongs.add(song);
                }
            }

            Log.d(TAG, "Filtering complete. Found " + filteredSongs.size() + " results");

            // Post results back to the main UI thread
            final List<Song> results = filteredSongs;
            mainHandler.post(() -> {
                Log.d(TAG, "Updating UI on thread: " + Thread.currentThread().getName());
                
                if (getContext() == null) return; // Fragment may be detached
                
                if (results.isEmpty()) {
                    recyclerSearchResults.setVisibility(View.VISIBLE);
                    searchResultsAdapter.clearResults();
                } else {
                    recyclerSearchResults.setVisibility(View.VISIBLE);
                    searchResultsAdapter.updateResults(results);
                }
            });
        });
    }

    private void hideSearchResults() {
        recyclerSearchResults.setVisibility(View.GONE);
        searchResultsAdapter.clearResults();
    }

    private void clearSearch() {
        editTextSearch.setText("");
        hideSearchResults();
        btnClearSearch.setVisibility(View.GONE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Shutdown the executor to prevent memory leaks
        if (searchExecutor != null && !searchExecutor.isShutdown()) {
            searchExecutor.shutdown();
            Log.d(TAG, "Search executor shutdown");
        }
    }
}
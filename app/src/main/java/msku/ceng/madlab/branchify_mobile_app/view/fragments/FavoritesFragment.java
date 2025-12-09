package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.FavoritesAdapter;

public class FavoritesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // 1. setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. arrow back logic
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
        // 3. dummy dataset
        List<Song> favList = new ArrayList<>();
        favList.add(new Song("Lorem ipsum dolor", "Lorem Lorem", "1:35"));
        favList.add(new Song("Another Hit", "Artist Name", "3:40"));
        favList.add(new Song("Best Song Ever", "Pop Star", "2:15"));
        favList.add(new Song("Chill Mix", "DJ Cool", "4:20"));
        favList.add(new Song("Jazz Vibes", "Smooth Jazz", "3:00"));
        favList.add(new Song("Rock Anthem", "The Rockers", "5:10"));
        favList.add(new Song("Piano Solo", "Classic Man", "2:50"));

        // 4. connect adapter
        FavoritesAdapter adapter = new FavoritesAdapter(favList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}

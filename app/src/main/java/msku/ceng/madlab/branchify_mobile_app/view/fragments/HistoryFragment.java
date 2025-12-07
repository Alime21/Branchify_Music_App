package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.HistoryAdapter;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // 1. find a RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);

        // 2. line up one below the other
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Dummy dataset
        List<Song> historyList = new ArrayList<>();
        historyList.add(new Song("Lorem ipsum dolor", "Lorem Lorem", "1:35"));
        historyList.add(new Song("Sit Amet Song", "Artist Two", "3:10"));
        historyList.add(new Song("Consectetur", "Band Name", "2:45"));
        historyList.add(new Song("Adipiscing Elit", "Pop Star", "4:20"));
        historyList.add(new Song("Sed Do Eiusmod", "Jazz Trio", "3:00"));
        historyList.add(new Song("Tempor Incididunt", "Rock Band", "2:15"));
        historyList.add(new Song("Ut Labore Et", "Singer One", "3:30"));

        // 4. Connect the adapter
        HistoryAdapter adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast; // Hata mesajı için
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.HistoryContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile.presenter.HistoryPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.HistoryAdapter;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private HistoryPresenter presenter;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Back button
        ImageView btnBack = view.findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> {
            // come back to Home
            if (getActivity() != null) {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_playlists); // Home'a git
                }
            }
        });

        // 1. call the Presenter
        presenter = new HistoryPresenter(this);

        // 2. call the Presenter's method
        presenter.loadHistory();

        return view;
    }


    @Override
    public void showHistoryList(List<Song> songs) {
        adapter = new HistoryAdapter(songs);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
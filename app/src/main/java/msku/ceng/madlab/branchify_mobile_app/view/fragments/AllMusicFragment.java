package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.AllMusicContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.presenter.AllMusicPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.AllMusicAdapter;

public class AllMusicFragment extends Fragment implements AllMusicContract.View {

    private AllMusicPresenter presenter;
    private RecyclerView recyclerView;
    private AllMusicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_music, container, false);

        // RecyclerView
        recyclerView = view.findViewById(R.id.recyclerAllMusic);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Back button
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        presenter = new AllMusicPresenter(this, requireContext());
        presenter.loadAllMusic();

        return view;
    }

    //MVP Methods

    @Override
    public void showAllMusicList(List<Song> songs) {
        adapter = new AllMusicAdapter(songs);
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
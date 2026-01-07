package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.HistoryContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.presenter.HistoryPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.activities.MainActivity;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.HistoryAdapter;

public class HistoryFragment extends Fragment implements HistoryContract.View, HistoryAdapter.OnItemClickListener {

    private HistoryPresenter presenter;
    private RecyclerView recyclerView;
    private List<Song> historyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        presenter = new HistoryPresenter(this, requireContext());
        presenter.loadData();

        return view;
    }

    @Override
    public void showHistory(List<Song> history, List<Song> favorites) {
        this.historyList = history;
        HistoryAdapter adapter = new HistoryAdapter(historyList, favorites, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoading() {
        // You can show a progress bar here
    }

    @Override
    public void hideLoading() {
        // You can hide the progress bar here
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        List<Song> allMusic = ContentResolverHelper.getAllMusicCache();
        if (allMusic == null || allMusic.isEmpty()) {
            Toast.makeText(getContext(), "Local music library not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Song> fullHistoryQueue = historyList.stream()
                .map(historySong -> allMusic.stream()
                        .filter(localSong -> localSong.getTitle().equals(historySong.getTitle()) && localSong.getArtist().equals(historySong.getArtist()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!fullHistoryQueue.isEmpty() && getActivity() instanceof MainActivity) {
            Song clickedSong = historyList.get(position);
            int newPosition = -1;
            for (int i = 0; i < fullHistoryQueue.size(); i++) {
                if (fullHistoryQueue.get(i).getTitle().equals(clickedSong.getTitle()) && fullHistoryQueue.get(i).getArtist().equals(clickedSong.getArtist())) {
                    newPosition = i;
                    break;
                }
            }
            if (newPosition != -1) {
                ((MainActivity) getActivity()).playSong(fullHistoryQueue, newPosition);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity; // Gerekli

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Eksik olabilir

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.presenter.TreePresenter;

public class TreeFragment extends Fragment { // Class başlangıcı şart

    private TreePresenter presenter;
    private LinearLayout genreContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree, container, false);

        presenter = new TreePresenter();
        // XML'e eklediğin popSubContainer'ı burada bağlıyoruz
        genreContainer = view.findViewById(R.id.popSubContainer);

        View btnAdd = view.findViewById(R.id.btnAddPop);
        btnAdd.setOnClickListener(v -> {
            addNewGenreNode("new genre");
        });

        return view;
    }

    private void addNewGenreNode(String genreName) {
        if (getContext() == null) return;

        presenter.addGenre(genreName);

        TextView newBadge = new TextView(getContext());
        newBadge.setText(genreName);
        newBadge.setGravity(Gravity.CENTER);

        newBadge.setBackgroundResource(R.drawable.bg_tree_node);
        newBadge.setPadding(20, 10, 20, 10);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        newBadge.setLayoutParams(params);

        genreContainer.addView(newBadge);
    }
}
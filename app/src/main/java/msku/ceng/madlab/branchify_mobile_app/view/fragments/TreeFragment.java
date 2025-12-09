package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import msku.ceng.madlab.branchify_mobile_app.R;

public class TreeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree, container, false);

        // example: pop node
        TextView nodePop = view.findViewById(R.id.nodePop);
        nodePop.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening Pop Genre...", Toast.LENGTH_SHORT).show();
            // buraya playlist açma kodu gelecek
        });

        // example: jazz button
        TextView nodeJazz = view.findViewById(R.id.nodeJazz);
        nodeJazz.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening Jazz Genre...", Toast.LENGTH_SHORT).show();
        });

        // Add Button
        TextView btnAdd = view.findViewById(R.id.btnAddPop);
        btnAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add new sub-genre logic", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
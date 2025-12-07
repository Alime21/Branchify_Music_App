package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Aşağıdaki importlar çok önemlidir, eksik olursa Override hatası verir
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import msku.ceng.madlab.branchify_mobile_app.R;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout dosyasını bağlıyoruz
        return inflater.inflate(R.layout.fragment_history, container, false);
    }
}
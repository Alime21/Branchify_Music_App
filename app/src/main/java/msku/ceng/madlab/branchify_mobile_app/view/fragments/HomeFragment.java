package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import msku.ceng.madlab.branchify_mobile_app.R;

public class HomeFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // DİKKAT: Her fragment için kendi layout dosyasını buraya yazacağız.
        // Şimdilik hepsi için 'fragment_home' kullanabilirsin veya
        // 'fragment_favorites', 'fragment_tree' diye xml dosyaları oluşturup onları yazabilirsin.
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}

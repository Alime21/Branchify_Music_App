package msku.ceng.madlab.branchify_mobile_app.view.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import msku.ceng.madlab.branchify_mobile_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // close button
        ImageView btnClose = view.findViewById(R.id.btnCloseSettings);
        btnClose.setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);

                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_playlists);
                }
            }
        });
        // notifications
        TextView btnNotifications = view.findViewById(R.id.optionNotifications);
        btnNotifications.setOnClickListener(v ->
            Toast.makeText(getContext(), "Notifications settings", Toast.LENGTH_SHORT).show()
        );
        //about
        TextView btnAbout = view.findViewById(R.id.optionAbout);
        btnAbout.setOnClickListener(v ->
            Toast.makeText(getContext(), "About settings", Toast.LENGTH_SHORT).show()
        );
        // reset tree
        TextView btnResetTree = view.findViewById(R.id.optionResetTree);
        btnResetTree.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Reset Tree settings", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}

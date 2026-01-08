package msku.ceng.madlab.branchify_mobile_app.presenter;

import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.TreeNode; // Modelinde TreeNode olduğunu görüyorum

public class TreePresenter {
    private List<String> genres;

    public TreePresenter() {
        genres = new ArrayList<>();
        // Başlangıç verileri
        genres.add("Pop");
        genres.add("Jazz");
        genres.add("Hiphop");
    }

    public List<String> getGenres() {
        return genres;
    }

    public void addGenre(String newGenre) {
        genres.add(newGenre);
        // Burada istersen Firebase'e (Firestore) de kaydedebilirsin
    }
}
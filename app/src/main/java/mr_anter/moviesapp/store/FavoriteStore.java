package mr_anter.moviesapp.store;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import mr_anter.moviesapp.models.FavoriteModel;

/**
 * Created by mostafa on 22/03/16.
 */
public class FavoriteStore {
    private static final String PREFKEY = "favorites";
    private SharedPreferences favoritePrefs;

    public FavoriteStore(Context context) {
        favoritePrefs = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }

    public List<FavoriteModel> findAll() {

        Map<String, ?> notesMap = favoritePrefs.getAll();

        SortedSet<String> keys = new TreeSet<String>(notesMap.keySet());

        List<FavoriteModel> noteList = new ArrayList<>();
        for (String key : keys) {
            FavoriteModel note = new FavoriteModel();
            note.setTitleKey(key);
            note.setIdValue((String) notesMap.get(key));
            noteList.add(note);
        }

        return noteList;
    }

    public boolean findItem(String value, String key){
        return value.equalsIgnoreCase(favoritePrefs.getString(key, ""));
    }

    public boolean update(FavoriteModel note) {

        SharedPreferences.Editor editor = favoritePrefs.edit();
        editor.putString(note.getTitleKey(), note.getIdValue());
        editor.commit();
        return true;
    }

    public boolean remove(FavoriteModel note) {

        if (favoritePrefs.contains(note.getTitleKey())) {
            SharedPreferences.Editor editor = favoritePrefs.edit();
            editor.remove(note.getTitleKey());
            editor.commit();
        }

        return true;
    }
}

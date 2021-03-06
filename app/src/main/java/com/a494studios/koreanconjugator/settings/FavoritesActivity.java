package com.a494studios.koreanconjugator.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a494studios.koreanconjugator.ConjugationNamesQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.utils.Logger;
import com.a494studios.koreanconjugator.utils.Utils;
import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.parsing.Server;
import com.apollographql.apollo.api.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.observers.DisposableObserver;


public class FavoritesActivity extends AppCompatActivity implements AddFavoriteFragment.AddFavoriteFragmentListener {

    FavoritesAdapter adapter;
    AddFavoriteFragment addFavoriteFragment;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favorites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.fav_listView);
        adapter = new FavoritesAdapter(Utils.getFavorites(this));
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fav_fab);
        fab.setEnabled(false);

        Server.doConjugationNamesQuery((CustomApplication)getApplication())
                .subscribeWith(new DisposableObserver<Response<ConjugationNamesQuery.Data>>() {
                    @Override
                    public void onNext(Response<ConjugationNamesQuery.Data> response) {
                        if (response.getData() == null) {
                            return;
                        }

                        List<String> names = response.getData().conjugationNames();
                        HashMap<String, Boolean> data = new HashMap<>();
                        for (String name : names) {
                            name = name.toLowerCase();
                            boolean showSpeechLevels = false;
                            if (name.contains("informal low")) {
                                showSpeechLevels = true;
                                name = name.replace("informal low", "");
                            } else if (name.contains("informal high")) {
                                showSpeechLevels = true;
                                name = name.replace("informal high", "");
                            } else if (name.contains("formal low")) {
                                showSpeechLevels = true;
                                name = name.replace("formal low", "");
                            } else if (name.contains("formal high")) {
                                showSpeechLevels = true;
                                name = name.replace("formal high", "");
                            }

                            if (!data.containsKey(name)) {
                                data.put(Utils.toTitleCase(name), showSpeechLevels);
                            }
                        }
                        addFavoriteFragment = AddFavoriteFragment.newInstance(data);
                        fab.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                       Utils.handleError(e, FavoritesActivity.this, 9);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if( item.getItemId() == R.id.context_delete) {
            ArrayList<Favorite> favorites = adapter.remove(adapter.getItem(info.position));
            Utils.setFavorites(favorites,this);
            adapter.notifyDataSetChanged();
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFavoriteAdded(Favorite entry) {
        Logger.getInstance().logFavoriteAdded(entry.getName(), entry.getConjugationName(), entry.isHonorific());
        ArrayList<Favorite> favorites = adapter.add(entry);
        Utils.setFavorites(favorites,this);
        adapter.notifyDataSetChanged();
    }

    // FAB's onClick listener
    public void onAddFavorite(View view) {
        addFavoriteFragment.show(getSupportFragmentManager(),"add_fav_frag");
    }

}
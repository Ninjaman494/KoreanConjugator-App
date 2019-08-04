package com.a494studios.koreanconjugator.conjugations;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.parsing.Server;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ConjugationActivity extends AppCompatActivity {

    public static final String EXTRA_STEM = "stem";
    public static final String EXTRA_HONORIFIC = "honorific";
    public static final String EXTRA_ISADJ = "isAdj";

    private ConjugationAnimationHandler animationHandler;
    private RecyclerView recyclerView;
    private boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conjugation);

        final String stem = getIntent().getStringExtra(EXTRA_STEM);
        final boolean honorific = getIntent().getBooleanExtra(EXTRA_HONORIFIC,false);
        final boolean isAdj = getIntent().getBooleanExtra(EXTRA_ISADJ,false);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Conjugations");
            actionBar.setElevation(0);
        }

        setLoading(true);
        getConjugations(stem, honorific, isAdj);
        TextView switchText = findViewById(R.id.conj_switchText);
        ((SwitchCompat)findViewById(R.id.conj_switch)).setOnCheckedChangeListener((compoundButon, checked) -> {
            setLoading(true);
            if(checked) {
                switchText.setText(getString(R.string.honorific_forms));
                getConjugations(stem,true,isAdj);
            } else {
                switchText.setText(getString(R.string.regular_forms));
                getConjugations(stem,false,isAdj);
            }
        });

        View extendedBar = findViewById(R.id.conj_switchBar);
        recyclerView = findViewById(R.id.conj_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        animationHandler = new ConjugationAnimationHandler(extendedBar,recyclerView,this);
        animationHandler.setupScrollAnimations(layoutManager);
    }

    private void getConjugations(String stem, boolean honorific, boolean isAdj) {
        Server.doConjugationQuery(stem, honorific, isAdj, new ApolloCall.Callback<ConjugationQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ConjugationQuery.Data> response) {
                        if(response.data() == null) {
                            return;
                        }

                        List<ConjugationQuery.Conjugation> conjugations = response.data().conjugations();
                        final TreeMap<String,List<ConjugationQuery.Conjugation>> conjMap = new TreeMap<>();
                        for(ConjugationQuery.Conjugation conjugation : conjugations){
                            String type = conjugation.type();
                            if(conjMap.keySet().contains(type)){
                                conjMap.get(type).add(conjugation);
                            }else{
                                List<ConjugationQuery.Conjugation> value = new ArrayList<>();
                                value.add(conjugation);
                                conjMap.put(type,value);
                            }
                        }

                        runOnUiThread(() -> {
                            List<List<ConjugationQuery.Conjugation>> conjugations1 = new ArrayList<>(conjMap.values());
                            recyclerView.setAdapter(new ConjugationCardsAdapter(conjugations1));
                            setLoading(false);
                            dataLoaded = true;
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        animationHandler.animateListView();
    }

    private void setLoading(boolean loading){
        if(loading) {
            findViewById(R.id.conj_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.conj_list).setVisibility(View.GONE);
        } else {
            findViewById(R.id.conj_progress).setVisibility(View.GONE);
            findViewById(R.id.conj_list).setVisibility(View.VISIBLE);

            if(dataLoaded) {
                animationHandler.slideInConjugations();
            } else {
                animationHandler.animateListView();
            }
        }
    }
}

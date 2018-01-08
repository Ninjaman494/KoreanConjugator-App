package com.a494studios.koreanconjugator;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.parsing.Tense;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    public static final String EXTRA_CONJ = "conj";
    public static final String EXTRA_DEF = "definition";

    private String definition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        final TextView defView = findViewById(R.id.defCard_content);
        ArrayList<Conjugation> conjugations = (ArrayList<Conjugation>)getIntent().getSerializableExtra(EXTRA_CONJ);
        if(savedInstanceState != null){
            definition = savedInstanceState.getString(EXTRA_DEF);
        }else{
            definition = getIntent().getStringExtra(EXTRA_DEF);
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Result: "+conjugations.get(0).getInfinitive());
        }

        if(definition == null) {
            Server.requestKorDefinition(conjugations.get(0).getInfinitive(), this, new Server.DefinitionListener() {
                @Override
                public void onDefinitionReceived(String result) {
                    definition = result;
                    defView.setText(definition);
                }

                @Override
                public void onErrorOccurred(String errorMsg) {
                    System.out.println(errorMsg);
                }
            });
        }else{
            defView.setText(definition);
        }

        // Declarative
        ArrayList<Conjugation> decPast = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.PAST);
        ArrayList<Conjugation> decPres = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.PRESENT);
        ArrayList<Conjugation> decFut = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.FUTURE);
        ArrayList<Conjugation> decFutC = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.FUT_COND);
        // Inquisitive
        ArrayList<Conjugation> inqPast = Category.Categories.getSubSet(conjugations, null, Form.INQUISITIVE, Tense.PAST);
        ArrayList<Conjugation> inqPres = Category.Categories.getSubSet(conjugations,null, Form.INQUISITIVE, Tense.PRESENT);
        // Imperative
        ArrayList<Conjugation> imPres = Category.Categories.getSubSet(conjugations,null, Form.IMPERATIVE, Tense.PRESENT);
        // Propositive
        ArrayList<Conjugation> propPres = Category.Categories.getSubSet(conjugations,null, Form.PROPOSITIVE, Tense.PRESENT);
        // Other
        ArrayList<Conjugation> other = Category.Categories.getSubSet(conjugations,Form.NOMINAL,Form.CON_AND,Form.CON_IF);
        // Favorites
        Conjugation past = Category.Categories.getSubSet(conjugations, Formality.INFORMAL_HIGH,Form.DECLARATIVE, Tense.PAST).get(0);
        Conjugation present = Category.Categories.getSubSet(conjugations, Formality.INFORMAL_HIGH,Form.DECLARATIVE, Tense.PRESENT).get(0);
        Conjugation future = Category.Categories.getSubSet(conjugations, Formality.INFORMAL_HIGH,Form.DECLARATIVE, Tense.FUTURE).get(0);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_1,FavoritesFragment.newInstance(past,present,future));
        transaction.replace(R.id.frag_2,ConjugationCardFragment.newInstance("Declarative Past", decPast));
        transaction.replace(R.id.frag_3,ConjugationCardFragment.newInstance("Declarative Present", decPres));
        transaction.replace(R.id.frag_4,ConjugationCardFragment.newInstance("Declarative Future", decFut));
        transaction.replace(R.id.frag_5,ConjugationCardFragment.newInstance("Declarative Future Conditional", decFutC));
        transaction.replace(R.id.frag_6,ConjugationCardFragment.newInstance("Inquisitive Past", inqPast));
        transaction.replace(R.id.frag_7,ConjugationCardFragment.newInstance("Inquisitive Present", inqPres));
        transaction.replace(R.id.frag_8,ConjugationCardFragment.newInstance("Imperative Present", imPres));
        transaction.replace(R.id.frag_9,ConjugationCardFragment.newInstance("Propositive Present", propPres));
        transaction.replace(R.id.frag_10,ConjugationCardFragment.newInstance("Other Forms", other));
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(EXTRA_DEF, definition);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ArrayList<ViewGroup> fragViews = makeFragViewList();
            for (int i = 0; i < fragViews.size(); i++) {
                ViewGroup v = fragViews.get(i);
                Transition t = new Fade(Fade.IN);
                t.setStartDelay(((i + 1) * 80));
                TransitionManager.beginDelayedTransition(v, t);
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    private ArrayList<ViewGroup> makeFragViewList(){
        ArrayList<ViewGroup> views = new ArrayList<>();
        views.add((ViewGroup) findViewById(R.id.disp_defCard));
        views.add((ViewGroup) findViewById(R.id.frag_1));
        views.add((ViewGroup) findViewById(R.id.frag_2));
        views.add((ViewGroup) findViewById(R.id.frag_3));
        views.add((ViewGroup) findViewById(R.id.frag_4));
        views.add((ViewGroup) findViewById(R.id.frag_5));
        views.add((ViewGroup) findViewById(R.id.frag_6));
        views.add((ViewGroup) findViewById(R.id.frag_7));
        views.add((ViewGroup) findViewById(R.id.frag_8));
        views.add((ViewGroup) findViewById(R.id.frag_9));
        views.add((ViewGroup) findViewById(R.id.frag_10));
        return views;
    }
}
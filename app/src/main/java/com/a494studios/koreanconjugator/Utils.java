package com.a494studios.koreanconjugator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.EntrySerializer;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Tense;
import com.a494studios.koreanconjugator.settings.LegalDisplayActivity;
import com.a494studios.koreanconjugator.utils.ErrorDialogFragment;
import com.a494studios.koreanconjugator.utils.SlackListener;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.crashlytics.android.Crashlytics;
import com.eggheadgames.aboutbox.AboutConfig;
import com.eggheadgames.aboutbox.IDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.rm3l.maoni.Maoni;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by akash on 1/9/2018.
 */

public class Utils {
    public static final String PREF_LUCKY_KOR = "pref_luckyKorean";
    public static final String PREF_LUCKY_ENG = "pref_luckyEnglish";
    public static final String PREF_FAV_COUNT = "pref_fav_count";
    private static final String PREF_FAV_VALUES = "FAVORITES_VALUES";
    private static final String PREF_FIRST_BOOT = "FIRST_BOOT";

    public static boolean isFirstBoot(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_FIRST_BOOT,true);
    }

    public static void setFirstBoot(Context context, boolean firstBoot){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_FIRST_BOOT,firstBoot).apply();
    }

    public static boolean getKoreanLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_KOR, false);
    }

    public static boolean getEnglishLuck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LUCKY_ENG, false);
    }

    public static int getFavCount(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_FAV_COUNT, 3);
    }

    public static void setFavorites(ArrayList<Map.Entry<String,Category[]>> data, Context context){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(PREF_FAV_VALUES,gson.toJson(data));
        editor.putInt(PREF_FAV_COUNT,data.size());
        editor.apply();
    }

    public static ArrayList<Map.Entry<String,Category[]>> getFavorites(Context context) {
        String jsonString = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FAV_VALUES,"");
        if(jsonString.isEmpty()){
            return new ArrayList<>();
        }

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Map.Entry.class, new EntrySerializer());
        java.lang.reflect.Type type = new TypeToken<ArrayList<Map.Entry<String,Category[]>>>(){}.getType();
        return builder.create().fromJson(jsonString,type);
    }

    public static boolean isHangul(String korean){
        if(korean.isEmpty()){
            return false;
        }

        korean = korean.replace(" ","");
        for(int i=0;i<korean.length();i++){
            char c = korean.charAt(i);
            if(!((int)c >= '가' && (int)c <= '힣')){
                return false;
            }
        }
        return true;
    }

    public static Formality generateFormality(String type) {
        if (type.contains(Formality.INFORMAL_LOW.toString())) {
            return Formality.INFORMAL_LOW;
        } else if (type.contains(Formality.INFORMAL_HIGH.toString())) {
            return Formality.INFORMAL_HIGH;
        } else if (type.contains(Formality.FORMAL_LOW.toString())) {
            return Formality.FORMAL_LOW;
        } else if (type.contains(Formality.FORMAL_HIGH.toString())) {
            return Formality.FORMAL_HIGH;
        } else if(type.contains(Formality.HONORIFIC_LOW.toString())) {
            return Formality.HONORIFIC_LOW;
        }else if(type.contains(Formality.HONORIFIC_HIGH.toString())){
            return Formality.HONORIFIC_HIGH;
        }else {
            return Formality.NONE;
        }
    }

    public static Tense generateTense(String type){
        if(type.contains(Tense.FUT_COND.toString())){
            return Tense.FUT_COND;
        }else if(type.contains(Tense.FUTURE.toString())){
            return Tense.FUTURE;
        }else if(type.contains(Tense.PRESENT.toString())){
            return Tense.PRESENT;
        }else if(type.contains(Tense.PAST.toString())){
            return Tense.PAST;
        }else{
            return Tense.NONE;
        }
    }

    public static Form generateForm(String type) {
        if (type.contains(Form.DECLARATIVE.toString())) {
            return Form.DECLARATIVE;
        } else if (type.contains(Form.INQUISITIVE.toString())) {
            return Form.INQUISITIVE;
        } else if (type.contains(Form.IMPERATIVE.toString())) {
            return Form.IMPERATIVE;
        } else if (type.contains(Form.PROPOSITIVE.toString())) {
            return Form.PROPOSITIVE;
        } else if (type.contains(Form.CON_IF.toString())) {
            return Form.CON_IF;
        } else if (type.contains(Form.CON_AND.toString())) {
            return Form.CON_AND;
        } else if (type.contains(Form.CON_BUT.toString())){
            return Form.CON_BUT;
        } else if (type.contains(Form.NOMINAL.toString())) {
            return Form.NOMINAL;
        } else if (type.contains(Form.PAST_BASE.toString())) {
            return Form.PAST_BASE;
        } else if (type.contains(Form.FUTURE_BASE.toString())) {
            return Form.FUTURE_BASE;
        }else if (type.contains(Form.ADJ.toString())) {
            return Form.ADJ;
        } else {
            return Form.UNKNOWN;
        }
    }

    public static void makeAboutBox(final Activity activity){
        final AboutConfig aboutConfig = AboutConfig.getInstance();
        aboutConfig.appName = activity.getString(R.string.app_name);
        aboutConfig.appIcon = R.mipmap.ic_launcher;
        aboutConfig.version = BuildConfig.VERSION_NAME;
        aboutConfig.author = "494 Studios";
        aboutConfig.aboutLabelTitle = "About App";
        aboutConfig.packageName = activity.getPackageName();
        aboutConfig.buildType = AboutConfig.BuildType.GOOGLE;
        aboutConfig.appPublisher = "494 Studios"; // app publisher for "Try Other Apps" item
        // Contact Support email details
        aboutConfig.emailAddress = "494studios@gmail.com";
        aboutConfig.emailSubject = "Hanji - Contact Us";
        aboutConfig.privacyHtmlPath = "file:///android_asset/PrivacyPolicy.html";
        aboutConfig.acknowledgmentHtmlPath = "www.google.com";
        // Custom handler for Acknowledgements and Privacy Policy options
        aboutConfig.dialog = new IDialog() {
            @Override
            public void open(AppCompatActivity appCompatActivity, String url, String tag) {
                if(tag.equals(activity.getString(R.string.egab_privacy_policy))) {
                    Intent intent = new Intent(activity,LegalDisplayActivity.class);
                    intent.putExtra("type",LegalDisplayActivity.TYPE_PRIV_POLICY);
                    activity.startActivity(intent);
                }else if(tag.equals(activity.getString(R.string.egab_acknowledgements))){
                    new LibsBuilder()
                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                            .withExcludedLibraries("support_cardview","support_v4","support_annotations","AppCompat","appcompat_v7","recyclerview_v7","GooglePlayServices","design","volleyplus")
                            .withLicenseDialog(true)
                            .withLicenseShown(true)
                            .withActivityTitle("Libraries Used")
                            .withLibraries("aboutBox","linear_list","transitions")
                            .start(activity);
                }
            }
        };
    }

    @Nullable
    public static Maoni makeMaoniActivity(AppCompatActivity context){
        SlackListener listener = new SlackListener(context);
        if(!listener.auth()){
            displayErrorDialog(context,"Can't Connect to Server","Check your network settings and try again",null);
            return null;
        }
        return new Maoni.Builder(context, "com.a494studios.koreanconjugator.fileprovider")
                .enableScreenCapturingFeature()
                .withLogsCapturingFeature(false)
                .withListener(listener)
                .withHeader(R.drawable.feedback_header)
                .withTheme(R.style.AppTheme_NoActionBar)
                .build();
    }

    public static void handleError(Exception error, AppCompatActivity context, DialogInterface.OnClickListener listener){
        ErrorDialogFragment fragment;
        if(error instanceof NoConnectionError){
            fragment = ErrorDialogFragment.newInstance("Can't load results",
                    "Check your network settings and try again");
        } else if(error instanceof ParseError) {
            fragment = ErrorDialogFragment.newInstance("Can't read results",
                    "A response was given that we couldn't understand");
        }else{
            Crashlytics.log("Unrecognized Error: "+ error.toString());
            fragment = ErrorDialogFragment.newInstance("Something went wrong",
                    "Try again later or contact support");
        }

        if(listener != null){
            fragment.setListener(listener);
        }
        context.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment,"frag_alert")
                .commitAllowingStateLoss();
    }

    public static void handleError(Exception error, AppCompatActivity context) {
        handleError(error,context,null);
    }

    public static void displayErrorDialog(AppCompatActivity context, String title, String msg,DialogInterface.OnClickListener listener){
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(title, msg);
        if(listener != null){
            fragment.setListener(listener);
        }

        context.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment,"frag_alert")
                .commitAllowingStateLoss();
    }
}

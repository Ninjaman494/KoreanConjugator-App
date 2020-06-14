package com.a494studios.koreanconjugator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.display.cards.DisplayCardBody;
import com.a494studios.koreanconjugator.parsing.Server;
import com.apollographql.apollo.api.Response;

import io.reactivex.observers.DisposableObserver;

public class WordOfDayCard implements DisplayCardBody {
    private View view;
    private String id;


    @SuppressLint("CheckResult")
    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_wod,parentView);
        }

        Server.doWODQuery()
                .subscribeWith(new DisposableObserver<Response<WordOfTheDayQuery.Data>>() {
                    @Override
                    public void onNext(Response<WordOfTheDayQuery.Data> dataResponse) {
                        TextView textView = view.findViewById(R.id.wod_text);
                        textView.setText(dataResponse.data().wordOfTheDay.term);

                        id = dataResponse.data().wordOfTheDay.id;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return view;
    }

    @Override
    public void onButtonClick() {
        Intent intent = new Intent(view.getContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        view.getContext().startActivity(intent);
    }

    @Override
    public boolean shouldHideButton() {
        return false;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getButtonText() {
        return view.getContext().getString(R.string.see_entry);
    }

    @Override
    public String getHeading() {
        return "Word of the Day";
    }
}
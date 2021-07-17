package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.LEGACY)
public class AdCardUnitTest {
    private AdCard card;
    private DisplayCardView cardView;
    private LinearLayout viewGroup;

    @Before
    public void init() {
        Context context = ApplicationProvider.getApplicationContext();
        card = new AdCard("Ad unit id");
        cardView = new DisplayCardView(context);
        viewGroup = new LinearLayout(context);
    }

    @Test
    public void test_shouldHideButton() {
        card.addBodyView(ApplicationProvider.getApplicationContext(), viewGroup, cardView);
        assertEquals(cardView.findViewById(R.id.displayCard_button).getVisibility(), ViewGroup.GONE);
    }

    @Test
    public void test_getCount() {
        assertEquals(1, card.getCount());
    }

    @Test
    public void test_getHeading() {
        assertEquals("Ad", card.getHeading());
    }
}

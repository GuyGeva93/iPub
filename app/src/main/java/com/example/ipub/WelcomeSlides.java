package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class WelcomeSlides extends AppCompatActivity {

    private WelcomeSlidesAdapter welcomeSlidesAdapter;
    private LinearLayout layoutSlidesIndicators;
    private Button button;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_slides);
        tinyDB = new TinyDB(this);
        if (tinyDB.getBoolean("welcomeSlides") == true) {
            startActivity(new Intent(WelcomeSlides.this, MainActivity.class));
        }

        layoutSlidesIndicators = findViewById(R.id.WelcomeSlidesLayoutIndicators);
        button = findViewById(R.id.WelcomeSlidesButton);

        setupWelcomeSlidesItems();

        final ViewPager2 WelcomeSlidesViewPager = findViewById(R.id.WelcomeSlidesViewPager);
        WelcomeSlidesViewPager.setAdapter(welcomeSlidesAdapter);

        setupSlidesIndicators();
        setCurrentSlideIndicator(0);


        WelcomeSlidesViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSlideIndicator(position);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WelcomeSlidesViewPager.getCurrentItem() + 1 < welcomeSlidesAdapter.getItemCount()) {
                    WelcomeSlidesViewPager.setCurrentItem(WelcomeSlidesViewPager.getCurrentItem() + 1);
                } else {
                    tinyDB.putBoolean("welcomeSlides", true);
                    startActivity(new Intent(WelcomeSlides.this, MainActivity.class));
                    finish();
                }
            }
        });

    }
    // Setting the slides list , adding descriptions and images resources.
    private void setupWelcomeSlidesItems() {

        List<WelcomeSlidesItem> welcomeSlidesItems = new ArrayList<>();
        WelcomeSlidesItem slide1 = new WelcomeSlidesItem();
        slide1.setTitle("iPub");
        slide1.setDescription("ברוך הבא!");
        slide1.setImage(R.drawable.slide_image_welcome);

        WelcomeSlidesItem slide2 = new WelcomeSlidesItem();
        slide2.setTitle("ניווט בלחיצה!");
        slide2.setDescription("נווט בקלות אל הפאב הנבחר");
        slide2.setImage(R.drawable.slide_navigate_image);

        WelcomeSlidesItem slide3 = new WelcomeSlidesItem();
        slide3.setTitle("דירוג פאבים");
        slide3.setDescription("ראה דירוגים של אחרים ודרג בעצמך!");
        slide3.setImage(R.drawable.slide_image_star);

        WelcomeSlidesItem slide4 = new WelcomeSlidesItem();
        slide4.setTitle("אפשרויות סינון");
        slide4.setDescription("סנן את החיפוש על פי אזורים, כשרויות ודירוג");
        slide4.setImage(R.drawable.slides_image_filter);

        WelcomeSlidesItem slide5 = new WelcomeSlidesItem();
        slide5.setTitle("מנהל פאב?");
        slide5.setDescription("צור קשר וקבל גישה לדף ניהול אישי לפאב שלך!");
        slide5.setImage(R.drawable.slide_image_manager);

        WelcomeSlidesItem slide6 = new WelcomeSlidesItem();
        slide6.setTitle("בוא נתחיל!");
        slide6.setImage(R.drawable.slide_image_start);

        welcomeSlidesItems.add(slide1);
        welcomeSlidesItems.add(slide2);
        welcomeSlidesItems.add(slide3);
        welcomeSlidesItems.add(slide4);
        welcomeSlidesItems.add(slide5);
        welcomeSlidesItems.add(slide6);

        welcomeSlidesAdapter = new WelcomeSlidesAdapter(welcomeSlidesItems);

    }

    // setting up grey dots as the number of the slides in the list.
    private void setupSlidesIndicators() {
        ImageView[] indicators = new ImageView[welcomeSlidesAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.slides_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutSlidesIndicators.addView(indicators[i]);
        }
    }

    // setting a blue dot on the current slide showing
    private void setCurrentSlideIndicator(int index) {
        int childCount = layoutSlidesIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutSlidesIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.slides_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.slides_indicator_inactive)
                );
            }
        }
        if (index == welcomeSlidesAdapter.getItemCount() - 1) {
            button.setText("התחל");
        } else {
            button.setText("הבא");
        }

    }

}
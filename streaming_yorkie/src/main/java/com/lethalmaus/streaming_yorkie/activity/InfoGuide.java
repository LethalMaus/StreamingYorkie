package com.lethalmaus.streaming_yorkie.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lethalmaus.streaming_yorkie.R;

/**
 * Activity for showing ChannelEntity Guide
 * @author LethalMaus
 */
public class InfoGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_guide);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Button menusButton = findViewById(R.id.guide_menus_button);
        menusButton.setOnClickListener((View v) -> {
            ConstraintLayout menusView = findViewById(R.id.guide_menus);
            if (menusView.getChildCount() <= 0) {
                resetViews();
                menusButton.setBackgroundResource(R.drawable.button_selected);
                View menus = LayoutInflater.from(InfoGuide.this).inflate(R.layout.info_guide_menus, menusView, false);
                menusView.addView(menus, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                resetViews();
            }
        });

        final Button categoriesButton = findViewById(R.id.guide_categories_button);
        categoriesButton.setOnClickListener((View v) -> {
            ConstraintLayout categoriesView = findViewById(R.id.guide_categories);
            if (categoriesView.getChildCount() <= 0) {
                resetViews();
                categoriesButton.setBackgroundResource(R.drawable.button_selected);
                View categories = LayoutInflater.from(InfoGuide.this).inflate(R.layout.info_guide_categories, categoriesView, false);
                categoriesView.addView(categories, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                resetViews();
            }
        });

        final Button actionsButton = findViewById(R.id.guide_actions_button);
        actionsButton.setOnClickListener((View v) -> {
            ConstraintLayout actionsView = findViewById(R.id.guide_actions);
            if (actionsView.getChildCount() <= 0) {
                resetViews();
                actionsButton.setBackgroundResource(R.drawable.button_selected);
                View actions = LayoutInflater.from(InfoGuide.this).inflate(R.layout.info_guide_actions, actionsView, false);
                actionsView.addView(actions, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                resetViews();
            }
        });

        //Link to StreamingYorkie Guide on Github
        Button readme = findViewById(R.id.guide_readme);
        readme.setOnClickListener((View v) ->
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#guide")))
        );
    }

    //The only option is the back button for finishing the activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    /**
     * Resets Views and buttons to maintain performance (too many views)
     * @author LethalMaus
     */
    private void resetViews() {
        findViewById(R.id.guide_menus_button).setBackgroundResource(R.drawable.button);
        findViewById(R.id.guide_categories_button).setBackgroundResource(R.drawable.button);
        findViewById(R.id.guide_actions_button).setBackgroundResource(R.drawable.button);
        ViewGroup menus = findViewById(R.id.guide_menus);
        menus.removeAllViews();
        ViewGroup categories = findViewById(R.id.guide_categories);
        categories.removeAllViews();
        ViewGroup actions = findViewById(R.id.guide_actions);
        actions.removeAllViews();
    }
}

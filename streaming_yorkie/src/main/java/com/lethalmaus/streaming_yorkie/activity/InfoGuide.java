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

        final Button menus_button = findViewById(R.id.guide_menus_button);
        menus_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConstraintLayout menus_view = findViewById(R.id.guide_menus);
                        if (menus_view.getChildCount() <= 0) {
                            resetViews();
                            menus_button.setBackgroundResource(R.drawable.button_selected);
                            View menus = LayoutInflater.from(InfoGuide.this).inflate(R.layout.info_guide_menus, menus_view, false);
                            menus_view.addView(menus, new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
                        } else {
                            resetViews();
                        }
                    }
                });

        final Button categories_button = findViewById(R.id.guide_categories_button);
        categories_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConstraintLayout categories_view = findViewById(R.id.guide_categories);
                        if (categories_view.getChildCount() <= 0) {
                            resetViews();
                            categories_button.setBackgroundResource(R.drawable.button_selected);
                            View categories = LayoutInflater.from(InfoGuide.this).inflate(R.layout.info_guide_categories, categories_view, false);
                            categories_view.addView(categories, new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
                        } else {
                            resetViews();
                        }
                    }
                });

        final Button actions_button = findViewById(R.id.guide_actions_button);
        actions_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConstraintLayout actions_view = findViewById(R.id.guide_actions);
                        if (actions_view.getChildCount() <= 0) {
                            resetViews();
                            actions_button.setBackgroundResource(R.drawable.button_selected);
                            View actions = LayoutInflater.from(InfoGuide.this).inflate(R.layout.info_guide_actions, actions_view, false);
                            actions_view.addView(actions, new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
                        } else {
                            resetViews();
                        }
                    }
                });

        //Link to StreamingYorkie Guide on Github
        Button readme = findViewById(R.id.guide_readme);
        readme.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#guide"));
                        startActivity(intent);
                    }
                });
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

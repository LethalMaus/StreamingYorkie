package com.lethalmaus.twitchfollowerservice;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ViewDefinitions {

    Context context;

    protected ConstraintLayout userTableRow(Context context, String userLogo, String username, ImageButton button1, ImageButton button2, ImageButton button3) {

        this.context = context;
        float dpScale = context.getResources().getDisplayMetrics().density;
        int margin = (int) (8 * dpScale);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,90));
        imageView.setAdjustViewBounds(true);
        Glide.with(context).load(userLogo).into(imageView);
        imageView.setId(View.generateViewId());

        TextView textView = new TextView(context);
        textView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,90));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(username);
        textView.setId(View.generateViewId());

        setUserTableRowButton(button1);
        setUserTableRowButton(button2);
        setUserTableRowButton(button3);

        ConstraintLayout tableRow = new ConstraintLayout(context);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,90);
        params.setMargins( 0, margin, 0, 0);
        tableRow.setLayoutParams(params);
        tableRow.setId(View.generateViewId());
        tableRow.addView(imageView);
        tableRow.addView(textView);
        tableRow.addView(button1);
        tableRow.addView(button2);
        tableRow.addView(button3);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(tableRow);

        constraintSet.setMargin(imageView.getId(), ConstraintSet.START, margin);
        constraintSet.setMargin(imageView.getId(), ConstraintSet.TOP, margin);
        constraintSet.setMargin(imageView.getId(), ConstraintSet.BOTTOM, margin);
        constraintSet.connect(imageView.getId(), ConstraintSet.START, tableRow.getId(), ConstraintSet.START, margin);
        constraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, tableRow.getId(), ConstraintSet.BOTTOM, margin);
        constraintSet.connect(imageView.getId(), ConstraintSet.TOP, tableRow.getId(), ConstraintSet.TOP, margin);

        constraintSet.setMargin(textView.getId(), ConstraintSet.START, margin);
        constraintSet.setMargin(textView.getId(), ConstraintSet.END, margin);
        constraintSet.setMargin(textView.getId(), ConstraintSet.TOP, margin);
        constraintSet.setMargin(textView.getId(), ConstraintSet.BOTTOM, margin);
        constraintSet.connect(textView.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, tableRow.getId(), ConstraintSet.TOP);
        constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, tableRow.getId(), ConstraintSet.BOTTOM);

        constraintSet.setMargin(button1.getId(), ConstraintSet.END, margin);
        constraintSet.setMargin(button1.getId(), ConstraintSet.TOP, margin);
        constraintSet.setMargin(button1.getId(), ConstraintSet.BOTTOM, margin);
        constraintSet.connect(button1.getId(), ConstraintSet.END, tableRow.getId(), ConstraintSet.END);
        constraintSet.connect(button1.getId(), ConstraintSet.TOP, tableRow.getId(), ConstraintSet.TOP);
        constraintSet.connect(button1.getId(), ConstraintSet.BOTTOM, tableRow.getId(), ConstraintSet.BOTTOM);

        constraintSet.setMargin(button2.getId(), ConstraintSet.END, margin);
        constraintSet.setMargin(button2.getId(), ConstraintSet.TOP, margin);
        constraintSet.setMargin(button2.getId(), ConstraintSet.BOTTOM, margin);
        constraintSet.connect(button2.getId(), ConstraintSet.END, button1.getId(), ConstraintSet.START);
        constraintSet.connect(button2.getId(), ConstraintSet.TOP, tableRow.getId(), ConstraintSet.TOP);
        constraintSet.connect(button2.getId(), ConstraintSet.BOTTOM, tableRow.getId(), ConstraintSet.BOTTOM);

        constraintSet.setMargin(button3.getId(), ConstraintSet.END, margin);
        constraintSet.setMargin(button3.getId(), ConstraintSet.TOP, margin);
        constraintSet.setMargin(button3.getId(), ConstraintSet.BOTTOM, margin);
        constraintSet.connect(button3.getId(), ConstraintSet.END, button2.getId(), ConstraintSet.START);
        constraintSet.connect(button3.getId(), ConstraintSet.TOP, tableRow.getId(), ConstraintSet.TOP);
        constraintSet.connect(button3.getId(), ConstraintSet.BOTTOM, tableRow.getId(), ConstraintSet.BOTTOM);

        constraintSet.applyTo(tableRow);
        return tableRow;
    }

    protected void setUserTableRowButton(ImageButton button) {
        if (!button.hasOnClickListeners()) {
            button.setVisibility(View.GONE);
        } else {
            button.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 130));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setAdjustViewBounds(true);
        }
        button.setId(View.generateViewId());
    }
}

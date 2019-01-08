package com.lethalmaus.twitchfollowerservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/*FIXME
request timeout takes too long (test on local device)
 */

/*TODO
settings & properties
automize follow/unfollow
documentation & support
github cleanup
app icon & name
RELEASE!!
logout.xml design
multiple user prefs (log out & keep info?)
refactor buttons
sort fonts
error logging
streaming schedule
host4host
load user portion wise if possible (this might need a big refactor)
 */
public class MainActivity extends AppCompatActivity {

    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        globals = new Globals(MainActivity.this, getApplicationContext());
        if (!globals.userLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, Authorization.class);
            startActivity(intent);
        }
        globals.showUser();

        ImageButton user = findViewById(R.id.userinfo_menu);
        user.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, User.class);
                        startActivity(intent);
                    }
                });

        ImageButton followers = findViewById(R.id.followers_menu);
        followers.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Followers.class);
                        startActivity(intent);
                    }
                });

        ImageButton authorization = findViewById(R.id.authorization_menu);
        authorization.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Authorization.class);
                        startActivity(intent);
                    }
                });

        ImageButton following = findViewById(R.id.following_menu);
        following.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Following.class);
                        startActivity(intent);
                    }
                });

        ImageButton f4f = findViewById(R.id.f4f_menu);
        f4f.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Follow4Follow.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        globals.showUser();
    }
}
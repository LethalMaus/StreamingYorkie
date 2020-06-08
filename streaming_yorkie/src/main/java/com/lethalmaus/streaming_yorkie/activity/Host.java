package com.lethalmaus.streaming_yorkie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.AutoHostRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowersUpdateRequestHandler;
import com.lethalmaus.streaming_yorkie.request.HostedByRequestHandler;
import com.lethalmaus.streaming_yorkie.request.HostingRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for Hosting view that extends UserParent
 * @author LethalMaus
 */
public class Host extends UserParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        final String daoType = "HOST";
        findViewById(R.id.page1).setVisibility(View.GONE);
        findViewById(R.id.count1).setVisibility(View.GONE);

        final ImageButton autoHost = findViewById(R.id.page2);
        autoHost.setOnClickListener((View v) -> {
            requestHandler = new AutoHostRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
            pageButtonListenerAction(autoHost, "Auto Host", daoType, "AUTOHOST", Globals.HOST_BUTTON, Globals.DELETE_BUTTON);
        });
        final ImageButton hostedByButton = findViewById(R.id.page3);
        hostedByButton.setOnClickListener((View v) -> {
            requestHandler = new HostedByRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
            pageButtonListenerAction(hostedByButton, "Hosted By", daoType, "HOSTED_BY", null, null);
        });
        final ImageButton hostingButton = findViewById(R.id.page4);
        hostingButton.setOnClickListener((View v) -> {
            requestHandler = new HostingRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
            pageButtonListenerAction(hostingButton, "Hosting", daoType, "HOSTING", null, null);

        });
        requestHandler = new FollowersUpdateRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
        pageButtonListenerAction(autoHost, "Auto Host", daoType,"AUTOHOST", Globals.HOST_BUTTON, Globals.DELETE_BUTTON);
    }
}
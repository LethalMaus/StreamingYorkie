package com.lethalmaus.streaming_yorkie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.PurchaseMadeRequestHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.worker.AutoFollowWorker;
import com.lethalmaus.streaming_yorkie.worker.AutoVODExportWorker;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Main Activity. If the channel isn't logged in then the activity changes to Authorization.
 * Otherwise it shows the menu.
 * @author LethalMaus
 */
public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.streaming_yorkie);
        }

        Globals.createNotificationChannel(new WeakReference<>(getApplicationContext()), Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_NAME, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION);

        findViewById(R.id.menu_followers).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Followers.class))
        );

        findViewById(R.id.menu_following).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Following.class))
        );

        findViewById(R.id.menu_f4f).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Follow4Follow.class))
        );

        findViewById(R.id.menu_vod).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, VODs.class))
        );

        findViewById(R.id.menu_multi).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, MultiView.class))
        );

        findViewById(R.id.menu_lurk).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Lurk.class))
        );

        findViewById(R.id.menu_userinfo).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Channel.class))
        );

        findViewById(R.id.menu_info).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Info.class))
        );

        findViewById(R.id.menu_shop).setOnClickListener((View v) ->
                startActivity(new Intent(MainActivity.this, Shop.class))
        );

        findViewById(R.id.menu_settings).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, SettingsMenu.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userLoggedIn()) {
            Globals.activateWorker(new WeakReference<>(getApplicationContext()), Globals.FILE_SETTINGS_F4F, Globals.SETTINGS_AUTOFOLLOW, AutoFollowWorker.class, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION);
            Globals.activateWorker(new WeakReference<>(getApplicationContext()), Globals.FILE_SETTINGS_VOD, Globals.SETTINGS_AUTOVODEXPORT, AutoVODExportWorker.class, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION);
            Globals.activateAlarm(new WeakReference<>(getApplicationContext()), Globals.SETTINGS_AUTOLURK, 10000);
            checkPurchases();
        }
    }

    /**
     * Method for checking if purchases have been made and are still active
     * @author LethalMaus
     */
    private void checkPurchases() {
        billingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && billingClient.isReady()) {
                    checkForActiveSubs();
                    checkPurchaseHistory(BillingClient.SkuType.INAPP, false);
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(MainActivity.this, "Cannot connect to Google Play Store", Toast.LENGTH_SHORT).show();
            }
        });
        if (new File(getFilesDir().toString() + File.separator + Globals.FILE_SUPPORTER).exists() || new File(getFilesDir().toString() + File.separator + Globals.FILE_SUBSCRIBER).exists()) {
            findViewById(R.id.supporterTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.supporterDescription).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.supporterTitle).setVisibility(View.GONE);
            findViewById(R.id.supporterDescription).setVisibility(View.GONE);
        }
    }

    /**
     * Method for checking active subs
     * @author LethalMaus
     */
    private void checkForActiveSubs() {
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        boolean subActive = false;
        if (purchasesResult.getPurchasesList() != null) {
            for (Purchase purchase : purchasesResult.getPurchasesList()) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAutoRenewing()) {
                    subActive = true;
                }
                if (!purchasesResult.getPurchasesList().isEmpty() && !new File(getFilesDir().toString() + File.separator + Globals.FILE_SUPPORTER).exists()) {
                    new WriteFileHandler(new WeakReference<>(MainActivity.this), new WeakReference<>(getApplicationContext()), Globals.FILE_SUPPORTER, null, purchase.getPurchaseToken(), true).run();
                }
            }
            if (!subActive && new File(getFilesDir().toString() + File.separator + Globals.FILE_SUBSCRIBER).exists()) {
                subNowInactive();
            }
        }
    }

    /**
     * Method for handling inactive subs
     * @author LethalMaus
     */
    private void subNowInactive() {
        new Thread(() -> {
            try {
                JSONObject postBody = new JSONObject();
                String username = StreamingYorkieDB.getInstance(getApplicationContext()).channelDAO().getChannel().getDisplay_name();
                postBody.put("content", username + " is no longer a subscriber");
                new PurchaseMadeRequestHandler(new WeakReference<>(MainActivity.this), new WeakReference<>(getApplicationContext())) {
                    @Override
                    public void responseHandler(final JSONObject response) {
                        new DeleteFileHandler(new WeakReference<>(MainActivity.this), new WeakReference<>(getApplicationContext()), Globals.FILE_SUBSCRIBER).run();
                    }
                }.setPostBody(postBody).sendRequest(false);
            } catch (JSONException e) {
                new WriteFileHandler(new WeakReference<>(MainActivity.this), new WeakReference<>(getApplicationContext()), Globals.FILE_ERROR, null, "Error informing developer of subscription status" + " | " + e.toString(), true).run();
            }
        }).start();
    }

    /**
     * Checks purchase history of user
     * @author LethalMaus
     * @param purchaseType subs or in-app
     * @param checkedBoth stops loop after both types are checked
     */
    private void checkPurchaseHistory(String purchaseType, Boolean checkedBoth) {
        if (!new File(getFilesDir().toString() + File.separator + Globals.FILE_SUPPORTER).exists()) {
            billingClient.queryPurchaseHistoryAsync(purchaseType, (BillingResult billingResult, List<PurchaseHistoryRecord> purchasesList) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchasesList != null && !purchasesList.isEmpty()) {
                    for (PurchaseHistoryRecord purchase : purchasesList) {
                        new WriteFileHandler(new WeakReference<>(MainActivity.this), new WeakReference<>(getApplicationContext()), Globals.FILE_SUPPORTER, null, purchase.getPurchaseToken(), true).run();
                    }
                    if (!checkedBoth) {
                        checkPurchaseHistory(BillingClient.SkuType.SUBS, true);
                    }
                }
            });
        }
    }

    /**
     * Checks if a ChannelEntity has logged in before then it updates & displays the Username & Logo. Otherwise it starts a login process.
     * @author LethalMaus
     * @return boolean if user is logged in or not
     */
    private boolean userLoggedIn() {
        if (!new File(getFilesDir().toString() + File.separator + Globals.FILE_TOKEN).exists()) {
            startActivity(new Intent(MainActivity.this, Authorization.class));
            return false;
        } else if ((new Date().getTime() - new File(getFilesDir().toString() + File.separator + Globals.FILE_TOKEN).lastModified()) > 5184000000L) {
            new DeleteFileHandler(new WeakReference<>(this), new WeakReference<>(getApplicationContext()), null).deleteFileOrPath(Globals.FILE_TOKEN);
            startActivity(new Intent(MainActivity.this, Authorization.class));
            return false;
        }
        new UserRequestHandler(new WeakReference<>(this), new WeakReference<>(getApplicationContext())).sendRequest(false);
        return true;
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                Globals.handlePurchase(new WeakReference<>(this), new WeakReference<>(getApplicationContext()), billingClient, purchase);
            }
        }
    }
}
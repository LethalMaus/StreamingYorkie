package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recycler View Adapter for shop SKUs
 * @author LethalMaus
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> implements PurchasesUpdatedListener {

    private BillingClient billingClient;

    private static final HashMap<String, List<String>> SKUS;
    static
    {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList("appropriate_badger", "impressive_deer", "substantial_fox"));
        SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("familiar_giraffe", "competitive_manatee"));
    }

    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private List<SkuDetails> items;

    /**
     * Simple View Holder for loading the View
     * @author LethalMaus
     */
    static class ShopViewHolder extends RecyclerView.ViewHolder {
        View shopRow;
        /**
         * Holder for shop view
         * @param shopRow View for shop row
         */
        ShopViewHolder(View shopRow) {
            super(shopRow);
            this.shopRow = shopRow;
        }
    }

    /**
     * Adapter for displaying a Shop SKUs
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public ShopAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        setupBillingClient();
    }

    @Override
    @NonNull
    public ShopAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View shopRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_row, parent, false);
        return new ShopViewHolder(shopRow);
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     */
    public void datasetChanged() {
        notifyDataSetChanged();
        weakActivity.get().runOnUiThread(() -> {
            if (getItemCount() > 0) {
                weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
                weakActivity.get().findViewById(R.id.emptyshop).setVisibility(View.GONE);
            } else {
                weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                weakActivity.get().findViewById(R.id.emptyshop).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopViewHolder shopViewHolder, final int position) {
        SkuDetails skuDetails = items.get(position);
        TextView title = shopViewHolder.shopRow.findViewById(R.id.title);
        String skuTitleAppNameRegex = "(?> \\(.+?\\))$";
        Pattern pattern = Pattern.compile(skuTitleAppNameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(skuDetails.getTitle());
        String titleWithoutAppName = matcher.replaceAll("");
        title.setText(titleWithoutAppName);
        TextView description = shopViewHolder.shopRow.findViewById(R.id.description);
        description.setText(skuDetails.getDescription());
        TextView price = shopViewHolder.shopRow.findViewById(R.id.price);
        price.setText(skuDetails.getPrice());

        ImageView button = shopViewHolder.shopRow.findViewById(R.id.buyButton);
        if (!skuDetails.getSubscriptionPeriod().isEmpty() && new File(weakActivity.get().getFilesDir().toString() + File.separator + Globals.FILE_SUBSCRIBER).exists()) {
            button.setImageResource(R.drawable.error);
            button.setOnClickListener((View view) ->
                Toast.makeText(weakActivity.get(), "You already have a subscription, if you wish to change, please cancel the original subscription.", Toast.LENGTH_SHORT).show()
            );
        } else {
            button.setImageResource(R.drawable.buy);
            button.setOnClickListener((View view) -> {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                billingClient.launchBillingFlow(weakActivity.get(), flowParams);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    /**
     * Sets up the billing client for this adapter
     * @author LethalMaus
     */
    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(weakActivity.get()).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && billingClient.isReady()) {
                    items = new ArrayList<>();
                    querySkuDetailsAsync(BillingClient.SkuType.INAPP, SKUS.get(BillingClient.SkuType.INAPP));
                    if (billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        querySkuDetailsAsync(BillingClient.SkuType.SUBS, SKUS.get(BillingClient.SkuType.SUBS));
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                if (Globals.checkWeakActivity(weakActivity)) {
                    Toast.makeText(weakActivity.get(), "Cannot connect to Google Play Store", Toast.LENGTH_SHORT).show();
                    weakActivity.get().onBackPressed();
                }
            }
        });
    }

    /**
     * Queries for SKUs and adds them to the recycler view
     * @author LethalMaus
     * @param itemType SKU type
     * @param skuList List of SKUs
     */
    private void querySkuDetailsAsync(@BillingClient.SkuType final String itemType, final List<String> skuList) {
        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(itemType).build();
        billingClient.querySkuDetailsAsync(skuDetailsParams, (BillingResult billingResult, List<SkuDetails> skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                items.addAll(skuDetailsList);
                datasetChanged();
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                Globals.handlePurchase(weakActivity, weakContext, billingClient, purchase);
                datasetChanged();
            }
        }
    }
}

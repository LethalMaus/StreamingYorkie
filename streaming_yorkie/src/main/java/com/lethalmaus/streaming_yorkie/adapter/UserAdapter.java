package com.lethalmaus.streaming_yorkie.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.F4FEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.entity.UserEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.request.FollowRequestHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;

import java.lang.ref.WeakReference;

/**
 * Recycler View Adapter for Followers/FollowingEntity/F4FEntity
 * @author LethalMaus
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    //All activities & contexts are weak referenced to avoid memory leaks
    private static WeakReference<Activity> weakActivity;
    private static WeakReference<Context> weakContext;
    private static StreamingYorkieDB streamingYorkieDB;
    private RecyclerView recyclerView;
    private String userType;
    private String userStatus;
    private String actionButtonType1;
    private String actionButtonType2;
    private int pageCount1;
    private int pageCount2;
    private int pageCount3;
    private int pageCount4;
    private int currentPageCount = 0;
    private float scale;
    private int animationsRunning = 0;

    /**
     * Simple View Holder for loading the View with a Dataset Row
     * @author LethalMaus
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        View userRow;
        /**
         * Holder for user View
         * @param userRow View for user Row
         */
        UserViewHolder(View userRow) {
            super(userRow);
            this.userRow = userRow;
        }
    }

    /**
     * Adapter for displaying a UserEntity Dataset
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public UserAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        UserAdapter.weakActivity = weakActivity;
        UserAdapter.weakContext = weakContext;
        if (Globals.checkWeakReference(weakContext)) {
            scale = weakContext.get().getResources().getDisplayMetrics().density;
            streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
        }
    }

    /**
     * Sets Display preferences
     * @author LethalMaus
     * @param userType eg. FOLLOWERS, FOLLOWING
     * @param userStatus constant of which users are to be displayed
     * @param actionButtonType1 constant of which button is required in relation to the itemsToDisplay
     * @param actionButtonType2 constant of which button is required in relation to the itemsToDisplay
     * @return an instance of itself for method building
     */
    public UserAdapter setDisplayPreferences(String userType, String userStatus, String actionButtonType1, String actionButtonType2) {
        this.userType = userType;
        this.userStatus = userStatus;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        animationsRunning = 0;
        return this;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    @NonNull
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_parent_row, parent, false);
        return new UserViewHolder(userRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, final int position) {
        weakActivity.get().runOnUiThread(() ->
                new UserAsyncTask(UserAdapter.this, userViewHolder, userType, userStatus, position, actionButtonType1, actionButtonType2).execute()
        );
    }

    @Override
    public void onViewRecycled(@NonNull final UserViewHolder userViewHolder) {
        final LottieAnimationView button1 = userViewHolder.userRow.findViewById(R.id.userrow_button1);
        final LottieAnimationView button2 = userViewHolder.userRow.findViewById(R.id.userrow_button2);
        final LottieAnimationView button3 = userViewHolder.userRow.findViewById(R.id.userrow_button3);
        if (button1.isAnimating()) {
            button1.cancelAnimation();
        }
        if (button2.isAnimating()) {
            button2.cancelAnimation();
        }
        if (button3.isAnimating()) {
            button3.cancelAnimation();
        }
    }

    /**
     * Async Task to request UserEntity and display it
     * @author LethalMaus
     */
    private static class UserAsyncTask extends AsyncTask<Void, Void, UserEntity> {

        private UserAdapter userAdapter;
        private UserViewHolder userViewHolder;
        private String userType;
        private String userStatus;
        private int position;
        private String actionButtonType1;
        private String actionButtonType2;

        /**
         * Async Task constructor
         * @author LethalMaus
         * @param userAdapter inner class reference
         * @param userViewHolder inner class reference
         * @param userType inner class reference
         * @param userStatus inner class reference
         * @param position inner class reference
         * @param actionButtonType1 inner class reference
         * @param actionButtonType2 inner class reference
         */
        UserAsyncTask(UserAdapter userAdapter, UserViewHolder userViewHolder, String userType, String userStatus, int position, String actionButtonType1, String actionButtonType2) {
            this.userAdapter = userAdapter;
            this.userViewHolder = userViewHolder;
            this.userType = userType;
            this.userStatus = userStatus;
            this.position = position;
            this.actionButtonType1 = actionButtonType1;
            this.actionButtonType2 = actionButtonType2;
        }

        @Override
        protected UserEntity doInBackground(Void... params) {
            UserEntity userEntity;
            if (userType.contentEquals("FOLLOWERS")) {
                if (userStatus.contentEquals("CURRENT")) {
                    userEntity = streamingYorkieDB.followerDAO().getCurrentUserByPosition(position);
                } else {
                    userEntity = streamingYorkieDB.followerDAO().getUserByStatusAndPosition(userStatus, position);
                }
            } else if (userType.contentEquals("FOLLOWING")) {
                if (userStatus.contentEquals("CURRENT")) {
                    userEntity = streamingYorkieDB.followingDAO().getCurrentUserByPosition(position);
                } else {
                    userEntity = streamingYorkieDB.followingDAO().getUserByStatusAndPosition(userStatus, position);
                }
            } else if (userStatus.contentEquals("FOLLOWED_NOTFOLLOWING")) {
                userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserByPosition(position);
            } else if (userStatus.contentEquals("FOLLOW4FOLLOW")) {
                userEntity = streamingYorkieDB.f4fDAO().getFollow4FollowUserByPosition(position);
            } else if (userStatus.contentEquals("NOTFOLLOWED_FOLLOWING")) {
                userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserByPosition(position);
            } else {
                userEntity = streamingYorkieDB.f4fDAO().getExcludedFollow4FollowUserByPosition(position);
            }
            return userEntity;
        }

        @Override
        protected void onPostExecute(final UserEntity userEntity) {
            if (Globals.checkWeakActivity(weakActivity) && Globals.checkWeakReference(weakContext)) {
                if (userEntity != null) {
                    ImageView plusSign = userViewHolder.userRow.findViewById(R.id.userNew);
                    if (userEntity.getStatus() != null && userEntity.getStatus().contentEquals("NEW")) {
                        plusSign.setVisibility(View.VISIBLE);
                    } else {
                        plusSign.setVisibility(View.GONE);
                    }
                    TextView textView = userViewHolder.userRow.findViewById(R.id.userrow_username);
                    textView.setText(userEntity.getDisplay_name());

                    ImageView imageView = userViewHolder.userRow.findViewById(R.id.userrow_logo);
                    Glide.with(weakContext.get()).load(userEntity.getLogo()).placeholder(R.drawable.user).into(imageView);
                    imageView.setOnClickListener((View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://twitch.tv/" + userEntity.getDisplay_name())
                    );

                    final LottieAnimationView button1 = userViewHolder.userRow.findViewById(R.id.userrow_button1);
                    final LottieAnimationView button2 = userViewHolder.userRow.findViewById(R.id.userrow_button2);
                    final LottieAnimationView button3 = userViewHolder.userRow.findViewById(R.id.userrow_button3);
                    new Thread(() -> {
                        userAdapter.editButton(button1, actionButtonType1, userEntity.getId());
                        userAdapter.editButton(button2, actionButtonType2, userEntity.getId());
                        userAdapter.followButton(button3, userEntity.getId());
                    }).start();
                }
                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(currentPageCount, 0);
    }

    /**
     * Edits the button based on the actionButtonType
     * @author LethalMaus
     * @param button which button is to be changed
     * @param actionButtonType a constant of the action button type
     * @param userID the user id which is related to the button
     */
    private void editButton(final LottieAnimationView button, final String actionButtonType, final int userID) {
        if (actionButtonType != null) {
            switch (actionButtonType) {
                case "DELETE_BUTTON":
                    deleteButton(button, userID);
                    break;
                case "EXCLUDE_BUTTON":
                    excludeButton(button, userID);
                    break;
                case "INCLUDE_BUTTON":
                    includeButton(button, userID);
                    break;
                case "NOTIFICATIONS_BUTTON":
                    notificationsButton(button, userID);
                    break;
            }
        } else {
            weakActivity.get().runOnUiThread(() ->
                    button.setVisibility(View.INVISIBLE)
            );
        }
    }

    /**
     * Button for deleting a user
     * @author LethalMaus
     * @param lottieButton button view
     * @param userID user to be deleted
     */
    private void deleteButton(final LottieAnimationView lottieButton, final int userID) {
        weakActivity.get().runOnUiThread(() -> {
            lottieButton.setAnimation("delete.json");
            lottieButton.setTag("DELETE_BUTTON");
            lottieButton.setProgress(0);
            int padding = (int) (-20 * scale);
            lottieButton.setPadding(padding, padding, padding, padding);
            lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    animationsRunning--;
                    datasetChanged();
                }
                @Override
                public void onAnimationStart(Animator animator) {animationsRunning++;}
                @Override
                public void onAnimationCancel(Animator animator) {animationsRunning--;}
                @Override
                public void onAnimationRepeat(Animator animator) {/* Do nothing */}
            });
            lottieButton.setOnClickListener((View v) -> {
                if (!lottieButton.isAnimating()) {
                    lottieButton.playAnimation();
                    new Thread(() -> {
                        if (userType.contentEquals("FOLLOWERS")) {
                            streamingYorkieDB.followerDAO().deleteUserById(userID);
                        } else {
                            streamingYorkieDB.followingDAO().deleteUserById(userID);
                        }
                    }).start();
                }
            });
        });
    }

    /**
     * Button for excluding a user from automation and other views
     * @author LethalMaus
     * @param lottieButton button view
     * @param userID user to be excluded
     */
    private void excludeButton(final LottieAnimationView lottieButton, final int userID) {
        weakActivity.get().runOnUiThread(() -> {
            lottieButton.setAnimation("visibility.json");
            lottieButton.setTag("EXCLUDE_BUTTON");
            lottieButton.setMinAndMaxFrame(0, 15);
            lottieButton.setProgress(0);
            int padding = (int) (-5 * scale);
            lottieButton.setPadding(padding, padding, padding, padding);
            lottieButton.setSpeed(0.5F);
            lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    animationsRunning--;
                    datasetChanged();
                }
                @Override
                public void onAnimationStart(Animator animator) {animationsRunning++;}
                @Override
                public void onAnimationCancel(Animator animator) {animationsRunning--;}
                @Override
                public void onAnimationRepeat(Animator animator) {/* Do nothing */}
            });
            lottieButton.setOnClickListener((View v) -> {
                if (!lottieButton.isAnimating()) {
                    lottieButton.playAnimation();
                    new Thread(() -> {
                        UserEntity userEntity = streamingYorkieDB.followerDAO().getUserById(userID);
                        if (userEntity == null) {
                            userEntity = streamingYorkieDB.followingDAO().getUserById(userID);
                        }
                        if (userType.contentEquals("FOLLOWERS")) {
                            streamingYorkieDB.followerDAO().updateUserStatusById("EXCLUDED", userID);
                        } else if (userType.contentEquals("FOLLOWING")) {
                            streamingYorkieDB.followingDAO().updateUserStatusById("EXCLUDED", userID);
                        } else if (userType.contentEquals("F4FEntity")) {
                            streamingYorkieDB.f4fDAO().insertUser(new F4FEntity(userEntity.getId(), userEntity.getDisplay_name(), userEntity.getLogo(), userEntity.getCreated_at(), userEntity.isNotifications(), userEntity.getLast_updated()));
                        }
                    }).start();
                }
            });
        });
    }

    /**
     * Button for including a user to automation and other views
     * @author LethalMaus
     * @param lottieButton button view
     * @param userID user to be included
     */
    private void includeButton(final LottieAnimationView lottieButton, final int userID) {
        weakActivity.get().runOnUiThread(() -> {
            lottieButton.setAnimation("visibility.json");
            lottieButton.setTag("INCLUDE_BUTTON");
            lottieButton.setMinAndMaxFrame(15, 30);
            lottieButton.setProgress(0);
            int padding = (int) (-5 * scale);
            lottieButton.setPadding(padding, padding, padding, padding);
            lottieButton.setSpeed(0.5F);
            lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    animationsRunning--;
                    datasetChanged();
                }
                @Override
                public void onAnimationStart(Animator animator) {animationsRunning++;}
                @Override
                public void onAnimationCancel(Animator animator) {animationsRunning--;}
                @Override
                public void onAnimationRepeat(Animator animator) {/* Do nothing */}
            });
            lottieButton.setOnClickListener((View v) -> {
                if (!lottieButton.isAnimating()) {
                    lottieButton.playAnimation();
                    new Thread(() -> {
                        if (userType.contentEquals("FOLLOWERS")) {
                            String timestamp = new ReadFileHandler(weakActivity, weakContext, "FOLLOWERS_TIMESTAMP").readFile();
                            if (timestamp.isEmpty()) {
                                timestamp = "0";
                            }
                            FollowerEntity followerEntity = streamingYorkieDB.followerDAO().getUserById(userID);
                            if (followerEntity.getLast_updated() == Long.parseLong(timestamp)) {
                                streamingYorkieDB.followerDAO().updateUserStatusById("CURRENT", userID);
                            } else {
                                streamingYorkieDB.followerDAO().updateUserStatusById("UNFOLLOWED", userID);
                            }
                        } else if (userType.contentEquals("FOLLOWING")) {
                            String timestamp = new ReadFileHandler(weakActivity, weakContext, "FOLLOWING_TIMESTAMP").readFile();
                            if (timestamp.isEmpty()) {
                                timestamp = "0";
                            }
                            FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
                            if (followingEntity.getLast_updated() == Long.parseLong(timestamp)) {
                                streamingYorkieDB.followingDAO().updateUserStatusById("CURRENT", userID);
                            } else {
                                streamingYorkieDB.followingDAO().updateUserStatusById("UNFOLLOWED", userID);
                            }
                        } else if (userType.contentEquals("F4FEntity")) {
                            streamingYorkieDB.f4fDAO().deleteUserById(userID);
                        }
                    }).start();
                }
            });
        });
    }

    /**
     * Button for following/unfollowing a user
     * @author LethalMaus
     * @param lottieButton LottieAnimationView
     * @param userID user to be followed/unfollowed
     */
    private void followButton(final LottieAnimationView lottieButton, final int userID) {
        final FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
        final String timestamp = new ReadFileHandler(weakActivity, weakContext, "FOLLOWING_TIMESTAMP").readFile();
        weakActivity.get().runOnUiThread(() -> {
                        if (followingEntity == null ||
                                followingEntity.getStatus().contentEquals("UNFOLLOWED") ||
                                (followingEntity.getStatus().contentEquals("EXCLUDED") && !timestamp.isEmpty() &&
                                        followingEntity.getLast_updated() != Long.parseLong(timestamp)
                                )) {
                            lottieButton.setAnimation("follow.json");
                        } else {
                            lottieButton.setAnimation("unfollow.json");
                        }
                        lottieButton.setTag("FOLLOW_BUTTON");
                        lottieButton.setProgress(0);
                        int padding = (int) (-5 * scale);
                        lottieButton.setPadding(padding, padding, padding, padding);
                        lottieButton.setOnClickListener((View v) -> {
                            if (!lottieButton.isAnimating()) {
                                lottieButton.playAnimation();
                                new Thread(() -> {
                                    if (RequestHandler.networkIsAvailable(weakContext)) {
                                        final FollowingEntity followingEntityAfter = streamingYorkieDB.followingDAO().getUserById(userID);
                                        if (followingEntityAfter == null ||
                                                followingEntityAfter.getStatus().contentEquals("UNFOLLOWED") ||
                                                (followingEntityAfter.getStatus().contentEquals("EXCLUDED") && !timestamp.isEmpty() &&
                                                        followingEntityAfter.getLast_updated() != Long.parseLong(timestamp)
                                                )) {
                                            new FollowRequestHandler(weakActivity, weakContext) {
                                                @Override
                                                public void onCompletion(boolean hideProgressBar) {
                                                    super.onCompletion(hideProgressBar);
                                                    if ((userType.contentEquals("FOLLOWING") && userStatus.contentEquals("UNFOLLOWED")) || userStatus.contains("FOLLOWED_NOTFOLLOWING")) {
                                                        lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                                                            @Override
                                                            public void onAnimationEnd(Animator animator) {
                                                                animationsRunning--;
                                                                datasetChanged();
                                                            }
                                                            @Override
                                                            public void onAnimationStart(Animator animator) { animationsRunning++; }
                                                            @Override
                                                            public void onAnimationCancel(Animator animator) { animationsRunning--; }
                                                            @Override
                                                            public void onAnimationRepeat(Animator animator) {/* Do nothing */}
                                                        });
                                                    } else {
                                                        weakActivity.get().runOnUiThread(() -> {
                                                            lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                                                                @Override
                                                                public void onAnimationEnd(Animator animator) {
                                                                    animationsRunning--;
                                                                    lottieButton.setAnimation("unfollow.json");
                                                                    lottieButton.setProgress(0);
                                                                }

                                                                @Override
                                                                public void onAnimationStart(Animator animator) {
                                                                    animationsRunning++;
                                                                }

                                                                @Override
                                                                public void onAnimationCancel(Animator animator) {
                                                                    animationsRunning--;
                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(Animator animator) {/* Do nothing */}
                                                            });
                                                            ViewGroup row = (ViewGroup) lottieButton.getParent();
                                                            final LottieAnimationView imageButton2 = row.findViewById(R.id.userrow_button2);
                                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                            new Thread(() ->
                                                                    editButton(imageButton2, "NOTIFICATIONS_BUTTON", userID)
                                                            ).start();
                                                        });
                                                    }
                                                }
                                            }.setRequestParameters(Request.Method.PUT, userID, false).sendRequest(false);
                                        } else {
                                            new FollowRequestHandler(weakActivity, weakContext) {
                                                @Override
                                                public void onCompletion(boolean hideProgressBar) {
                                                    super.onCompletion(hideProgressBar);
                                                    if (userType.contentEquals("FOLLOWING") || userStatus.contains("NOTFOLLOWED_FOLLOWING") || userStatus.contains("FOLLOW4FOLLOW")) {
                                                        lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                                                            @Override
                                                            public void onAnimationEnd(Animator animator) {
                                                                animationsRunning--;
                                                                datasetChanged();
                                                            }
                                                            @Override
                                                            public void onAnimationStart(Animator animator) { animationsRunning++; }
                                                            @Override
                                                            public void onAnimationCancel(Animator animator) { animationsRunning--; }
                                                            @Override
                                                            public void onAnimationRepeat(Animator animator) {/* Do nothing */}
                                                        });
                                                    } else {
                                                        weakActivity.get().runOnUiThread(() -> {
                                                            lottieButton.addAnimatorListener(new Animator.AnimatorListener() {
                                                                @Override
                                                                public void onAnimationEnd(Animator animator) {
                                                                    animationsRunning--;
                                                                    lottieButton.setAnimation("follow.json");
                                                                    lottieButton.setProgress(0);
                                                                }
                                                                @Override
                                                                public void onAnimationStart(Animator animator) { animationsRunning++; }
                                                                @Override
                                                                public void onAnimationCancel(Animator animator) { animationsRunning--; }
                                                                @Override
                                                                public void onAnimationRepeat(Animator animator) {/* Do nothing */}
                                                            });
                                                            ViewGroup row = (ViewGroup) lottieButton.getParent();
                                                            row.findViewById(R.id.userrow_button2).setVisibility(View.INVISIBLE);
                                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                        });
                                                    }
                                                }
                                            }.setRequestParameters(Request.Method.DELETE, userID, false).sendRequest(false);
                                        }
                                    } else if (weakActivity != null && weakActivity.get() != null) {
                                        weakActivity.get().runOnUiThread(() ->
                                                Toast.makeText(weakActivity.get(), "Cannot change FollowingEntity preferences when offline", Toast.LENGTH_SHORT).show()
                                        );
                                    }
                                }).start();
                            }
                        });
                });
    }

    /**
     * Button for activating/deactivating notifications of users
     * @author LethalMaus
     * @param lottieButton button view
     * @param userID user to have notifications activated/deactivated
     */
    private void notificationsButton(final LottieAnimationView lottieButton, final int userID) {
        final FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
        weakActivity.get().runOnUiThread(() -> {
            if (followingEntity == null || followingEntity.getStatus().contentEquals("UNFOLLOWED")) {
                lottieButton.setVisibility(View.INVISIBLE);
            } else {
                lottieButton.setAnimation("notifications.json");
                int padding = (int) (-30 * scale);
                lottieButton.setPadding(padding, padding, padding, padding);
                if (followingEntity.isNotifications()) {
                    lottieButton.setProgress(100);
                } else {
                    lottieButton.setProgress(0);
                }
                lottieButton.setVisibility(View.VISIBLE);
                lottieButton.setTag("NOTIFICATIONS_BUTTON");
                lottieButton.setOnClickListener((View v) -> {
                    if (!lottieButton.isAnimating()) {
                        new Thread(() -> {
                            final FollowingEntity followingEntityAfter = streamingYorkieDB.followingDAO().getUserById(userID);
                            weakActivity.get().runOnUiThread(() -> {
                                if (followingEntityAfter.isNotifications()) {
                                    lottieButton.setProgress(100);
                                    lottieButton.setSpeed(-1);
                                } else {
                                    lottieButton.setProgress(0);
                                    lottieButton.setSpeed(1);
                                }
                                lottieButton.playAnimation();
                            });
                            if (RequestHandler.networkIsAvailable(weakContext)) {
                                if (followingEntityAfter.isNotifications()) {
                                    new FollowRequestHandler(weakActivity, weakContext).setRequestParameters(Request.Method.PUT, userID, false).sendRequest(false);
                                    weakActivity.get().runOnUiThread(() ->
                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE)
                                    );
                                } else {
                                    new FollowRequestHandler(weakActivity, weakContext).setRequestParameters(Request.Method.PUT, userID, true).sendRequest(false);
                                    weakActivity.get().runOnUiThread(() ->
                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE)
                                    );
                                }
                            } else if (weakActivity != null && weakActivity.get() != null) {
                                weakActivity.get().runOnUiThread(() ->
                                        Toast.makeText(weakActivity.get(), "Cannot change Notification preferences when offline", Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    }
                });
            }
        });
    }

    /**
     * Button for following / unfollowing all users within menu.
     * @author LethalMaus
     * @param userType String for which all button should be used
     * @param userStatus String for which action the all button should perform
     */
    private void actionAllButton(final String userType, final String userStatus) {
        if (Globals.checkWeakActivity(weakActivity)) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            final ImageButton imageButton = weakActivity.get().findViewById(R.id.follow_unfollow_all);
                            if (currentPageCount > 1) {
                                imageButton.setVisibility(View.VISIBLE);
                                if (userType.contentEquals("FOLLOWED_NOTFOLLOWING")) {
                                    imageButton.setImageResource(R.drawable.followed);
                                    imageButton.setOnClickListener((View v) ->
                                            new Thread(() -> {
                                                weakActivity.get().runOnUiThread(() ->
                                                        weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE)
                                                );
                                                FollowRequestHandler followRequestHandler =  new FollowRequestHandler(weakActivity, weakContext) {
                                                    @Override
                                                    public void onCompletion(boolean hideProgressBar) {
                                                        super.onCompletion(hideProgressBar);
                                                        UserEntity userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserByPosition(0);
                                                        if (userEntity != null) {
                                                            setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                                                    .sendRequest(true);
                                                        } else if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                                            datasetChanged();
                                                            weakActivity.get().runOnUiThread(() ->
                                                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE)
                                                            );
                                                        }
                                                    }
                                                };
                                                UserEntity userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserByPosition(0);
                                                if (userEntity != null) {
                                                    followRequestHandler.setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                                            .sendRequest(true);
                                                }
                                            }).start()
                                    );
                                } else if (userType.contentEquals("NOTFOLLOWED_FOLLOWING")) {
                                    imageButton.setImageResource(R.drawable.unfollowed);
                                    imageButton.setOnClickListener((View v) ->
                                            new Thread(() -> {
                                                weakActivity.get().runOnUiThread(() ->
                                                        weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE)
                                                );
                                                FollowRequestHandler followRequestHandler =  new FollowRequestHandler(weakActivity, weakContext) {
                                                    @Override
                                                    public void onCompletion(boolean hideProgressBar) {
                                                        super.onCompletion(hideProgressBar);
                                                        UserEntity userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserByPosition(0);
                                                        if (userEntity != null) {
                                                            setRequestParameters(Request.Method.DELETE, userEntity.getId(), false)
                                                                    .sendRequest(true);
                                                        } else if (Globals.checkWeakActivity(weakActivity)) {
                                                            datasetChanged();
                                                            weakActivity.get().runOnUiThread(() ->
                                                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE)
                                                            );
                                                        }
                                                    }
                                                };
                                                UserEntity userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserByPosition(0);
                                                if (userEntity != null) {
                                                    followRequestHandler.setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                                            .sendRequest(true);
                                                }
                                            }).start()
                                    );
                                } else if (userType.contentEquals("EXCLUDED")) {
                                    imageButton.setImageResource(R.drawable.included);
                                    imageButton.setOnClickListener((View v) ->
                                            new Thread(() -> {
                                                weakActivity.get().runOnUiThread(() ->
                                                        weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.VISIBLE)
                                                );
                                                if (userStatus.contentEquals("FOLLOWERS")) {
                                                    String timestamp = new ReadFileHandler(weakActivity, weakContext, "FOLLOWERS_TIMESTAMP").readFile();
                                                    if (timestamp.isEmpty()) {
                                                        timestamp = "0";
                                                    }
                                                    for (int i = currentPageCount-1; i >= 0; i--) {
                                                        FollowerEntity followerEntity = streamingYorkieDB.followerDAO().getUserByStatusAndPosition("EXCLUDED", i);
                                                        if (followerEntity.getLast_updated() == Long.parseLong(timestamp)) {
                                                            streamingYorkieDB.followerDAO().updateUserStatusById("CURRENT", followerEntity.getId());
                                                        } else {
                                                            streamingYorkieDB.followerDAO().updateUserStatusById("UNFOLLOWED", followerEntity.getId());
                                                        }
                                                    }
                                                } else if (userStatus.contentEquals("FOLLOWING")) {
                                                    String timestamp = new ReadFileHandler(weakActivity, weakContext, "FOLLOWING_TIMESTAMP").readFile();
                                                    if (timestamp.isEmpty()) {
                                                        timestamp = "0";
                                                    }
                                                    for (int i = currentPageCount-1; i >= 0; i--) {
                                                        FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserByStatusAndPosition("EXCLUDED", i);
                                                        if (followingEntity.getLast_updated() == Long.parseLong(timestamp)) {
                                                            streamingYorkieDB.followingDAO().updateUserStatusById("CURRENT", followingEntity.getId());
                                                        } else {
                                                            streamingYorkieDB.followingDAO().updateUserStatusById("UNFOLLOWED", followingEntity.getId());
                                                        }
                                                    }
                                                } else if (userStatus.contentEquals("F4FEntity")) {
                                                    for (int i = currentPageCount-1; i >= 0; i--) {
                                                        F4FEntity f4FEntity = streamingYorkieDB.f4fDAO().getExcludedFollow4FollowUserByPosition(i);
                                                        streamingYorkieDB.f4fDAO().deleteUserById(f4FEntity.getId());
                                                    }
                                                }
                                                if (Globals.checkWeakActivity(weakActivity)) {
                                                    datasetChanged();
                                                    weakActivity.get().runOnUiThread(() ->
                                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE)
                                                    );
                                                }
                                            }).start()
                                    );
                                } else {
                                    imageButton.setVisibility(View.GONE);
                                }
                            } else {
                                imageButton.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     */
    public void datasetChanged() {
        if (animationsRunning <= 1) {
            new Thread(() -> {
                setPageCounts();
                actionAllButton(userStatus, userType);
                weakActivity.get().runOnUiThread(() -> {
                    setPageCountViews();
                    //An empty row or table can be displayed based on if the dataset is empty or not
                    if (currentPageCount > 0) {
                        weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
                        weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
                    } else {
                        weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                        weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
                        weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
                        weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                    }
                });
            }).start();
        }
    }

    /**
     * Method for counting files within user directories
     * @author LethalMaus
     */
    private void setPageCounts() {
        if (userType.contentEquals("FOLLOWERS")){
            pageCount1 = streamingYorkieDB.followerDAO().countUsersByStatus("NEW");
            pageCount2 = streamingYorkieDB.followerDAO().countUsersByStatus("CURRENT") + pageCount1;
            pageCount3 = streamingYorkieDB.followerDAO().countUsersByStatus("UNFOLLOWED");
            pageCount4 = streamingYorkieDB.followerDAO().countUsersByStatus("EXCLUDED");
        } else if (userType.contentEquals("FOLLOWING")){
            pageCount1 = streamingYorkieDB.followingDAO().countUsersByStatus("NEW");
            pageCount2 = streamingYorkieDB.followingDAO().countUsersByStatus("CURRENT") + pageCount1;
            pageCount3 = streamingYorkieDB.followingDAO().countUsersByStatus("UNFOLLOWED");
            pageCount4 = streamingYorkieDB.followingDAO().countUsersByStatus("EXCLUDED");
        } else {
            pageCount1 = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserCount();
            pageCount2 = streamingYorkieDB.f4fDAO().getFollow4FollowUserCount();
            pageCount3 = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserCount();
            pageCount4 = streamingYorkieDB.f4fDAO().getExcludedFollow4FollowUserCount();
        }
        recyclerView.post(() -> {
            if (userStatus.contentEquals("NEW") || userStatus.contentEquals("FOLLOWED_NOTFOLLOWING")) {
                currentPageCount = pageCount1;
            } else if (userStatus.contentEquals("CURRENT") || userStatus.contentEquals("FOLLOW4FOLLOW")) {
                currentPageCount = pageCount2;
            } else if (userStatus.contentEquals("UNFOLLOWED") || userStatus.contentEquals("NOTFOLLOWED_FOLLOWING")) {
                currentPageCount = pageCount3;
            } else if (userStatus.contentEquals("EXCLUDED")) {
                currentPageCount = pageCount4;
            }
            notifyDataSetChanged();
        });
    }

    /**
     * Method for setting the page count views
     * @author LethalMaus
     */
    private void setPageCountViews() {
        if (Globals.checkWeakActivity(weakActivity)) {
            weakActivity.get().runOnUiThread(() -> {
                TextView page1 = weakActivity.get().findViewById(R.id.count1);
                TextView page2 = weakActivity.get().findViewById(R.id.count2);
                TextView page3 = weakActivity.get().findViewById(R.id.count3);
                TextView page4 = weakActivity.get().findViewById(R.id.count4);
                page1.setText(String.valueOf(pageCount1));
                page2.setText(String.valueOf(pageCount2));
                page3.setText(String.valueOf(pageCount3));
                page4.setText(String.valueOf(pageCount4));
            });
        }
    }
}

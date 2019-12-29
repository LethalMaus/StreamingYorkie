package com.lethalmaus.streaming_yorkie.adapter;

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

import com.android.volley.Request;
import com.bumptech.glide.Glide;
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
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private RecyclerView recyclerView;
    private String userType;
    private String userStatus;
    private String actionButtonType1;
    private String actionButtonType2;
    private String actionButtonType3;
    private int pageCount1;
    private int pageCount2;
    private int pageCount3;
    private int pageCount4;
    private int currentPageCount = 0;
    private int previousPageCount = 0;
    private StreamingYorkieDB streamingYorkieDB;

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
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
    }

    /**
     * Sets Display preferences
     * @author LethalMaus
     * @param userType eg. FOLLOWERS, FOLLOWING
     * @param userStatus constant of which users are to be displayed
     * @param actionButtonType1 constant of which button is required in relation to the itemsToDisplay
     * @param actionButtonType2 constant of which button is required in relation to the itemsToDisplay
     * @param actionButtonType3 constant of which button is required in relation to the itemsToDisplay
     * @return an instance of itself for method building
     */
    public UserAdapter setDisplayPreferences(String userType, String userStatus, String actionButtonType1, String actionButtonType2, String actionButtonType3) {
        this.userType = userType;
        this.userStatus = userStatus;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        this.actionButtonType3 = actionButtonType3;
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
        weakActivity.get().runOnUiThread(new Runnable() {
            public void run() {
                new UserAsyncTask(weakActivity, weakContext, UserAdapter.this, userViewHolder, streamingYorkieDB, userType, userStatus, position, actionButtonType1, actionButtonType2, actionButtonType3).execute();
            }
        });
    }

    /**
     * Async Task to request UserEntity and display it
     * @author LethalMaus
     */
    private static class UserAsyncTask extends AsyncTask<Void, Void, UserEntity> {

        private WeakReference<Activity> weakActivity;
        private WeakReference<Context> weakContext;
        private UserAdapter userAdapter;
        private UserViewHolder userViewHolder;
        private StreamingYorkieDB streamingYorkieDB;
        private String userType;
        private String userStatus;
        private int position;
        private String actionButtonType1;
        private String actionButtonType2;
        private String actionButtonType3;

        /**
         * Async Task constructor
         * @author LethalMaus
         * @param weakActivity inner class reference
         * @param weakContext inner class reference
         * @param userAdapter inner class reference
         * @param userViewHolder inner class reference
         * @param streamingYorkieDB inner class reference
         * @param userType inner class reference
         * @param userStatus inner class reference
         * @param position inner class reference
         * @param actionButtonType1 inner class reference
         * @param actionButtonType2 inner class reference
         * @param actionButtonType3 inner class reference
         */
        UserAsyncTask(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, UserAdapter userAdapter, UserViewHolder userViewHolder, StreamingYorkieDB streamingYorkieDB, String userType, String userStatus, int position, String actionButtonType1, String actionButtonType2, String actionButtonType3) {
            this.weakActivity = weakActivity;
            this.weakContext = weakContext;
            this.userAdapter = userAdapter;
            this.userViewHolder = userViewHolder;
            this.streamingYorkieDB = streamingYorkieDB;
            this.userType = userType;
            this.userStatus = userStatus;
            this.position = position;
            this.actionButtonType1 = actionButtonType1;
            this.actionButtonType2 = actionButtonType2;
            this.actionButtonType3 = actionButtonType3;
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
            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
                if (userEntity != null) {
                    TextView textView = userViewHolder.userRow.findViewById(R.id.userrow_username);
                    textView.setText(userEntity.getDisplay_name());

                    ImageView imageView = userViewHolder.userRow.findViewById(R.id.userrow_logo);
                    Glide.with(weakContext.get()).load(userEntity.getLogo()).placeholder(R.drawable.user).into(imageView);

                    final ImageButton button1 = userViewHolder.userRow.findViewById(R.id.userrow_button1);
                    final ImageButton button2 = userViewHolder.userRow.findViewById(R.id.userrow_button2);
                    final ImageButton button3 = userViewHolder.userRow.findViewById(R.id.userrow_button3);
                    new Thread(new Runnable() {
                        public void run() {
                            userAdapter.editButton(button1, actionButtonType1, userEntity.getId());
                            userAdapter.editButton(button2, actionButtonType2, userEntity.getId());
                            userAdapter.editButton(button3, actionButtonType3, userEntity.getId());
                        }
                    }).start();
                }
                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     */
    public void datasetChanged() {
        new Thread(new Runnable() {
            public void run() {
                setPageCounts();
                if (userStatus.contentEquals("FOLLOWED_NOTFOLLOWING")) {
                    actionAllButton(true);
                } else if (userStatus.contentEquals("NOTFOLLOWED_FOLLOWING")) {
                    actionAllButton(false);
                } else {
                    weakActivity.get().runOnUiThread(new Runnable() {
                        public void run() {
                            weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
                        }
                    });
                }
                weakActivity.get().runOnUiThread(
                        new Runnable() {
                            public void run() {
                                setPageCountViews();
                                //An empty row or table can be displayed based on if the dataset is empty or not
                                if (currentPageCount > 0) {
                                    if (currentPageCount > 1 && (userStatus.contentEquals("FOLLOWED_NOTFOLLOWING") || userStatus.contentEquals("NOTFOLLOWED_FOLLOWING"))) {
                                        weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.VISIBLE);
                                    } else {
                                        weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
                                    }
                                    weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
                                    weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
                                } else {
                                    weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                                    weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
                                    weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return currentPageCount < 0 ? 0 : currentPageCount;
    }

    /**
     * Edits the button based on the actionButtonType
     * @author LethalMaus
     * @param button which button is to be changed
     * @param actionButtonType a constant of the action button type
     * @param userID the user id which is related to the button
     */
    private void editButton(final ImageButton button, final String actionButtonType, final int userID) {
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
                case "FOLLOW_BUTTON":
                    followButton(button, userID);
                    break;
                case "NOTIFICATIONS_BUTTON":
                    notificationsButton(button, userID);
                    break;
            }
        } else {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            button.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    /**
     * Button for deleting a user
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be deleted
     */
    private void deleteButton(final ImageButton imageButton, final int userID) {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        imageButton.setImageResource(R.drawable.delete);
                        imageButton.setTag("DELETE_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        if (userType.contentEquals("FOLLOWERS")) {
                                            streamingYorkieDB.followerDAO().deleteUserById(userID);
                                        } else {
                                            streamingYorkieDB.followingDAO().deleteUserById(userID);
                                        }
                                        datasetChanged();
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for excluding a user from automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be excluded
     */
    private void excludeButton(final ImageButton imageButton, final int userID) {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        imageButton.setImageResource(R.drawable.excluded);
                        imageButton.setTag("EXCLUDE_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
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
                                        datasetChanged();
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for including a user to automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be included
     */
    private void includeButton(final ImageButton imageButton, final int userID) {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        imageButton.setImageResource(R.drawable.include);
                        imageButton.setTag("INCLUDE_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
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
                                        datasetChanged();
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for following/unfollowing a user
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be followed/unfollowed
     */
    private void followButton(final ImageButton imageButton, final int userID) {
        final FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
        final String timestamp = new ReadFileHandler(weakActivity, weakContext, "FOLLOWING_TIMESTAMP").readFile();
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        if (followingEntity == null ||
                                followingEntity.getStatus().contentEquals("UNFOLLOWED") ||
                                (followingEntity.getStatus().contentEquals("EXCLUDED") && !timestamp.isEmpty() &&
                                        followingEntity.getLast_updated() != Long.parseLong(timestamp)
                                )) {
                            imageButton.setImageResource(R.drawable.follow);
                        } else {
                            imageButton.setImageResource(R.drawable.unfollow);
                        }
                        imageButton.setTag("FOLLOW_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        if (RequestHandler.networkIsAvailable(weakContext)) {
                                            final FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
                                            if (followingEntity == null ||
                                                    followingEntity.getStatus().contentEquals("UNFOLLOWED") ||
                                                    (followingEntity.getStatus().contentEquals("EXCLUDED") && !timestamp.isEmpty() &&
                                                            followingEntity.getLast_updated() != Long.parseLong(timestamp)
                                                    )) {
                                                new FollowRequestHandler(weakActivity, weakContext){
                                                    @Override
                                                    public void onCompletion() {
                                                        super.onCompletion();
                                                        if ((userType.contentEquals("FOLLOWING") && userStatus.contentEquals("UNFOLLOWED")) || userStatus.contains("FOLLOWED_NOTFOLLOWING")) {
                                                            datasetChanged();
                                                        } else {
                                                            weakActivity.get().runOnUiThread(
                                                                    new Runnable() {
                                                                        public void run() {
                                                                            imageButton.setImageResource(R.drawable.unfollow);
                                                                            ViewGroup row = (ViewGroup) imageButton.getParent();
                                                                            final ImageButton imageButton2 = row.findViewById(R.id.userrow_button2);
                                                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                                            new Thread(new Runnable() {
                                                                                public void run() {
                                                                                    editButton(imageButton2, "NOTIFICATIONS_BUTTON", userID);
                                                                                }
                                                                            }).start();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }.setRequestParameters(Request.Method.PUT, userID, false)
                                                        .sendRequest();
                                            } else {
                                                new FollowRequestHandler(weakActivity, weakContext){
                                                    @Override
                                                    public void onCompletion() {
                                                        super.onCompletion();
                                                        if (userType.contentEquals("FOLLOWING") || userStatus.contains("NOTFOLLOWED_FOLLOWING") || userStatus.contains("FOLLOW4FOLLOW")) {
                                                            datasetChanged();
                                                        } else {
                                                            weakActivity.get().runOnUiThread(
                                                                    new Runnable() {
                                                                        public void run() {
                                                                            imageButton.setImageResource(R.drawable.follow);
                                                                            ViewGroup row = (ViewGroup) imageButton.getParent();
                                                                            row.findViewById(R.id.userrow_button2).setVisibility(View.INVISIBLE);
                                                                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }.setRequestParameters(Request.Method.DELETE, userID, false)
                                                        .sendRequest();
                                            }
                                        } else if (weakActivity != null && weakActivity.get() != null) {
                                            weakActivity.get().runOnUiThread(
                                                    new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(weakActivity.get(), "Cannot change FollowingEntity preferences when offline", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for activating/deactivating notifications of users
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to have notifications activated/deactivated
     */
    private void notificationsButton(final ImageButton imageButton, final int userID) {
        final FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        if (followingEntity == null || followingEntity.getStatus().contentEquals("UNFOLLOWED")) {
                            imageButton.setVisibility(View.INVISIBLE);
                        } else {
                            if (followingEntity.isNotifications()) {
                                imageButton.setImageResource(R.drawable.deactivate_notifications);
                            } else {
                                imageButton.setImageResource(R.drawable.notifications);
                            }
                            imageButton.setVisibility(View.VISIBLE);
                            imageButton.setTag("NOTIFICATIONS_BUTTON");
                            imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            final FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(userID);
                                            if (RequestHandler.networkIsAvailable(weakContext)) {
                                                if (followingEntity.isNotifications()) {
                                                    new FollowRequestHandler(weakActivity, weakContext).setRequestParameters(Request.Method.PUT, userID, false)
                                                            .sendRequest();
                                                    weakActivity.get().runOnUiThread(
                                                            new Runnable() {
                                                                public void run() {
                                                                    imageButton.setImageResource(R.drawable.notifications);
                                                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                    );
                                                } else {
                                                    new FollowRequestHandler(weakActivity, weakContext).setRequestParameters(Request.Method.PUT, userID, true)
                                                            .sendRequest();
                                                    weakActivity.get().runOnUiThread(
                                                            new Runnable() {
                                                                public void run() {
                                                                    imageButton.setImageResource(R.drawable.deactivate_notifications);
                                                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                    );
                                                }
                                            } else if (weakActivity != null && weakActivity.get() != null) {
                                                weakActivity.get().runOnUiThread(
                                                        new Runnable() {
                                                            public void run() {
                                                                Toast.makeText(weakActivity.get(), "Cannot change Notification preferences when offline", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }).start();
                                }
                            });
                        }
                    }
                });
    }

    /**
     * Button for following / unfollowing all users within menu.
     * @author LethalMaus
     * @param followAll if true followAll, else unfollowAll
     */
    private void actionAllButton(final boolean followAll) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            final ImageButton imageButton = weakActivity.get().findViewById(R.id.follow_unfollow_all);
                            if (currentPageCount > 1) {
                                if (followAll) {
                                    imageButton.setImageResource(R.drawable.follow);
                                } else {
                                    imageButton.setImageResource(R.drawable.unfollow);
                                }
                                imageButton.setVisibility(View.VISIBLE);
                                imageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                weakActivity.get().runOnUiThread(
                                                        new Runnable() {
                                                            public void run() {
                                                                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                            }
                                                        });
                                                if (followAll) {
                                                    FollowRequestHandler followRequestHandler =  new FollowRequestHandler(weakActivity, weakContext) {
                                                        @Override
                                                        public void onCompletion() {
                                                            super.onCompletion();
                                                            UserEntity userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserByPosition(0);
                                                            if (userEntity != null) {
                                                                setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                                                        .sendRequest();
                                                            } else if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                                                datasetChanged();
                                                                weakActivity.get().runOnUiThread(
                                                                        new Runnable() {
                                                                            public void run() {
                                                                                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    };
                                                    UserEntity userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserByPosition(0);
                                                    if (userEntity != null) {
                                                        followRequestHandler.setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                                                .sendRequest();
                                                    }
                                                } else {
                                                    FollowRequestHandler followRequestHandler =  new FollowRequestHandler(weakActivity, weakContext) {
                                                        @Override
                                                        public void onCompletion() {
                                                            super.onCompletion();
                                                            UserEntity userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserByPosition(0);
                                                            if (userEntity != null) {
                                                                setRequestParameters(Request.Method.DELETE, userEntity.getId(), false)
                                                                        .sendRequest();
                                                            } else if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                                                datasetChanged();
                                                                weakActivity.get().runOnUiThread(
                                                                        new Runnable() {
                                                                            public void run() {
                                                                                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    };
                                                    UserEntity userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserByPosition(0);
                                                    if (userEntity != null) {
                                                        followRequestHandler.setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                                                .sendRequest();
                                                    }
                                                }
                                            }
                                        }).start();
                                    }
                                });
                            } else {
                                imageButton.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    /**
     * Method for counting files within user directories
     * @author LethalMaus
     */
    public void setPageCounts() {
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
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    /**
     * Method for setting the page count views
     * @author LethalMaus
     */
    private void setPageCountViews() {
        if (pageCount1 < 0) {
            pageCount1 = 0;
        }
        if (pageCount2 < 0) {
            pageCount2 = 0;
        }
        if (pageCount3 < 0) {
            pageCount3 = 0;
        }
        if (pageCount4 < 0) {
            pageCount4 = 0;
        }
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            TextView page1 = weakActivity.get().findViewById(R.id.count1);
                            TextView page2 = weakActivity.get().findViewById(R.id.count2);
                            TextView page3 = weakActivity.get().findViewById(R.id.count3);
                            TextView page4 = weakActivity.get().findViewById(R.id.count4);
                            page1.setText(String.valueOf(pageCount1));
                            page2.setText(String.valueOf(pageCount2));
                            page3.setText(String.valueOf(pageCount3));
                            page4.setText(String.valueOf(pageCount4));
                        }
                    });
        }
    }
}

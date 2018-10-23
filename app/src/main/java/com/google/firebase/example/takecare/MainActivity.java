package com.google.firebase.example.takecare;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.takecare.adapter.RestaurantAdapter;
import com.google.firebase.example.takecare.model.User;
import com.google.firebase.example.takecare.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements TaskListFragment.OnTaskSelectedListener,
        GroupListFragment.OnGroupSelectedListener{

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNav;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;


    private FirebaseFirestore mFirestore;
    private Query mQuery;

//    private FilterDialogFragment mFilterDialog;
//    private RestaurantAdapter mAdapter;

    private MainActivityViewModel mViewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case  R.id.navigation_home:
                            Log.d(TAG, "Navigation Home");
                            switchToTodaysTasks();
                            return true;
                        case R.id.navigation_groups:
                            Log.d(TAG, "Navigation Groups");
                            switchToGroups();
                            return true;
                        case R.id.navigation_notifications:
                            Log.d(TAG, "Navigation Notifications");

                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mBottomNav.setOnNavigationItemSelectedListener(mNavigationListener);

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        if (!shouldStartSignIn()) {
            switchToTodaysTasks();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Start listening for Firestore updates
//        if (mAdapter != null) {
//            mAdapter.startListening();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAdapter != null) {
//            mAdapter.stopListening();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK) {
                if (response == null) {
                    // User pressed the back button.
                    finish();
                } else if (response.getError() != null
                        && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSignInErrorDialog(R.string.message_no_network);
                } else {
                    showSignInErrorDialog(R.string.message_unknown);
                }
            } else {
                // Store user in database
                final FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String token = instanceIdResult.getToken();
                            final User user = new User(fbUser, token);
                            addUser(user).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, user.getEmail() + " added to Firestore");
                                }
                            });
                        }
                    });

                }
            }
        }
    }

    @Override
    public void onTaskClicked(com.google.firebase.example.takecare.model.Task item) {
        // TODO
    }

    @Override
    public void onTaskCheckBoxChange(com.google.firebase.example.takecare.model.Task task, boolean checked) {
        // TODO
        Log.d(TAG, "Task checkbox: " + checked);
    }

    @Override
    public void onTaskDeleteClicked(com.google.firebase.example.takecare.model.Task task) {
        // TODO
        Log.d(TAG, "Task delete click");
    }

    @Override
    public void onGroupSelected(DocumentSnapshot group) {
        // TODO go to group activity
        String groupId = group.getId();
        Log.d(TAG, "Group ID: " + groupId + " clicked");
        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra(GroupDetailActivity.GROUP_ID_KEY, groupId);

        startActivity(intent);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
        mViewModel.setIsSigningIn(true);
    }

    private Task<Void> addUser(final User user) {
        final DocumentReference userRef = mFirestore.collection("users")
                .document(user.getEmail());
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(userRef, user);
                return null;
            }
        });
    }

//    private void onAddItemsClicked() {
//        // Add a bunch of random restaurants
//        WriteBatch batch = mFirestore.batch();
//        for (int i = 0; i < 10; i++) {
//            DocumentReference restRef = mFirestore.collection("restaurants").document();
//
//            // Create random restaurant / ratings
//            Restaurant randomRestaurant = RestaurantUtil.getRandom(this);
//            List<Rating> randomRatings = RatingUtil.getRandomList(randomRestaurant.getNumRatings());
//            randomRestaurant.setAvgRating(RatingUtil.getAverageRating(randomRatings));
//
//            // Add restaurant
//            batch.set(restRef, randomRestaurant);
//
//            // Add ratings to subcollection
//            for (Rating rating : randomRatings) {
//                batch.set(restRef.collection("ratings").document(), rating);
//            }
//        }
//
//        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "Write batch succeeded.");
//                } else {
//                    Log.w(TAG, "write batch failed.", task.getException());
//                }
//            }
//        });
//    }

//    private void onAddMyRestauraunt() {
//        WriteBatch batch = mFirestore.batch();
//        DocumentReference restRef = mFirestore.collection("restaurants").document();
//
//        final String RESTAURANT_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";
//        /*Restaurant restaurant = new Restaurant();
//        restaurant.setName("My Restaurant");
//        restaurant.setCity("Jacksonville");
//        restaurant.setCategory("Poke Bowls");
//        restaurant.setPhoto(String.format(Locale.getDefault(), RESTAURANT_URL_FMT, 2));
//        restaurant.setPrice(2);
//        restaurant.setNumRatings(0);*/
//        Restaurant restaurant = RestaurantUtil.getRandom(this);
//        restaurant.setNumRatings(0);
//
//        batch.set(restRef, restaurant);
//
//        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "Write batch succeeded.");
//                } else {
//                    Log.w(TAG, "write batch failed.", task.getException());
//                }
//            }
//        });
//    }

    private void showSignInErrorDialog(@StringRes int message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_sign_in_error)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.option_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      startSignIn();
                    }
                })
                .setNegativeButton(R.string.option_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();

        dialog.show();
    }

    private void switchToTodaysTasks() {
        mToolbar.setTitle(R.string.todays_tasks);
        String curUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(mFragmentContainer.getId(),
                TaskListFragment.newInstance(TaskListFragment.TaskListType.UserTasks, curUserEmail))
                .commit();
    }

    private void switchToGroups() {
        mToolbar.setTitle(R.string.groups);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(mFragmentContainer.getId(),
                GroupListViewFragment.newInstance()).commit();
    }

}

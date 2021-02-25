package com.dron.githubusersgraber.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dron.githubusersgraber.R;
import com.dron.githubusersgraber.adapter.MyUserAdapter;
import com.dron.githubusersgraber.model.User;
import com.dron.githubusersgraber.model.UserDetail;
import com.dron.githubusersgraber.model.UserRepo;
import com.dron.githubusersgraber.retrofit.Api;
import com.dron.githubusersgraber.retrofit.MyRetrofitClient;
import com.dron.githubusersgraber.service.DataBaseService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.dron.githubusersgraber.misc.Const.ACTION_NAME;
import static com.dron.githubusersgraber.misc.Const.CHANGES_COUNT_MESS;
import static com.dron.githubusersgraber.misc.Const.USER_ID_MESS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RecyclerView usersRecyclerView;
    ProgressBar mProgressBar;

    MyUserAdapter adapter;

    Api api = MyRetrofitClient.getRequestApi();
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    DataBaseService dbService = new DataBaseService();
    Realm realm;
    MyBroadcastReceiver receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersRecyclerView = findViewById(R.id.users_recyclerview);
        mProgressBar = findViewById(R.id.progress_round);

        getSupportActionBar().setTitle(getString(R.string.git_hub_users));

        //Init Broadcast Receiver
        receiver = new MyBroadcastReceiver();

        realm = Realm.getDefaultInstance();
        //Init FCM and print token
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });

        initRecyclerView();

        displayLocalData();

        fetchData();

    }

    private void fetchData() {
        List<UserDetail> fetchedUsers = new ArrayList<>();

        getUsersObservable()
                .subscribeOn(Schedulers.io())

                //For each user get repositories
                .flatMap((Function<User, ObservableSource<UserDetail>>) user -> getRepositoriesObservable(user.getUserDetail()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserDetail>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserDetail userDetail) {
                        fetchedUsers.add(userDetail);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //On error (connectivity)
                        //Show local data
                        Log.e(TAG, "onError: ", e);
                        displayFetchedData();
                    }

                    @Override
                    public void onComplete() {

                        //Add data to local db
                        persistFetchedData(userResponseToUser(fetchedUsers));
                        displayFetchedData();
                    }
                });
    }

    //Get repositories of user from GitHub Api
    private ObservableSource<UserDetail> getRepositoriesObservable(final UserDetail userDetail) {
        return api.getRepo(userDetail.getLogin())
                .map(userRepo -> {
                    RealmList<UserRepo> repositories = new RealmList<>();
                    repositories.addAll(userRepo);
                    userDetail.setRepositories(repositories);
                    return userDetail;
                })
                .subscribeOn(Schedulers.io());
    }

    //Get users from GitHub api
    private Observable<User> getUsersObservable() {

        return api.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<List<UserDetail>, ObservableSource<User>>) userDet -> Observable.fromIterable(userResponseToUser(userDet))
                        .subscribeOn(Schedulers.io()));

    }

    //Convert list of user from response to list of user entity for local db
    private ArrayList<User> userResponseToUser(List<UserDetail> userDet) {
        ArrayList<User> users = new ArrayList<>();
        for (UserDetail user : userDet) {

            users.add(new User(user.getId(), user));

        }
        return users;
    }

    //Add data from api to local db
    private void persistFetchedData(List<User> users) {
        realm.beginTransaction();

        //Local db has data, so changesCount has been initialized
        if (!realm.isEmpty()) {

            for (User user : users) {

                User currentUser = realm.where(User.class).equalTo("id", user.getId()).findFirst();

                if (currentUser != null) user.setCountChange(currentUser.getCountChange());

            }

        }
        //First opening of the application
        //Init changes count to default (0)
        else {

            for (User user : users) {

                user.setCountChange(0);

            }

        }

        realm.copyToRealmOrUpdate(users);

        realm.commitTransaction();
        realm.close();
    }

    private void initRecyclerView() {

        adapter = new MyUserAdapter(this, new ArrayList<>());
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(adapter);

    }

    private void displayLocalData() {

        //Convert all statements from db to list of user
        List<User> users = Realm.getDefaultInstance().copyFromRealm(dbService.getAll(User.class));

        if (!users.isEmpty()) {
            adapter.setUsers(users);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void displayFetchedData() {

        //Convert all statements from db to list of user
        List<User> users = Realm.getDefaultInstance().copyFromRealm(dbService.getAll(User.class));

        if (!users.isEmpty()) {
            adapter.setUsers(users);
            mProgressBar.setVisibility(View.GONE);
        }
        else{
            mProgressBar.setVisibility(View.GONE);
            showNoUsersSnackBar();
        }
    }

    private void showNoUsersSnackBar() {
        Snackbar.make(findViewById(R.id.user_container),
                R.string.user_list_is_empty, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Init filters for receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NAME);

        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Clear all disposables from rxjava calls
        mCompositeDisposable.clear();

        unregisterReceiver(receiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            if (extras != null) {

                //Get extras from onMessageReceived in MyFirebaseMessagingService
                Integer userId = extras.getInt(USER_ID_MESS);
                Integer changesCount = extras.getInt(CHANGES_COUNT_MESS);
                //Update user in local db
                updateUser(userId, changesCount);

            }

            //Update user list on the screen
            displayLocalData();
        }
    }

    public void updateUser(Integer userId, Integer changesCount) {
        Realm realm = Realm.getDefaultInstance();

        User currentUser;
        try {

            //Get user by id
            RealmResults<User> users = realm.where(User.class).equalTo("id", userId).findAll();
            currentUser = users.get(0);

            realm.beginTransaction();

            //Set new changes count
            currentUser.setCountChange(changesCount);

            realm.commitTransaction();
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(TAG, "User does not exist");
        }
    }
}
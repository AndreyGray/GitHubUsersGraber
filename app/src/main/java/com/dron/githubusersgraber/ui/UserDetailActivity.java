package com.dron.githubusersgraber.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dron.githubusersgraber.R;
import com.dron.githubusersgraber.adapter.MyUserRepoAdapter;
import com.dron.githubusersgraber.service.DataBaseService;
import com.dron.githubusersgraber.model.User;

import java.util.Objects;

import static android.view.View.GONE;
import static com.dron.githubusersgraber.misc.Const.USER_ID_MESS;

public class UserDetailActivity extends AppCompatActivity {

    RecyclerView repositoriesRecyclerview;
    TextView noRepositories;

    DataBaseService mDBService = new DataBaseService();
    MyUserRepoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        repositoriesRecyclerview = findViewById(R.id.user_repo_list);
        noRepositories = findViewById(R.id.no_repo);

        //Get user id from intent
        Integer userId = (Integer) getIntent().getSerializableExtra(USER_ID_MESS);

        //Get user from local db
        User user = mDBService.getById(userId, User.class);

        changeSupportActionBarTitle(user.getUserDetail().getLogin());

        initRecyclerView(user);

    }

    private void changeSupportActionBarTitle(String userLogin) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.repo));
        getSupportActionBar().setSubtitle(userLogin);
    }

    private void initRecyclerView(User user) {

        //If user does not have repositories show message
        if (user.getUserDetail().getRepositories().isEmpty()) {
            repositoriesRecyclerview.setVisibility(GONE);
            noRepositories.setText(getString(R.string.user_does_not_have_repositories, user.getUserDetail().getLogin()));
            noRepositories.setVisibility(View.VISIBLE);
        }
        //Show all repositories of specific user from local db
        else {
            adapter = new MyUserRepoAdapter(user.getUserDetail().getRepositories());
            repositoriesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            repositoriesRecyclerview.setHasFixedSize(true);
            repositoriesRecyclerview.setAdapter(adapter);
        }
    }

}

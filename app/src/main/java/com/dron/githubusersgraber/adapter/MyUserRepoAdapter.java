package com.dron.githubusersgraber.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dron.githubusersgraber.R;
import com.dron.githubusersgraber.model.UserRepo;

import java.util.List;

public class MyUserRepoAdapter extends RecyclerView.Adapter<MyUserRepoViewHolder> {

    List<UserRepo> mUserRepos;

    public MyUserRepoAdapter(List<UserRepo> userRepos) {
       this.mUserRepos = userRepos;
    }

    @NonNull
    @Override
    public MyUserRepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_item, parent, false);
        return new MyUserRepoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserRepoViewHolder holder, int position) {

        holder.repoName.setText(mUserRepos.get(position).getName());
        holder.repoUrl.setText(mUserRepos.get(position).getHtmlUrl());

    }

    @Override
    public int getItemCount() {
        return mUserRepos.size();
    }
}

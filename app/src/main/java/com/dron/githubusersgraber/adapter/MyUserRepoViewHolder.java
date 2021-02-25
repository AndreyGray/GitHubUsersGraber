package com.dron.githubusersgraber.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dron.githubusersgraber.R;

public class MyUserRepoViewHolder extends RecyclerView.ViewHolder {

    TextView repoName,
            repoUrl;

    public MyUserRepoViewHolder(@NonNull View itemView) {
        super(itemView);

        repoName = itemView.findViewById(R.id.repo_name);
        repoUrl = itemView.findViewById(R.id.repo_url);
    }
}
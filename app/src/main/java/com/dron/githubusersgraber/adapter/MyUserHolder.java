package com.dron.githubusersgraber.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dron.githubusersgraber.R;

public class MyUserHolder extends RecyclerView.ViewHolder{

     CardView container;
     TextView login;
     TextView count;

    public MyUserHolder(@NonNull View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.user_container);
        login = itemView.findViewById(R.id.user_login);
        count = itemView.findViewById(R.id.user_count);
    }
}

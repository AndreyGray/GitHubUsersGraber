package com.dron.githubusersgraber.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dron.githubusersgraber.R;
import com.dron.githubusersgraber.model.User;
import com.dron.githubusersgraber.ui.UserDetailActivity;

import java.util.List;

import static com.dron.githubusersgraber.misc.Const.USER_ID_MESS;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserHolder> {

    Context mContext;
    List<User> users;

    public MyUserAdapter(Context context, List<User> users) {
        mContext = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new MyUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserHolder holder, int position) {
        holder.login.setText(users.get(position).getUserDetail().getLogin().concat(" Id: ").concat(users.get(position).getId().toString()));

        //Display if count not 0
        if (users.get(position).getCountChange() != 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(String.valueOf(users.get(position).getCountChange()));
        }else{
            holder.count.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserDetailActivity.class);
            intent.putExtra(USER_ID_MESS, users.get(position).getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

}

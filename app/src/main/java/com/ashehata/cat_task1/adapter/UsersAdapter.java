package com.ashehata.cat_task1.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ashehata.cat_task1.R;
import com.ashehata.cat_task1.data.local.room.AppDatabase;
import com.ashehata.cat_task1.data.local.room.User;
import com.ashehata.cat_task1.utility.HelperMethod;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.Executors;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private View.OnClickListener onClickListener;
    private Context context;
    private List<User> users;

    public UsersAdapter(Context context, List<User> User) {
        this.context = context;
        this.users = User;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_user,
                parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setData(holder, position);
        setAction(holder, position);
    }

    private void setData(ViewHolder holder, int position) {
        // set user id
        holder.userId.setText(users.get(position).getId()+"");
        // set user title
        if(users.get(position).getTitle().length() >40){
            holder.userTitle.setText(users.get(position).getTitle().substring(0,40)+"...");
        }else {
            holder.userTitle.setText(users.get(position).getTitle());
        }
        // set user description
        if(users.get(position).getDescription().length() >40){
            holder.userDescrition.setText(users.get(position).getDescription().substring(0,40)+"...");
        }else {
            holder.userDescrition.setText(users.get(position).getDescription());
        }
    }

    private void setAction(ViewHolder holder, int position) {
        // Delete user
        holder.deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(position);
            }
        });

        // Update user
        holder.updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser(position);
            }
        });


    }

    private void updateUser(int position) {
        // convert object to json
        Bundle bundle = new Bundle();
        bundle.putString("user_data",new Gson().toJson(users.get(position)));
         HelperMethod.initializeNavigation((Activity) context)
                 .navigate(R.id.action_usersFragment_to_userDataFragment,bundle);

    }

    private void deleteUser(int position) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(context).userDao().deleteUser(users.get(position));
                HelperMethod.displayMessage((Activity)context,context.getString(R.string.deleted));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView userId , userTitle , userDescrition ;
        public FrameLayout deleteUser ,updateUser;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userId = view.findViewById(R.id.user_id);
            userTitle = view.findViewById(R.id.user_title);
            userDescrition = view.findViewById(R.id.user_description);
            deleteUser = view.findViewById(R.id.delete_user);
            updateUser = view.findViewById(R.id.update);

        }
    }
}

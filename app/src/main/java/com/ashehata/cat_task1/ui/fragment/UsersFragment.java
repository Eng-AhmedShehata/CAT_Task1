package com.ashehata.cat_task1.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.ashehata.cat_task1.R;
import com.ashehata.cat_task1.adapter.UsersAdapter;
import com.ashehata.cat_task1.data.local.room.AppDatabase;
import com.ashehata.cat_task1.data.local.room.User;
import com.ashehata.cat_task1.ui.activity.HomeActivity;
import com.ashehata.cat_task1.utility.HelperMethod;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsersFragment extends Fragment {

    @BindView(R.id.users_fragment_fab_add_user)
    FloatingActionButton usersFragmentFabAddUser;
    @BindView(R.id.users_fragment_rv_users)
    RecyclerView usersFragmentRvUsers;
    @BindView(R.id.users_fragment_group_no_user)
    Group usersFragmentGroupNoUser;

    private NavController navController;
    private LiveData<List<User>> usersData;
    private UsersAdapter usersAdapter;
    private HomeActivity homeActivity;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        // initialize butter knife
        ButterKnife.bind(this, view);
        // initialize toolbar
        initializeToolbar();
        // get navigation controller
        navController = HelperMethod.initializeNavigation(getActivity());
        // display all users
        displayUsers();

        return view;
    }

    private void initializeToolbar() {
        homeActivity = (HomeActivity) getActivity();
        // get toolbar object from home activity
        mToolbar = homeActivity.mToolbar;
        mToolbar.inflateMenu(R.menu.menu_main);
        // set items click listener
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete_all) {
                    deleteAllUsers();
                } else if (item.getItemId() == R.id.action_dummy_user) {
                    insertDummyUser();
                }
                return true;
            }
        });
    }

    private void insertDummyUser() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.setTitle("My Title");
                user.setDescription("My description");
                AppDatabase.getInstance(getContext()).userDao().insertUser(user);
            }
        });
    }

    private void deleteAllUsers() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getContext()).userDao().deleteAllUsers();
            }
        });
    }

    private void displayUsers() {
        HelperMethod.setRecyclerConfig(usersFragmentRvUsers, getContext());
        // get data from room database
        usersData = AppDatabase.getInstance(getContext()).userDao().getAllUsers();
        usersData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if(users.size() == 0){
                    usersFragmentGroupNoUser.setVisibility(View.VISIBLE);
                }else {
                    usersFragmentGroupNoUser.setVisibility(View.GONE);
                }
                usersAdapter = new UsersAdapter(getContext(), users);
                usersFragmentRvUsers.setAdapter(usersAdapter);
            }
        });
    }

    @OnClick(R.id.users_fragment_fab_add_user)
    public void onViewClicked() {
        navController.navigate(R.id.action_usersFragment_to_userDataFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release/delete menu items from fragment after destroy it's views
        mToolbar.getMenu().clear();
    }
}
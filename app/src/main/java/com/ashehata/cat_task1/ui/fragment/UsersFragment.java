package com.ashehata.cat_task1.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
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
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    @BindView(R.id.progress)
    ProgressBar progress;

    private NavController navController;
    private LiveData<List<User>> usersData;
    private UsersAdapter usersAdapter;
    private HomeActivity homeActivity;
    private Toolbar mToolbar;
    private String dataBaseName = "user_data";

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
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete_all:
                    deleteAllUsers();
                    break;
                case R.id.action_dummy_user:
                    insertDummyUser();
                    break;
                case R.id.create_backup:
                    createBackup();
                    break;
                case R.id.upload_backup:
                    uploadBackup();
                    break;
            }
            return true;
        });
    }

    private void createBackup() {

        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progress.setProgress(70, true);

            } else {
                progress.setProgress(70);

            }

            File db = getContext().getDatabasePath(dataBaseName);
            File dbShm = new File(db.getParent(), dataBaseName + "-shm");
            File dbWal = new File(db.getParent(), dataBaseName + "-wal");

            File db2 = new File(getContext().getCacheDir(), dataBaseName);
            File dbShm2 = new File(db2.getParent(), dataBaseName + "-shm");
            File dbWal2 = new File(db2.getParent(), dataBaseName + "-wal");

            try {
                FileUtils.copyFile(db, db2);
                FileUtils.copyFile(dbShm, dbShm2);
                FileUtils.copyFile(dbWal, dbWal2);
                Toast.makeText(getContext(), "Backup successfully :)", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("SAVEDB", e.toString());
                Toast.makeText(getContext(), "Backup faild :(", Toast.LENGTH_SHORT).show();

            } finally {
                progress.setVisibility(View.GONE);

            }

        } else {
            Snackbar.make(getView(), "Please allow access to your storage", Snackbar.LENGTH_LONG)
                    .setAction("Allow", view -> ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0)).show();
        }

    }

    private void uploadBackup() {

        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            restoreBackup();
        } else {

            Snackbar.make(getView(), "Please allow access to your storage", Snackbar.LENGTH_LONG)
                    .setAction("Allow", view -> ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0)).show();
        }
    }

    private void restoreBackup() {
        File db = new File(getContext().getCacheDir(), dataBaseName);
        File dbshm = new File(db.getParent(), dataBaseName + "-shm");
        Date date = new Date(dbshm.lastModified());
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, hh:mm aaa", Locale.ENGLISH);
        String dateFormat = format.format(date);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Restore a backup")
                .setCancelable(false)
                .setMessage("When you restore a backup, you 'll lose the current data.\n" +
                        "Last backup date: " + dateFormat)
                .setPositiveButton("Ok", (dialog, which) -> {
                    // get backup file
                    replaceCurrent(db, dbshm);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        alertDialog.show();

    }

    private void replaceCurrent(File currentBackup, File shmFile) {

        File db = currentBackup;
        File dbShm = shmFile;
        File dbWal = new File(db.getParent(), dataBaseName + "-wal");

        File db2 = getContext().getDatabasePath(dataBaseName);
        File dbShm2 = new File(db2.getParent(), dataBaseName + "-shm");
        File dbWal2 = new File(db2.getParent(), dataBaseName + "-wal");

        try {
            FileUtils.copyFile(db, db2);
            FileUtils.copyFile(dbShm, dbShm2);
            FileUtils.copyFile(dbWal, dbWal2);
            reopenActivity();
        } catch (Exception e) {
            Log.e("RESTOREDB", e.toString());
        }
    }

    private void reopenActivity() {
/*
        Intent mStartActivity = new Intent(getActivity(), HomeActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId,mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
*/

        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        startActivity(getActivity().getIntent());
        getActivity().overridePendingTransition(0, 0);

    }

    private void insertDummyUser() {
        // The same problem as the one in the UserDataFragment
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = new User();
            user.setTitle("My Title");
            user.setDescription("My description");
            AppDatabase.getInstance(getContext()).userDao().insertUser(user);
        });
    }

    private void deleteAllUsers() {
        // The same problem as the one in the UserDataFragment
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
                if (users.size() == 0) {
                    usersFragmentGroupNoUser.setVisibility(View.VISIBLE);
                } else {
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
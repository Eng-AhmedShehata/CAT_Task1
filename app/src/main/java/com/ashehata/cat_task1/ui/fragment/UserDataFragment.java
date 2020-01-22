package com.ashehata.cat_task1.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.ashehata.cat_task1.R;
import com.ashehata.cat_task1.data.local.room.AppDatabase;
import com.ashehata.cat_task1.data.local.room.User;
import com.ashehata.cat_task1.utility.HelperMethod;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDataFragment extends Fragment {

    @BindView(R.id.user_data_fragment_et_title)
    TextInputLayout userDataFragmentEtTitle;
    @BindView(R.id.user_data_fragment_et_description)
    TextInputLayout userDataFragmentEtDescription;
    @BindView(R.id.user_data_fragment_btn_add)
    Button userDataFragmentBtnAdd;
    // Set these fields to private
    String title = "";
    String description = "";
    User user;
    boolean isUpdate = false;
    private NavController navController;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_data, container, false);
        ButterKnife.bind(this, view);
        // get navigation controller
        navController = HelperMethod.initializeNavigation(getActivity());
        //Display user data
        displayData();

        return view;
    }

    private void displayData() {
        if (getArguments() != null) {
            isUpdate = true;
            userDataFragmentBtnAdd.setText(getString(R.string.update));
            // convert gson to User object
            String userData = getArguments().getString("user_data");
            user = new Gson().fromJson(userData, User.class);
            // Display correct data of the object
            userDataFragmentEtTitle.getEditText().setText(user.getTitle());
            userDataFragmentEtDescription.getEditText().setText(user.getDescription());
            // set curser at end of text
            userDataFragmentEtTitle.getEditText().setSelection(user.getTitle().length());
            userDataFragmentEtDescription.getEditText().setSelection(user.getDescription().length());
        }
    }

    @OnClick(R.id.user_data_fragment_btn_add)
    public void onViewClicked() {
        // hide keypad
        HelperMethod.disappearKeypad(getActivity(), getView());

        if (isUpdate) {
            updateCurrentUser();
        } else {
            addNewUser();
        }
    }

    private void updateCurrentUser() {
        // get entered data from fields
        title = userDataFragmentEtTitle.getEditText().getText().toString().trim();
        description = userDataFragmentEtDescription.getEditText().getText().toString().trim();
        // set entered data to an User object
        user.setTitle(title);
        user.setDescription(description);

        if (title.equals("")) {
            HelperMethod.displayMessage(getActivity(), getString(R.string.insert_title));
        } else {
            // insert object to database
            updateToDataBase();
        }
    }

    // This is what we call memory leaks, if you
    private void updateToDataBase() {
        // Initializing a new executor every time might cause memory leaks, please read about memory leaks.
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getContext()).userDao().updateUser(user);
                navController.popBackStack();
                HelperMethod.displayMessage(getActivity(), getString(R.string.updated));
            }
        });

    }

    private void addNewUser() {
        // get entered data from fields
        title = userDataFragmentEtTitle.getEditText().getText().toString().trim();
        description = userDataFragmentEtDescription.getEditText().getText().toString().trim();

        // set entered data to an User object
        user = new User();
        user.setTitle(title);
        user.setDescription(description);

        if (title.equals("")) {
            HelperMethod.displayMessage(getActivity(), getString(R.string.insert_title));
        } else {
            // insert object to database
            insertToDataBase();
        }
    }

    private void insertToDataBase() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(getContext()).userDao().insertUser(user);
                navController.popBackStack();
                // Check if the activity is not null !!
                if (getActivity() == null) return;
                // The getActivity() here is nullable, the user might press the back button then the app would crash
                HelperMethod.displayMessage(getActivity(), getString(R.string.done));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // prevent memory leaks !!
        executorService.shutdown();
    }
}
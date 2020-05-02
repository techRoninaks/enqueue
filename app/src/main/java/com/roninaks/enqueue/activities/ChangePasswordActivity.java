package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.ActivityChangePasswordBinding;
import com.roninaks.enqueue.helpers.EmailHelper;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.helpers.UserHelper;
import com.roninaks.enqueue.models.UserModel;
import com.roninaks.enqueue.viewmodels.UserViewModel;

public class ChangePasswordActivity extends AppCompatActivity {
    public static final String INTENT_PARAM_MODE = "mode";
    public static final String INTENT_PARAM_EMAIL = "email";
    public static final int MODE_CHANGE_PASSWORD = 0;
    public static final int MODE_FORGOT_PASSWORD = 1;
    private int mode;
    private String email;
    private ActivityChangePasswordBinding mBinding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mode = intent.getIntExtra(INTENT_PARAM_MODE, MODE_CHANGE_PASSWORD);
        email = intent.getStringExtra(INTENT_PARAM_EMAIL);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        if (mode == MODE_FORGOT_PASSWORD) {
            mBinding.etPasswordOld.setVisibility(View.GONE);
        }

        //Set onclicks
        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    try {
                        final UserHelper userHelper = new UserHelper(ChangePasswordActivity.this);
                        final String encryptedPassword = StringHelper.encryptPassword(mBinding.etPasswordNew.getText().toString());
                        if(email == null || email.isEmpty()){
                            if(UserHelper.userModel != null){
                                UserHelper.userModel.setPassword(encryptedPassword);
                                userViewModel.update(UserHelper.userModel);
                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_change_successful), Toast.LENGTH_SHORT).show();
                                userHelper.logout();
                            }
                        }else{
                            userViewModel.getUser(email).observe(ChangePasswordActivity.this, new Observer<UserModel>() {
                                @Override
                                public void onChanged(UserModel userModel) {
                                    if(userModel != null){
                                        if(userModel.getPassword().equals(encryptedPassword)){
                                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_change_successful), Toast.LENGTH_SHORT).show();
                                            userHelper.logout();
                                        }else{
                                            userModel.setPassword(encryptedPassword);
                                            userViewModel.update(userModel);
                                        }
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        EmailHelper emailHelper = new EmailHelper(ChangePasswordActivity.this, EmailHelper.TECH_SUPPORT, "Error: ChangePasswordActivity - Button Submit", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                        emailHelper.sendEmail();
                    }
                }
            }
        });

        //Set ViewModel
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);
    }

    private boolean validate() {
        boolean valid = true;
        try {
            if (mode == MODE_CHANGE_PASSWORD) {
                if (mBinding.etPasswordOld.getText().toString().isEmpty()) {
                    mBinding.etPasswordOld.setError(getString(R.string.required_field));
                    valid = false;
                } else if (UserHelper.userModel != null &&
                        !UserHelper.userModel.getPassword().equals(
                                StringHelper.encryptPassword(mBinding.etPasswordOld.getText().toString(),
                                        StringHelper.hexStringToByteArray(UserHelper.userModel.getPassword().split(":")[0])))) {
                    mBinding.etPasswordOld.setError(getString(R.string.incorrect_password));
                    valid = false;
                } else if (mBinding.etPasswordOld.getText().toString().equals(mBinding.etPasswordNew.getText().toString())) {
                    mBinding.etPasswordNew.setError(getString(R.string.previous_password));
                    valid = false;
                }
            }
            if (mBinding.etPasswordConfirm.getText().toString().isEmpty()) {
                mBinding.etPasswordConfirm.setError(getString(R.string.required_field));
                valid = false;
            } else if (!mBinding.etPasswordConfirm.getText().toString().equals(mBinding.etPasswordNew.getText().toString())) {
                mBinding.etPasswordConfirm.setError(getString(R.string.passowrd_mismatch));
                valid = false;
            }
            if (mBinding.etPasswordNew.getText().toString().isEmpty()) {
                mBinding.etPasswordNew.setError(getString(R.string.required_field));
                valid = false;
            }
        } catch (Exception e) {
            valid = false;
            EmailHelper emailHelper = new EmailHelper(ChangePasswordActivity.this, EmailHelper.TECH_SUPPORT, "Error: ChangePasswordActivity - validate()", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return valid;
    }
}

package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.ActivityForgotPasswordBinding;
import com.roninaks.enqueue.helpers.SqlHelper;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.interfaces.SqlDelegate;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding mBinding;
    private String otp;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        mBinding.setStage(getString(R.string.fp_stage_one));

        //Click listeners
        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.getStage().equals(ForgotPasswordActivity.this.getString(R.string.fp_stage_one))) {
                    if (validate()) {
                        otp = StringHelper.generateOtp(4);
                        email = mBinding.etUsername.getText().toString();
                        sendOtp(otp);
                        mBinding.setStage(ForgotPasswordActivity.this.getString(R.string.fp_stage_two));
                    }
                }else if(mBinding.getStage().equals(ForgotPasswordActivity.this.getString(R.string.fp_stage_two))){
                    if(validateOtp()){
                        Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                        intent.putExtra(ChangePasswordActivity.INTENT_PARAM_MODE, ChangePasswordActivity.MODE_FORGOT_PASSWORD);
                        intent.putExtra(ChangePasswordActivity.INTENT_PARAM_EMAIL, email);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        mBinding.tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Text Watchers
        mBinding.etOtpOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    mBinding.etOtpOne.clearFocus();
                    mBinding.etOtpTwo.requestFocus();
                }
            }
        });
        mBinding.etOtpTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    mBinding.etOtpTwo.clearFocus();
                    mBinding.etOtpOne.requestFocus();
                }else {
                    mBinding.etOtpTwo.clearFocus();
                    mBinding.etOtpThree.requestFocus();
                }
            }
        });
        mBinding.etOtpThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    mBinding.etOtpThree.clearFocus();
                    mBinding.etOtpTwo.requestFocus();
                }else {
                    mBinding.etOtpThree.clearFocus();
                    mBinding.etOtpFour.requestFocus();
                }
            }
        });
        mBinding.etOtpFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    mBinding.etOtpFour.clearFocus();
                    mBinding.etOtpThree.requestFocus();
                }else {
                    mBinding.etOtpFour.clearFocus();
                    InputMethodManager imm = (InputMethodManager) ForgotPasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mBinding.etOtpFour.getWindowToken(), 0);
                    mBinding.btnSubmit.callOnClick();
                    mBinding.btnSubmit.requestFocus();
                }
            }
        });
    }

    /***
     * Validations for Email field
     * @return true if validation checks are passed, false if any tests fail
     */
    private boolean validate(){
        boolean valid = true;
        if(mBinding.etUsername.getText().toString().isEmpty()){
            valid = false;
            mBinding.etUsername.setError(getString(R.string.required_field));
        }else if(!LoginActivity.isValidEmail(mBinding.etUsername.getText().toString())){
            mBinding.etUsername.setError(getString(R.string.invalid_email));
            valid = false;
        }
        return valid;
    }

    /***
     * Validates OTP fields
     * @return true if validation checks are passed, false if any tests fail
     */
    private boolean validateOtp(){
        boolean valid = true;
        if(mBinding.etOtpOne.getText().toString().isEmpty()){
            mBinding.etOtpOne.setError(getString(R.string.required_field));
            valid = false;
        }
        if(mBinding.etOtpTwo.getText().toString().isEmpty()){
            mBinding.etOtpTwo.setError(getString(R.string.required_field));
            valid = false;
        }
        if(mBinding.etOtpThree.getText().toString().isEmpty()){
            mBinding.etOtpThree.setError(getString(R.string.required_field));
            valid = false;
        }
        if(mBinding.etOtpFour.getText().toString().isEmpty()){
            mBinding.etOtpFour.setError(getString(R.string.required_field));
            valid = false;
        }
        String typedOtp = mBinding.etOtpOne.getText().toString() + mBinding.etOtpTwo.getText().toString()
                + mBinding.etOtpThree.getText().toString() + mBinding.etOtpFour.getText().toString();
        if(!typedOtp.equals(otp)) {
            valid = false;
            Toast.makeText(this,getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    /***
     * Sends OTP to specified usermail
     * @param otp - OTP to send
     */
    private void sendOtp(String otp){
        SqlHelper sqlHelper = new SqlHelper(this, new SqlDelegate() {
            @Override
            public void onResponse(SqlHelper sqlHelper) {

            }
        });
        sqlHelper.setService(false);
        sqlHelper.setExecutePath("send-otp.php");
        sqlHelper.setMethod(getString(R.string.method_post));
        ContentValues params = new ContentValues();
        params.put("otp", otp);
        params.put("email", email);
        sqlHelper.setParams(params);
        sqlHelper.executeUrl(true);

    }

}

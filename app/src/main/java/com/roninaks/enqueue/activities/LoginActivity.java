package com.roninaks.enqueue.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.helpers.EmailHelper;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.helpers.UserHelper;
import com.roninaks.enqueue.models.UserModel;
import com.roninaks.enqueue.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private TextView tvForgotPassword;
    private Button btnSubmit;
    private UserViewModel userViewModel;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performChecks();
        setContentView(R.layout.activity_login);
//        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);
        //Set default values
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        //Onclick listeners
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_submit:{
                        if (validateSignIn()) {
                            btnSubmit.setEnabled(false);
                            btnSubmit.setBackground(getDrawable(R.drawable.button_background_filled_disabled));
                            attemptLogin(getString(R.string.default_signin), editTextUsername.getText().toString());
                        }
                    }
                    break;
                    case R.id.tv_forgot_password:{
                        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                    }
                    break;
                }
            }
        };
        btnSubmit.setOnClickListener(onClickListener);
        tvForgotPassword.setOnClickListener(onClickListener);
    }

    /***
     * Perform required checks
     */
    private void performChecks() {
        if (isLoggedIn()) {
            Intent intent = new Intent(this, ServicesActivity.class);
            intent.putExtra(ServicesActivity.INTENT_PARAM_USERID, userId);
            startActivity(intent);
            finish();
        }
    }

    /***
     * Checks whether a user is logged in
     * @return True if a user is logged in. False if no users are logged in.
     */
    private boolean isLoggedIn() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
            if ((userId = sharedPreferences.getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1)) >= 0) {
                return true;
            }
        } catch (Exception e) {
            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity - isLoggedIn()", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return false;
    }

    /***
     * Check whether username password combination is valid or not
     * @param type - Type of login
     * @param username - Email login credential
     */
    private void attemptLogin(String type, String username) {
        try {
            if (type.equals(getString(R.string.default_signin))) {
                userViewModel.getUser(username).observe(this, new Observer<UserModel>() {
                    @Override
                    public void onChanged(UserModel userModel) {
                        try {
                            if (userModel != null) {
                                String password = userModel.getPassword();
                                if (password.equals(StringHelper.encryptPassword(editTextPassword.getText().toString(), StringHelper.hexStringToByteArray(password.split(":")[0])))) {
                                    userId = userModel.getUserId();
                                    UserHelper.userModel = userModel;
                                    SharedPreferences sharedPreferences = getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
                                    sharedPreferences.edit().putInt(StringHelper.SHARED_PREFERENCE_USER_ID, userId).apply();
                                    sharedPreferences.edit().putBoolean(StringHelper.SHARED_PREFERENCE_USER_RESET, false).apply();
                                    Intent intent = new Intent(LoginActivity.this, ServicesActivity.class);
                                    intent.putExtra(ServicesActivity.INTENT_PARAM_USERID, userId);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.invalid_cred), Toast.LENGTH_LONG).show();
                                    btnSubmit.setEnabled(true);
                                    btnSubmit.setBackground(getDrawable(R.drawable.button_background_filled_primary));
                                }
                            }
                        }catch (Exception e){
                            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity - attemptSignup", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                            emailHelper.sendEmail();
                        }
                    }
                });
            } else if (type.equals(getString(R.string.google_signin))) {
//                Intent signInIntent = googleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, PermissionsHelper.REQUEST_GOOGLE_AUTHENTICATION);
            } else if (type.equals(getString(R.string.fb_signin))) {
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//                if (isLoggedIn)
//                    LoginManager.getInstance().logOut();
//                loginButton.performClick();
            }
        } catch (Exception e) {
            EmailHelper emailHelper = new EmailHelper(LoginActivity.this, EmailHelper.TECH_SUPPORT, "Error: LoginActivity", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    /***
     * Check whether credentials are valid
     * @return true if details are valid, false if either email or password is invalid
     */
    private boolean validateSignIn() {
        String userName, password;
        boolean signIn = true;
        userName = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        if (userName.isEmpty()) {
            editTextUsername.setError(getString(R.string.required_field));
            signIn = false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.required_field));
            signIn = false;
        } else if (!userName.isEmpty() || !password.isEmpty()) {
            if (!isValidEmail(userName)) {
                editTextUsername.setError(getString(R.string.invalid_email));
                editTextPassword.setText("");
                editTextUsername.setText("");
//                Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
//                Toast.makeText(this, R.string.invalid_cred, Toast.LENGTH_SHORT).show();
                signIn = false;
            }
            if (!isValidPassword(password)) {
                editTextPassword.setError(getString(R.string.invalid_password));
                editTextPassword.setText("");
                editTextUsername.setText("");
//                Toasty.error(LoginActivity.this, R.string.invalid_cred, Toast.LENGTH_SHORT, false).show();
                Toast.makeText(this, R.string.invalid_cred, Toast.LENGTH_SHORT).show();
                signIn = false;
            }
        }


        return signIn;
    }

    /***
     * Check whether email is valid
     * @param email - Email entered in the password
     * @return True if email is Valid and false if it is invalid
     */
    public static boolean isValidEmail(String email) {
        boolean isValid = false;
        if (email.contains("@"))
            isValid = true;
        return isValid;
    }

    /***
     * Check whether password is valid
     * @param password - Entered password
     * @return true if password is valid, false if password is invalid
     */
    public static boolean isValidPassword(String password) {
        int charCount = 0, numCount = 0;
        int length = password.length();
        if (length < 6)
            return false;
        String numeral = "0123456789";
        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            if (c >= 'A' && c <= 'Z')
                charCount++;
            else if (numeral.contains("" + c))
                numCount++;
        }
        if (charCount > 0 && numCount > 0)
            return true;
        else
            return false;
    }
}

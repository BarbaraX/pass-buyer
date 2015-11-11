package com.barbara.passbuyer.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.barbara.passbuyer.Utils.ViewHelper;
import com.barbara.passbuyer.Model.LoginResponse;
import com.barbara.passbuyer.R;
import com.barbara.passbuyer.Utils.BaseUrls;
import com.barbara.passbuyer.Utils.HttpUtil;
import com.google.gson.Gson;


/**
 * A login screen that offers login via userName/password.
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    //Shared Peferences.
    private SharedPreferences pref;

    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mRememberPassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.userName);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 100 || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin(BaseUrls.LOGIN_BASE_URL);
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(BaseUrls.LOGIN_BASE_URL);
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(BaseUrls.REGISTER_BASE_URL);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mRememberPassView = (CheckBox)findViewById(R.id.remember_box);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_login_info", false);
        if (isRemember) {
            mUserNameView.setText(pref.getString("user_name", ""));
            mPasswordView.setText(pref.getString("password",""));
            mRememberPassView.setChecked(true);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid userName, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String url) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userName.
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_userName_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //记住用户设置信息
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            rememberLoginInfo(userName, password);
//            showProgress(true);
            ViewHelper.switchShowedView(this, mLoginFormView, mProgressView);
            mAuthTask = new UserLoginTask(userName, password, url);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
/*
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
*/


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUserName;
        private final String mPassword;
        private final String mRequestUrl;

        UserLoginTask(String userName, String password, String url) {
            mUserName = userName;
            mPassword = password;
            mRequestUrl = url+"?buyerPhone="+mUserName+"&buyerPwd="+mPassword;
        }


        @Override
        protected String doInBackground(Void... params) {
            // 发送HTTP请求
            String responseStr = HttpUtil.sendHttpGetRequest(mRequestUrl);

            if (responseStr==null) {
                return "";
            } else {
                // 利用Gson解析HTTP相应
                LoginResponse response = new Gson().fromJson(responseStr, LoginResponse.class);
                Log.i("passBuyer",response.toString());
                //根据响应数据生成相应的resultCode给onPostExecute
                return response.getResultInfo();
            }
        }

        @Override
        protected void onPostExecute(final String resultInfo) {
            mAuthTask = null;
//            showProgress(false);
            ViewHelper.switchShowedView(LoginActivity.this, mProgressView, mLoginFormView);

            switch (resultInfo) {
                case "登陆成功":
                    startActivity(new Intent(LoginActivity.this, ShopListActivity.class));
                    finish();
                    break;
                case "请注册账号":
                    mUserNameView.setError(getString(R.string.error_invalid_userName));
                    mUserNameView.requestFocus();
                    break;
                case "密码错误":
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.setText("");
                    mPasswordView.requestFocus();
                    break;
                case "注册成功":
                    Toast.makeText(LoginActivity.this, R.string.success_register, Toast.LENGTH_SHORT).show();
                    break;
                case "账号已存在":
                    mUserNameView.setError(getString(R.string.error_register_failed));
                    mUserNameView.requestFocus();
                    break;
                default:
                    Toast.makeText(LoginActivity.this, R.string.error_action_failed, Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
            ViewHelper.switchShowedView(LoginActivity.this, mProgressView, mLoginFormView);
        }
    }

    private void rememberLoginInfo(String account, String pwd){
        SharedPreferences.Editor editor = pref.edit();
        if (mRememberPassView.isChecked()) {
            editor.putBoolean("remember_login_info",true);
            editor.putString("user_name", account);
            editor.putString("password", pwd);
        } else {
            editor.clear();
        }
        editor.commit();
    }



}


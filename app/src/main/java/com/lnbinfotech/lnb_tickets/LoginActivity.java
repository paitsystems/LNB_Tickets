package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;
import com.lnbinfotech.lnb_tickets.post.Post;

import java.net.URLEncoder;

// Created by lnb on 8/11/2016.

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView;
    private CheckBox cb_remember;
    private ProgressDialog pd;
    private InputMethodManager input;
    private Drawable drawable;
    private Toast toast;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar()!=null){
            getSupportActionBar().show();
        }
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        cb_remember = (CheckBox) findViewById(R.id.remember);
        input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        drawable = mEmailView.getBackground();
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);

        toast = Toast.makeText(LoginActivity.this,"",Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER,0,0);

        mEmailView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSoftKeyboard(mEmailView);
                mEmailView.setBackgroundDrawable(drawable);
                mEmailView.setError(null);
                return false;
            }
        });

        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSoftKeyboard(mPasswordView);
                mPasswordView.setBackgroundDrawable(drawable);
                mPasswordView.setError(null);
                return false;
            }
        });

        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mPasswordView.setBackgroundDrawable(drawable);
                mPasswordView.setError(null);
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    input.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                    handled = true;
                    attemptLogin();
                }
                return handled;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.login);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.hideSoftInputFromWindow(mEmailSignInButton.getWindowToken(), 0);
                attemptLogin();
            }
        });
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            mPasswordView.setBackgroundResource(R.drawable.apptheme_textfield_activated_holo_light);
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            mEmailView.setBackgroundResource(R.drawable.apptheme_textfield_activated_holo_light);
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (!FirstActivity.pref.contains(getString(R.string.pref_logged))) {
                if (ConnectivityTest.getNetStat(LoginActivity.this)) {
                    try {
                        String user = URLEncoder.encode(mEmailView.getText().toString(), "UTF-8");
                        String pass = URLEncoder.encode(mPasswordView.getText().toString(), "UTF-8");
                        String url = Constant.ipaddress + "/getEmpValid?UserName=" + user + "&Password=" +pass;
                        writeLog("LoginActivity_attemptLogin_"+url);
                        Constant.showLog(url);
                        new UserLoginTask().execute(url);
                    }catch (Exception e){
                        e.printStackTrace();
                        writeLog("LoginActivity_attemptLogin_"+e.getMessage());
                        toast.setText("Something Went Wrong");
                        toast.show();
                    }
                } else {
                    toast.setText("Network Connection Error");
                    toast.show();
                }
            } else if (FirstActivity.pref.contains(getString(R.string.pref_logged))) {
                if (FirstActivity.pref.getBoolean(getString(R.string.pref_logged), false)) {
                    startMainActivity();
                } else {
                    if (FirstActivity.pref.getString(getString(R.string.pref_username), "").equals(mEmailView.getText().toString()) && FirstActivity.pref.getString(getString(R.string.pref_password), "").equals(mPasswordView.getText().toString())) {
                        startMainActivity();
                    } else {
                        toast.setText("Invalid Username/Password");
                        toast.show();
                    }
                }
            }
        }
    }

    private class UserLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return Post.POST(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("")) {
                try {
                    result = result.replace("\\", "");
                    result = result.replace("''", "");
                    result = result.substring(1, result.length() - 1);
                    int type = new ParseJSON(result,getApplicationContext()).parseUserData();
                    if(type != 0){
                        SharedPreferences.Editor editor = FirstActivity.pref.edit();
                        String uname = mEmailView.getText().toString();
                        editor.putString(getString(R.string.pref_username), uname);
                        editor.putString(getString(R.string.pref_password), mPasswordView.getText().toString());
                        editor.apply();
                        startMainActivity();
                        pd.dismiss();
                    }else {
                        pd.dismiss();
                        toast.setText("Invalid Username/Password");
                        toast.show();
                    }
                } catch (Exception e) {
                    writeLog("LoginActivity_UserLoginTask_"+e.getMessage());
                    e.printStackTrace();
                    toast.setText("Something Went Wrong");
                    toast.show();
                    pd.dismiss();
                }
            } else {
                writeLog("LoginActivity_UserLoginTask_Network_Connection_Error");
                toast.setText("Network Connection Error");
                toast.show();
                pd.dismiss();
            }
        }
    }

    private void startMainActivity() {
        SharedPreferences.Editor editor = FirstActivity.pref.edit();
        if (cb_remember.isChecked()) {
            editor.putBoolean(getString(R.string.pref_logged), true);
        } else {
            editor.putBoolean(getString(R.string.pref_logged), false);
        }
        editor.apply();
        if (FirstActivity.pref.contains(getString(R.string.pref_nickname))) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), NickNameActivity.class));
        }
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }
}

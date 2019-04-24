package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;

import java.net.URLEncoder;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private EditText ed_old_password, ed_new_password, ed_re_new_password;
    private Button btn_update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btn_update.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                verifyPassword();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(ChangePasswordActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ChangePasswordActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyPassword() {
        String old_pass = FirstActivity.pref.getString(getString(R.string.pref_password), "");
        String old_pass1 = ed_old_password.getText().toString();
        String new_pass = ed_new_password.getText().toString();
        String new_pass1 = ed_re_new_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(old_pass1)) {
            ed_old_password.setError(getString(R.string.error_field_required));
            focusView = ed_old_password;
            cancel = true;
        } else if (TextUtils.isEmpty(new_pass)) {
            ed_new_password.setError(getString(R.string.error_field_required));
            focusView = ed_new_password;
            cancel = true;
        } else if (TextUtils.isEmpty(new_pass1)) {
            ed_re_new_password.setError(getString(R.string.error_field_required));
            focusView = ed_re_new_password;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            if (old_pass.equals(old_pass1)) {
                if (new_pass.equals(new_pass1)) {
                    updatePassword(new_pass);
                } else {
                    toast.setText("New Password Does Not Macth");
                    toast.show();
                }
            } else {
                toast.setText("Old Password Does Not Macth");
                toast.show();
            }
        }
    }

    private void updatePassword(final String new_pass) {
        constant.showPD();
        try {
            String type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
            int auto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
            String newpass = URLEncoder.encode(new_pass, "UTF-8");
            String url = Constant.ipaddress + "/UpdatePassword?auto=" + auto + "&type=" + type + "&newpass=" + newpass;
            writeLog("ChangePasswordActivity_updatePassword_" + url);
            Constant.showLog(url);
            StringRequest request = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            Constant.showLog(result);
                            result = result.replace("\\", "");
                            result = result.replace("''", "");
                            result = result.replace("\"", "");
                            if (!result.equals("0") && !result.equals("")) {
                                SharedPreferences.Editor editor = FirstActivity.pref.edit();
                                editor.putString(getString(R.string.pref_password), new_pass);
                                editor.apply();
                                showDia(1);
                            } else {
                                showDia(3);
                            }
                            Constant.showLog(result);
                            constant.showPD();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            writeLog("ChangePasswordActivity_updatePassword_volley_" + error.getMessage());
                            error.printStackTrace();
                            constant.showPD();
                            showDia(3);
                        }
                    }
            );
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
        } catch (Exception e) {
            writeLog("ChangePasswordActivity_updatePassword_" + e.getMessage());
            e.printStackTrace();
            toast.setText("Something Went Wrong");
            toast.show();
        }
    }

    private void init() {
        constant = new Constant(ChangePasswordActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ed_old_password = findViewById(R.id.ed_old_passord);
        ed_new_password = findViewById(R.id.ed_new_password);
        ed_re_new_password = findViewById(R.id.ed_re_new_password);
        btn_update = findViewById(R.id.btn_update);
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME, MODE_PRIVATE);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
        builder.setCancelable(false);
        if (a == 1) {
            builder.setMessage("Password Updated Successfully");
            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ChangePasswordActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
        } else if (a == 3) {
            builder.setMessage("Error While Updating Password");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    verifyPassword();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ChangePasswordActivity_" + _data);
    }
}
package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Gravity;

import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.post.Post;

import java.net.URL;
import java.net.URLEncoder;

public class NickNameActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private EditText ed_nickname, ed_omobno;
    private Button btn_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btn_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set:
                validation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(NickNameActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(NickNameActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(NickNameActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        ed_nickname = (EditText) findViewById(R.id.ed_nickname);
        ed_omobno = (EditText) findViewById(R.id.ed_omobno);
        btn_set = (Button) findViewById(R.id.btn_set);

    }

    private void validation(){
        String nickname = ed_nickname.getText().toString();
        String omobno = ed_omobno.getText().toString();
        if(!nickname.equals("") && ! omobno.equals("")){
            if(omobno.length()==10) {
                updateNickName(nickname, omobno);
            }else{
                toast.setText("Please Enter Valid Mobile Number");
                toast.show();
            }
        }else{
            toast.setText("Please Enter Details");
            toast.show();
        }
    }

    private void updateNickName(String nickname, String omobno){
        try {
            int clientauto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
            String empType = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
            nickname = URLEncoder.encode(nickname, "UTF-8");
            omobno = URLEncoder.encode(omobno,"UTF-8");
            String url = Constant.ipaddress + "/GetNickName?auto="+clientauto+"&type="+empType+"&empId="+clientauto
                    +"&nickname="+nickname+"&omobno="+omobno;
            Constant.showLog(url);
            new updateNickName().execute(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class updateNickName extends AsyncTask<String,Void,String>{

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(NickNameActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Post.POST(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if(!s.equals("")) {
                Constant.showLog(s);
                if(s.equals("\"1\"")) {
                    DBHandler db = new DBHandler(getApplicationContext());
                    int clientauto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
                    db.updateNickName(ed_nickname.getText().toString(),ed_omobno.getText().toString(), String.valueOf(clientauto));
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putString(getString(R.string.pref_nickname), ed_nickname.getText().toString());
                    editor.apply();
                    showDia(1);
                }else{
                    showDia(2);
                }
            }

        }
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NickNameActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(NickNameActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 1) {
            builder.setMessage("NickName Updated Successfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                    dialog.dismiss();
                }
            });

        }else if (a == 2) {
            builder.setMessage("Error While Updating NickName");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    validation();
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
        new WriteLog().writeLog(getApplicationContext(), "NickNameActivity_" + _data);
    }
}

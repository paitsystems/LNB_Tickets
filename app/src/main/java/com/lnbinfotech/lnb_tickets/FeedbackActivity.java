package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private RatingBar ratingBar, ratingBar2;
    private TextView tv_rating_value1, tv_rating_value2, tv_cat1, tv_cat2, tv_cat3, tv_cat4, tv_cat5, tv_q1, tv_q2;
    private AppCompatButton btn_submit, btn_next;
    //private Animation zoomin, zoomout;
    private ImageView img_feed, img_terrible, img_bad, img_good, img_okay, img_awesome, img_rating_val;
    private LinearLayout lay_feed, lay_smily, lay_rating, lay_sugg;
    private String rate_value = "";
    private DBHandler db;
    private List<Integer> QList;
    private int flag = 1, leadid = 0;
    private String rate = "", q = "", type = "", sugg = "";
    private HashMap<Integer, String> fed_map;
    private EditText ed_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btn_submit.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        img_okay.setOnClickListener(this);
        img_terrible.setOnClickListener(this);
        img_good.setOnClickListener(this);
        img_bad.setOnClickListener(this);
        img_awesome.setOnClickListener(this);

        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                rate = String.valueOf(rating);
                tv_rating_value2.setText(rate);
            }
        });

        Cursor res = db.getFeedQuestions();
        if(res.getCount()>0){
            getFeedData();
            setQues();
            //saveAnswers();
        }else{
            if(ConnectivityTest.getNetStat(getApplicationContext())){
                String url = "1";
                new getQuestBank().execute(url);
            }else{
                toast.setText("You Are Offline");
                toast.show();
            }
        }
        res.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                saveAnswers();
                Constant.showLog("fed_map-" + fed_map);
                sugg = ed_description.getText().toString();
                /*new Constant(this).doFinish();
                Intent intent = new Intent(FeedbackActivity.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);*/
                showDia(1);
                break;

            case R.id.img_bad:
                rate_value = "Bad";
                tv_rating_value1.setText(rate_value);
                img_rating_val.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sentiment_dissatisfied_black_24dp, null));
                tv_cat2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                tv_cat4.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat5.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                break;

            case R.id.img_terrible:
                rate_value = "Terrible";
                tv_rating_value1.setText(rate_value);
                img_rating_val.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sentiment_very_dissatisfied_black_24dp, null));
                tv_cat2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat4.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                tv_cat3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat5.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                break;

            case R.id.img_okay:
                rate_value = "Okay";
                tv_rating_value1.setText(rate_value);
                img_rating_val.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sentiment_neutral_black_24dp, null));
                tv_cat2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat4.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                tv_cat5.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                break;

            case R.id.img_good:
                rate_value = "Good";
                tv_rating_value1.setText(rate_value);
                img_rating_val.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sentiment_satisfied_black_24dp, null));
                tv_cat2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat4.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                tv_cat1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat5.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                break;

            case R.id.img_awesome:
                rate_value = "Awesome";
                tv_rating_value1.setText(rate_value);
                img_rating_val.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sentiment_very_satisfied_black_24dp, null));
                tv_cat2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat4.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat5.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                break;

            case R.id.btn_next:
                saveAnswers();
                flag = flag + 1;
                //img_rating_val.setImageDrawable(getResources().getDrawable(R.drawable.ic_sentiment_very_satisfied_black_24dp));
                img_rating_val.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sentiment_neutral_black_24dp, null));
                /*img_bad.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                img_good.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                img_terrible.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                img_okay.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                img_awesome.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));*/
                tv_cat2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat4.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat3.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_cat5.setTextColor(ResourcesCompat.getColor(getResources(), R.color.ldblue, null));
                tv_rating_value1.setText("");
                ratingBar2.setRating(0F);
                tv_rating_value2.setText("");
                setQues();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(FeedbackActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(FeedbackActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        fed_map = new HashMap<>();
        db = new DBHandler(FeedbackActivity.this);
        constant = new Constant(FeedbackActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        // ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        tv_rating_value1 = (TextView) findViewById(R.id.tv_rating_value1);
        tv_rating_value2 = (TextView) findViewById(R.id.tv_rating_value2);
        tv_cat1 = (TextView) findViewById(R.id.tv_cat1);
        tv_cat2 = (TextView) findViewById(R.id.tv_cat2);
        tv_cat3 = (TextView) findViewById(R.id.tv_cat3);
        tv_cat4 = (TextView) findViewById(R.id.tv_cat4);
        tv_cat5 = (TextView) findViewById(R.id.tv_cat5);
        tv_q1 = (TextView) findViewById(R.id.tv_q1);
        tv_q2 = (TextView) findViewById(R.id.tv_q2);
        btn_submit = (AppCompatButton) findViewById(R.id.btn_submit);
        btn_next = (AppCompatButton) findViewById(R.id.btn_next);
        img_feed = (ImageView) findViewById(R.id.img_feed);
        img_terrible = (ImageView) findViewById(R.id.img_terrible);
        img_okay = (ImageView) findViewById(R.id.img_okay);
        img_bad = (ImageView) findViewById(R.id.img_bad);
        img_good = (ImageView) findViewById(R.id.img_good);
        img_awesome = (ImageView) findViewById(R.id.img_awesome);
        img_rating_val = (ImageView) findViewById(R.id.img_rating_val);
        lay_feed = (LinearLayout) findViewById(R.id.lay_feed);
        lay_smily = (LinearLayout) findViewById(R.id.lay_smily);
        lay_rating = (LinearLayout) findViewById(R.id.lay_rating);
        lay_sugg = (LinearLayout) findViewById(R.id.lay_sugg);
        ed_description = (EditText) findViewById(R.id.ed_description);
        QList = new ArrayList<>();
    }

    private void saveAnswers() {
        String rate_val = "";
        if (type.equals("R")) {
            rate_val = tv_rating_value2.getText().toString();
        } else {
            rate_val = tv_rating_value1.getText().toString();
        }
        fed_map.put(flag, rate_val);
        //Constant.showLog("fed_map-" + fed_map);
        //Constant.showLog("fed_map-" + fed_map.size());
    }

    private void setQues() {
        q = db.getQuestion(flag);
        type = db.getFedType1(q);

        if (type.equals("R")) {
            tv_q2.setText(q);
            lay_rating.setVisibility(View.VISIBLE);
            lay_smily.setVisibility(View.GONE);
        } else {
            tv_q1.setText(q);
            lay_rating.setVisibility(View.GONE);
            lay_smily.setVisibility(View.VISIBLE);
        }

        Cursor res = db.getQuesOPTIONS(flag);
        if (res.moveToFirst()) {
            do {
                String c1 = res.getString(res.getColumnIndex(DBHandler.FED_CAT1));
                if (!c1.equals("NA")) {
                    tv_cat1.setText(c1);
                } else {
                    tv_cat1.setVisibility(View.GONE);
                }

                String c2 = res.getString(res.getColumnIndex(DBHandler.FED_CAT2));
                if (!c1.equals("NA")) {
                    tv_cat2.setText(c2);
                } else {
                    tv_cat2.setVisibility(View.GONE);
                }

                String c3 = res.getString(res.getColumnIndex(DBHandler.FED_CAT3));
                if (!c1.equals("NA")) {
                    tv_cat3.setText(c3);
                } else {
                    tv_cat3.setVisibility(View.GONE);
                }

                String c4 = res.getString(res.getColumnIndex(DBHandler.FED_CAT4));
                if (!c1.equals("NA")) {
                    tv_cat4.setText(c4);
                } else {
                    tv_cat4.setVisibility(View.GONE);
                }
                String c5 = res.getString(res.getColumnIndex(DBHandler.FED_CAT5));
                if (!c1.equals("NA")) {
                    tv_cat5.setText(c5);
                } else {
                    tv_cat5.setVisibility(View.GONE);
                }
                /*String c6 = res.getString(res.getColumnIndex(DBHandler.FED_CAT1));
                if(!c1.equals("NA")){
                    tv_cat6.setText(c6);
                }else {
                    tv_cat6.setVisibility(View.GONE);
                }*/

            } while (res.moveToNext());
        }
        res.close();

        if (flag == QList.size()) {
            lay_sugg.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
            btn_submit.setVisibility(View.VISIBLE);
        } else {
            lay_sugg.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
            btn_submit.setVisibility(View.GONE);
        }
    }

    private void getFeedData() {
        QList.clear();
        Cursor res = db.getFeedQuestions();
        if (res.moveToFirst()) {
            do {
                QList.add(res.getInt(res.getColumnIndex(DBHandler.FED_AUTO)));
            } while (res.moveToNext());
        }
        res.close();
        Constant.showLog("QList" + QList);



        /*if (flag == 0){

        if (db.getFedType(QList.get(0)).equals("R")) {
            tv_q2.setText(QList.get(0));
            lay_rating.setVisibility(View.VISIBLE);
            lay_smily.setVisibility(View.GONE);
        } else {
            tv_q1.setText(QList.get(0));
            lay_rating.setVisibility(View.GONE);
            lay_smily.setVisibility(View.VISIBLE);
        }
        }else if (flag == 1)*/

        /*if (db.getFedType(QList.get(1)).equals("R")) {
            tv_q2.setText(QList.get(1));
            lay_rating.setVisibility(View.VISIBLE);
            lay_smily.setVisibility(View.GONE);
        } else {
            tv_q1.setText(QList.get(1));
            lay_rating.setVisibility(View.GONE);
            lay_smily.setVisibility(View.VISIBLE);
        }*/
    }

    private void saveData() {
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
        int clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
        String nickname = FirstActivity.pref.getString(getString(R.string.pref_nickname), "NA");
        String contactNo  = FirstActivity.pref.getString(getString(R.string.pref_contactNo), "0");
        String imeino= new Constant(getApplicationContext()).getIMEINo1();
        String data = "";
        for (int i = 0; i < QList.size(); i++) {
            data = data + QList.get(i) + "^" + fed_map.get(QList.get(i)) + ",";
        }
        data = data.substring(0, data.length() - 1);
        Constant.showLog("data-" + data);

        String url = clientAuto + "|Branch|" + nickname + "|" + contactNo + "|" + imeino +"|" + sugg + "|" + data;
        Constant.showLog(url);

        if (ConnectivityTest.getNetStat(getApplicationContext())) {
            new saveFeedbackMaster().execute(url);
        } else {
            toast.setText("You Are Offline");
            toast.show();
        }
    }

    private class saveFeedbackMaster extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(FeedbackActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String value = "";
            HttpPost request = new HttpPost(Constant.ipaddress + "/SaveFeedBack");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            try {
                JSONStringer vehicle = new JSONStringer().object().key("rData").object().key("details").value(url[0]).endObject().endObject();
                StringEntity entity = new StringEntity(vehicle.toString());
                request.setEntity(entity);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                Constant.showLog("Saving : " + response.getStatusLine().getStatusCode());
                value = new BasicResponseHandler().handleResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("saveFeedbackMaster_doInBackground_" + e.getMessage());
            }
            return value;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Constant.showLog(result);
                String str = new JSONObject(result).getString("SaveFeedBackResult");
                str = str.replace("\"", "");
                Constant.showLog(str);
                pd.dismiss();
                writeLog("saveFeedbackMaster_result_" + str + "_" + result);
                String[] retAutoBranchId = str.split("\\-");
                pd.dismiss();
                if (retAutoBranchId.length > 1) {
                    if (!retAutoBranchId[0].equals("0") && !retAutoBranchId[0].equals("+2") && !retAutoBranchId[0].equals("+3")) {
                        showDia(3);
                    } else {
                        showDia(4);
                    }
                } else {
                    showDia(4);
                }

                pd.dismiss();
                writeLog("saveFeedbackMaster_result_" + str);
            } catch (Exception e) {
                writeLog("saveFeedbackMaster_onPostExecute_" + e.getMessage());
                e.printStackTrace();
                pd.dismiss();
                toast.setText("Something Went Wrong");
                toast.show();
                showDia(4);
            }
        }
    }

    private void showDia(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
        builder.setCancelable(false);
        if (i == 1) {
            builder.setMessage("Do You Want To Save Feedback?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveData();
                    // showDia(3);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 2) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 0) {
            builder.setMessage("Do You Want To Logout App?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putBoolean(getResources().getString(R.string.pref_logged), false);
                    editor.apply();
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (i == 3) {
            builder.setMessage("Data Saved Successfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.dismiss();
                    //SurveyScheduleActivity.leadSurveyState = 1;
                    fed_map.clear();
                    finish();
                    // Intent intent = new Intent(FeedbackActivity.this,SurveyScheduleActivity.class);
                    // intent.putExtra("leadid_state",leadid);
                    //  startActivity(intent);
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });

        } else if (i == 4) {
            builder.setMessage("Error While Loading Data");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (ConnectivityTest.getNetStat(getApplicationContext())) {
                        saveData();
                    } else {
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (i == 5) {
            builder.setMessage("Error While Loading Data");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private class getQuestBank extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(FeedbackActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String value = "";
            HttpPost request = new HttpPost(Constant.ipaddress + "/GetFeedBackQuestion");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            try {
                JSONStringer vehicle = new JSONStringer().object().key("rData").object().key("details").value(url[0]).endObject().endObject();
                StringEntity entity = new StringEntity(vehicle.toString());
                request.setEntity(entity);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                Constant.showLog("Saving : " + response.getStatusLine().getStatusCode());
                value = new BasicResponseHandler().handleResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("getQuestBank_" + e.getMessage());
            }
            return value;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Constant.showLog(result);
                String str = new JSONObject(result).getString("GetFeedBackQuestionResult");
                str = str.replace("\\","");
                JSONArray jsonArray =  new JSONArray(str);
                Constant.showLog(str+"-"+jsonArray.length());
                if(jsonArray.length()>=1) {
                    db.deleteTabel(DBHandler.Table_QuestBank);
                    int ret = new ParseJSON(str, getApplicationContext()).parseQuestBank();
                    if(ret == 1) {
                        getFeedData();
                        setQues();
                    }
                }else {
                    toast.setText("Data Not Available");
                    toast.show();
                }
                pd.dismiss();
                writeLog("getQuestBank_result_" + str);
            } catch (Exception e) {
                writeLog("getQuestBank_onPostExecute_" + e.getMessage());
                e.printStackTrace();
                pd.dismiss();
                showDia(5);
            }
        }
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "FeedbackActivity_" + _data);
    }
}

package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.view.Gravity;

import com.lnbinfotech.lnb_tickets.adapter.ReleaseNoteExpandableListAdapter;
import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.ReleaseNoteClass;
import com.lnbinfotech.lnb_tickets.post.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReleaseNoteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private ExpandableListView exListView;
    private DBHandler db;
    private List<Integer> parentIdList;
    private HashMap<Integer, List<ReleaseNoteClass>> parentIdchildListMap;
    private HashMap<Integer, ReleaseNoteClass> parentMap;
    private HashMap<Integer, List<ReleaseNoteClass>> childMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_note_detail);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (ConnectivityTest.getNetStat(getApplicationContext())) {
            loadData();
        } else {
            setData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(ReleaseNoteDetailActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ReleaseNoteDetailActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(ReleaseNoteDetailActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        exListView = (ExpandableListView) findViewById(R.id.expandableListView);
        db = new DBHandler(getApplicationContext());
        parentIdList = new ArrayList<>();
        parentIdchildListMap = new HashMap<>();
        parentMap = new HashMap<>();
        childMap = new HashMap<>();
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME, MODE_PRIVATE);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseNoteDetailActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ReleaseNoteDetailActivity.this).doFinish();
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

    private void loadData() {
        int auto = db.getSVDMax();
        String url = Constant.ipaddress + "/GetReleaseNote?auto=" + auto;
        Constant.showLog(url);
        new ReleaseNote().execute(url);
    }

    private class ReleaseNote extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ReleaseNoteDetailActivity.this);
            pd.setMessage("Please Wait...");
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
            if (s != null) {
                try {
                    s = s.replace("\\", "");
                    s = s.replace("''", "");
                    s = s.substring(1, s.length() - 1);
                    JSONArray jsonArray = new JSONArray(s);
                    List<ReleaseNoteClass> _noteList = new ArrayList<>();
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ReleaseNoteClass note = new ReleaseNoteClass();
                            note.setAuto(jsonArray.getJSONObject(i).getInt("Auto"));
                            note.setVersionNo(jsonArray.getJSONObject(i).getString("VersionNo"));
                            note.setDesc(jsonArray.getJSONObject(i).getString("Descriptions"));
                            note.setCrby(jsonArray.getJSONObject(i).getInt("CrBy"));
                            note.setCrDate(jsonArray.getJSONObject(i).getString("CrDate"));
                            note.setCrTime(jsonArray.getJSONObject(i).getString("CrTime"));
                            note.setModby(jsonArray.getJSONObject(i).getInt("ModBy"));
                            note.setModDate(jsonArray.getJSONObject(i).getString("ModDate"));
                            note.setModTime(jsonArray.getJSONObject(i).getString("ModTime"));
                            _noteList.add(note);
                        }
                        db.addReleaseNote(_noteList);
                        setData();
                    } else {
                        setData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    writeLog("ReleaseNote_" + e.getMessage());
                }
            } else {
                setData();
            }
        }
    }

    private void setData() {
        parentIdList.clear();
        parentMap.clear();
        parentIdchildListMap.clear();
        childMap.clear();

        Cursor res = db.getReleaseNotes();
        if (res.moveToFirst()) {
            do {
                List<ReleaseNoteClass> childList = new ArrayList<>();
                List<ReleaseNoteClass> parentList = new ArrayList<>();

                ReleaseNoteClass note = new ReleaseNoteClass();
                note.setAuto(res.getInt(res.getColumnIndex(DBHandler.SVD_Auto)));
                note.setVersionNo(res.getString(res.getColumnIndex(DBHandler.SVD_Version)));
                note.setDesc(res.getString(res.getColumnIndex(DBHandler.SVD_Desc)));
                note.setCrby(res.getInt(res.getColumnIndex(DBHandler.SVD_CrBy)));
                note.setCrDate(res.getString(res.getColumnIndex(DBHandler.SVD_CrDate)));
                note.setCrTime(res.getString(res.getColumnIndex(DBHandler.SVD_CrTime)));
                note.setModby(res.getInt(res.getColumnIndex(DBHandler.SVD_ModBy)));
                note.setModDate(res.getString(res.getColumnIndex(DBHandler.SVD_ModDate)));
                note.setModTime(res.getString(res.getColumnIndex(DBHandler.SVD_ModTime)));

                parentList.add(note);
                childList.add(note);

                parentIdList.add(res.getInt(res.getColumnIndex(DBHandler.SVD_Auto)));
                parentIdchildListMap.put(res.getInt(res.getColumnIndex(DBHandler.SVD_Auto)), childList);
                parentMap.put(res.getInt(res.getColumnIndex(DBHandler.SVD_Auto)), note);

            } while (res.moveToNext());
        }
        res.close();

        if (parentIdList.size() != 0 && parentIdchildListMap.size() != 0 && parentMap.size() != 0) {
            exListView.setAdapter(new ReleaseNoteExpandableListAdapter(getApplicationContext(), parentIdList, parentIdchildListMap, parentMap));
        }
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "RelaseNoteDetailActivity_" + _data);
    }
}

package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class AddNewTicketActivity extends AppCompatActivity implements View.OnClickListener{

    EditText ed_subject, ed_description, ed_search;
    LinearLayout lay_attachment, lay_branch_search;
    ImageView img;
    Button btn_generate_ticket, btn_attachment;
    Toast toast;
    String imagePath = "";
    Constant constant;
    AtomicInteger atomicInteger;
    Spinner sp_branch;
    //List<String> branchList;
    DBHandler db;
    public static String selBranch = null;
    AdView mAdView;
    int isDataImageSaved = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        init();

        lay_attachment.setOnClickListener(this);
        lay_branch_search.setOnClickListener(this);
        btn_generate_ticket.setOnClickListener(this);
        btn_attachment.setOnClickListener(this);
        ed_search.setOnClickListener(this);

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest;
        if(Constant.liveTestFlag==1) {
            adRequest = new AdRequest.Builder().build();
        }else {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("0558B791C50AB34B5650C3C48C9BD15E")
                    .build();
        }

        mAdView.loadAd(adRequest);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null){
            mAdView.resume();
        }
        if(selBranch!=null){
            ed_search.setText(selBranch);
        }
        constant = new Constant(AddNewTicketActivity.this);
    }

    @Override
    protected void onPause() {
        if(mAdView!=null){
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mAdView!=null){
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lay_attachment:
                takeimage();
                break;
            case R.id.ed_search:
                startActivity(new Intent(getApplicationContext(),ClientSearchActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.btn_attachment:
                takeimage();
                break;
            case R.id.btn_generate_ticket:
                validation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(AddNewTicketActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                new Constant(AddNewTicketActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            try {
                img.setVisibility(View.VISIBLE);
                String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + "temp.jpg");
                img.setImageBitmap(scaleBitmap(_imagePath));
                long datetime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.ENGLISH);
                Date resultdate = new Date(datetime);
                String clientID = FirstActivity.pref.getString(getString(R.string.pref_clientID), "");
                imagePath = clientID + "_" + sdf.format(resultdate) + ".jpg";

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name);
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }

                OutputStream outFile;
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name,imagePath);
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 15, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (Exception e) {
                    writeLog("AddNewTicketActivity_onActivityResult_outFile_"+e.getMessage());
                    e.printStackTrace();
                }
            }catch (Exception e){
                writeLog("AddNewTicketActivity_onActivityResult_"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void init(){
        selBranch = null;
        btn_generate_ticket = (Button) findViewById(R.id.btn_generate_ticket);
        btn_attachment = (Button) findViewById(R.id.btn_attachment);
        ed_search = (EditText) findViewById(R.id.ed_search);
        ed_subject = (EditText) findViewById(R.id.ed_subject);
        ed_description = (EditText) findViewById(R.id.ed_description);
        lay_attachment = (LinearLayout) findViewById(R.id.lay_attachment);
        lay_branch_search = (LinearLayout) findViewById(R.id.lay_branch_search);
        img = (ImageView) findViewById(R.id.img);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(AddNewTicketActivity.this);
        db = new DBHandler(getApplicationContext());
        sp_branch = (Spinner) findViewById(R.id.sp_branch);
        /*branchList = db.getDistinctBranch();
        if(branchList!=null){
            if(branchList.size()!=0){
                sp_branch.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,branchList));
            }
        }else{
            branchList.add(FirstActivity.pref.getString(getString(R.string.pref_ClientName),""));
        }*/
    }

    void takeimage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = Constant.checkFolder(Constant.folder_name);
        f = new File(f.getAbsolutePath(),"temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent,1);
    }

    void validation() {
        String _branch = ed_search.getText().toString();
        String _subject = ed_subject.getText().toString();
        String _description = ed_description.getText().toString();

        boolean check = true;
        View view = null;
        if (_branch.equals("") || _branch.length() == 0) {
            toast.setText("Please Select Branch");
            view = ed_search;
            check = false;
        }else if (_subject.equals("") || _subject.length() == 0) {
            toast.setText("Please Enter Subject");
            view = ed_subject;
            check = false;
        }else if (_description.equals("") || _description.length() == 0) {
            view = ed_description;
            toast.setText("Please Enter Description");
            check = false;
        }
        if(check){
            generateTicket();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    void generateTicket(){
        try {
            int clientAuto = 0, ok = 0;
            String type, clientName = null, mobno = null, _subject = null, _description = null,
                    branch, _imagePath = imagePath, _status = "Open", imgFolder;
            type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
            if (type.equals("C")) {
                clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
                clientName = FirstActivity.pref.getString(getString(R.string.pref_ClientName), "0");
                mobno = FirstActivity.pref.getString(getString(R.string.pref_mobno), "");
                _subject = ed_subject.getText().toString();
                _description = ed_description.getText().toString();
                //branch = branchList.get(sp_branch.getSelectedItemPosition());
                branch = ed_search.getText().toString();
                ok = 1;
            } else {
                //branch = branchList.get(sp_branch.getSelectedItemPosition());
                branch = ed_search.getText().toString();
                String _data = db.getAutoFolder(branch);
                if(!_data.equals("0")) {
                    String data[] = _data.split("\\^");
                    clientAuto = Integer.parseInt(data[0]);
                    imgFolder = data[1];
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putString(getString(R.string.pref_FTPImgFolder),imgFolder);
                    editor.apply();
                    clientName = FirstActivity.pref.getString(getString(R.string.pref_ClientName), "0");
                    mobno = FirstActivity.pref.getString(getString(R.string.pref_mobno), "");
                    _subject = ed_subject.getText().toString();
                    _description = ed_description.getText().toString();
                    ok = 1;
                }
            }
            if(ok == 1) {
                clientName = URLEncoder.encode(clientName, "UTF-8");
                _subject = URLEncoder.encode(_subject, "UTF-8");
                _description = URLEncoder.encode(_description, "UTF-8");
                _imagePath = URLEncoder.encode(_imagePath, "UTF-8");
                _status = URLEncoder.encode(_status, "UTF-8");
                mobno = URLEncoder.encode(mobno, "UTF-8");
                branch = URLEncoder.encode(branch, "UTF-8");
                Constant.showLog(clientAuto + "-" + _subject + "-" + _description + "-" + _imagePath + "-" + branch);

                String url = Constant.ipaddress + "/AddTicketMaster?clientAuto=" + clientAuto +
                        "&subject=" + _subject + "&desc=" + _description + "&imagePath=" + _imagePath +
                        "&status=" + _status + "&CrBy=" + clientName + "&type=" + type + "&mobno=" + mobno + "&branch=" + branch;
                writeLog("AddNewTicketActivity_generateTicket_" + url);
                Constant.showLog(url);
                if (ConnectivityTest.getNetStat(getApplicationContext())) {
                    if (!imagePath.equals("") && imagePath != null) {
                        atomicInteger = new AtomicInteger(2);
                    } else {
                        atomicInteger = new AtomicInteger(1);
                    }
                    if(isDataImageSaved == -1 || isDataImageSaved == 2) {
                        saveTicket(url);
                    }
                    if(isDataImageSaved == -1 || isDataImageSaved == 1)  {
                        if (!imagePath.equals("") && imagePath != null) {
                            new UploadImage().execute();
                        }else{
                            isDataImageSaved = 2;
                        }
                    }
                } else {
                    writeLog("AddNewTicketActivity_generateTicket_Network_Connection_Error");
                    toast.setText("Network Connection Error");
                    toast.show();
                }
            }else{
                writeLog("AddNewTicketActivity_generateTicket_auto_0");
                toast.setText("Unable To Save Ticket");
                toast.show();
            }
        }catch (Exception e){
            writeLog("AddNewTicketActivity_generateTicket_"+e.getMessage());
            e.printStackTrace();
        }
    }

    void saveTicket(String url){
        constant.showPD();
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.replace("\"", "");
                        Constant.showLog(result);
                        if(atomicInteger.decrementAndGet()==0) {
                            constant.showPD();
                            if (!result.equals("0") && !result.equals("")) {
                                writeLog("AddNewTicketActivity_saveTicket_Success");
                                isDataImageSaved = 1;
                                showDia(1);
                            } else {
                                writeLog("AddNewTicketActivity_saveTicket_UnSuccess");
                                showDia(2);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("AddNewTicketActivity_saveTicket_volley_"+ error.getMessage());
                        error.printStackTrace();
                        if(atomicInteger.decrementAndGet()==0){
                            constant.showPD();
                        }
                        showDia(2);
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private class UploadImage extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            try {
                String ftpaddress = FirstActivity.pref.getString(getString(R.string.pref_FTPLocation), "");
                if(!ftpaddress.equals("")) {
                    String ftpuser = FirstActivity.pref.getString(getString(R.string.pref_FTPUser), "");
                    String ftppass = FirstActivity.pref.getString(getString(R.string.pref_FTPPass), "");
                    String ftpfolder = FirstActivity.pref.getString(getString(R.string.pref_FTPImgFolder), "");
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name, imagePath);
                    FTPClient client = new FTPClient();
                    client.connect(ftpaddress, 21);
                    client.login(ftpuser, ftppass);
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    client.enterLocalPassiveMode();
                    FileInputStream ifile = new FileInputStream(f);
                    client.cwd(ftpfolder);
                    client.storeFile(imagePath, ifile);
                    client.disconnect();
                    result = "1";
                    writeLog("AddNewTicketActivity_UploadImage_Success");
                }else{
                    writeLog("AddNewTicketActivity_UploadImage_"+ftpaddress);
                    result = "2";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "0";
                writeLog("AddNewTicketActivity_UploadImage_"+e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch (result) {
                case "1":
                    if (atomicInteger.decrementAndGet() == 0) {
                        constant.showPD();
                        isDataImageSaved  = 2;
                        showDia(1);
                    }
                    break;
                case "0":
                    if (atomicInteger.decrementAndGet() == 0) {
                        constant.showPD();
                        showDia(3);
                    }
                    break;
                case "2":
                    if (atomicInteger.decrementAndGet() == 0) {
                        constant.showPD();
                        showDia(4);
                    }
                    break;
            }
        }
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String s = cursor.getString(idx);
            cursor.close();
            return s;
        }
    }

    public Bitmap scaleBitmap(String imagePath) {
        Bitmap resizedBitmap = null;
        try {
            int inWidth, inHeight;
            InputStream in;
            in = new FileInputStream(imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            //in = null;
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            in = new FileInputStream(imagePath);
            options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(inWidth / 300, inHeight / 300);
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);
            resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewTicketActivity.this);
        if(a==0) {
            builder.setMessage("Do You Want To Go Back?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(AddNewTicketActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(a==1) {
            builder.setMessage("Ticket Generated Successfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.isUpdate = 1;
                    dialog.dismiss();
                    new Constant(AddNewTicketActivity.this).doFinish();
                }
            });
        }else if(a==2) {
            builder.setMessage("Error While Generating Ticket");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    generateTicket();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(a==3) {
            builder.setMessage("Error While Generating Ticket");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    atomicInteger = new AtomicInteger(1);
                    constant.showPD();
                    new UploadImage().execute();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(a==4) {
            builder.setTitle("Invalid FTP Server");
            builder.setMessage("Please Contact Support Team");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new Constant(AddNewTicketActivity.this).doFinish();
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

}

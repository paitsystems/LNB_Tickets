package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
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
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.lnbinfotech.lnb_tickets.post.Post;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class AddNewTicketActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ed_subject, ed_description, ed_search;
    private LinearLayout lay_attachment, lay_branch_search, lay_pointtype;
    private ImageView img;
    private Button btn_generate_ticket, btn_attachment;
    private Toast toast;
    private String imagePath = "", empType = "", isHWapplicable = "";
    private Constant constant;
    private AtomicInteger atomicInteger;
    private Spinner sp_branch;
    //List<String> branchList;
    private DBHandler db;
    public static String selBranch = null;
    private AdView mAdView;
    private int isDataImageSaved = -1, REQUEST_IMAGE_PICK_UP = 2, REQUEST_IMAGE_TAKE = 3;
    private RadioButton rdo_sw, rdo_hw, rdo_it;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private Spinner sp_status;
    private List<String> statusList;

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

        rdo_sw.setOnClickListener(this);
        rdo_hw.setOnClickListener(this);
        rdo_it.setOnClickListener(this);

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest;
        if(Constant.liveTestFlag==1) {
            adRequest = new AdRequest.Builder().build();
        }else {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Constant.adMobID)
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
                showDia(5);
                break;
            case R.id.ed_search:
                startActivity(new Intent(getApplicationContext(),ClientSearchActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.btn_attachment:
                showDia(5);
                break;
            case R.id.btn_generate_ticket:
                validation();
                break;
            case R.id.rdo_sw:
                rdo_sw.setChecked(true);
                rdo_hw.setChecked(false);
                rdo_it.setChecked(false);
                break;
            case R.id.rdo_hw:
                rdo_sw.setChecked(false);
                rdo_hw.setChecked(true);
                rdo_it.setChecked(false);
                break;
            case R.id.rdo_it:
                rdo_sw.setChecked(false);
                rdo_hw.setChecked(false);
                rdo_it.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        wl.release();
        AddNewTicketActivity.selBranch="";
        new Constant(AddNewTicketActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                wl.release();
                new Constant(AddNewTicketActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_TAKE && resultCode==RESULT_OK){
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
        }else if (requestCode == REQUEST_IMAGE_PICK_UP && resultCode == RESULT_OK && data!=null) {
            try {
                Uri selectedImage = data.getData();
                String[] filepathcoloum = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filepathcoloum, null, null, null);
                if(cursor!=null) {
                    if (cursor.moveToFirst()) {
                        int columnindex = cursor.getColumnIndex(filepathcoloum[0]);
                        String imgDecodedString = cursor.getString(columnindex);
                        long datetime = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.ENGLISH);
                        Date resultdate = new Date(datetime);
                        String clientID = FirstActivity.pref.getString(getString(R.string.pref_clientID), "");
                        String fname = clientID + "_" + sdf.format(resultdate);
                        File sourcefile = new File(imgDecodedString);
                        File destinationfile = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + "/" + fname + ".jpg");
                        copyImage(sourcefile, destinationfile);
                        cursor.close();
                    }
                }else{
                    toast.setText("Please Try Again");
                    toast.show();
                }
            }catch (Exception e){
                e.printStackTrace();
                toast.setText("Something Went Wrong");
                toast.show();
            }
        }
    }

    private void init(){
        selBranch = null;
        btn_generate_ticket = (Button) findViewById(R.id.btn_generate_ticket);
        btn_attachment = (Button) findViewById(R.id.btn_attachment);
        ed_search = (EditText) findViewById(R.id.ed_search);
        ed_subject = (EditText) findViewById(R.id.ed_subject);
        ed_description = (EditText) findViewById(R.id.ed_description);
        lay_attachment = (LinearLayout) findViewById(R.id.lay_attachment);
        lay_branch_search = (LinearLayout) findViewById(R.id.lay_branch_search);
        lay_pointtype = (LinearLayout) findViewById(R.id.lay_pointtype);
        img = (ImageView) findViewById(R.id.img);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(AddNewTicketActivity.this);
        db = new DBHandler(getApplicationContext());
        sp_branch = (Spinner) findViewById(R.id.sp_branch);
        sp_status = (Spinner) findViewById(R.id.sp_status);
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);

        statusList = new ArrayList<>();
        statusList.add("Open");statusList.add("Closed");statusList.add("Pending");statusList.add("Scheduled");
        statusList.add("Hold");statusList.add("Cancel");statusList.add("ReOpen");statusList.add("ClientClosed");
        sp_status.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,statusList));

        pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"Log");
        wl.acquire();

        rdo_sw = (RadioButton) findViewById(R.id.rdo_sw);
        rdo_hw = (RadioButton) findViewById(R.id.rdo_hw);
        rdo_it = (RadioButton) findViewById(R.id.rdo_it);

        empType = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        isHWapplicable = FirstActivity.pref.getString(getString(R.string.pref_isHWapplicable),"");

        if(empType.equals("C")){
            rdo_it.setVisibility(View.GONE);
            if(isHWapplicable.equals("S")){
                rdo_sw.setVisibility(View.VISIBLE);
                rdo_sw.setChecked(true);
                rdo_hw.setVisibility(View.GONE);
            }else if(isHWapplicable.equals("H")){
                rdo_sw.setVisibility(View.GONE);
                rdo_hw.setVisibility(View.VISIBLE);
                rdo_hw.setChecked(true);
            }else if(isHWapplicable.equals("SH")){
                rdo_sw.setVisibility(View.VISIBLE);
                rdo_hw.setVisibility(View.VISIBLE);
            }
        }
        /*branchList = db.getDistinctBranch();
        if(branchList!=null){
            if(branchList.size()!=0){
                sp_branch.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,branchList));
            }
        }else{
            branchList.add(FirstActivity.pref.getString(getString(R.string.pref_ClientName),""));
        }*/
    }

    private void takeimage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = Constant.checkFolder(Constant.folder_name);
        f = new File(f.getAbsolutePath(),"temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent,REQUEST_IMAGE_TAKE);
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery,REQUEST_IMAGE_PICK_UP);
    }

    private void copyImage(File source, File destination){
        try {
            FileChannel sourcechannel, destinationchannel;
            sourcechannel = new FileInputStream(source).getChannel();
            destinationchannel = new FileOutputStream(destination).getChannel();
            if(sourcechannel!=null){
                destinationchannel.transferFrom(sourcechannel,0,sourcechannel.size());
            }
            if(sourcechannel!=null){
                sourcechannel.close();
            }
            destinationchannel.close();
            setImage(destination, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setImage(File f, int i){
        OutputStream outFile;
        try {
            img.setVisibility(View.VISIBLE);
            Bitmap bitmap;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
            Bitmap bmp = scaleBitmap(f.getAbsolutePath());
            img.setImageBitmap(bmp);
            File file;
            if(i == 0) {
                String OImgPath = Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name+"/";
                if (f.delete()) {
                    Log.d("log", "log");
                }
                long datetime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.ENGLISH);
                Date resultdate = new Date(datetime);
                String clientID = FirstActivity.pref.getString(getString(R.string.pref_clientID), "");
                String fname = clientID+"_" + sdf.format(resultdate);
                file = new File(OImgPath, "/" + fname + ".jpg");
                //imagePath = file.getAbsolutePath();
                imagePath = fname + ".jpg";
            }else{
                file = f;
                imagePath = f.getAbsolutePath();
                imagePath = f.getName();
            }
            try {
                outFile = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, outFile);
                outFile.flush();
                outFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validation() {
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

        if(empType.equals("C")) {
            if (!rdo_sw.isChecked() && !rdo_hw.isChecked()) {
                toast.setText("Please Select Point Type");
                check = false;
                view = rdo_sw;
            }
        }else{
            if (!rdo_sw.isChecked() && !rdo_hw.isChecked() && !rdo_it.isChecked()) {
                toast.setText("Please Select Point Type");
                check = false;
                view = rdo_sw;
            }
        }

        if(check){
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btn_generate_ticket.getWindowToken(),0);
            generateTicket();
        }else{
            view.requestFocus();
            toast.show();
        }
    }

    private void generateTicket(){
        try {
            int clientAuto = 0, ok = 0;
            String type, clientName = null, nickname = null, mobno = null, _subject = null, _description = null,
                    branch, _imagePath = imagePath, _status, imgFolder, pointtype = null;

            type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");

            _status = statusList.get(sp_status.getSelectedItemPosition());

            if(rdo_sw.isChecked()){
                pointtype = "S";
            }else if(rdo_hw.isChecked()){
                pointtype = "H";
            }else if(rdo_it.isChecked()){
                pointtype = "I";
            }

            if (type.equals("C")) {
                clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
                clientName = FirstActivity.pref.getString(getString(R.string.pref_ClientName), "0");
                nickname = FirstActivity.pref.getString(getString(R.string.pref_nickname), "NA");
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
                    nickname = FirstActivity.pref.getString(getString(R.string.pref_nickname), "NA");
                    mobno = FirstActivity.pref.getString(getString(R.string.pref_mobno), "");
                    _subject = ed_subject.getText().toString();
                    _description = ed_description.getText().toString();
                    ok = 1;
                }
            }
            if(ok == 1) {
                clientName = URLEncoder.encode(clientName, "UTF-8");
                nickname = URLEncoder.encode(nickname, "UTF-8");
                _subject = URLEncoder.encode(_subject, "UTF-8");
                _description = URLEncoder.encode(_description, "UTF-8");
                _imagePath = URLEncoder.encode(_imagePath, "UTF-8");
                _status = URLEncoder.encode(_status, "UTF-8");
                mobno = URLEncoder.encode(mobno, "UTF-8");
                branch = URLEncoder.encode(branch, "UTF-8");
                pointtype = URLEncoder.encode(pointtype, "UTF-8");

                Constant.showLog(clientAuto + "-" + _subject + "-" + _description + "-" + _imagePath + "-" + branch+"-"+pointtype+"-"+nickname);

                if(type.equals("C")) {
                    if (nickname.equals("NA")) {
                        nickname = clientName;
                    }
                }else{
                    nickname = clientName;
                }

                String url = Constant.ipaddress + "/AddTicketMaster?clientAuto=" + clientAuto +
                        "&subject=" + _subject + "&desc=" + _description + "&imagePath=" + _imagePath +
                        "&status=" + _status + "&CrBy=" + nickname + "&type=" + type + "&mobno=" + mobno +
                        "&branch=" + branch + "&ptype="+pointtype;

                writeLog("AddNewTicketActivity_generateTicket_" + url);
                Constant.showLog(url);

                if (ConnectivityTest.getNetStat(getApplicationContext())) {
                    /*if (!imagePath.equals("") && imagePath != null) {
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
                    }*/
                    new saveTicket().execute(url);
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

    private void saveTicket(String url){
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

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddNewTicketActivity.this);
            pd.setMessage("Uploading Image...");
            pd.setCancelable(false);
            pd.show();
        }

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
            pd.dismiss();
            switch (result) {
                case "1":
                    /*if (atomicInteger.decrementAndGet() == 0) {
                        constant.showPD();
                        isDataImageSaved  = 2;
                        showDia(1);
                    }*/
                    showDia(1);
                    break;
                case "0":
                    /*if (atomicInteger.decrementAndGet() == 0) {
                        constant.showPD();
                        showDia(3);
                    }*/
                    showDia(3);
                    break;
                case "2":
                    /*if (atomicInteger.decrementAndGet() == 0) {
                        constant.showPD();
                        showDia(4);
                    }*/
                    showDia(4);
                    break;
            }
        }
    }

    private class saveTicket extends AsyncTask<String,Void,String>{

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddNewTicketActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Post.POST(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            result = result.replace("\\", "");
            result = result.replace("''", "");
            result = result.replace("\"", "");
            Constant.showLog(result);
            if (!result.equals("0") && !result.equals("")) {
                writeLog("AddNewTicketActivity_saveTicket_Success");
                if (!imagePath.equals("") && imagePath != null) {
                    new UploadImage().execute();
                }else{
                    showDia(1);
                }
            } else {
                writeLog("AddNewTicketActivity_saveTicket_UnSuccess");
                showDia(2);
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

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewTicketActivity.this);
        builder.setCancelable(false);
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
                    //new Constant(AddNewTicketActivity.this).doFinish();
                    clearFields();
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
                    //constant.showPD();
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
        }else if(a==5) {
            builder.setMessage("Select Attachment From...");
            builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    openGallery();
                }
            });
            builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    takeimage();
                }
            });
            builder.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void clearFields(){
        ed_subject.setText(null);
        ed_subject.requestFocus();
        ed_description.setText(null);
        img.setImageResource(0);
        imagePath = "";
        img.setVisibility(View.GONE);
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

}

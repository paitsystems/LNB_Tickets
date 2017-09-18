package com.lnbinfotech.lnb_tickets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;
import com.lnbinfotech.lnb_tickets.services.DownloadImageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UpdateTicketActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_subject, tv_ticket_no, tv_date, tv_time;
    private EditText ed_description;
    private ImageView img;
    //private ListView listView;
    private Button btn_update_ticket, btn_reply;
    public static final String BROADCAST = "imageDownloadedBroadcast";
    private BroadcastReceiver receiver;
    private Intent startService = null;
    private String imageName = null, status;
    private Spinner sp_status;
    private List<String> statusList;
    public static int auto = 0;
    private TicketMasterClass ticketMasterClass;
    private Constant constant;
    private Toast toast;
    private AdView mAdView;
    private ProgressBar pb;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ticket);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        pb.setVisibility(View.GONE);
        btn_update_ticket.setOnClickListener(this);
        btn_reply.setOnClickListener(this);
        img.setOnClickListener(this);

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

        ticketMasterClass = (TicketMasterClass) getIntent().getSerializableExtra("data");

        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        constant = new Constant(UpdateTicketActivity.this);
        if(mAdView!=null){
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAdView!=null){
            mAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mAdView!=null){
            mAdView.destroy();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_ticket:
                updateStatus();
                break;
            case R.id.btn_reply:
                startActivity(new Intent(getApplicationContext(),ReplyResponseActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.img:
                if(!imageName.equals("") && imageName!=null) {
                    Intent intent = new Intent(getApplicationContext(), FullImageActivity.class);
                    intent.putExtra("imagename", imageName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void init() {
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        ed_description = (EditText) findViewById(R.id.ed_description);
        tv_ticket_no = (TextView) findViewById(R.id.tv_ticket_no);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        btn_update_ticket = (Button) findViewById(R.id.btn_update_ticket);
        btn_reply = (Button) findViewById(R.id.btn_reply);
        img = (ImageView) findViewById(R.id.img);
        //listView = (ListView) findViewById(R.id.listView);
        sp_status = (Spinner) findViewById(R.id.sp_status);
        pb = (ProgressBar) findViewById(R.id.pb);
        db = new DBHandler(getApplicationContext());

        statusList = new ArrayList<>();
        statusList.add("Open");statusList.add("Closed");statusList.add("Pending");statusList.add("Scheduled");
        statusList.add("Hold");statusList.add("Cancel");statusList.add("ReOpen");statusList.add("ClientClosed");
        sp_status.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,statusList));
        toast = Toast.makeText(getApplicationContext(),"File Not Found",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Constant.checkFolder(Constant.folder_name);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + imageName);
                pb.setVisibility(View.GONE);
                img.setImageResource(R.drawable.bg);
                if(f.length()!=0) {
                    String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + imageName);
                    img.setImageBitmap(scaleBitmap(_imagePath));
                }else{
                    writeLog("UpdateTicketActivity_onReceive_File_Not_Found_"+f.getAbsolutePath());
                    toast.show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        registerReceiver(receiver, intentFilter);

    }

    void setData(){
        auto = ticketMasterClass.auto;
        tv_subject.setText(ticketMasterClass.getSubject());
        tv_ticket_no.setText(ticketMasterClass.getTicketNo());
        tv_date.setText(ticketMasterClass.getCrDate());
        String _time = ticketMasterClass.getCrTime();
        String time[] = _time.split("\\:");
        if(time.length>1) {
            String s = time[0]+":"+time[1];
            tv_time.setText(s);
        }else{
            tv_time.setText(_time);
        }
        //tv_time.setText(ticketMasterClass.getCrTime());
        ed_description.setText(ticketMasterClass.getParticular());
        imageName = ticketMasterClass.getImagePAth();
        for (int i=0; i<statusList.size();i++){
            if(statusList.get(i).equals(ticketMasterClass.getStatus())){
                sp_status.setSelection(i);
                break;
            }
        }
        if(!imageName.equals("") && imageName!=null) {
            setImage();
        }
    }

    void updateStatus(){
        constant.showPD();
        try {
            int id = ticketMasterClass.getId();
            int clientAuto = ticketMasterClass.getClientAuto();
            String _finyr = ticketMasterClass.getFinyr();
            String _clientName = FirstActivity.pref.getString(getString(R.string.pref_ClientName), "0");
            status = statusList.get(sp_status.getSelectedItemPosition());
            String _ticketno = ticketMasterClass.getTicketNo();
            String clientName  = URLEncoder.encode(_clientName,"UTF-8");
            String finyr = URLEncoder.encode(_finyr,"UTF-8");
            String _status = URLEncoder.encode(status,"UTF-8");
            String ticketno = URLEncoder.encode(_ticketno,"UTF-8");
            String mobno = db.getMobile(clientAuto);
            //String mobno = FirstActivity.pref.getString(getString(R.string.pref_mobno), "");

            String url = Constant.ipaddress+"/updateTicketStatus?auto=" + auto + "&id="+id+"&clientAuto="+clientAuto+
                    "&finyr="+finyr+"&status="+_status+"&modBy="+clientName+"&ticketno="+ticketno+"&mobno="+mobno;
            Constant.showLog(url);

            db.updateTicketStatus(auto,id,clientAuto,_finyr,status,_clientName,ticketno);

            StringRequest request = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            Constant.showLog(result);
                            result = result.replace("\\", "");
                            result = result.replace("''", "");
                            result = result.replace("\"","");
                            if(!result.equals("0") && !result.equals("")){
                                showDia(1);
                            }else{
                                showDia(3);
                            }
                            Constant.showLog(result);
                            constant.showPD();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            writeLog("UpdateTicketActivity_updateStatus_volley_"+ error.getMessage());
                            error.printStackTrace();
                            constant.showPD();
                            showDia(3);
                        }
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);

        }catch (Exception e){
            writeLog("UpdateTicketActivity_updateStatus_"+ e.getMessage());
            e.printStackTrace();
            constant.showPD();
        }
    }

    void setImage(){
        File file = Constant.checkFolder(Constant.folder_name);
        File fileArray[] = file.listFiles();
        int isAvailable = 0;
        if(fileArray.length!=0){
            for(File f:fileArray){
                if(f.getName().equals(imageName)) {
                    if(f.length()!=0) {
                        String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + imageName);
                        img.setImageBitmap(scaleBitmap(_imagePath));
                        isAvailable = 1;
                    }
                    break;
                }
            }
        }
        if(isAvailable == 0){
            if (ConnectivityTest.getNetStat(getApplicationContext())) {

                String data = db.getFolder(ticketMasterClass.getClientAuto());
                if(!data.equals("0")){
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putString(getString(R.string.pref_FTPImgFolder),data);
                    editor.apply();
                }
                pb.setVisibility(View.VISIBLE);
                startService = new Intent(getApplicationContext(),DownloadImageService.class);
                startService.putExtra("imageName",imageName);
                writeLog("UpdateTicketActivity_setImage_DownloadImageService_Started");
                startService(startService);
                Constant.showLog("Service Started");
            } else {
                pb.setVisibility(View.GONE);
                writeLog("UpdateTicketActivity_setImage_Offline");
                img.setImageResource(R.drawable.bg);
                toast.setText("You Are Offline");
                toast.show();
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
        } catch (Exception e) {
            e.printStackTrace();
            pb.setVisibility(View.GONE);
            img.setImageResource(R.drawable.bg);
            toast.show();
        }
        return resizedBitmap;
    }

    void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTicketActivity.this);
        if (a == 0) {
            builder.setMessage("Do You Want To Go Back?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doFinish();
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
            builder.setMessage("Status Updated Successfully");
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.isUpdate = 1;
                    AllTicketListActivity.selStat = status;
                    doFinish();
                    dialog.dismiss();
                }
            });
        }else if (a == 3) {
            builder.setMessage("Error While Updating status");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateStatus();
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

    void doFinish(){
        unregisterReceiver(receiver);
        if(startService!=null) {
            stopService(startService);
        }
        new Constant(UpdateTicketActivity.this).doFinish();
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

}

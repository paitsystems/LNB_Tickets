package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.view.Gravity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.AssetClass;
import com.lnbinfotech.lnb_tickets.services.DownloadImageService;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddAssetInfo extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private EditText ed_location, ed_brandname, ed_processor, ed_ram, ed_harddisk,
            ed_swinstalled, ed_osinstalled, ed_licensesw, ed_reamrk,
            ed_printername, ed_printermodel, ed_printermodelno, ed_printertype,
            ed_routername, ed_routermodel, ed_routermodelno, ed_routertype,ed_other,
            ed_usernm,ed_contactno;
    private Button btn_save, btn_attachment;
    private LinearLayout lay_desktop,lay_printer,lay_router,lay_other,lay_btn,lay_img;
    private ImageView img, img_updated;
    private int REQUEST_IMAGE_PICK_UP = 2, REQUEST_IMAGE_TAKE = 3, id = 1, mode = 1;
    private String imagePath = "", deviceId="";
    private AssetClass asset;
    private RadioButton rdo_active, rdo_inactive;
    private ProgressBar pb;
    public static final String BROADCAST = "imageDownloadedBroadcast";
    private BroadcastReceiver receiver;
    private Uri uri;
    private Intent startService = null;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assetinfo);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        try {
            pb.setVisibility(View.GONE);
            id = getIntent().getExtras().getInt("id");
            mode = getIntent().getExtras().getInt("mode");
            asset = (AssetClass) getIntent().getExtras().getSerializable("asset");
        }catch (Exception e){
            e.printStackTrace();
            writeLog(e.getMessage());
        }

        switch (id){
            case R.id.lay_desktop:
                lay_desktop.setVisibility(View.VISIBLE);
                break;
            case R.id.lay_printer:
                lay_printer.setVisibility(View.VISIBLE);
                break;
            case R.id.lay_router:
                lay_router.setVisibility(View.VISIBLE);
                break;
            case R.id.lay_other:
                lay_other.setVisibility(View.VISIBLE);
                break;
        }

        if(mode==1){
            setFieldDisable();
            lay_btn.setVisibility(View.GONE);
            setImage();
            setData(asset);
        }else if(mode==2){
            setFieldEnable();
            lay_btn.setVisibility(View.VISIBLE);
            btn_save.setText("Update");
            setImage();
            setData(asset);
        }else{
            setFieldEnable();
        }

        btn_save.setOnClickListener(this);
        btn_attachment.setOnClickListener(this);
        rdo_active.setOnClickListener(this);
        rdo_inactive.setOnClickListener(this);
        img.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null){
            mAdView.resume();
        }
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
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveAssetInfo();
                break;
            case R.id.btn_attachment:
                showDia(5);
                break;
            case R.id.rdo_active:
                rdo_active.setChecked(true);
                rdo_inactive.setChecked(false);
                break;
            case R.id.rdo_inactive:
                rdo_active.setChecked(false);
                rdo_inactive.setChecked(true);
            case R.id.img:
                if(!imagePath.equals("") && imagePath!=null) {
                    Intent intent = new Intent(getApplicationContext(), FullImageActivity.class);
                    intent.putExtra("imagename", imagePath);
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
        //showDia(0);
        new Constant(AddAssetInfo.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(AddAssetInfo.this).doFinish();
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
                lay_img.setVisibility(View.GONE);
                String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + "temp.jpg");
                img.setImageBitmap(scaleBitmap(_imagePath));
                long datetime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.ENGLISH);
                Date resultdate = new Date(datetime);
                imagePath = AssetManagementActivity.machineType + "_" + sdf.format(resultdate) + ".jpg";

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
                    writeLog("AddAssetInfo_onActivityResult_outFile_"+e.getMessage());
                    e.printStackTrace();
                }
            }catch (Exception e){
                writeLog("AddAssetInfo_onActivityResult_"+e.getMessage());
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
                        String fname = AssetManagementActivity.machineType + "_" + sdf.format(resultdate);
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

    private void init() {
        constant = new Constant(AddAssetInfo.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ed_location = (EditText) findViewById(R.id.ed_location);
        ed_brandname = (EditText) findViewById(R.id.ed_brandname);
        ed_processor = (EditText) findViewById(R.id.ed_processor);
        ed_ram = (EditText) findViewById(R.id.ed_ram);
        ed_harddisk = (EditText) findViewById(R.id.ed_harddisk);
        ed_swinstalled = (EditText) findViewById(R.id.ed_swinstalled);
        ed_osinstalled = (EditText) findViewById(R.id.ed_osinstalled);
        ed_licensesw = (EditText) findViewById(R.id.ed_licensesw);
        ed_printername = (EditText) findViewById(R.id.ed_printername);
        ed_printermodel = (EditText) findViewById(R.id.ed_printermodel);
        ed_printermodelno = (EditText) findViewById(R.id.ed_printermodelno);
        ed_printertype = (EditText) findViewById(R.id.ed_printertype);
        ed_routername = (EditText) findViewById(R.id.ed_routername);
        ed_routermodel = (EditText) findViewById(R.id.ed_routermodel);
        ed_routermodelno = (EditText) findViewById(R.id.ed_routermodelno);
        ed_routertype = (EditText) findViewById(R.id.ed_routertype);
        ed_other = (EditText) findViewById(R.id.ed_other);
        ed_reamrk = (EditText) findViewById(R.id.ed_remark);
        ed_usernm = (EditText) findViewById(R.id.ed_usernm);
        ed_contactno = (EditText) findViewById(R.id.ed_contactno);
        btn_save  = (Button) findViewById(R.id.btn_save);
        btn_attachment  = (Button) findViewById(R.id.btn_attachment);
        lay_desktop = (LinearLayout) findViewById(R.id.lay_desktop);
        lay_printer = (LinearLayout) findViewById(R.id.lay_printer);
        lay_router = (LinearLayout) findViewById(R.id.lay_router);
        lay_other = (LinearLayout) findViewById(R.id.lay_other);
        lay_btn = (LinearLayout) findViewById(R.id.lay_btn);
        img = (ImageView) findViewById(R.id.img);
        rdo_active = (RadioButton) findViewById(R.id.rdo_active);
        rdo_inactive = (RadioButton) findViewById(R.id.rdo_inactive);
        lay_img = (LinearLayout) findViewById(R.id.lay_img);
        img_updated = (ImageView) findViewById(R.id.img_updated);
        pb = (ProgressBar) findViewById(R.id.pb);

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Constant.checkFolder(Constant.folder_name);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + imagePath);
                pb.setVisibility(View.GONE);
                img_updated.setImageResource(R.drawable.ic_broken_image_black_24dp);
                if(f.length()!=0) {
                    String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + imagePath);
                    img_updated.setImageBitmap(scaleBitmap(_imagePath));
                }else{
                    writeLog("UpdateTicketActivity_onReceive_File_Not_Found_"+f.getAbsolutePath());
                    toast.show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        registerReceiver(receiver, intentFilter);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddAssetInfo.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(AddAssetInfo.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 3) {
            builder.setTitle("Data Saved Successfully");
            builder.setMessage("Device Id - "+deviceId);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AssetManagementActivity.isUpdated = 1;
                    clearFields();
                }
            });
        }else if (a == 4) {
            builder.setMessage("Error While Saving Data");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    saveAssetInfo();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if(a==5) {
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
        }else if (a == 6) {
            builder.setTitle("Data Updated Successfully");
            builder.setMessage("Device Id - "+deviceId);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AssetManagementActivity.isUpdated = 1;
                }
            });
        }else if (a == 7) {
            builder.setMessage("Error While Uploading Image");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new UploadImage().execute();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clearFields();
                }
            });
        }
        builder.create().show();
    }

    private void saveAssetInfo() {
        String loation = ed_location.getText().toString();
        String brandname = ed_brandname.getText().toString();
        String processor = ed_processor.getText().toString();
        String ram = ed_ram.getText().toString();
        String harddisk = ed_harddisk.getText().toString();
        String osinstalled = ed_osinstalled.getText().toString();
        String swinstalled = ed_swinstalled.getText().toString();
        String licensesw = ed_licensesw.getText().toString();
        String printername = ed_printername.getText().toString();
        String printermodel = ed_printermodel.getText().toString();
        String printermodelno = ed_printermodelno.getText().toString();
        String printertype = ed_printertype.getText().toString();
        String routername = ed_routername.getText().toString();
        String routermodel = ed_routermodel.getText().toString();
        String routermodelno = ed_routermodelno.getText().toString();
        String routertype = ed_routertype.getText().toString();
        String other = ed_other.getText().toString();
        String userNm = ed_usernm.getText().toString();
        String contactNo = ed_contactno.getText().toString();
        String remark = ed_reamrk.getText().toString();
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
        int empId = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
        String active;
        if(rdo_active.isChecked()){
            active = "A";
        }else{
            active = "C";
        }

        if(mode==0) {
            String url = AssetManagementActivity.clientAuto + "|" + loation + "|" + AssetManagementActivity.machineType + "|" + brandname + "|" + processor
                    + "|" + ram + "|" + harddisk + "|" + osinstalled + "|" + swinstalled + "|" + licensesw + "|" + remark + "|" + imagePath
                    + "|" + printername + "|" + printermodel + "|" + printermodelno + "|" + printertype + "|" + routername + "|" +
                    routermodel + "|" + routermodelno + "|" + routertype + "|" + other + "|" + empId + "|" + userNm + "|" + contactNo + "|" + active;
            Constant.showLog(url);
            new saveAssetInfo().execute(url);
        }else if(mode==2){
            String url = asset.getAuto()+"|"+asset.getId()+"|"+AssetManagementActivity.clientAuto + "|" + loation + "|" + AssetManagementActivity.machineType + "|" + brandname + "|" + processor
                    + "|" + ram + "|" + harddisk + "|" + osinstalled + "|" + swinstalled + "|" + licensesw + "|" + remark + "|" + imagePath
                    + "|" + printername + "|" + printermodel + "|" + printermodelno + "|" + printertype + "|" + routername + "|" +
                    routermodel + "|" + routermodelno + "|" + routertype + "|" + other + "|" + empId + "|" + userNm + "|" + contactNo
                    + "|" + active;
            Constant.showLog(url);
            new updateAssetInfo().execute(url);
        }
    }

    private class saveAssetInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddAssetInfo.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String value = "";
            HttpPost request = new HttpPost(Constant.ipaddress + "/SaveAssetInfo");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            try {
                JSONStringer vehicle = new JSONStringer().object().key("rData").object().key("details").value(url[0]).endObject().endObject();

                StringEntity entity = new StringEntity(vehicle.toString());
                request.setEntity(entity);

                // Send request to WCF service
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                Constant.showLog("Saving : " + response.getStatusLine().getStatusCode());
                value = new BasicResponseHandler().handleResponse(response);
                //return Post.POST(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("saveAssetInfo_result_" + e.getMessage());
            }
            return value;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Constant.showLog(result);
                //String str = new JSONObject(result).getString("SaveCustOrderMasterResult");
                String str = new JSONObject(result).getString("SaveAssetInfoResult");
                str = str.replace("\"", "");
                Constant.showLog(str);
                pd.dismiss();
                writeLog("saveAssetInfo_result_" + str + "_" + result);
                String[] retAutoBranchId = str.split("\\-");
                if (retAutoBranchId.length > 1) {
                    if (!retAutoBranchId[0].equals("0") && !retAutoBranchId[0].equals("+2") && !retAutoBranchId[0].equals("+3")) {
                        deviceId = retAutoBranchId[0];
                        if (!imagePath.equals("") && imagePath != null) {
                            new UploadImage().execute();
                        }else{
                            showDia(3);
                        }
                    } else {
                        showDia(4);
                    }
                } else {
                    showDia(4);
                }
            } catch (Exception e) {
                writeLog("saveAssetInfo_" + e.getMessage());
                e.printStackTrace();
                showDia(4);
                pd.dismiss();
            }
        }
    }

    private class updateAssetInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddAssetInfo.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String value = "";
            HttpPost request = new HttpPost(Constant.ipaddress + "/UpdateAssetInfo");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            try {
                JSONStringer vehicle = new JSONStringer().object().key("rData").object().key("details").value(url[0]).endObject().endObject();

                StringEntity entity = new StringEntity(vehicle.toString());
                request.setEntity(entity);

                // Send request to WCF service
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                Constant.showLog("Saving : " + response.getStatusLine().getStatusCode());
                value = new BasicResponseHandler().handleResponse(response);
                //return Post.POST(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog("updateAssetInfo_result_" + e.getMessage());
            }
            return value;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Constant.showLog(result);
                //String str = new JSONObject(result).getString("SaveCustOrderMasterResult");
                String str = new JSONObject(result).getString("UpdateAssetInfoResult");
                str = str.replace("\"", "");
                Constant.showLog(str);
                pd.dismiss();
                writeLog("updateAssetInfo_result_" + str + "_" + result);
                String[] retAutoBranchId = str.split("\\-");
                if (retAutoBranchId.length > 1) {
                    if (!retAutoBranchId[0].equals("0") && !retAutoBranchId[0].equals("+2") && !retAutoBranchId[0].equals("+3")) {
                        deviceId = retAutoBranchId[0];
                        if(!imagePath.equals("") && imagePath != null && !imagePath.equals(asset.getImageName())) {
                            new UploadImage().execute();
                        }else{
                            showDia(6);
                        }
                    } else {
                        showDia(4);
                    }
                } else {
                    showDia(4);
                }
            } catch (Exception e) {
                writeLog("updateAssetInfo_" + e.getMessage());
                e.printStackTrace();
                showDia(4);
                pd.dismiss();
            }
        }
    }

    private class UploadImage extends AsyncTask<Void,Void,String>{

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddAssetInfo.this);
            pd.setMessage("Uploading Image...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            try {
                String _data = new DBHandler(getApplicationContext()).getAutoFolder(AddNewTicketActivity.selBranch);
                if (!_data.equals("0")) {
                    String data[] = _data.split("\\^");
                    String imgFolder = data[1];
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putString(getString(R.string.pref_FTPImgFolder),imgFolder);
                    editor.apply();
                }
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
                    writeLog("AddAssetInfo_UploadImage_Success");
                }else{
                    writeLog("AddAssetInfo_UploadImage_"+ftpaddress);
                    result = "2";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "0";
                writeLog("AddAssetInfo_UploadImage_"+e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            switch (result) {
                case "1":
                    showDia(3);
                    break;
                case "0":
                    showDia(7);
                    break;
                case "2":
                    showDia(7);
                    break;
            }
        }
    }

    private void setImage(){
        img.setVisibility(View.GONE);
        lay_img.setVisibility(View.VISIBLE);
        imagePath = asset.getImageName();
        File file = Constant.checkFolder(Constant.folder_name);
        File fileArray[] = file.listFiles();
        int isAvailable = 0;
        if(fileArray.length!=0){
            for(File f:fileArray){
                if(f.getName().equals(imagePath)) {
                    if(f.length()!=0) {
                        String _imagePath = getRealPathFromURI(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + File.separator + imagePath);
                        img_updated.setImageBitmap(scaleBitmap(_imagePath));
                        File fi1 = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name + "/" + imagePath);
                        uri = Uri.fromFile(fi1);
                        isAvailable = 1;
                    }
                    break;
                }
            }
        }
        if(isAvailable == 0){
            if (ConnectivityTest.getNetStat(getApplicationContext())) {
                String data = new DBHandler(getApplicationContext()).getFolder(AssetManagementActivity.clientAuto);
                if(!data.equals("0")){
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putString(getString(R.string.pref_FTPImgFolder),data);
                    editor.apply();
                }
                pb.setVisibility(View.VISIBLE);
                startService = new Intent(getApplicationContext(),DownloadImageService.class);
                startService.putExtra("imageName",imagePath);
                writeLog("AddAssetInfo_setImage_DownloadImageService_Started");
                startService(startService);
                Constant.showLog("Service Started");
            } else {
                pb.setVisibility(View.GONE);
                writeLog("AddAssetInfo_setImage_Offline");
                img_updated.setImageResource(R.drawable.ic_broken_image_black_24dp);
                toast.setText("You Are Offline");
                toast.show();
            }
        }

    }

    private void clearFields(){
        ed_location.setText(null);
        ed_brandname.setText(null);
        ed_processor.setText(null);
        ed_ram.setText(null);
        ed_harddisk.setText(null);
        ed_swinstalled.setText(null);
        ed_osinstalled.setText(null);
        ed_licensesw.setText(null);
        ed_reamrk.setText(null);
        ed_printername.setText(null);
        ed_printermodel.setText(null);
        ed_printermodelno.setText(null);
        ed_printertype.setText(null);
        ed_routername.setText(null);
        ed_routermodel.setText(null);
        ed_routermodelno.setText(null);
        ed_routertype.setText(null);
        ed_other.setText(null);
        img.setImageResource(0);
        imagePath = "";
        deviceId = "";
        img.setVisibility(View.GONE);
        ed_location.requestFocus();
    }

    private void setFieldDisable(){
        //ed_location.setEnabled(false);
        ed_location.setClickable(false);
        ed_location.setFocusable(false);

        //ed_brandname.setEnabled(false);
        ed_brandname.setClickable(false);
        ed_brandname.setFocusable(false);

        //ed_processor.setEnabled(false);
        ed_processor.setClickable(false);
        ed_processor.setFocusable(false);

        //ed_ram.setEnabled(false);
        ed_ram.setClickable(false);
        ed_ram.setFocusable(false);

        //ed_harddisk.setEnabled(false);
        ed_harddisk.setClickable(false);
        ed_harddisk.setFocusable(false);

        //ed_swinstalled.setEnabled(false);
        ed_swinstalled.setClickable(false);
        ed_swinstalled.setFocusable(false);

        //ed_osinstalled.setEnabled(false);
        ed_osinstalled.setClickable(false);
        ed_osinstalled.setFocusable(false);

        //ed_licensesw.setEnabled(false);
        ed_licensesw.setClickable(false);
        ed_licensesw.setFocusable(false);

        //ed_reamrk.setEnabled(false);
        ed_reamrk.setClickable(false);
        ed_reamrk.setFocusable(false);

        //ed_printername.setEnabled(false);
        ed_printername.setClickable(false);
        ed_printername.setFocusable(false);

        //ed_printermodel.setEnabled(false);
        ed_printermodel.setClickable(false);
        ed_printermodel.setFocusable(false);

        //ed_printermodelno.setEnabled(false);
        ed_printermodelno.setClickable(false);
        ed_printermodelno.setFocusable(false);


        //ed_printertype.setEnabled(false);
        ed_printertype.setClickable(false);
        ed_printertype.setFocusable(false);

        //ed_routername.setEnabled(false);
        ed_routername.setClickable(false);
        ed_routername.setFocusable(false);

        //ed_routermodel.setEnabled(false);
        ed_routermodel.setClickable(false);
        ed_routermodel.setFocusable(false);

        ///ed_routermodelno.setEnabled(false);
        ed_routermodelno.setClickable(false);
        ed_routermodelno.setFocusable(false);

        //ed_routertype.setEnabled(false);
        ed_routertype.setClickable(false);
        ed_routertype.setFocusable(false);

        //ed_other.setEnabled(false);
        ed_other.setClickable(false);
        ed_other.setFocusable(false);

        //ed_usernm.setEnabled(false);
        ed_usernm.setClickable(false);
        ed_usernm.setFocusable(false);

        //ed_contactno.setEnabled(false);
        ed_contactno.setClickable(false);
        ed_contactno.setFocusable(false);

        ed_printertype.setClickable(false);
        ed_printertype.setFocusable(false);

        //ed_routername.setEnabled(false);
        ed_routername.setClickable(false);
        ed_routername.setFocusable(false);

        //ed_routermodel.setEnabled(false);
        ed_routermodel.setClickable(false);
        ed_routermodel.setFocusable(false);

        ///ed_routermodelno.setEnabled(false);
        ed_routermodelno.setClickable(false);
        ed_routermodelno.setFocusable(false);

        //ed_routertype.setEnabled(false);
        ed_routertype.setClickable(false);
        ed_routertype.setFocusable(false);

        //ed_other.setEnabled(false);
        ed_other.setClickable(false);
        ed_other.setFocusable(false);

        //ed_usernm.setEnabled(false);
        ed_usernm.setClickable(false);
        ed_usernm.setFocusable(false);

        //ed_contactno.setEnabled(false);
        ed_contactno.setClickable(false);
        ed_contactno.setFocusable(false);

        rdo_active.setClickable(false);
        rdo_active.setFocusable(false);
        rdo_active.setEnabled(false);

        rdo_inactive.setClickable(false);
        rdo_inactive.setFocusable(false);
        rdo_inactive.setEnabled(false);

        img.setVisibility(View.GONE);
        lay_img.setVisibility(View.VISIBLE);
    }

    private void setFieldEnable(){
        ed_location.setEnabled(true);
        ed_location.setClickable(true);
        ed_location.setFocusable(true);

        ed_brandname.setEnabled(true);
        ed_brandname.setClickable(true);
        ed_brandname.setFocusable(true);

        ed_processor.setEnabled(true);
        ed_processor.setClickable(true);
        ed_processor.setFocusable(true);

        ed_ram.setEnabled(true);
        ed_ram.setClickable(true);
        ed_ram.setFocusable(true);

        ed_harddisk.setEnabled(true);
        ed_harddisk.setClickable(true);
        ed_harddisk.setFocusable(true);

        ed_swinstalled.setEnabled(true);
        ed_swinstalled.setClickable(true);
        ed_swinstalled.setFocusable(true);

        ed_osinstalled.setEnabled(true);
        ed_osinstalled.setClickable(true);
        ed_osinstalled.setFocusable(true);

        ed_licensesw.setEnabled(true);
        ed_licensesw.setClickable(true);
        ed_licensesw.setFocusable(true);

        ed_reamrk.setEnabled(true);
        ed_reamrk.setClickable(true);
        ed_reamrk.setFocusable(true);

        ed_printername.setEnabled(true);
        ed_printername.setClickable(true);
        ed_printername.setFocusable(true);

        ed_printermodel.setEnabled(true);
        ed_printermodel.setClickable(true);
        ed_printermodel.setFocusable(true);

        ed_printermodelno.setEnabled(true);
        ed_printermodelno.setClickable(true);
        ed_printermodelno.setFocusable(true);

        ed_printertype.setEnabled(true);
        ed_printertype.setClickable(true);
        ed_printertype.setFocusable(true);

        ed_routername.setEnabled(true);
        ed_routername.setClickable(true);
        ed_routername.setFocusable(true);

        ed_routermodel.setEnabled(true);
        ed_routermodel.setClickable(true);
        ed_routermodel.setFocusable(true);

        ed_routermodelno.setEnabled(true);
        ed_routermodelno.setClickable(true);
        ed_routermodelno.setFocusable(true);

        ed_routertype.setEnabled(true);
        ed_routertype.setClickable(true);
        ed_routertype.setFocusable(true);

        ed_other.setEnabled(true);
        ed_other.setClickable(true);
        ed_other.setFocusable(true);

        ed_usernm.setEnabled(true);
        ed_usernm.setClickable(true);
        ed_usernm.setFocusable(true);

        ed_contactno.setEnabled(true);
        ed_contactno.setClickable(true);
        ed_contactno.setFocusable(true);

        rdo_active.setClickable(true);
        rdo_active.setFocusable(true);
        rdo_active.setEnabled(true);

        rdo_inactive.setClickable(true);
        rdo_inactive.setFocusable(true);
        rdo_inactive.setEnabled(true);

        //img.setVisibility(View.VISIBLE);
        //lay_img.setVisibility(View.GONE);
    }

    private void setData(AssetClass asset){
        ed_location.setText(asset.getLocation());
        ed_brandname.setText(asset.getBrandName());
        ed_processor.setText(asset.getProcessor());
        ed_ram.setText(asset.getRAM());
        ed_harddisk.setText(asset.getHardDisk());
        ed_swinstalled.setText(asset.getSWInstalled());
        ed_osinstalled.setText(asset.getOS());
        ed_licensesw.setText(asset.getLicenseSW());
        ed_reamrk.setText(asset.getRemark());
        ed_printername.setText(asset.getPrinterName());
        ed_printermodel.setText(asset.getModel());
        ed_printermodelno.setText(asset.getModelNo());
        ed_printertype.setText(asset.getPrinterType());
        ed_routername.setText(asset.getRouterName());
        ed_routermodel.setText(asset.getRouterModel());
        ed_routermodelno.setText(asset.getRouterModelNo());
        ed_routertype.setText(asset.getRouterType());
        ed_other.setText(asset.getOther());
        ed_reamrk.setText(asset.getRemark());
        ed_usernm.setText(asset.getUserNm());
        ed_contactno.setText(asset.getContactNo());
        imagePath = asset.getImageName();
        String active = asset.getActive();
        if(active.equals("A")){
            rdo_active.setChecked(true);
            rdo_inactive.setChecked(false);
        }else{
            rdo_active.setChecked(false);
            rdo_inactive.setChecked(true);
        }
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
            lay_img.setVisibility(View.GONE);
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
            pb.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ic_broken_image_black_24dp);
        }
        return resizedBitmap;
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "AddAssetInfo_" + _data);
    }
}

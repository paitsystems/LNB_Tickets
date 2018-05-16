package com.lnbinfotech.lnb_tickets.services;

// Created by lnb on 8/16/2017.

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownloadImageService extends IntentService {

    public static final String BROADCAST = "imageDownloadedBroadcast";

    public DownloadImageService() {
        super(DownloadImageService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Constant.showLog("Service Started");
            String ftpaddress = FirstActivity.pref.getString(getString(R.string.pref_FTPLocation), "");
            String ftpuser = FirstActivity.pref.getString(getString(R.string.pref_FTPUser), "");
            String ftppass = FirstActivity.pref.getString(getString(R.string.pref_FTPPass), "");
            String ftpfolder = FirstActivity.pref.getString(getString(R.string.pref_FTPImgFolder), "");
            if(!ftpaddress.equals("")) {
                String imageName = intent.getExtras().getString("imageName");
                FTPClient ftp = new FTPClient();
                ftp.connect(ftpaddress, 21);
                int reply = ftp.getReplyCode();
                if (FTPReply.isPositiveCompletion(reply)) {
                    ftp.login(ftpuser, ftppass);
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp.enterLocalPassiveMode();
                    if (ftp.changeWorkingDirectory(ftpfolder)) {
                        File fname = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.folder_name, imageName);
                        OutputStream outstream;
                        outstream = new BufferedOutputStream(new FileOutputStream(fname));
                        ftp.retrieveFile(imageName, outstream);
                        outstream.close();
                        ftp.logout();
                        ftp.disconnect();
                        Intent intent1 = new Intent(BROADCAST);
                        sendBroadcast(intent1);
                        writeLog("DownloadImageService_onHandleIntent_broadcastSend");
                    } else {
                        writeLog("DownloadImageService_onHandleIntent_changeWorkingDirectory");
                    }
                } else {
                    writeLog("DownloadImageService_onHandleIntent_isPositiveCompletion");
                }
            }else {
                writeLog("DownloadImageService_onHandleIntent_ftpAddress_Not_Available");
            }
        } catch (Exception e) {
            Intent intent1 = new Intent(BROADCAST);
            sendBroadcast(intent1);
            e.printStackTrace();
            writeLog("DownloadImageService_onHandleIntent_"+e.getMessage());
        }
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

}


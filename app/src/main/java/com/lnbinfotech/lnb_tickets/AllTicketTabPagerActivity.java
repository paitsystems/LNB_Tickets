package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.adapter.ViewPagerAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.fragments.CancelFragments;
import com.lnbinfotech.lnb_tickets.fragments.CompleteFragments;
import com.lnbinfotech.lnb_tickets.fragments.HardwarePointFragment;
import com.lnbinfotech.lnb_tickets.fragments.HoldFrangments;
import com.lnbinfotech.lnb_tickets.fragments.InternalPointFragment;
import com.lnbinfotech.lnb_tickets.fragments.MessageFragment;
import com.lnbinfotech.lnb_tickets.fragments.PendingFragments;

public class AllTicketTabPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alltickettabpager);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAdView = findViewById(R.id.adView);

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
        setViewPager();
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if(MainActivity.isUpdate==1) {
            new PendingFragments().setData();
        }*/

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
        super.onDestroy();
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
        new Constant(AllTicketTabPagerActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(AllTicketTabPagerActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewPager(){
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
        String isHWApplicable = FirstActivity.pref.getString(getString(R.string.pref_isHWapplicable),"");
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PendingFragments(),"Pending");
        adapter.addFragment(new MessageFragment(),"Conversation");
        adapter.addFragment(new CompleteFragments(),"Complete");
        adapter.addFragment(new HoldFrangments(),"Hold");
        adapter.addFragment(new CancelFragments(),"Cancel");
        if(isHWApplicable.equals("SH")) {
            adapter.addFragment(new HardwarePointFragment(), "Hardware");
        }
        if(type.equals("E")) {
            adapter.addFragment(new InternalPointFragment(), "Internal");
        }
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
    }

    private void init() {
        tabLayout = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AllTicketTabPagerActivity.this);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(AllTicketTabPagerActivity.this).doFinish();
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

}

package com.lnbinfotech.lnb_tickets.interfaces;

import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitApiInterface {

    @Headers("Content-Type: application/json")
    @POST("GetTicketMasterV1")
    Call<List<TicketMasterClass>> getTicketMasterV1(@Body RequestBody url);

    @Headers("Content-Type: application/json")
    @POST("GetTicketDetailV1")
    Call<List<TicketDetailClass>> getTicketDetailsV1(@Body RequestBody url);

}

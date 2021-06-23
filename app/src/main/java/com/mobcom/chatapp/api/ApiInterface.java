package com.mobcom.chatapp.api;

import com.mobcom.chatapp.model.MainModel;
import com.mobcom.chatapp.model.Response;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({"Authorization: key=AAAAgf_ZpCM:APA91bHybUZ_5yHu7QC9VXEckhf96XbpSkqUkF2bFDkUiSBm0RjOtDTEs8ZBr7mWGuyk_lBa2U-pfLKjUyMAwq0APSPyhJlcy2PY7OyqT6TbTwCwhMX47syjUXYVMgnXzkxe06OMGG5b",
            "Content-Type:application/json"})
    @POST("send")
    Call<Response> SendMessage(@Body MainModel mainModel);
}

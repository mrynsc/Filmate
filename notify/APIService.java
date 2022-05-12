package com.yeslabapps.fictionfocus.notify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {

                    "Content-Type:application/json",
                    "Authorization:key=AAAAcMPfOvQ:APA91bG-tAeAYv_pfaO_SIVd1IULdKBHCVZOV2eNKIhi52W_lBsM23EWdn_e1j-7iADZyGBGGic2HcMcwkVuKb96f9pV6kuei57cyLZ5TiF8ZeQB58fVpbK_nxC0xeOu-WS1XWabdF5v"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
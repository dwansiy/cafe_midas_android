package com.xema.cafemidas.network.service;

import android.support.annotation.Keep;

import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Photo;
import com.xema.cafemidas.model.Profile;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

@Keep
public interface AccountService {
    @FormUrlEncoded
    @POST("/add_user/")
    Call<ApiResult> signUp(@Field("id") String id, @Field("pw") String password);

    @FormUrlEncoded
    @POST("/login/")
    Call<Profile> signIn(@Field("id") String id, @Field("pw") String password);

    // TODO: 2018-05-25
    //회원 탈퇴
    @FormUrlEncoded
    @POST("/remove_user/")
    Call<ApiResult> dropAccount(@Field("id") String id, @Field("pw") String password);

    @Multipart
    @POST("/change_profile_image/")
    Call<Photo> uploadProfileImage(@Part MultipartBody.Part id, @Part MultipartBody.Part pw, @Part MultipartBody.Part profileImage);

    @FormUrlEncoded
    @POST("/change_profile_name/")
    Call<ApiResult> changeProfileName(@Field("id") String id, @Field("pw") String pw, @Field("name") String name);

    @FormUrlEncoded
    @POST("/change_profile_comment/")
    Call<ApiResult> changeProfileComment(@Field("id") String id, @Field("pw") String pw, @Field("comment") String comment);
}

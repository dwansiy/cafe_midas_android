package com.xema.cafemidas.network.service;

import android.support.annotation.Keep;

import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Photo;
import com.xema.cafemidas.model.Profile;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

@Keep
public interface AccountService {
    @FormUrlEncoded
    @POST("/add_user/")
    Call<ApiResult> signUp(@Field("id") String id, @Field("pw") String password, @Field("name") String name);

    @FormUrlEncoded
    @POST("/login/")
    Call<Profile> signIn(@Field("id") String id, @Field("pw") String password);

    //회원 리스트 가져오기
    @FormUrlEncoded
    @POST("/get_profiles/")
    Call<List<Profile>> getProfileList(@Field("id") String id, @Field("pw") String pw);

    // TODO: 2018-05-26
    //회원탈퇴
    @FormUrlEncoded
    @POST("/remove_user/")
    Call<ApiResult> dropAccount(@Field("id") String id, @Field("pw") String pw);

    //회원탈퇴 시키기
    @FormUrlEncoded
    @POST("/remove_user_by_id/")
    Call<ApiResult> dropAccountById(@Field("id") String id, @Field("pw") String pw, @Field("uid") String uid);

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

package com.xema.cafemidas.network.service;

import android.support.annotation.Keep;

import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Photo;
import com.xema.cafemidas.model.Product;
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
    Call<ApiResult> signUp(@Field("id") String id, @Field("pw") String password, @Field("name") String name, @Field("fcm_token") String fcmToken);

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
    Call<ApiResult> dropAccountById(@Field("id") String id, @Field("pw") String pw, @Field("uid") int uid);

    //개인정보수정(본인)
    @Multipart
    @POST("/edit_user/")
    Call<Profile> editProfile(@Part MultipartBody.Part id, @Part MultipartBody.Part pw, @Part MultipartBody.Part name, @Part MultipartBody.Part image, @Part MultipartBody.Part comment);

    //개인정보수정(본인- 사진없을때)
    @FormUrlEncoded
    @POST("/edit_user/")
    Call<Profile> editProfile(@Field("id") String id, @Field("pw") String pw, @Field("name") String name, @Field("comment") String comment);

    //개인정보수정(관리자가)
    @Multipart
    @POST("/edit_user_by_id/")
    Call<Profile> editProfileById(@Part MultipartBody.Part id, @Part MultipartBody.Part pw, @Part MultipartBody.Part uid, @Part MultipartBody.Part name, @Part MultipartBody.Part image, @Part MultipartBody.Part comment);

    //개인정보수정(관리자가- 사진없을때)
    @FormUrlEncoded
    @POST("/edit_user_by_id/")
    Call<Profile> editProfileById(@Field("id") String id, @Field("pw") String pw, @Field("uid") int uid, @Field("name") String name, @Field("comment") String comment);

}

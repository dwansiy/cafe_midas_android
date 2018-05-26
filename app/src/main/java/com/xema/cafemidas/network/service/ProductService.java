package com.xema.cafemidas.network.service;

import com.xema.cafemidas.model.Category;
import com.xema.cafemidas.model.Product;

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

/**
 * Created by xema0 on 2018-05-26.
 */

public interface ProductService {
    @FormUrlEncoded
    @POST("/create_category/")
    Call<Category> makeCategory(@Field("id") String id, @Field("pw") String password, @Field("name") String categoryName);

    @FormUrlEncoded
    @POST("/delete_category/")
    Call<Category> deleteCategory(@Field("id") String id, @Field("pw") String password, @Field("category_id") int categoryId);

    @GET("/get_categories/")
    Call<List<Category>> getCategoryList();

    @Multipart
    @POST("/create_category/")
    Call<Product> makeProduct(@Part MultipartBody.Part id, @Part MultipartBody.Part pw, @Part MultipartBody.Part categoryId, @Part MultipartBody.Part name, @Part MultipartBody.Part price, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("/delete_menu/")
    Call<Product> deleteProduct(@Field("id") String id, @Field("pw") String password, @Field("menu_id") int menuId);

    @GET("/get_menus/")
    Call<List<Product>> getProductList();
}

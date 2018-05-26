package com.xema.cafemidas.network.service;

import com.xema.cafemidas.model.Order;
import com.xema.cafemidas.model.OrderItem;
import com.xema.cafemidas.model.OrderMenuList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xema0 on 2018-05-26.
 */

public interface OrderService {
    @FormUrlEncoded
    @POST("/get_order_by_id/")
    Call<List<OrderItem>> getDetailOrder(@Field("id") String id, @Field("pw") String pw, @Field("order_id") int orderId);

    @POST("/create_order/")
    Call<String> postOrder(@Body OrderMenuList orderMenuList);


}

package com.xema.cafemidas.network;

import com.xema.cafemidas.network.service.AccountService;
import com.xema.cafemidas.network.service.OrderService;
import com.xema.cafemidas.network.service.ProductService;

public class ApiUtil {
    public static AccountService getAccountService() {
        return RetrofitClient.getClient().create(AccountService.class);
    }

    public static ProductService getProductService() {
        return RetrofitClient.getClient().create(ProductService.class);
    }

    public static OrderService getOrderService() {
        return RetrofitClient.getClient().create(OrderService.class);
    }
}

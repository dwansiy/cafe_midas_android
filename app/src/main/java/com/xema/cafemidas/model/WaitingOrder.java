package com.xema.cafemidas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xema0 on 2018-05-27.
 */

public class WaitingOrder {
    @SerializedName("waiting_cnt")
    private int waitingOrder;

    public int getWaitingOrder() {
        return waitingOrder;
    }

    public void setWaitingOrder(int waitingOrder) {
        this.waitingOrder = waitingOrder;
    }
}

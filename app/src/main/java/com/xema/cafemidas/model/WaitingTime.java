package com.xema.cafemidas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xema0 on 2018-05-27.
 */

public class WaitingTime {
    @SerializedName("waiting_time")
    private int waitingTime;

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}

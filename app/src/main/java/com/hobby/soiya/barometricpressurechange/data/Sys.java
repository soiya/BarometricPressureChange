package com.hobby.soiya.barometricpressurechange.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Sys {

    @SerializedName("pod")
    @Expose
    private String pod;

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }
}

package com.hobby.soiya.barometricpressurechange.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Rain {

    @SerializedName("3h")
    @Expose
    private Double _3h;

    public Double get3h() {
        return _3h;
    }

    public void set3h(Double _3h) {
        this._3h = _3h;
    }
}

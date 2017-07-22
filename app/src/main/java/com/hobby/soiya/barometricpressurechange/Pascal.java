package com.hobby.soiya.barometricpressurechange;

import java.util.Date;

public class Pascal {

    private Double pascal;
    private Date date;

    public Pascal(){

    }

    public Pascal(Double pascal, Date date){
        this.pascal = pascal;
        this.date = date;
    }

    public Double getPascal() { return pascal; }

    public void setPascal(Double pascal) { this.pascal = pascal; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

}

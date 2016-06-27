package com.gailardia.lymbo.Users;

import com.firebase.client.Firebase;
import com.gailardia.lymbo.dsignup;

/**
 * Created by Gailardia on 6/26/2016.
 */
public class Users {
    String driverName;
    String driverPassword;
    String driverIMEI;
    String driverPhone;
    String type;

    public Users() {

    }

    public Users(String driverPassword, String driverName, String driverIMEI, String driverPhone, String type) {
        this.driverPassword = driverPassword;
        this.driverName = driverName;
        this.driverIMEI = driverIMEI;
        this.driverPhone = driverPhone;
        this.type = type;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverPassword() {
        return driverPassword;
    }

    public String getDriverIMEI() {
        return driverIMEI;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public String getType() {
        return type;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setDriverPassword(String driverPassword) {
        this.driverPassword = driverPassword;
    }

    public void setDriverIMEI(String driverIMEI) {
        this.driverIMEI = driverIMEI;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public void setType(String type) {
        this.type = type;
    }
}

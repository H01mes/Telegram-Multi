package org.telegram.ui.Components;

import android.graphics.Bitmap;

/**
 * Created by oleg.svs on 22.05.2017.
 */

public class UserItems {
    String userName;
    String userPhone;
    Bitmap userPhoto;

    public String getName() {
        return userName;
    }

    public void setName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return userPhone;
    }

    public void setPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Bitmap getPhoto() {
        return userPhoto;
    }

    public void setPhoto(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }
}

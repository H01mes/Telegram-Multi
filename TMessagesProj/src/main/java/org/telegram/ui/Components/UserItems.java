package org.telegram.ui.Components;

/**
 * Created by oleg.svs on 22.05.2017.
 */

public class UserItems {
    String userName;
    String userPhone;
    int userPhoto;

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

    public int getPhoto() {
        return userPhoto;
    }

    public void setPhoto(int image) {
        this.userPhoto = userPhoto;
    }
}

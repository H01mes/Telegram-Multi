package org.telegram.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger_test.LocaleController;
import org.telegram.messenger_test.R;
import org.telegram.messenger_test.UserConfig;
import org.telegram.tgnet.TLRPC;

public class ChangeUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

//        TextView tvName = (TextView)findViewById(R.id.tvName);
//        TextView tvPnone = (TextView)findViewById(R.id.tvPhone);

        TLRPC.User user = UserConfig.getCurrentUser();
        String userName;
        String userPhone;
        if (user != null && user.phone != null && user.phone.length() != 0) {
            userPhone = PhoneFormat.getInstance().format("+" + user.phone);
        } else {
            userPhone = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        }
        if (user != null && user.username != null && user.username.length() != 0) {
            userName =  user.first_name + "@" + user.username;

        } else {
            userName = user.first_name;
        }
//        tvName.setText(userName);
//        tvPnone.setText(userPhone);
        Toast.makeText(this,userName.toString() + " " + userPhone.toString(),Toast.LENGTH_LONG).show();
    }
}

package org.telegram.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger_test.Change_user_helper;
import org.telegram.messenger_test.LocaleController;
import org.telegram.messenger_test.R;
import org.telegram.messenger_test.UserConfig;
import org.telegram.tgnet.TLRPC;

public class ChangeUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        TextView userCurrent = (TextView)findViewById(R.id.tvUserCurrent);
//        TextView tvPnone = (TextView)findViewById(R.id.tvPhone);
        Button btUser1 = (Button)findViewById(R.id.setUser1);
        Button btUser2 = (Button)findViewById(R.id.setUser2);
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

        btUser1.setText(getUserTag("_user_0"));
        btUser2.setText(getUserTag("_user_1"));
        userCurrent.setText("Current user " + userName.toString() + " " + userPhone.toString());
        Toast.makeText(this,"Current user " + userName.toString() + " " + userPhone.toString(),Toast.LENGTH_LONG).show();
    }

    public void setUser1(View v) {
        Change_user_helper.setUserTag("_user_0");
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putString("userID", Change_user_helper.getUserTag()).commit();
        sharedPref.edit().apply();
        Log.i("userTAG", "setUser2: tag changed to " + Change_user_helper.getUserTag());
        restart();
    }

    public void setUser2(View v) {
        Change_user_helper.setUserTag("_user_1");
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putString("userID", Change_user_helper.getUserTag()).commit();
        sharedPref.edit().apply();
        Log.i("userTAG", "setUser2: tag changed to " + Change_user_helper.getUserTag());
        restart();
    }

    public void restart() {
        Intent launchIntent = new Intent(getApplicationContext(), org.telegram.ui.LaunchActivity.class);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, launchIntent , 0);
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, intent);
        Log.i("userTAG", "restarting... " + Change_user_helper.getUserTag());
        System.exit(2);
    }

    public String getUserTag(String tag) {
        SharedPreferences userPhone = getSharedPreferences("users" + tag.toString(), Context.MODE_PRIVATE);
        return userPhone.getString("phone_user1","не задан");
//        Log.i("userTAG", "postInitApplication: " + Change_user_helper.getUserTag());
    }
}

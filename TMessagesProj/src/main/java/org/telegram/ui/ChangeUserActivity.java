package org.telegram.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.ChangeUserHelper;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.UserItemsAdapter;
import org.telegram.ui.Components.UserItems;

import java.util.ArrayList;

public class ChangeUserActivity extends Activity implements AdapterView.OnItemClickListener {

    ListView lvUserList;
    UserItemsAdapter adapter;
    private ArrayList<Object> itemList;
    private UserItems userItems;
    private static int usersCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        prepareArrayLits();
        lvUserList = (ListView) findViewById(R.id.users_listview);
        adapter = new UserItemsAdapter(this, itemList);
        lvUserList.setAdapter(adapter);

        lvUserList.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });
//        TextView userCurrent = (TextView)findViewById(R.id.tvUserCurrent);
//        TextView tvPnone = (TextView)findViewById(R.id.tvPhone);
//        Button btUser1 = (Button)findViewById(R.id.setUser1);
//        Button btUser2 = (Button)findViewById(R.id.setUser2);
    }

    public void addUser() {
        ChangeUserHelper.setUserTag("_user_" + String.valueOf(getUsersCount()));
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putString("userID", ChangeUserHelper.getUserTag()).commit();
        sharedPref.edit().putInt("usersCount", getUsersCount() + 1).commit();
        sharedPref.edit().apply();
        Log.i("userTAG", "addUser: tag changed to " + ChangeUserHelper.getUserTag());
        restart();
    }

    public int getUsersCount() {
        SharedPreferences userPhone = getSharedPreferences("userID", Context.MODE_PRIVATE);
        return userPhone.getInt("usersCount",1);
    }

    public void setUser(int position) {
        ChangeUserHelper.setUserTag("_user_" + position);
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putString("userID", ChangeUserHelper.getUserTag()).commit();
        sharedPref.edit().apply();
        Log.i("userTAG", "setUser: tag changed to " + ChangeUserHelper.getUserTag());
        restart();
    }

    public void restart() {
        Intent launchIntent = new Intent(getApplicationContext(), org.telegram.ui.LaunchActivity.class);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, launchIntent , 0);
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, intent);
        Log.i("userTAG", "restarting... " + ChangeUserHelper.getUserTag());
        System.exit(2);
    }

    public String getUserTag(String tag) {
        SharedPreferences userPhone = getSharedPreferences("users" + tag.toString(), Context.MODE_PRIVATE);
        return userPhone.getString("phone_user1","не задан");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserItems userItems = (UserItems) adapter.getItem(position);
        Toast.makeText(this, "Name => "+userItems.getName()+" \n Phone => "+userItems.getPhone(), Toast.LENGTH_SHORT).show();
        setUser(position);
    }

    public void prepareArrayLits()
    {
        TLRPC.User user = UserConfig.getCurrentUser();
        String userName;
        String userPhone;
        String userLastName;
        if (user != null && user.phone != null && user.phone.length() != 0) {
            userPhone = PhoneFormat.getInstance().format("+" + user.phone);
        } else {
            userPhone = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        }
        if (user != null && user.username != null && user.username.length() != 0) {
            userName =  user.first_name + "@" + user.username;
            userLastName = user.last_name;

        } else {
            userName = user.first_name;
            userLastName = user.last_name;
        }
//        tvName.setText(userName);
//        tvPnone.setText(userPhone);
//        btUser1.setText(getUserTag("_user_0"));
//        btUser2.setText(getUserTag("_user_1"));
//        userCurrent.setText("Current user " + userName.toString() + " " + userLastName.toString() + " " + userPhone.toString());
//        Toast.makeText(this,"Current user " + userName.toString() + " " + userPhone.toString(),Toast.LENGTH_LONG).show();


        itemList = new ArrayList<Object>();
        int usersCount = getUsersCount();
        for (int i = 0; i < usersCount ; i++) {
            AddObjectToList(R.drawable.book_user, "User" , getUserTag("_user_" + i));
        }
    }

    // Add one item into the Array List
    public void AddObjectToList(int image, String title, String desc)
    {
        userItems = new UserItems();
        userItems.setPhone(desc);
        userItems.setPhoto(image);
        userItems.setName(title);
        itemList.add(userItems);
    }
}

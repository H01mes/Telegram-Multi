package org.telegram.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.telegram.messenger.ApplicationLoader2;
import org.telegram.messenger.ChangeUserHelper;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig2;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.UserItemsAdapter;
import org.telegram.ui.Components.UserItems;


import java.io.File;
import java.util.ArrayList;

public class ChangeUserActivity extends Activity implements AdapterView.OnItemClickListener {

    ListView lvUserList = null;
    UserItemsAdapter adapter = null;
    private ArrayList<Object> itemList;
    private UserItems userItems ;
    static ProgressDialog prepareProgress;
    static Context ctx ;

    @Override
    public void onBackPressed()
    {
        System.gc();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        try {
            ctx = this;
            itemList = new ArrayList<Object>();
            lvUserList = (ListView) findViewById(R.id.users_listview);
            lvUserList.setOnItemClickListener(this);
            lvUserList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int position, long id) {
                    // TODO Auto-generated method stub
                    // TODO deleting user on random position
                    if (position > 0)
                        showAlertDeleteUser(position);
                    else Toast.makeText(ChangeUserActivity.this, "Impossible delete first user!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            prepareArrayLits();
            Log.i("TGM", "onCreate: prepareArray");
            prepareProgress.dismiss();
            Thread prepareThread = new Thread(
                    new Runnable() {
                        public void run() {
                            prepareArrayLits();
                            runOnUiThread(new Runnable() {
                                public void run() {
//                                    prepareProgress.dismiss();
                                }
                            });
                        }
                    }
            );
//            prepareThread.start();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setColorNormal(Theme.getColor(Theme.key_chats_actionBackground));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lvUserList.getCount() <= 9)
                        addUser();
                    else
                        Toast.makeText(ChangeUserActivity.this, "Maximum 10 users!", Toast.LENGTH_SHORT).show();
                }
            });

            FloatingActionButton fabBack = (FloatingActionButton) findViewById(R.id.fabBack);
            fab.setColorNormal(Theme.getColor(Theme.key_chats_actionBackground));
            fabBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } catch (Throwable th) {
            Log.i("TGM", "onCreate: " + th.toString());
        }
    }


    public static void showPrepareDialog(Context ctx) {
        prepareProgress= new ProgressDialog(ctx);
        prepareProgress.setMessage("Сканирование профилей");
        prepareProgress.setIndeterminate(false);
        prepareProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prepareProgress.setCancelable(false);
        prepareProgress.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         lvUserList = null;
         adapter = null;
         itemList = null;
         userItems = null;
         System.gc();
    }

    private void deleteUser(int position) {
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putInt("usersCount", getUsersCount() -1).commit();
        sharedPref.edit().apply();
        deleteDir(getApplicationInfo().dataDir + "/files_user_" + String.valueOf(position));
        adapter.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void showAlertDeleteUser(final int position) {
        String title = "You are sure?";
        String message = "Delete user?";
        String button1String = "Yes";
        String button2String = "No";

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                deleteUser(position);
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.show();
    }

    public void deleteDir(String folder) {
        File dir = new File(folder);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
            dir.delete();
        }
    }

    public void addUser() {
        ChangeUserHelper.setUserTag(getUsersCount());
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putInt("userID", ChangeUserHelper.getID()).commit();
        sharedPref.edit().putInt("usersCount", getUsersCount() + 1).commit();
        sharedPref.edit().apply();
        Log.i("userTAG", "addUser: tag changed to " + ChangeUserHelper.getUserTag());
        restart();
    }

    public int getUsersCount() {
        Log.i("TGM", "getUsersCount: called");
        SharedPreferences userPhone = getSharedPreferences("userID", Context.MODE_PRIVATE);
        return userPhone.getInt("usersCount",1);
    }

    public void setUser(int position) {
        ChangeUserHelper.setUserTag(position);
        SharedPreferences sharedPref = getSharedPreferences("userID", Context.MODE_PRIVATE);
        sharedPref.edit().putInt("userID", ChangeUserHelper.getID()).commit();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserItems userItems = (UserItems) adapter.getItem(position);
        Toast.makeText(this, "Name => "+userItems.getName()+" \n Phone => "+userItems.getPhone(), Toast.LENGTH_SHORT).show();
        setUser(position);
    }

    public void prepareArrayLits()
    {   itemList = new ArrayList<Object>();
        int usersCount = getUsersCount();
        for (int i = 0; i < usersCount ; i++) {
            Log.i("TGM", "prepareArrayLits: " + i);
            String first_name = "null";
            if (getUserByTag("_user_" + i).last_name == null) first_name = getUserByTag("_user_" + i).first_name;
            else first_name = getUserByTag("_user_" + i).first_name + " " + getUserByTag("_user_" + i).last_name;
            String phone = getUserByTag("_user_" + i).phone;
            Bitmap photo = getBitmap(getUserByTag("_user_" + i));
            if(ChangeUserHelper.getID() == i) AddObjectToList(photo, first_name, phone, i);
            else
                AddObjectToList(photo, first_name, phone);
        }
        adapter = new UserItemsAdapter(this, itemList);
        lvUserList.setAdapter(adapter);
        Log.i("TGM", "prepareArrayLits: setAdapters");
    }

    private TLRPC.User getUserByTag(String tag) {
        ApplicationLoader2.convertConfig2(tag);
        TLRPC.User user = UserConfig2.getCurrentUser(tag);
        Log.i("TGM", "getUserByTag: called " + tag.toString());
        return user;
    }

    public Bitmap getBitmap(TLRPC.User user) {
        Bitmap icon;
        Log.i("TGM", "getBitmap: called");
        if (user.photo != null && user.photo.photo_small != null) {
            Log.i("TGM", "getBitmap: photo != null");
            icon = createRoundBitmap(FileLoader.getPathToAttach(user.photo.photo_small, true));
            return icon;
        }
        return drawableToBitmap(R.drawable.logo_avatar);
    }

    public void AddObjectToList(Bitmap image, String title, String desc)
    {
        Log.i("TGM", "AddObjectToList: called");
        userItems = new UserItems();
        userItems.setPhone(desc);
        userItems.setPhoto(image);
        userItems.setName(title);
        itemList.add(userItems);
    }

    public void AddObjectToList(Bitmap image, String title, String desc, int pos)
    {
        Log.i("TGM", "AddObjectToList: called");
        userItems = new UserItems();
        userItems.setPhone(desc);
        userItems.setPhoto(image);
        userItems.setCurrent(pos);
        userItems.setName(title);
        itemList.add(userItems);
    }

    public Bitmap drawableToBitmap (int drawable) {
        Log.i("TGM", "drawableToBitmap: called");
        Bitmap b = null;
        Drawable d = getResources().getDrawable(drawable);
        Drawable currentState = d.getCurrent();
        if(currentState instanceof BitmapDrawable)
            b = ((BitmapDrawable)currentState).getBitmap();
        return b;
    }

    public Bitmap createRoundBitmap(File path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path.toString());
            if (bitmap != null) {
                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        circleBitmap, (int) convertDpToPixel(50),(int) convertDpToPixel(50), false);
                return resizedBitmap;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        return null;
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
            onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

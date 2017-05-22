package org.telegram.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UserItems;

import java.util.ArrayList;

/**
 * Created by oleg.svs on 22.05.2017.
 */

public class UserItemsAdapter extends BaseAdapter {

    ArrayList<Object> itemList;

    public Activity context;
    public LayoutInflater inflater;

    public UserItemsAdapter(Activity context, ArrayList<Object> itemList) {
        super();

        this.context = context;
        this.itemList = itemList;

        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder
    {
        ImageView imgViewPhoto;
        TextView txtViewName;
        TextView txtViewPhone;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_list_item, null);

            holder.imgViewPhoto = (ImageView) convertView.findViewById(R.id.userPhoto);
            holder.txtViewName = (TextView) convertView.findViewById(R.id.userName);
            holder.txtViewPhone = (TextView) convertView.findViewById(R.id.userPhone);

            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();

        UserItems userItems = (UserItems) itemList.get(position);

        holder.imgViewPhoto.setImageResource(userItems.getPhoto());
        holder.txtViewName.setText(userItems.getName());
        holder.txtViewPhone.setText(userItems.getPhone());

        return convertView;
    }

}

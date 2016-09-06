package com.xmpp.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmpp.client.R;

import java.util.List;
import java.util.Map;

/**
 * Created by yxm on 2016/8/31.
 */
public class FriendAdapter extends BaseAdapter {
    public List<Map<String, String>> list;
    public Context context;

    public FriendAdapter(List<Map<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item_layout, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(list.get(position).get("name"));
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}

package com.xmpp.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmpp.client.R;
import com.xmpp.client.bean.MsgBean;

import java.util.List;

/**
 * Created by yxm on 2016/9/1.
 */
public class ChatAdapter extends BaseAdapter {
    public List<MsgBean> list;
    public Context context;

    public ChatAdapter(List<MsgBean> list, Context context) {
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
    public int getItemViewType(int position) {
        MsgBean chatMessage = list.get(position);
        if (chatMessage.getType() == MsgBean.Type.INCOMING) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        MsgBean bean = list.get(position);
        if (convertView == null) {
            //IN,OUT的图片
            if (getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_in, null);
                holder = new ViewHolder();
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_to, null);
                holder = new ViewHolder();
            }
            holder.name = (TextView) convertView.findViewById(R.id.formclient_row_userid);
            holder.data = (TextView) convertView.findViewById(R.id.formclient_row_date);
            holder.msg = (TextView) convertView.findViewById(R.id.formclient_row_msg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(bean.getUserid());
        holder.data.setText(bean.getDate());
        holder.msg.setText(bean.getMsg());
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView data;
        TextView msg;
    }
}

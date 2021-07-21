package com.example.photoera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoutListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ShoutingDTO> shoutDatas;

    TextView user_name;
    TextView shout_date;
    TextView user_message;

    public ShoutListAdapter(Context context, ArrayList<ShoutingDTO> shoutDatas) {
        this.context = context;
        this.shoutDatas = shoutDatas;
    }

    @Override
    public int getCount() {
        return this.shoutDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.shoutDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list, null);
             user_name = (TextView) convertView.findViewById(R.id.user_name);
             shout_date = (TextView) convertView.findViewById(R.id.shout_date);
             user_message = (TextView) convertView.findViewById(R.id.user_message);

        }
        user_name.setText(shoutDatas.get(position).name);
        String sd = shoutDatas.get(position).date;
        shout_date.setText(sd.substring(0,4) + "년 " + sd.substring(4,6) + "월 " + sd.substring(6,8) + "일");
        user_message.setText(shoutDatas.get(position).message);
        return convertView;
    }
}


package com.example.administrator.chatdemo;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<EMGroup> {

    private List<EMGroup> groups;
    public GroupAdapter(Context context, int res, List<EMGroup> groups) {
        super(context, res, groups);
        this.groups=groups;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getContext());
            }
            ((TextView) convertView).setText(groups.get(position).getGroupName());
        return convertView;
    }

    @Override
    public int getCount() {
        return groups.size() ;
    }

}

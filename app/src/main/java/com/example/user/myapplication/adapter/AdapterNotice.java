package com.example.user.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.item.ItemNotice;

import java.util.ArrayList;

public class AdapterNotice extends BaseExpandableListAdapter {

    private ArrayList<ItemNotice> list;

    public AdapterNotice(ArrayList<ItemNotice> list) {
        this.list = list;
    }

    public void updateList(ArrayList<ItemNotice> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public String getGroup(int groupPosition) {
        return null;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getText();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_parent, parent, false);
        }

        ImageView profile = convertView.findViewById(R.id.parent_iv_arrow);
        TextView subject = convertView.findViewById(R.id.parent_tv_subject);
        TextView date = convertView.findViewById(R.id.parent_tv_date);
//        TextView hits = convertView.findViewById(R.id.parent_tv_hits);

        if (isExpanded) {
            profile.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        } else {
            profile.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }
        subject.setText(list.get(groupPosition).getSubject());
        date.setText(list.get(groupPosition).getDate());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_children, parent, false);
        }

        TextView tv = convertView.findViewById(R.id.children_tv);
        tv.setText(getChild(groupPosition, childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

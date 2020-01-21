package com.example.user.myapplication.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.item.ItemMember;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class AdapterMember extends BaseAdapter {

    private ArrayList<ItemMember> arrayList;

    public AdapterMember(ArrayList<ItemMember> arrayList) {
        this.arrayList = arrayList;
    }

    public void updateList(ArrayList<ItemMember> list) {
        arrayList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_member, null);
        }

        ImageView iv = convertView.findViewById(R.id.listMember_iv_image);
        TextView tv1 = convertView.findViewById(R.id.listMember_tv_grade);
        TextView tv2 = convertView.findViewById(R.id.listMember_tv_id);

        try {
            Uri uri = arrayList.get(position).getImageUri();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(parent.getContext().getContentResolver(), uri);
            String grade = Integer.toString(arrayList.get(position).getGrade());

            iv.setImageBitmap(bitmap);
            tv1.setText(grade);
            tv2.setText(arrayList.get(position).getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}

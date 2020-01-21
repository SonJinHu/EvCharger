package com.example.user.myapplication.garbage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.myapplication.R;

public class listview extends AppCompatActivity {


//    ListView listView;
//    MyAdapter adapter;
//    // Integer ... 배열에 클래스 객체가 삽입.
//    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_join);
//
//        listView = findViewById(R.id.listview);
//        adapter = new MyAdapter();
//
//        arrayList = new ArrayList<>();
//        arrayList.add("외골수");
//        arrayList.add("외골수");
//        arrayList.add("외골수");arrayList.add("외골수");arrayList.add("외골수");arrayList.add("외골수");arrayList.add("외골수");arrayList.add("외골수");
//
//        listView.setAdapter(adapter);
    }

//    public class MyAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return arrayList.size(); // 몇 줄 그릴지 개발자가 정해줌.
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null; // 안에 들어갈 데이터, 무시.
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0; // 몇 번째 줄 데이터의 무슨줄?, 무시.
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            if(convertView==null) { // null인경우는 최초일때밖에 없음. 올렸다가 내렸다가 하면 재사용. 돌려막기.
//
//                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.listview_parsing, null);
//                Log.e("최초", "최초");
//            } else {
//                Log.e("재사용", "재사용");
//            }
//
//
//            TextView tv = convertView.findViewById(R.id.textView2); // converView를 쓰는 이유....
//            tv.setText(arrayList.get(position));
//            return convertView;
//            // 여기서 모든 역사가ㅏ 일어난다.
//        }
//    }
}


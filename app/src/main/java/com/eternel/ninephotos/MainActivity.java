package com.eternel.ninephotos;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.eternel.ninephotos.ninephotos.NinePhotoLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
private RecyclerView rlList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlList=findViewById(R.id.list);
        rlList.setLayoutManager(new LinearLayoutManager(this));
        ListAdapter listAdapter = new ListAdapter(R.layout.item_list);
        rlList.setAdapter(listAdapter);
        ArrayList<String> objects = new ArrayList<>();
        listAdapter.addData(objects);
        objects.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539849835187&di=3bd89ced5467efafc9a22d8f5da2e1f5&imgtype=0&src=http%3A%2F%2Fpic14.nipic.com%2F20110605%2F1369025_165540642000_2.jpg");
        objects.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539849835187&di=3bd89ced5467efafc9a22d8f5da2e1f5&imgtype=0&src=http%3A%2F%2Fpic14.nipic.com%2F20110605%2F1369025_165540642000_2.jpg");
        objects.add("http://pic.peepic.com/uploads_pic/20180310/1112228/1112228_001.jpg");
        objects.add("http://pic.peepic.com/uploads_pic/20180310/1112228/1112228_001.jpg");
        listAdapter.addData(objects);
        listAdapter.setImageClickListener(new ListAdapter.onImageClickListener() {
            @Override
            public void setOnImageClickListener(NinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
                ToastUtils.showShort(position+model);
            }
        });
    }
}

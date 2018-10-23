package com.eternel.ninephotos;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.eternel.ninephotos.ninephotos.NinePhotoLayout;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseQuickAdapter<ArrayList<String>, BaseViewHolder> {
    public ListAdapter(int layoutResId) {
        super(layoutResId);
    }

    private onImageClickListener imageClickListener;

    @Override
    protected void convert(BaseViewHolder helper, ArrayList<String> item) {
        NinePhotoLayout view = helper.getView(R.id.images);
        view.setData( item);
        view.setDelegate(new NinePhotoLayout.Delegate() {
            @Override
            public void onClickNinePhotoItem(NinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
                if (imageClickListener != null) {
                    imageClickListener.setOnImageClickListener(ninePhotoLayout,view,position,model,models);
                }
            }
        });
    }

    public void setImageClickListener(onImageClickListener listener) {
        imageClickListener = listener;
    }

    public interface onImageClickListener {
        void setOnImageClickListener(NinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);
    }
}

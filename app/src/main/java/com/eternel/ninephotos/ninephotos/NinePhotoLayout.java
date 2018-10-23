package com.eternel.ninephotos.ninephotos;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.eternel.ninephotos.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NinePhotoLayout extends FrameLayout implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    private static final int ITEM_NUM_COLUMNS = 3;
    private PhotoAdapter mPhotoAdapter;
    private ImageView mPhotoIv;
    private RecyclerView mPhotoGv;
    private Delegate mDelegate;
    private int mCurrentClickItemPosition;
    private boolean mShowAsLargeWhenOnlyOne;
    private int mItemWhiteSpacing;
    private int mPlaceholderDrawableResId;
    private int mItemSpanCount;

    private int mItemWidth;
    private GridLayoutManager gridLayoutManager;
    private int measuredWidth;
    private ArrayList<String> images;
    private boolean init;

    public NinePhotoLayout(Context context) {
        this(context, null);
    }

    public NinePhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NinePhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultAttrs();
        initCustomAttrs(context, attrs);
        afterInitDefaultAndCustomAttrs();
    }

    private void initDefaultAttrs() {
        mItemWidth = 0;
        mShowAsLargeWhenOnlyOne = true;
        mItemWhiteSpacing = SizeUtils.dp2px(1);
        mPlaceholderDrawableResId = R.mipmap.ic_launcher;
        mItemSpanCount = 3;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NinePhotoLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.NinePhotoLayout_showAsLargeWhenOnlyOne) {
            mShowAsLargeWhenOnlyOne = typedArray.getBoolean(attr, mShowAsLargeWhenOnlyOne);
        } else if (attr == R.styleable.NinePhotoLayout_itemWhiteSpacing) {
            mItemWhiteSpacing = typedArray.getDimensionPixelSize(attr, mItemWhiteSpacing);
        } else if (attr == R.styleable.NinePhotoLayout_placeholderDrawable) {
            mPlaceholderDrawableResId = typedArray.getResourceId(attr, mPlaceholderDrawableResId);
        } else if (attr == R.styleable.NinePhotoLayout_itemWidth) {
            mItemWidth = typedArray.getDimensionPixelSize(attr, mItemWidth);
        } else if (attr == R.styleable.NinePhotoLayout_itemSpanCount) {
            mItemSpanCount = typedArray.getInteger(attr, mItemSpanCount);
        }
    }


    private void afterInitDefaultAndCustomAttrs() {
        images = new ArrayList<>();
        mPhotoIv = new ImageView(getContext());
        mPhotoIv.setClickable(true);
        mPhotoIv.setOnClickListener(this);
        mPhotoGv = new RecyclerView(getContext());
        mPhotoGv.addItemDecoration(new GridDecoration(getContext(), mItemWhiteSpacing, getResources().getColor(R.color.white)) {
            @Override
            public boolean[] getItemSidesIsHaveOffsets(int itemPosition) {
                return new boolean[]{true, true, true, true};
            }
        });
        mPhotoAdapter = new PhotoAdapter(R.layout.item_nine_photo);
        mPhotoGv.setAdapter(mPhotoAdapter);
        gridLayoutManager = new GridLayoutManager(getContext(), ITEM_NUM_COLUMNS, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mPhotoGv.setLayoutManager(gridLayoutManager);
        mPhotoAdapter.setOnItemClickListener(this);
        addView(mPhotoIv, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mPhotoGv);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!init) {
            if (mItemWidth == 0) {
                mItemWidth = (measuredWidth - getPaddingStart() - getPaddingEnd() - (mItemSpanCount - 1) * mItemWhiteSpacing) / mItemSpanCount;
            }
            init = true;
            if (images.size() == 1 && mShowAsLargeWhenOnlyOne) {
                mPhotoGv.setVisibility(GONE);
                mPhotoAdapter.setNewData(images);
                mPhotoIv.setVisibility(VISIBLE);
                mPhotoAdapter.setNewData(images);
                int size = mItemWidth * 2 + mItemWhiteSpacing + mItemWidth / 4;
                mPhotoIv.setMaxWidth(size);
                mPhotoIv.setMaxHeight(size);
                Glide.with(getContext()).load(images.get(0))
                        .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher_round)
                                .override(size, size).dontAnimate())
                        .into(mPhotoIv);
//                BGAImage.display(mPhotoIv, mPlaceholderDrawableResId, photos.get(0), size);
            } else {
                mPhotoIv.setVisibility(GONE);
                mPhotoGv.setVisibility(VISIBLE);
                mPhotoAdapter.setNewData(images);
                ViewGroup.LayoutParams layoutParams = mPhotoGv.getLayoutParams();
                if (mItemSpanCount > 3) {
                    int itemSpanCount = images.size() < mItemSpanCount ? images.size() : mItemSpanCount;
                    gridLayoutManager.setSpanCount(itemSpanCount);
                    layoutParams.width = mItemWidth * itemSpanCount + (itemSpanCount - 1) * mItemWhiteSpacing;
                } else {
                    if (images.size() == 1) {
                        gridLayoutManager.setSpanCount(1);
                        layoutParams.width = mItemWidth;
                    } else if (images.size() == 2) {
                        gridLayoutManager.setSpanCount(2);
                        layoutParams.width = mItemWidth * 2 + mItemWhiteSpacing;
                    } else if (images.size() == 4) {
                        gridLayoutManager.setSpanCount(2);
                        layoutParams.width = mItemWidth * 2 + mItemWhiteSpacing;
                    } else {
                        gridLayoutManager.setSpanCount(3);
                        layoutParams.width = mItemWidth * 3 + 2 * mItemWhiteSpacing;
                    }
                }
                LogUtils.e(layoutParams.width + ":" + layoutParams.height);
                mPhotoGv.setLayoutParams(layoutParams);
            }
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        mCurrentClickItemPosition = position;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
    }

    @Override
    public void onClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
    }

    /**
     * 设置图片路径数据集合
     *
     * @param photos
     */
    public void setData(ArrayList<String> photos) {
        images = photos;
        if (photos.size() == 0) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);

            if (photos.size() == 1 && mShowAsLargeWhenOnlyOne) {
                mPhotoGv.setVisibility(GONE);
//                mPhotoAdapter.setNewData(photos);
                mPhotoIv.setVisibility(VISIBLE);
            } else {
                mPhotoIv.setVisibility(GONE);
                mPhotoGv.setVisibility(VISIBLE);
//                mPhotoAdapter.setNewData(photos);
            }
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
    }

    public int getItemCount() {
        return mPhotoAdapter.getItemCount();
    }

    public String getCurrentClickItem() {
        return mPhotoAdapter.getItem(mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return mCurrentClickItemPosition;
    }


    private class PhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PhotoAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            ImageView image = helper.getView(R.id.iv_item_nine_photo_photo);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SizeUtils.dp2px(mImageSize), SizeUtils.dp2px(mImageSize));
//            image.setLayoutParams(layoutParams);
            Glide.with(mContext).load(item).
                    apply(new RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher_round)
                            .override(mItemWidth, mItemWidth)
                            .centerCrop()
                            .dontAnimate())
                    .into((ImageView) image);
        }


    }

    public interface Delegate {
        void onClickNinePhotoItem(NinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);
    }
}
package com.application.anant.smartattendancemanager.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.application.anant.smartattendancemanager.R;

public class DaysViewPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    private int[] backDrops;

    private int[] days_backdrop = {R.drawable.monday_test_image_2, R.drawable.tuesday_test_image,
            R.drawable.wednesday_backdrop, R.drawable.thursday_test_image,
            R.drawable.friday_backdrop, R.drawable.saturday_backdrop, R.drawable.sunday_backdrop};

    private int[] days_backdrop_all = {R.drawable.all_subs_test};

    public DaysViewPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void assignNew(boolean isTimeTable) {
        if (!isTimeTable)
            this.backDrops = days_backdrop_all;
        else this.backDrops = days_backdrop;
    }

    @Override
    public int getCount() {
        return backDrops.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_time_table_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.days_backdrop);

        Glide.with(mContext)
                .load(backDrops[position])
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}

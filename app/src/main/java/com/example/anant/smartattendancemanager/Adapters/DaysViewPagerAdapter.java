package com.example.anant.smartattendancemanager.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.anant.smartattendancemanager.R;

public class DaysViewPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    private int[] days_backdrop = {R.drawable.monday_backdrop, R.drawable.tuesday_backdrop,
            R.drawable.wednesday_backdrop, R.drawable.thursday_backdrop,
            R.drawable.friday_backdrop, R.drawable.saturday_backdrop, R.drawable.sunday_backdrop};

    public DaysViewPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_time_table_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.days_backdrop);
        imageView.setImageResource(days_backdrop[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}

package bob.sun.mpod.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

/**
 * Created by bob.sun on 06/02/2017.
 */

public class CoverflowFragment extends Fragment implements OnTickListener {

    private ViewPager pager;
    private CoverflowPagerAdapter pagerAdapter;
    private PagerContainer pagerContainer;
    private CoverFlow flow;

    public CoverflowFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_coverflow, parent, false);
        pagerContainer = (PagerContainer) ret.findViewById(R.id.pager_container);
        pager = (ViewPager) ret.findViewById(R.id.view_pager);
        pagerAdapter = new CoverflowPagerAdapter();
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(10);
        pagerAdapter.notifyDataSetChanged();
        flow = new CoverFlow.Builder()
                .with(pager)
                .pagerMargin(0) //.pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                .scale(0.3f)
                .spaceSize(0f)
                .rotationY(30f)
                .build();
        pagerContainer.setOverlapEnabled(true);
        pager.post(new Runnable() {
            @Override public void run() {
                View view = (View) pager.getAdapter().instantiateItem(pager, 0);
                ViewCompat.setElevation(view, 8.0f);
            }
        });
        return ret;
    }

    @Override
    public void onNextTick() {
        int current = pager.getCurrentItem();
        if (!pagerAdapter.canGoNext(current)) {
            return;
        }
        current += 1;
        pager.setCurrentItem(current, true);
    }

    @Override
    public void onPreviousTick() {
        int current = pager.getCurrentItem();
        if (!pagerAdapter.canGoBack(current)) {
            return;
        }
        current -= 1;
        pager.setCurrentItem(current, true);
    }

    @Override
    public SelectionDetail getCurrentSelection() {
        return null;
    }

    class CoverflowPagerAdapter extends PagerAdapter {

        private ArrayList<String> imgs;

        public CoverflowPagerAdapter() {
            imgs = MediaLibrary.getStaticInstance(getContext()).getAllCoverUries();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View ret = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_coverflow_img, null);

            ImageView img = (ImageView) ret.findViewById(R.id.cover_image);
            Picasso.with(container.getContext())
                    .load(Uri.parse(imgs.get(position)))
                    .fit()
                    .centerCrop()
                    .into(img);
            container.addView(ret);
            return ret;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

            @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public boolean canGoNext(int current) {
            return current != imgs.size();
        }

        public boolean canGoBack(int current) {
            return current != 0;
        }
    }
}
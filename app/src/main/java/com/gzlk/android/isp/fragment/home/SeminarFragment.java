package com.gzlk.android.isp.fragment.home;

import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.home.ArchiveHomeViewHolder;
import com.gzlk.android.isp.holder.home.HomeImagesViewHolder;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;

import java.util.Arrays;

/**
 * <b>功能描述：</b>首页 - 会议<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 15:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 15:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SeminarFragment extends BaseSwipeRefreshSupportFragment {

    private SeminarAdapter mAdapter;
    private HomeImagesViewHolder homeImagesViewHolder;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        stopRefreshing();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void setTestData() {
        mAdapter.add(new Model());
        mAdapter.add(new Archive() {{
            setId("1");
            setTitle("比赛还未结束，聂卫平：AlphaGo已经赢了");
            setCreateDate("2017-05-27 11:33:21");
            setCover("http://cms-bucket.nosdn.127.net/f04579e8a87a468da7001db02a3e2e6d20170527140714.png");
            setReadNum(1187);
            setCmtNum(90);
            setColNum(3);
            setLikeNum(45);
        }});
        mAdapter.add(new Archive() {{
            setId("2");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("3");
            setTitle("主镜直径39米，欧洲极大望远镜在智利正式开建");
            setCreateDate("2017-06-02 18:19:17");
            setCover("http://cms-bucket.nosdn.127.net/e0e3e01c79054021a983e5e89a908f0f20170602182606.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("4");
            setTitle("三管齐下战超级细菌 万古霉素3.0药效提高2.5万倍");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("5");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("6");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("7");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("8");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("9");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
        mAdapter.add(new Archive() {{
            setId("10");
            setTitle("天文学家第三次发现引力波 或可解释黑洞形成");
            setCreateDate("2017-06-02 07:52:07");
            setCover("https://cms-bucket.nosdn.127.net/fb17864e78104e2e8b8149a9e72c183f20170602074949.jpeg");
        }});
    }

    private String[] images = new String[]{
            "https://img5.cache.netease.com/photo/0001/2017-06-02/CLTLCFMD00AN0001.jpg",
            "http://cms-bucket.nosdn.127.net/catchpic/8/8f/8f98fa0204ae83cbddec8f8d092f93d6.jpg",
            "https://cms-bucket.nosdn.127.net/catchpic/1/1f/1f12c0757a7a900b13fbfc40125507fd.png"
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new SeminarAdapter();
            mRecyclerView.setAdapter(mAdapter);
            setTestData();
        }
    }

    private class SeminarAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_HEADER = 0, VT_NORMAL = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_HEADER) {
                if (null == homeImagesViewHolder) {
                    homeImagesViewHolder = new HomeImagesViewHolder(itemView, SeminarFragment.this);
                    homeImagesViewHolder.addImages(Arrays.asList(images));
                }
                return homeImagesViewHolder;
            } else {
                return new ArchiveHomeViewHolder(itemView, SeminarFragment.this);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VT_HEADER : VT_NORMAL;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_HEADER ? R.layout.holder_view_home_images : R.layout.holder_view_home_seminar;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveHomeViewHolder) {
                ((ArchiveHomeViewHolder) holder).showContent((Archive) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}


package com.manbas.downmusic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.jaeger.library.StatusBarUtil;
import com.manbas.downmusic.MvpView.SongListView;
import com.manbas.downmusic.R;
import com.manbas.downmusic.adapter.SongListAdapter;
import com.manbas.downmusic.api.GetSongListApi;
import com.manbas.downmusic.base.BaseUtils;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.SongMsgBean;
import com.manbas.downmusic.presenter.SongListPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/19.
 */

public class SongListActivity extends Activity implements SongListView{

    @BindView(R.id.rv_songList)
    RecyclerView rv_songList;

    private ArrayList<SongMsgBean> mlist;
    private SongListAdapter adapter;
    private GetSongListApi getSongListApi;

    SongListPresenter songListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 设置状态栏完全透明
         */

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }

        setContentView(R.layout.songlist_activity);
        StatusBarUtil.setTranslucent(SongListActivity.this);
        ButterKnife.bind(this);
        songListPresenter=new SongListPresenter(this,this);
        mlist=new ArrayList<>();
        songListPresenter.getSongListMsg();
    }


    private void setSongList(){
        adapter=new SongListAdapter(this,mlist);
//      StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_songList.setLayoutManager(layoutManager);
        rv_songList.setAdapter(adapter);

        adapter.setOnSongItemClickListener(new SongListAdapter.OnSongItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SongMsgBean bean = mlist.get(position);
                Bundle b = new Bundle();
//                b.putString("songName", bean.getTitle());
//                b.putString("artistName", bean.getArtist_name());
//                b.putString("songImage", bean.getPic_big());
//                b.putString("songId",bean.getSong_id());
                b.putString("index",position+"");
//                b.putString("musicUrl",bean.get);
                b.putSerializable("songList",mlist);
                BaseUtils.ToAcb(SongListActivity.this, PlayingActivity.class, b);
            }
        });
    }

    @Override
    public void setSongList(ArrayList<SongMsgBean> list) {
        this.mlist=list;
        if(mlist!=null){
            setSongList();
        }
    }

    @Override
    public void toastMessage(String msg) {
        LogUtis.Log(msg);
    }
}

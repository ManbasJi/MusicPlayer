package com.manbas.downmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manbas.downmusic.R;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.SongMsgBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/19.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyHolder> {
    private Context mc;
    private ArrayList<SongMsgBean> list;
    private LayoutInflater layoutInflater;
    SimpleDateFormat format=new SimpleDateFormat("mm:ss");
    OnSongItemClickListener songItemClickListener;


    public interface OnSongItemClickListener{
        void onItemClick(View v,int position);
    }

    public SongListAdapter(Context c, ArrayList<SongMsgBean> list){
        this.mc=c;
        this.list=list;
        layoutInflater=LayoutInflater.from(mc);
    }

    public void setOnSongItemClickListener(OnSongItemClickListener listener){
        this.songItemClickListener=listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(layoutInflater.inflate(R.layout.song_hor_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        SongMsgBean bean=list.get(position);
        if(bean!=null){
            holder.tv_artistName.setText(bean.getAuthor());
            holder.tv_songName.setText(bean.getTitle());
            holder.tv_songTime.setText(format.format(new Date(bean.getFile_duration()*1000)));
            LogUtis.Log("time:"+holder.tv_songTime.getText());
            Glide.with(mc).load(bean.getPic_big()).into(holder.iv_song);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtis.Log("position | getLayoutPosition :"+position +"|"+ holder.getLayoutPosition());
                songItemClickListener.onItemClick(v,holder.getLayoutPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView iv_song;
        TextView tv_songName,tv_artistName,tv_songTime;

        public MyHolder(View itemView) {
            super(itemView);
            iv_song= (ImageView) itemView.findViewById(R.id.iv_song);
            tv_songName= (TextView) itemView.findViewById(R.id.tv_songName);
            tv_artistName= (TextView) itemView.findViewById(R.id.tv_artistName);
            tv_songTime= (TextView) itemView.findViewById(R.id.tv_songTime);
        }
    }
}

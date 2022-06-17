package com.example.wifi.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifi.R;
import com.example.wifi.database.ApData;

import java.util.ArrayList;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.InnerHolder> {

    private final ArrayList<ApData> mData;

    public ListViewAdapter(ArrayList<ApData> data) {
        this.mData = data;
    }

    /**
     * 创建条目的视图
     */
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 传入每个条目的界面
        View itemView = View.inflate(parent.getContext(), R.layout.item_list_view, null);
        return new InnerHolder(itemView);
    }

    /**
     * 绑定 holder，一般用来设置数据
     */
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mData.get(position));
    }

    /**
     * 返回条目个数
     */
    @Override
    public int getItemCount() {
        if(mData != null) {
            return mData.size();
        }
        return 0;
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {

        private final TextView mTvId;
        private final TextView mTvMac;
        private final TextView mTvRss;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            // 由 itemView找打条目视图的控件
            mTvId = itemView.findViewById(R.id.tv_id);
            mTvMac = itemView.findViewById(R.id.tv_mac);
            mTvRss = itemView.findViewById(R.id.tv_rss);
        }

        // 设置数据的方法
        public void setData(ApData apData) {
            // 开始设置数据
            String apId = "Id: " + apData.getApId();
            String apMac = "MacAddress: " + apData.getMacAddress();
            String apRss = "Rss: " + apData.getRss();
            mTvId.setText(apId);
            mTvMac.setText(apMac);
            mTvRss.setText(apRss);
        }
    }
}

package com.example.androidassignment.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidassignment.R;
import com.example.androidassignment.model.Data;
import com.example.androidassignment.utill.RecyclerViewItemEnabler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewItemEnabler {
    Context context;
    ArrayList<Data> arryList;
    ArrayList<Data> userList;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private OnLoadMoreListener onLoadMoreListener;
    private boolean isMoreLoading = true;
    private boolean mAllEnabled;

    public UserAdapter(OnLoadMoreListener onLoadMoreListener, ArrayList<Data> userList, Context context) {
        this.context = context;
        this.userList = userList;
        this.onLoadMoreListener = onLoadMoreListener;
        arryList = new ArrayList<>();
        arryList.addAll(userList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_user, viewGroup, false);
            vh = new TextViewHolder(view);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.progress_item, viewGroup, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TextViewHolder) {
            final Data result = userList.get(position);
           Glide.with(context)
                    .load(result.getAvatar())
                    .placeholder(R.drawable.place_holder)
                    .apply(RequestOptions.centerInsideTransform())
                    .skipMemoryCache(true)
                    .into(((TextViewHolder) holder).imageView)
            ;
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.imageview);
        }

    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void showLoading() {
        if (isMoreLoading && userList != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    userList.add(null);
                    notifyItemInserted(userList.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void setMore(boolean isMore) {
        this.isMoreLoading = isMore;
    }

    public void dismissLoading() {
        if (userList != null && userList.size() > 0) {
            userList.remove(userList.size() - 1);
            notifyItemRemoved(userList.size());
        }
    }

    public void addItemMore(ArrayList<Data> lst) {
        int sizeInit = userList.size();
        userList.addAll(lst);
        notifyItemRangeChanged(sizeInit, userList.size());
    }

    @Override
    public boolean getItemEnabled(int position) {
        return false;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setEnabled(isAllItemsEnabled());
    }

    public void setAllItemsEnabled(boolean enable) {
        mAllEnabled = enable;

        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        return userList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public boolean isAllItemsEnabled() {
        return mAllEnabled;
    }

}
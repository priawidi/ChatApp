package com.mobcom.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobcom.chatapp.R;
import com.mobcom.chatapp.model.MainModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "MessageAdapter";

    List<MainModel> mMessage = new ArrayList<>() ;

    public MessageAdapter(List<MainModel> message){
        this.mMessage = message;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.getTv_body().setText(mMessage.get(position).getNotification().getBody());
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView tv_title,tv_body;
        public ViewHolder(View v) {
            super(v);

            tv_title = v.findViewById(R.id.tv_title);
            tv_body = v.findViewById(R.id.tv_body);


        }
        public TextView getTv_body() {
            return tv_body;
        }

    }
}

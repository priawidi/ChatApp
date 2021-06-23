package com.mobcom.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobcom.chatapp.R;
import com.mobcom.chatapp.model.MainModel;
import com.mobcom.chatapp.model.Read;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "MessageAdapter";
    private String token;
    private ArrayList<Read> listMessage;

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private final TextView tv_body;
        public ViewHolder(View v) {
            super(v);

            //tv_title = v.findViewById(R.id.tv_title);
            tv_body = v.findViewById(R.id.tv_body);


        }

        public TextView getTextView(){
            return tv_body;
        }

    }

//    class ViewHolderReceiver extends RecyclerView.ViewHolder{
//
//        TextView tv_body_receiver;
//
//        public ViewHolderReceiver(View v) {
//            super(v);
//
//            tv_body_receiver = v.findViewById(R.id.tv_body_receiver);
//        }
//    }

    public MessageAdapter(ArrayList<Read> listMessage, String token ){

        this.listMessage = listMessage;
        this.token = token;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        //ViewHolder viewHolder = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.getTextView().setText(listMessage.get(position).getMessage());
        //viewHolder.tv_title.setText(listMessage.get(position));
    }

    @Override
    public int getItemCount() {

        return listMessage.size();
    }


}

package com.mobcom.chatapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobcom.chatapp.R;
import com.mobcom.chatapp.model.Read;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String DeviceToken1 = "cuPjhyGPSwK5jCs90DD6OB:APA91bESvh7UOhIKwbjLDDaD0p2AsJV3L14fVMQO_r32R3v_YCg4C-tdhsMElybADmWUSjt_V4_vMDoM8yYE80r39ENy_hvHuRdY8GLutEdLPRxfC41TDcM9x07QzkFA8hUDRBDo053X";
    private String DeviceToken2 = "f1bKhjpyRZi7bbE0JvkdpG:APA91bGwzU-XM_8WEASfrW0fFJKQiEqv52fIelr6tEJZJwcuIs4GVG7igBOPnwdHpVOiE-DCQY2wxhbVnHbyBRmeHYB3rV8jD-q4RUNvGIjN2XGGEXyMb--c0jjKBEv3H7tNPlD8pmEV";
    private String DeviceName;
    private static final String TAG = "MessageAdapter";
    private String token;
    private ArrayList<Read> listMessage;

    public MessageAdapter(ArrayList<Read> listMessage, String token ){

        this.listMessage = listMessage;
        this.token = token;
    }

    public class ViewHolderLeft extends RecyclerView.ViewHolder  {

        private final TextView tv_body_left, tv_name_left;
        public ViewHolderLeft(View v) {
            super(v);

            //tv_title = v.findViewById(R.id.tv_title);
            tv_body_left = v.findViewById(R.id.tv_body_left);
            tv_name_left = v.findViewById(R.id.tv_name_left);

        }

        public TextView getTv_body_left(){
            return tv_body_left;
        }
        public TextView getTv_name_left(){
            return tv_name_left;
        }


    }

    public class ViewHolderRight extends RecyclerView.ViewHolder  {

        private final TextView tv_body_right, tv_name_right;
        public ViewHolderRight(View v) {
            super(v);
            //tv_title = v.findViewById(R.id.tv_title);
            tv_body_right = v.findViewById(R.id.tv_body_right);
            tv_name_right = v.findViewById(R.id.tv_name_right);

        }

        public TextView getTv_body_right(){
            return tv_body_right;
        }
        public TextView getTv_name_right(){
            return tv_name_right;
        }


    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch(viewType){

            case 0:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item_left, viewGroup, false);
                return new ViewHolderLeft(v);

            case 1:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item_right, viewGroup, false);
                return new ViewHolderRight(v);

        }return null;
    }

    @Override
    public int getItemViewType(int position) {
        int place;
        if (!listMessage.get(position).getFrom().equals(token)){
            place = 0;
        }
        else {
            place = 1;
        }
        return place;
    }
    private String getDeviceNameLeft(){
        Log.d(TAG, "getDeviceName Left: "+ DeviceName);
        Log.d(TAG, "getDeviceName: "+ token);
        if(!token.equals(DeviceToken1)){
            return DeviceName = "LG";
        }
        else{
            return DeviceName = "Poco";
        }
    }
    private String getDeviceNameRight(){
        Log.d(TAG, "getDeviceName Right: "+ DeviceName);
        Log.d(TAG, "getDeviceName: "+ token);
        if(token.equals(DeviceToken1)){
            return DeviceName = "LG";
        }
        else{
            return DeviceName = "Poco";
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        switch (viewHolder.getItemViewType()){
            case 0 :
                ViewHolderLeft viewHolderLeft = (ViewHolderLeft) viewHolder;
                viewHolderLeft.getTv_body_left().setText(listMessage.get(position).getMessage());
                viewHolderLeft.getTv_name_left().setText(getDeviceNameLeft());
                Log.d(TAG, "CASE LEFT CHAT: " + token);
                Log.d(TAG, "CASE LEFT CHAT: " + DeviceName);

                break;

            case 1 :
                ViewHolderRight viewHolderRight = (ViewHolderRight) viewHolder;
                viewHolderRight.getTv_body_right().setText(listMessage.get(position).getMessage());
                viewHolderRight.getTv_name_right().setText(getDeviceNameRight());

                Log.d(TAG, "CASE RIGHT CHAT: " + token);
                Log.d(TAG, "CASE RIGHT CHAT: " + DeviceName);

                break;

        }
    }

    @Override
    public int getItemCount() {

        return listMessage.size();
    }


}

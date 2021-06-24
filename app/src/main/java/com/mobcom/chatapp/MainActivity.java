package com.mobcom.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobcom.chatapp.MyFirebaseMessagingService;
import com.mobcom.chatapp.adapter.MessageAdapter;
import com.mobcom.chatapp.api.ApiClient;
import com.mobcom.chatapp.api.ApiInterface;
import com.mobcom.chatapp.model.Data;
import com.mobcom.chatapp.model.MainModel;
import com.mobcom.chatapp.model.Notification;
import com.mobcom.chatapp.model.Read;
import com.mobcom.chatapp.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity  {

    private TextView tv_title, tv_body, tv_token;
    private EditText et_message;
    private ImageButton btn_send;
    public String token = "", msg;
    private String Topics ;
    private String Topic = "/topics/" + Topics;
    private String DeviceToken1 = "cuPjhyGPSwK5jCs90DD6OB:APA91bESvh7UOhIKwbjLDDaD0p2AsJV3L14fVMQO_r32R3v_YCg4C-tdhsMElybADmWUSjt_V4_vMDoM8yYE80r39ENy_hvHuRdY8GLutEdLPRxfC41TDcM9x07QzkFA8hUDRBDo053X";
    private String DeviceToken2 = "f1bKhjpyRZi7bbE0JvkdpG:APA91bGwzU-XM_8WEASfrW0fFJKQiEqv52fIelr6tEJZJwcuIs4GVG7igBOPnwdHpVOiE-DCQY2wxhbVnHbyBRmeHYB3rV8jD-q4RUNvGIjN2XGGEXyMb--c0jjKBEv3H7tNPlD8pmEV";
    private static final String TAG = "MainActivity";
    private MessageAdapter messageAdapter;
    private ApiInterface apiService;
    private DatabaseReference myRef;

    ArrayList<Read> read = new ArrayList<>();

    MyFirebaseMessagingService myFirebaseMessagingService;
    ArrayAdapter<String> arrayAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ListView listView;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Chat App Firebase");

        //Edit Text Box
        et_message = findViewById(R.id.et_message);

        //Button Send Message
        btn_send = findViewById(R.id.btn_send);

//        //ListView
//        listView = findViewById(R.id.listView);
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listMessage);
//        listView.setAdapter(arrayAdapter);


        //RecyclerView
        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        myRef = FirebaseDatabase.getInstance("https://chatapp-10c9a-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        request_topic();
        getDeviceToken();



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_message.getText().toString();
                if (message.length() > 0){
                    getMessageFromUser(Topics);
                    //sendMessageToUser(et_message.getText().toString(), "ChatApp" );
                    sendMessageNotificationToUser(Topic, message, "ChatApp" );
                    et_message.setText("");


                    Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void getMessageFromUser(String Topic){
        Log.d(TAG, "getMessageFromUser Initiated" + Topic);
        Query query = myRef.child(Topic);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Read msg_add = snapshot.getValue(Read.class);
                msg_add.setMessage_id(snapshot.getKey());

                read.add(msg_add);
                messageAdapter = new MessageAdapter(read, token);
                recyclerView.setAdapter(messageAdapter);
                int position = messageAdapter.getItemCount()-1;
                recyclerView.scrollToPosition(position);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void sendMessageNotificationToUser(String Topic, String message, String title) {
        String from = token;
        String to = Topic;
        HashMap<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Message", msg);

        MainModel mainModel = new MainModel(Topic, new Notification(message, title), data);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<Response> responseBodyCall = apiService.SendMessage(mainModel);
        responseBodyCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.d(TAG, "Successfully send notification");

                WriteToDB(response.body().getMessage_id(), from, to, message);

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
    }

    public void WriteToDB(long message_id, String from, String to, String message) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("message_id", message_id);
        data.put("from", from);
        data.put("to", to);
        data.put("message", message);

        myRef.child(Topic).child(Long.toString(message_id)).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Write_DB", "DB Sent");
                Log.d("Write_DB_From", from);
                Log.d("Write_DB_To", to);
                Log.d("Write_DB_Message", message);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Write_DB", "DB Failure");
                    }
                });
    }

    private void request_topic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Topic");
        final EditText input_field = new EditText(this);
        builder.setView(input_field);
        builder.setPositiveButton("OK ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Topics = "/topics/" + input_field.getText().toString();
                getDeviceTopic(Topics);
                getMessageFromUser(Topics);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_topic();
            }
        });
        builder.show();
    }
    private void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                token = task.getResult();

                // Log and toast
                msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeviceTopic(String Topics) {
        FirebaseMessaging.getInstance().subscribeToTopic(Topics)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        msg = "Subscribed to Topic : " + Topics;
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

//    private String chat_msg;
//    private void append_chat_conversatin(DataSnapshot dataSnapshot) {
//        Iterator i = dataSnapshot.getChildren().iterator();
//        while (i.hasNext())
//        {
//            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
//
//            tv_body.append(chat_msg +"\n");
//
//
//        }
//    }

//    private class Firebase extends FirebaseMessagingService{
//
//        public void onMessageReceived(RemoteMessage remoteMessage) {
//            final Map<String, String> ChatData;
//            Log.d(TAG, "From: " + remoteMessage.getFrom());
//            // Check if message contains a data payload.
//            if (remoteMessage.getData().size() > 0) {
//                ChatData = remoteMessage.getData();
//                Log.d(TAG, "Message data payload: " + ChatData);
//
////            if (/* Check if data needs to be processed by long running job */ true) {
////                // For long-running tasks (10 seconds or more) use WorkManager.
////                scheduleJob();
////            } else {
////                // Handle message within 10 seconds
////                handleNow();
////            }
//            Chat = ChatData;
//            }
//            // Check if message contains a notification payload.
//            if (remoteMessage.getNotification() != null) {
//                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            }
//
//            // Also if you intend on generating your own notifications as a result of a received FCM
//            // message, here is where that should be initiated. See sendNotification method below.
//        }
//
//    }

}
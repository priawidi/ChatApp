package com.mobcom.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
    private String Topics = "Business";
    private String Topic = "/topics/" + Topics;
    private String DeviceToken1 = "fe01yHY4SPqKgiY5nSucIW:APA91bEiwGGIIOYa00KAJR_EAecJK2gdupuHLPaPppKsb8sKQ7o4cidM_iYQAiFCuBZOibyBa469Ag5bcz1yDv4zgG8lj5-3SPXpQO3YeBiUMgFGu-Hp32hbV4yG15srwUk3_jcLWaKA";
    private String DeviceToken2 = "dUHxvCJtRRCqJWygL3gHwD:APA91bHqfNTGsURctoauDpYrbAM0eSXnGgx7K_xSUQlmIV0USCoqHYBzgWJi01bWtC-WMbvVRFEX7abiKDKzYyfH2Yu3yKsCSMC3QWmsYzLUgw70ZzVa7P_e_b-FiCxWbMlb9avsfhHC";
    private static final String TAG = "MainActivity";
    private MessageAdapter messageAdapter;
    private ApiInterface apiService;
    private DatabaseReference myRef;
    Map Chat;

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

        getDeviceToken();
        getDeviceTopic(Topics);
        getMessageFromUser();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_message.getText().toString();
                if (message.length() > 0){
                    sendMessageToUser(et_message.getText().toString());
                    sendNotificationToUser(Topic, message, "Judul" );
                    et_message.setText("");
                }

            }
        });



    }

    private void getMessageFromUser(){
        Query messageTopic_query = myRef.child(Topic);
        messageTopic_query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: ");
                Read msg_topic = snapshot.getValue(Read.class);
                msg_topic.setMessage_id(snapshot.getKey());

                read.add(msg_topic);
                for(int i = 0; i < read.size(); i++) {
                    Log.d(TAG, "listMessage[" + i + "] = " + read.get(i));
                }

                messageAdapter = new MessageAdapter(read, token);
                recyclerView.setAdapter(messageAdapter);
                int position = messageAdapter.getItemCount()-1;
                Log.d(TAG, "onChildAdded: adapter position" + position);
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

    private void sendMessageToUser(String msg){
        String from = token;
        String to = Topic;

        HashMap<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Message", msg);

        Notification notification = new Notification(msg, "Judul");

        MainModel message = new MainModel(Topic, notification, data);

        Call<Response> call = apiService.SendMessage(message);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Code: " + response.code());
                    Log.d(TAG, "Message"+ response.toString());
                    return;
                }


                Response sr = response.body();
                Log.d("Sukses Kirim", Long.toString(sr.getMessage_id()));
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("message");
//
//                myRef.setValue("Hello, World!");

                SendToDB(sr.getMessage_id(), from, to, msg);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

    }

    public void SendToDB(long message_id, String from, String to, String message) {
        Log.d("DB CRUD", "Masuk Ke Fungsi SendToDB");


        HashMap<String, Object> data = new HashMap<>();
//        data.put("message_id", message_id);
        data.put("from", from);
        data.put("to", to);
        data.put("message", msg);
        //data.put("timestamp", timestamp);

        myRef.child(Topic).child(Long.toString(message_id)).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Write_DB", "Berhasil Menulis Ke DB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Write_DB", "Gagal Menulis Ke DB");
                    }
                });
    }



    private void sendNotificationToUser(String Topic, String message, String title) {
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

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
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
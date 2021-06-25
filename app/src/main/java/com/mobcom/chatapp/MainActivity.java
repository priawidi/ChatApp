package com.mobcom.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.mobcom.chatapp.adapter.MessageAdapter;
import com.mobcom.chatapp.api.ApiClient;
import com.mobcom.chatapp.api.ApiInterface;
import com.mobcom.chatapp.model.MainModel;
import com.mobcom.chatapp.model.Notification;
import com.mobcom.chatapp.model.Read;
import com.mobcom.chatapp.model.Response;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    private TextView tv_title, tv_body, tv_token;
    private EditText et_message;
    private ImageButton btn_send, btn_topic;
    public String token = "", msg;
    private String time;
    private String Topics ;
    private String Topic ;
    private String DeviceToken1 = "fe01yHY4SPqKgiY5nSucIW:APA91bEiwGGIIOYa00KAJR_EAecJK2gdupuHLPaPppKsb8sKQ7o4cidM_iYQAiFCuBZOibyBa469Ag5bcz1yDv4zgG8lj5-3SPXpQO3YeBiUMgFGu-Hp32hbV4yG15srwUk3_jcLWaKA";
    private String DeviceToken2 = "dUHxvCJtRRCqJWygL3gHwD:APA91bHqfNTGsURctoauDpYrbAM0eSXnGgx7K_xSUQlmIV0USCoqHYBzgWJi01bWtC-WMbvVRFEX7abiKDKzYyfH2Yu3yKsCSMC3QWmsYzLUgw70ZzVa7P_e_b-FiCxWbMlb9avsfhHC";
    private static final String TAG = "MainActivity";
    private MessageAdapter messageAdapter;
    private ApiInterface apiService;
    private DatabaseReference myRef;

    String [] TopicArray = {"001", "002", "003", "004", "005", "006", "007", "008"};
    RemoteMessage remoteMessage;
    Map<String, String> Notif;
    Spinner spin;
    ArrayList<Read> read = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Chat App Firebase");
        //Edit Text Box
        et_message = findViewById(R.id.et_message);
        //Button Send Message
        btn_send = findViewById(R.id.btn_send);
        //RecyclerView
        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        myRef = FirebaseDatabase.getInstance("https://chatapp-10c9a-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();


        //Spinner Topic

        spin = (Spinner) findViewById(R.id.spinner_topic);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TopicArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        getDeviceToken();
        getDateTime();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_message.getText().toString();
                if (message.length() > 0){
                    sendMessageNotificationToUser(Topic, message, "ChatApp" );
                    et_message.setText("");
                    Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void getMessage(String topic) {
        Log.d(TAG, "getMessage Initiated" + topic);
        Query query =  myRef.child(topic).orderByChild("time");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

                Read msg_add = snapshot.getValue(Read.class);
                msg_add.setMessage_id(snapshot.getKey());

                read.add(msg_add);
                messageAdapter = new MessageAdapter(read, token);
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: Success");

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

    public void WriteToDB(long message_id, String from, String to, String message) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("from", from);
        data.put("to", to);
        data.put("message", message);
        data.put("time", time);

        myRef.child(Topic).child(Long.toString(message_id)).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Write_DB", "Succeed");
                        Log.d("Write_DB From : ", from);
                        Log.d("Write_DB To : ", to);
                        Log.d("Write_DB Message : ", message);
                        Log.d("Write_DB Time : ", time);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Write_DB", "Failure");
                    }
                });
    }



    private void sendMessageNotificationToUser(String Topic, String message, String title) {
        String from = token;
        String to = Topic;
        HashMap<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Message", message);
        data.put("Time", time);

        MainModel mainModel = new MainModel(Topic, new Notification(message, title), data);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<Response> responseBodyCall = apiService.SendMessage(mainModel);
        responseBodyCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                Log.d(TAG, "Successfully send notification");
                WriteToDB(response.body().getMessage_id(), from, to, message);
                Log.d(TAG, "Write message to DB");

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d(TAG, "FAILED SEND MESSAGE");
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDateTime(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        time = fmt.format(currentTime);
    }

    private void FirebaseMessagingService(){
        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
        myFirebaseMessagingService.onMessageReceived(remoteMessage);
        Notif = remoteMessage.getData();
        //tv_body = findViewById(R.id.test_view);
        //tv_body.setText(Notif);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Topics = spin.getSelectedItem().toString();
        Topic = "/topics/" + Topics;
        getDeviceTopic(Topics);
        read.clear();
        getMessage(Topic);
        Log.d(TAG, "onClick: "+ Topic);
        Toast.makeText(getApplicationContext(), "Selected Topic: "+TopicArray[position] ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
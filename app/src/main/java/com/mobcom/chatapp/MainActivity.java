package com.mobcom.chatapp;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    ArrayList<String> listMessage = new ArrayList<>();

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

        //Chat Message
        //tv_title = findViewById(R.id.tv_title);
        //tv_body = findViewById(R.id.tv_body);
        //tv_token=findViewById(R.id.tv_token);

        //Edit Text Box
        et_message = findViewById(R.id.et_message);

        //Button Send Message
        btn_send = findViewById(R.id.btn_send);

        //ListView
//        listView = findViewById(R.id.listView);
//        arrayAdapter = new ArrayAdapter<String>(this, R.layout.message_item, listMessage);
//        listView.setAdapter(arrayAdapter);


        //RecyclerView
//        recyclerView = findViewById(R.id.rv_chat);
//        linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        messageAdapter = new MessageAdapter(listMessage);
//        recyclerView.setAdapter(messageAdapter);

        getDeviceToken();
        getDeviceTopic(Topics);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello BICIS");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Log.d(TAG, "Value is: "  + value);
            }

            @Override
            public void onCancelled( DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_message.getText().toString();
                if (message.length() > 0){
                    listMessage.add(message);
                    //arrayAdapter.add(message);
                    sendNotificationToUser(Topic, message, "Judul", "ToT");
                    et_message.setText("");
                }

            }
        });
    }


    private void sendNotificationToUser(String Topic, String message, String title, String id) {
        MainModel mainModel = new MainModel(Topic, new Notification(message, title), new Data(id));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.SendMessage(mainModel);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int index = 0;
                //listMessage.add(mainModel);
//                ArrayList<MainModel> Chatlist = new ArrayList<>();
//                for (int i = 0; i < listMessage.size(); i++){
//                    Chatlist.add(listMessage.get(i));
//                }
//                messageAdapter = new MessageAdapter(Chatlist);
//                recyclerView.setAdapter(messageAdapter);

                Log.d(TAG, "Successfully send notification");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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

    @Override
    public void onClick(View v) {

    }
}
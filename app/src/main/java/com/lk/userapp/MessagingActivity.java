package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Adepter.MessageListAdapter;
import com.lk.userapp.Holder.MessageHolder;
import com.lk.userapp.Holder.ProductHolder;
import com.lk.userapp.Model.Chat;
import com.lk.userapp.Model.Message;
import com.lk.userapp.Model.Product;
import com.lk.userapp.Model.Users;
import com.lk.userapp.fcmHelper.FCmClient;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    private EditText message;
    private Button sendBtn;
    private CollectionReference messageCollection;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String saveCurrentDate;
    private String saveCurrentTime;
    private RecyclerView recyclerchat;
    private ArrayList<Message> messageArrayList = new ArrayList<>();
    private MessageListAdapter mMessageAdapter;
    FCmClient myfcmClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        message = findViewById(R.id.edit_gchat_message);
        sendBtn = findViewById(R.id.button_gchat_send);
        recyclerchat = findViewById(R.id.recycler_gchat);
        recyclerchat.setLayoutManager(new LinearLayoutManager(this));

        messageCollection = db.collection("Message");

        SharedPreferences spa = PreferenceManager.getDefaultSharedPreferences(this);
        String mail = spa.getString("email", "empty");
        String docid = spa.getString("docid", "empty");

        setAdepter(docid);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(docid);
            }
        });
    }

    private void setAdepter(String docid) {


        messageCollection.whereEqualTo("userDocId",docid).whereGreaterThanOrEqualTo("listCount",1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                messageArrayList.clear();

                    if (!value.isEmpty()) {
                        for (DocumentSnapshot list : value.getDocuments()) {
                            Message message = list.toObject(Message.class);
                            messageArrayList.add(message);
                            mMessageAdapter = new MessageListAdapter(MessagingActivity.this, messageArrayList);
                            recyclerchat.setAdapter(mMessageAdapter);
                        }


                }


            }
        });


    }

    private void sendMessage(String docid) {

        db.collection("Chat").whereEqualTo("userDoc",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    String customer_id = d.getId();
                    db.collection("Chat").document(customer_id).update("datetime",new Date(),"status","delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            succesMessage(docid);
                        }
                    });

                }else{
                    Chat newC = new Chat();
                    newC.setUserDoc(docid);
                    newC.setDatetime(new Date());
                    newC.setStatus("delivered");
                    db.collection("Chat").add(newC).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            succesMessage(docid);
                        }
                    });

                }
            }
        });

    }

    private void succesMessage(String docid) {
        messageCollection.whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.getDocuments().size();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MM:dd:yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());

                Message messageNew = new Message();
                messageNew.setUserDocId(docid);
                messageNew.setMessage(message.getText().toString());
                messageNew.setDate(saveCurrentDate);
                messageNew.setTime(saveCurrentTime);
                messageNew.setListCount(size+1);
                messageNew.setStatus("me");

                messageCollection.add(messageNew).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        db.collection("Admin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                if (documents.size()>0){
                                    DocumentSnapshot documentSnapshot = documents.get(0);
                                    String fcmToken = documentSnapshot.getString("fcmToken");
                                    db.collection("Users").document(docid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Users users = documentSnapshot.toObject(Users.class);
                                            Log.d("usssss",users.getEmail());
                                            myfcmClient.execute(fcmToken,users.getEmail(),message.getText().toString());
                                            message.setText("");
                                            Toast.makeText(MessagingActivity.this, "Sent Message", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myfcmClient = new FCmClient();
    }
}
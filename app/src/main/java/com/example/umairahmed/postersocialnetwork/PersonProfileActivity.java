package com.example.umairahmed.postersocialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestButton, DeclineFriendRequestButton;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef;
    private FirebaseAuth mAuth;

    private String senderUserId, recieverUserId, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        InitializeFields();

        UsersRef.child(recieverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationship status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: " + myDOB);
                    userCountry.setText("Country: " + myCountry);
                    userGender.setText("Gender: " + myGender);
                    userRelation.setText("Relationship: " + myRelationStatus);


                    MaintananceofButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);


        if(!senderUserId.equals(recieverUserId))
        {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SendFriendRequestButton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends")){

                        SendFriendRequestToaPerson();
                    }

                    if(CURRENT_STATE.equals("request_sent")){

                        CancelFriendRequest();
                    }

                    if(CURRENT_STATE.equals("request_received")){

                        AcceptFrinedRequest();
                    }

                    if(CURRENT_STATE.equals("friends")){

                        UnFriendAnExixtingFriend();
                    }
                }
            });
        }
        else
        {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void UnFriendAnExixtingFriend() {

        FriendsRef.child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FriendsRef.child(recieverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){}

                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            SendFriendRequestButton.setText("Send Friend Request");

                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFrinedRequest() {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        FriendsRef.child(senderUserId).child(recieverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FriendsRef.child(recieverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                FriendRequestRef.child(senderUserId).child(recieverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    FriendRequestRef.child(recieverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful()){}

                                                                                    SendFriendRequestButton.setEnabled(true);
                                                                                    CURRENT_STATE = "friends";
                                                                                    SendFriendRequestButton.setText("Unfriend This Person");

                                                                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineFriendRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void CancelFriendRequest() {

        FriendRequestRef.child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FriendRequestRef.child(recieverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){}

                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            SendFriendRequestButton.setText("Send Friend Request");

                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceofButtons() {

        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(recieverUserId)){

                            String request_type = dataSnapshot.child(recieverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")){

                                CURRENT_STATE = "request_sent";
                                SendFriendRequestButton.setText("Cancel Friend Request");

                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);

                            }

                            else if(request_type.equals("received")){

                                CURRENT_STATE = "request_received";
                                SendFriendRequestButton.setText("Accept Friend Request");

                                DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestButton.setEnabled(true);

                                DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }

                        else{
                            FriendsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.hasChild(recieverUserId)){

                                                CURRENT_STATE =  "friends";
                                                SendFriendRequestButton.setText("Unfriend This Person");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void SendFriendRequestToaPerson() {

        FriendRequestRef.child(senderUserId).child(recieverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FriendRequestRef.child(recieverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){}

                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "request_sent";
                                            SendFriendRequestButton.setText("Cancel Friend Request");

                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitializeFields() {

        userName = findViewById(R.id.person_username);
        userProfName = findViewById(R.id.person_full_name);
        userStatus = findViewById(R.id.person_profile_status);
        userCountry = findViewById(R.id.person_country);
        userGender = findViewById(R.id.person_gender);
        userRelation = findViewById(R.id.person_relationship_status);
        userDOB = findViewById(R.id.person_dob);
        userProfileImage = findViewById(R.id.person_profile_pic);

        SendFriendRequestButton = findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRequestButton = findViewById(R.id.person_decline_friend_request);

        CURRENT_STATE = "not_friends";
    }
}


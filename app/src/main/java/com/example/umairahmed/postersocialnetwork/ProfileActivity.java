package com.example.umairahmed.postersocialnetwork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, FriendsRef, PostsRef;
    private FirebaseAuth mAuth;

    private Button MyPosts, MyFriends;


    private String currentUserId;
    private int countFriends = 0, countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        userName = findViewById(R.id.my_username);
        userProfName = findViewById(R.id.my_profile_full_name);
        userStatus = findViewById(R.id.my_profile_status);
        userCountry = findViewById(R.id.my_country);
        userGender = findViewById(R.id.my_gender);
        userRelation = findViewById(R.id.my_relationship_status);
        userDOB = findViewById(R.id.my_dob);
        userProfileImage = findViewById(R.id.my_profile_pic);
        MyFriends = findViewById(R.id.my_friends_button);
        MyPosts = findViewById(R.id.my_post_button);




        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToFriendsActivity();
            }
        });

        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToMyPostsActivity();
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            MyPosts.setText(Integer.toString(countPosts) + "  Posts");
                        }
                        else{

                            MyPosts.setText("0  Posts");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        FriendsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    countFriends = (int) dataSnapshot.getChildrenCount();
                    MyFriends.setText(Integer.toString(countFriends) + "  Friends");
                }

                else {

                    MyFriends.setText("0  Friends");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        profileUserRef.addValueEventListener(new ValueEventListener() {
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

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(ProfileActivity.this,FriendsActivity.class);
        startActivity(friendsIntent);
    }

    private void SendUserToMyPostsActivity() {
        Intent friendsIntent = new Intent(ProfileActivity.this,MyPostsActivity.class);
        startActivity(friendsIntent);
    }
}

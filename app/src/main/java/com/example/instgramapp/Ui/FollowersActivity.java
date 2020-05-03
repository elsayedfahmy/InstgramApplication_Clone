package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.instgramapp.Adapter.UserAdapter;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FollowersActivity extends AppCompatActivity {

    List<String>id_list;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> mUser_list;

    String id,title,storyid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("Title");
        storyid=intent.getStringExtra("storyid");

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mUser_list=new ArrayList<>();
        userAdapter=new UserAdapter(this,mUser_list,false);
        recyclerView.setAdapter(userAdapter);

        id_list=new ArrayList<>();
       switch (title)
       {
           case "followers":
               goto_Followers();

               break;
           case "Following":
               goto_Following();
               break;
           case "likes":
               goto_likes();
               break;
           case "views":
               getviews();
               break;

       }



    }
    private void goto_likes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    id_list.add(snapshot.getKey());

                }
                showdata();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void goto_Followers()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow")
                .child(id)
                .child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    id_list.add(snapshot.getKey());

                }
                showdata();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void goto_Following()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow")
                .child(id)
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                //    Toast.makeText(FollowersActivity.this,""+snapshot.getKey(),Toast.LENGTH_LONG).show();
                    id_list.add(snapshot.getKey());

                }
                showdata();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

private void showdata()
{
    Log.d("showdata","in showdata  >>>>>>>");
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mUser_list.clear();
            for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                User user=snapshot.getValue(User.class);
                for(String id:id_list)
                {if (user.getId().equals(id))
                {
                    mUser_list.add(user);
                }

                }
            }
            userAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}


    private void getviews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(id).child(storyid)
                .child("views");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    id_list.add(snapshot.getKey());

                }
                showdata();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





}

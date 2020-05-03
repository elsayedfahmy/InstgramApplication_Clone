package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Adapter.CommentAdapter;
import com.example.instgramapp.Model.Comment;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    ImageView profile_image;
    EditText addcomment;
    TextView post;

    RecyclerView recyclerView_comments;
    List<Comment> mcommentslist;
    CommentAdapter commentAdapter;


    String postid,publisherid;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");
        publisherid=intent.getStringExtra("publisherid");


        recyclerView_comments=findViewById(R.id.recycler_ViewComments);
        recyclerView_comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView_comments.setLayoutManager(linearLayoutManager);

        mcommentslist=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,mcommentslist,postid);
        recyclerView_comments.setAdapter(commentAdapter);




        profile_image =findViewById(R.id.image_profile);
        addcomment =findViewById(R.id.add_comment);
        post =findViewById(R.id.post);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this,"You cant entered comment",Toast.LENGTH_LONG).show();
                }
                else Addcomment();
            }
        });

        getImage();
        readcomments();





    }

    private void Addcomment(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        String commentid=reference.push().getKey();
        HashMap<String, Object>hashMap=new HashMap<>();
        hashMap.put("comment",addcomment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());
        hashMap.put("commentid",commentid);

        reference.child(commentid).setValue(hashMap);
        addnotifagation();
        addcomment.setText("");

    }
    private void getImage() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private  void readcomments()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mcommentslist.clear();
                for (DataSnapshot snaphost:dataSnapshot.getChildren()) {
                    Comment comment=snaphost.getValue(Comment.class);
                    mcommentslist.add(comment);
                }
                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private  void addnotifagation()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifagations").child(publisherid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","commented: "+addcomment.getText().toString());
        hashMap.put("postid",postid);
        hashMap.put("ispost","true");
        reference.push().setValue(hashMap);
    }




    }
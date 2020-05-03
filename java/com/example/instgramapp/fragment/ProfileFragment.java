package com.example.instgramapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Adapter.MyPhotoAdapter;
import com.example.instgramapp.Ui.EditeProfileActivity;
import com.example.instgramapp.Ui.FollowersActivity;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.Ui.OptionActivity;
import com.example.instgramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class ProfileFragment extends Fragment {

    ImageView profile_image,option;
    TextView txt_username,txt_posts,txt_followers,txt_following,txt_bio,txt_fullname;
    ImageButton btn_myphoto,btn_savedphoto;
    Button btn_editeprofile;

    FirebaseUser firebaseUser;
    String profile_id;
    List<String> mysaves;

    RecyclerView recyclerView_myPotsPhoto;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> mpostslist;

    RecyclerView recyclerView_savedphoto;
    MyPhotoAdapter mSavedpostAdapter;
    List<Post> mSavedpostslist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        final SharedPreferences editor=getContext().getSharedPreferences("PREFS",MODE_PRIVATE);
       profile_id= editor.getString("profile_Id","none");

        profile_image=view.findViewById(R.id.image_profile);
        btn_editeprofile=view.findViewById(R.id.edit_profile);
        txt_username=view.findViewById(R.id.username);
        txt_posts=view.findViewById(R.id.posts);
        txt_followers=view.findViewById(R.id.followers);
        txt_following=view.findViewById(R.id.following);
        txt_bio=view.findViewById(R.id.bio);
        txt_fullname=view.findViewById(R.id.fullname);
        btn_myphoto=view.findViewById(R.id.my_photo);
        btn_savedphoto=view.findViewById(R.id.saved_photo);
        option=view.findViewById(R.id.image_option);


        recyclerView_myPotsPhoto=view.findViewById(R.id.recyclerView_myphoto);
        recyclerView_myPotsPhoto.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        recyclerView_myPotsPhoto.setLayoutManager(linearLayoutManager);
        mpostslist=new ArrayList<>();
        myPhotoAdapter=new MyPhotoAdapter(getContext(),mpostslist);
        recyclerView_myPotsPhoto.setAdapter(myPhotoAdapter);

        recyclerView_savedphoto=view.findViewById(R.id.recyclerView_savedphoto);
        recyclerView_savedphoto.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saved=new GridLayoutManager(getContext(),3);
        recyclerView_savedphoto.setLayoutManager(linearLayoutManager_saved);
        mSavedpostslist=new ArrayList<>();
        mSavedpostAdapter=new MyPhotoAdapter(getContext(),mSavedpostslist);
        recyclerView_savedphoto.setAdapter(mSavedpostAdapter);

        recyclerView_myPotsPhoto.setVisibility(View.VISIBLE);
        recyclerView_savedphoto.setVisibility(View.GONE);

       getprofileifo();
        getfolloewers();
        nrposts();
        getMyPotoPost();
        Mysaves();

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionActivity.class));
            }
        });
        txt_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profile_id);
                intent.putExtra("Title","followers");
                startActivity(intent);
            }
        });
        txt_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profile_id);
                intent.putExtra("Title","Following");
                startActivity(intent);
            }
        });


       // if (profile_id==firebaseUser.getUid())
        if (profile_id.equals(firebaseUser.getUid())){
            btn_editeprofile.setText("edit profile");
            btn_savedphoto.setVisibility(View.VISIBLE);
        }
        else {
            checkfolloew();
            btn_savedphoto.setVisibility(View.GONE);
        }


        btn_editeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=btn_editeprofile.getText().toString();
                if (text.equals("edit profile")){
                   startActivity(new Intent(getContext(), EditeProfileActivity.class));
                }else if (text.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                            .child("Following").child(profile_id).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(profile_id)
                            .child("followers").child( firebaseUser.getUid()).setValue(true);

                    addnotifagation();

                }else if(text.equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                        .child("Following").child(profile_id).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(profile_id)
                            .child("followers").child( firebaseUser.getUid()).removeValue();

                }

            }
        });

            btn_myphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView_myPotsPhoto.setVisibility(View.VISIBLE);
                    recyclerView_savedphoto.setVisibility(View.GONE);
                }
            });
        btn_savedphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_myPotsPhoto.setVisibility(View.GONE);
                recyclerView_savedphoto.setVisibility(View.VISIBLE);
            }
        });






        return view;
    }

    private void getprofileifo()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(profile_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext()==null)
                {
                    return;
                }
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageUrl()).into(profile_image);
                txt_username.setText(user.getUsername());
                txt_fullname.setText(user.getFullname());
                txt_bio.setText(user.getBio());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void checkfolloew() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if(firebaseUser.getUid().equals(profile_id))

              return;
                if (dataSnapshot.child(profile_id).exists())
                {
                    btn_editeprofile.setText("Following");

                }
                else {btn_editeprofile.setText("follow");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getfolloewers() {
        DatabaseReference reference_followers = FirebaseDatabase.getInstance().getReference().child("follow")
                .child(profile_id).child("followers");
        reference_followers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference_following = FirebaseDatabase.getInstance().getReference().child("follow")
                .child(profile_id).child("Following");
        reference_following.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void addnotifagation()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifagations").child(profile_id);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",profile_id);
        hashMap.put("text","stsrting following you");
        hashMap.put("postid","");
        hashMap.put("ispost","false");
        reference.push().setValue(hashMap);
    }
    private void nrposts() {

        DatabaseReference reeference= FirebaseDatabase.getInstance().getReference("Posts");
        reeference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                int i=0;

                for (DataSnapshot snapshot:dataSnapshot.getChildren() ){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profile_id)){
                        Log.d(TAG, "Value is: ");
                        //Toast.makeText(getContext(),"getPublisher==profile_id",Toast.LENGTH_LONG).show();
                       i++;
                    }


                }
                txt_posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMyPotoPost()
    {
        DatabaseReference reeference= FirebaseDatabase.getInstance().getReference("Posts");
        reeference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mpostslist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Post post=snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profile_id)){
                        mpostslist.add(post);
                    }

                }
                Collections.reverse(mpostslist);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void Mysaves(){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mysaves=new ArrayList<>();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    mysaves.add(snapshot.getKey());

                }
                readSaves();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readSaves()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              mSavedpostslist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Post post=snapshot.getValue(Post.class);
                  for (String id :mysaves){
                      if (post.getPostid().equals(id)){
                          mSavedpostslist.add(post);

                      }
                  }

                }
                mSavedpostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

package com.example.instgramapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instgramapp.Adapter.MyPhotoAdapter;
import com.example.instgramapp.Adapter.PostAdapter;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class PostDetaiksFragment extends Fragment {

    String postid;

    RecyclerView recyclerView_postPhoto;
    List<Post>mpostDetails_List;
    PostAdapter mpostAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post_detaiks, container, false);

        SharedPreferences preferences=getContext().getSharedPreferences("PREFS",MODE_PRIVATE);
        postid= preferences.getString("postid","none");
       // Toast.makeText(getContext(),postid,Toast.LENGTH_LONG).show();

        recyclerView_postPhoto=view.findViewById(R.id.recycler_View_postDetails);
        recyclerView_postPhoto.setHasFixedSize(true);
        recyclerView_postPhoto.setLayoutManager(new LinearLayoutManager(getContext()));
        mpostDetails_List=new ArrayList<>();
        mpostAdapter=new PostAdapter(getContext(),mpostDetails_List);
        recyclerView_postPhoto.setAdapter(mpostAdapter);

       readPost_Details();

        return view;
    }


    private  void readPost_Details()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mpostDetails_List.clear();
                    Post post =dataSnapshot.getValue(Post.class);
                    mpostDetails_List.add(post);
                    mpostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }










}

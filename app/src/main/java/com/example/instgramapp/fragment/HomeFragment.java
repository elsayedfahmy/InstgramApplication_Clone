package com.example.instgramapp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instgramapp.Adapter.PostAdapter;
import com.example.instgramapp.Adapter.StoryAdapter;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.Model.Story;
import com.example.instgramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    List<Post>mpostlist;
    PostAdapter postAdapter;

    RecyclerView recyclerView_Story;
    List<Story>mstory_List;
    StoryAdapter storyAdapter;

    List<String>mfollowinglist;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);


        progressBar=view.findViewById(R.id.progress_circular);
        // TADO  recycler_View_posts
     recyclerView=view.findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mpostlist=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),mpostlist);
        recyclerView.setAdapter(postAdapter);
// TADO  recycler_View_Story
        recyclerView_Story=view.findViewById(R.id.recycler_View_Story);
        recyclerView_Story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManage_story=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView_Story.setLayoutManager(linearLayoutManage_story);
        mstory_List=new ArrayList<>();
        storyAdapter=new StoryAdapter(getContext(),mstory_List);
        recyclerView_Story.setAdapter(storyAdapter);

       // Log.w(TAG, "to check   ...............>>>>>>>.");
        checkFollowing();


        return view;
    }

    private void checkFollowing()
    {
        mfollowinglist=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mfollowinglist.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren())
                {
                   // Toast.makeText(getContext(),"isFollowing .....",Toast.LENGTH_SHORT).show();
                   // Log.w(TAG, "isFollowing  ...............>>>>>>>.");
                    mfollowinglist.add(snapshot.getKey());

                }
                readPosts();
                readstory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"onCancelled .....",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void readPosts()
    {


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mpostlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mpostlist.add(post);
                    }
                    for (String id : mfollowinglist)
                    {
                        if(post.getPublisher().equals(id)) {
                            mpostlist.add(post);
                        }//else Toast.makeText(getContext(),"Not equal .....",Toast.LENGTH_SHORT).show();

                    }
                    postAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(getContext(),"on Cancelled .....",Toast.LENGTH_SHORT).show();
            }
        });

    }

public void readstory(){

        Log.d("readstory",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story");
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.d("readstory","onDataChange >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            long cuurenttime=System.currentTimeMillis();
            mstory_List.clear();
            mstory_List.add(new Story("",0,0,"",FirebaseAuth.getInstance().getCurrentUser().getUid()));
            for (String id:mfollowinglist)
            {
                int countstory=0;
                Story story=null;
                for (DataSnapshot snapshot:dataSnapshot.child(id).getChildren())
                {
                    story=snapshot.getValue(Story.class);
                if (cuurenttime>story.getStarttime()&&cuurenttime<story.getEndtime()){
                    countstory++;
                }

                }
                if (countstory>0)
                {
                    mstory_List.add(story);
                }
            }
            storyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}


}

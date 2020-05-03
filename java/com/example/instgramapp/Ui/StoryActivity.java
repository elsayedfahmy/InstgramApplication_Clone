package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import jp.shts.android.storiesprogressview.StoriesProgressView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Model.Story;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter=0;
    long pressTime=0L;
    long limit=500L;

    List<String> storyid_List,images_list;
    //List<String> images_list;
    String userid;
    LinearLayout r_seen;
    StoriesProgressView storiesProgressView;
    TextView story_username,seen_number;
    ImageView image,story_photo,story_delete;

     private View.OnTouchListener onTouchListener= new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {

             switch (event.getAction()){
                 case MotionEvent.ACTION_DOWN:
                     pressTime=System.currentTimeMillis();
                   storiesProgressView.pause();
                 return false;
                 case MotionEvent.ACTION_UP:

                     long now=System.currentTimeMillis();
                     storiesProgressView.resume();
                     return limit < now-pressTime;
             }
             return false;

         }
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        userid=getIntent().getStringExtra("userid");

        r_seen=findViewById(R.id.r_seen);
        story_delete=findViewById(R.id.story_delete);
        seen_number=findViewById(R.id.seen_numbser);



        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);
        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }


        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoryActivity.this,FollowersActivity.class);
                intent.putExtra("id",userid);
                intent.putExtra("Title","views");
                intent.putExtra("storyid",storyid_List.get(counter));
                startActivity(intent);

            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid)
                        .child(storyid_List.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(StoryActivity.this,"Deleted",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });

        storiesProgressView=findViewById(R.id.stories);
        image=findViewById(R.id.image);
        story_photo=findViewById(R.id.story_photo);
        story_username=findViewById(R.id.story_username);

         getstories(userid);
         getuserinfo(userid);

         View reserve=findViewById(R.id.reservee);
         reserve.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 storiesProgressView.reverse();
             }
         });
         reserve.setOnTouchListener(onTouchListener);

        View skip=findViewById(R.id.skipe);
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);


    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images_list.get(++counter)).into(image);
        addviews(storyid_List.get(counter));
        get_numder_seen(storyid_List.get(counter));
    }

    @Override
    public void onPrev() {
        if (counter-1<0)return;
        Log.d("onPrev","counter ="+counter);
        Glide.with(getApplicationContext()).load(images_list.get(--counter)).into(image);
        get_numder_seen(storyid_List.get(--counter));

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    private  void getstories(String userid){
        images_list=new ArrayList<>();
        storyid_List=new ArrayList<>();
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story").child(userid);
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            images_list.clear();
            storyid_List.clear();
            for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                Story story=snapshot.getValue(Story.class);
                Long cuurenttime=System.currentTimeMillis();
                if (cuurenttime>story.getStarttime()&&cuurenttime<story.getEndtime()){
                    images_list.add(story.getImagUrL());
                    storyid_List.add(snapshot.getKey());
                    String x=story.getStroryid();
                   // Log.d("snapshot","story id=========== >>>>>>>>>>>>>>"+x);
                    Log.d("snapshot","story id="+snapshot.getKey()+"  user id ="+story.getUserid()+"image url ="+story.getImagUrL()+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>-==============");
                }
            }
            storiesProgressView.setStoriesCount(images_list.size());
            storiesProgressView.setStoryDuration(5000L);
            storiesProgressView.setStoriesListener(StoryActivity.this);
            storiesProgressView.startStories(counter);

            Glide.with(getApplicationContext()).load(images_list.get(counter)).into(image);
           // for (String id:storyid_List){ Log.d("id","id=========== >>>>>>>>>>>>>>"+id); }
           /// Log.d("getstories","storyid_List.size +>>>>>>>>>>>>>>"+storyid_List.size());
         //   Log.d("getstories","storyid_List.get(counter) +>>>>>>>>>>>>>>"+storyid_List.get(counter));
            addviews(storyid_List.get(counter));
            get_numder_seen(storyid_List.get(counter));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });


}
private  void getuserinfo(final String userid)
{
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user=dataSnapshot.getValue(User.class);
            Glide.with(getApplicationContext()).load(user.getImageUrl()).into(story_photo);
            story_username.setText(user.getUsername());

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}

    private void addviews(String story_id) {
        Log.d("addviews","storyid_List.get(counter) +>>>>>>>>>>>>>>"+storyid_List.get(counter));
        FirebaseDatabase.getInstance().getReference("Story").child(userid).child(story_id)
                .child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }



    private void get_numder_seen(String story_id)
    {
      DatabaseReference reference=  FirebaseDatabase.getInstance().getReference("Story").child(userid).child(story_id)
                .child("views");
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              seen_number.setText(""+dataSnapshot.getChildrenCount());

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }
}

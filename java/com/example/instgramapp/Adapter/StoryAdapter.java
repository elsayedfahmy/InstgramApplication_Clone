package com.example.instgramapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Ui.AddStoryActivity;
import com.example.instgramapp.Model.Story;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.example.instgramapp.Ui.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.Viewholder> {

    private Context mcontext;
    private List<Story> mstory_list;

    public StoryAdapter(Context mcontext, List<Story> mstory_list) {
        this.mcontext = mcontext;
        this.mstory_list = mstory_list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.add_story, parent, false);
            return new Viewholder(view);
        }else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.story_item, parent, false);
            return new Viewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {
        final Story story=mstory_list.get(position);

        userinfo(holder,story.getUserid(),position);

        if (holder.getAdapterPosition()!=0){
            seenStory(holder,story.getUserid());
        }
        if (holder.getAdapterPosition()==0){
            mystory(holder.add_storytext,holder.story_plus,false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition()==0 ){
                    mystory(holder.add_storytext,holder.story_plus,true);
                }
                else {
                    //TADO go to story
                    Intent intent=new Intent(mcontext, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    mcontext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mstory_list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView story_photo,story_photo_seen,story_plus;
        TextView story_username,add_storytext;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            story_photo=itemView.findViewById(R.id.story_photo);
            story_photo_seen=itemView.findViewById(R.id.story_photo_seen);
            story_plus=itemView.findViewById(R.id.story_photo_add);
            story_username=itemView.findViewById(R.id.story_username);
            add_storytext=itemView.findViewById(R.id.addStory_text);

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position==0)
        {
            return 0;
        }
        return 1;

    }

    public void userinfo(final Viewholder viewholder, final String userid, final int postion ){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    Glide.with(mcontext).load(user.getImageUrl()).into(viewholder.story_photo);
                    if(postion!=0){
                        Glide.with(mcontext).load(user.getImageUrl()).into(viewholder.story_photo_seen);
                        viewholder.story_username.setText(user.getUsername());
                    }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public  void mystory(final TextView text, final ImageView imageView , final boolean click)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                 reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int count =0;
            long currentTime=System.currentTimeMillis();
            for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                Story story=snapshot.getValue(Story.class);
                if (currentTime>story.getStarttime()&&currentTime<story.getEndtime()){
                    count++;
                }
            }
            if (click){
                if (count>0){
                    final AlertDialog alertDialog=new AlertDialog.Builder(mcontext).create();
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TADO:go to Story
                            Intent intent=new Intent(mcontext, StoryActivity.class);
                            intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mcontext.startActivity(intent);
                            dialog.dismiss();

                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mcontext.startActivity(new Intent(mcontext, AddStoryActivity.class));
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }else {
                    mcontext.startActivity(new Intent(mcontext, AddStoryActivity.class));
                }
            }else {
                if(count>0){
                         imageView.setVisibility(View.GONE);

                          text.setText("My Story");
                          }else {
                              imageView.setVisibility(View.VISIBLE);
                                       text.setText("Add Story");

                                }
                 }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }
    public void seenStory(final Viewholder viewholder, String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(!snapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                            &&(System.currentTimeMillis() <snapshot.getValue(Story.class).getEndtime())){
                        i++;
                    }

                }
                if (i>0)
                {

                    viewholder.story_photo.setVisibility(View.VISIBLE);
                    viewholder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewholder.story_photo.setVisibility(View.GONE);
                    viewholder.story_photo_seen.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

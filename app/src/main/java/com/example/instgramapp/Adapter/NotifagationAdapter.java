package com.example.instgramapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Model.Notifagation;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.example.instgramapp.fragment.PostDetaiksFragment;
import com.example.instgramapp.fragment.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class NotifagationAdapter extends RecyclerView.Adapter<NotifagationAdapter.Viewholder>{

    private Context mcontext;
    private List<Notifagation>mnotifagations_List;

    public NotifagationAdapter(Context mcontext, List<Notifagation> mnotifagations_List) {
        this.mcontext = mcontext;
        this.mnotifagations_List = mnotifagations_List;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.notifagation_item,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
       final Notifagation notifagation= mnotifagations_List.get(position);

        holder.txt_text_Comment.setText(notifagation.getText());
        Toast.makeText(mcontext,"postid"+notifagation.getPostid(),Toast.LENGTH_LONG);
      getuser_data(holder.img_Profile,holder.txt_Username,notifagation.getUserid());

        if (notifagation.isIspost().equals("true")){
            holder.img_post.setVisibility(View.VISIBLE);
            getpostimage(holder.img_post,notifagation.getPostid());
        }else {
            holder.img_post.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
               public void onClick(View v) {
                if (notifagation.isIspost().equals("true")){
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("postid",notifagation.getPostid());
                    editor.apply();
                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new PostDetaiksFragment()).commit();
            }else {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                        editor.putString("profile_Id",notifagation.getUserid());
                    editor.apply();
                        ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,new ProfileFragment()).commit();
                }



    }
});



    }

    @Override
    public int getItemCount() {
        return mnotifagations_List.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        ImageView img_Profile,img_post;
        TextView txt_Username,txt_text_Comment;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            txt_Username=itemView.findViewById(R.id.txt_username);
            txt_text_Comment=itemView.findViewById(R.id.txt_comment);
            img_post=itemView.findViewById(R.id.post_image);
            img_Profile=itemView.findViewById(R.id.image_profile);


        }
    }


    private  void getuser_data(final ImageView img_profile, final TextView username, String publisheid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(publisheid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
              Glide.with(mcontext).load(user.getImageUrl()).into(img_profile);
              username.setText(user.getUsername());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private  void getpostimage(final ImageView img_post, String postid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post=dataSnapshot.getValue(Post.class);
                Glide.with(mcontext).load(post.getPostimage()).into(img_post);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}

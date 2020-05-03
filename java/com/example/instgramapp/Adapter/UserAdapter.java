package com.example.instgramapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Ui.MainActivity;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.example.instgramapp.fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
private Context mcontext;
private List<User> muser;
private FirebaseUser firebaseUser;

private  boolean isfragment;

    public UserAdapter(Context mcontext, List<User> muser, boolean isfragment) {
        this.mcontext = mcontext;
        this.muser = muser;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user=muser.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

       // Log.d(TAG, "image is>>>>>>>: " +user.getImageUrl());
        Glide.with(mcontext).load(user.getImageUrl()).into(holder.circleImageView);

        // Toast.makeText(mcontext,firebaseUser.getUid().toString()+"  .." ,Toast.LENGTH_LONG).show();
        isfollowing(user.getId(),holder.btn_follow);
        if (user.getId().equals(firebaseUser.getUid()))
        {

            holder.btn_follow.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfragment) {
                    SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profile_Id", user.getId());
                    editor.apply();
                    ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                }else {
                    Intent intent=new Intent(mcontext, MainActivity.class);
                    intent.putExtra("publisherid",user.getId());
                    mcontext.startActivity(intent);
                }

            }
        });
        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.btn_follow.getText().toString().equals("follow") )
                {

                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId())
                            .child("followers").child( firebaseUser.getUid()).setValue(true);
                    addnotifagation(user.getId());

                }else { FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                        .child("Following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId())
                            .child("followers").child( firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return muser.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        public    CircleImageView circleImageView;
        public TextView username,fullname,id;
        Button btn_follow;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView =itemView.findViewById(R.id.imageProfile);
            username =itemView.findViewById(R.id.username);
            fullname =itemView.findViewById(R.id.fullname);
            btn_follow =itemView.findViewById(R.id.btn_follow);


        }
    }

    public void isfollowing(final String userid, final Button button)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("follow")
                .child(firebaseUser.getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.child(userid).exists())
                {
                    button.setText("Following");
                }
                else {button.setText("follow");}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private  void addnotifagation(String userid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifagations").child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","starting following");
        hashMap.put("postid","");
        hashMap.put("ispost","false");
        reference.push().setValue(hashMap);
    }


}

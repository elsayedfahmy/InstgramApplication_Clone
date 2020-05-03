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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Ui.MainActivity;
import com.example.instgramapp.Model.Comment;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Viewholder>{

    private Context mcontect;
   private List<Comment> mcommentsList;
   private FirebaseUser firebaseUser;
   private String postid;

    public CommentAdapter(Context mcontect, List<Comment> mcommentsList,String postid) {
        this.mcontect = mcontect;
        this.mcommentsList = mcommentsList;
        this.postid = postid;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontect).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=mcommentsList.get(position);

        holder.comment.setText(comment.getComment());
        getUserinfo(holder.profileimage,holder.username,comment.getPublisher());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontect, MainActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                mcontect.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (comment.getPublisher().equals(firebaseUser.getUid())){
                    final AlertDialog alertDialog=new AlertDialog.Builder(mcontect).create();
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postid)
                                    .child(comment.getCommentid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mcontect,"Deleted",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            dialog.dismiss();

                        }
                    });
                    alertDialog.show();

                }

                return true;
            }
        });






    }

    @Override
    public int getItemCount() {
        return mcommentsList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView profileimage;
        TextView username,comment;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            profileimage=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);


        }
    }

    private void getUserinfo(final ImageView profileimage, final TextView username, String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mcontect).load(user.getImageUrl()).into(profileimage);
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

package com.example.instgramapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instgramapp.Ui.CommentsActivity;
import com.example.instgramapp.Ui.FollowersActivity;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.example.instgramapp.fragment.PostDetaiksFragment;
import com.example.instgramapp.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    public Context mcontext;
    List<Post>mposts;
    FirebaseUser firebaseUser;

    public PostAdapter(Context mcontext, List<Post> mposts) {
        this.mcontext = mcontext;
        this.mposts = mposts;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {
         firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

         final Post post=mposts.get(position);
         Glide.with(mcontext).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder))
                 .into(holder.post_image);
         if (post.getDescription().equals(""))
         {
             holder.description.setVisibility(View.GONE);
         }
         else {
             holder.description.setVisibility(View.VISIBLE);
             holder.description.setText(post.getDescription());

         }
         publisherInfo(holder.profile_image,holder.username,holder.publisher,post.getPublisher());
         isLiked(holder.like,post.getPostid());
         nrLikes(holder.likes,post.getPostid());
        getComments(post.getPostid(),holder.comments);

         holder.like.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if ( v.getTag().equals("isliked")){

                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                             .child(firebaseUser.getUid()).removeValue();

                 }else FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                         .child(firebaseUser.getUid()).setValue(true);
                 addnotifagation(post.getPublisher(),post.getPostid());
             }
         });
         holder.comment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(mcontext, CommentsActivity.class);
                 intent.putExtra("postid",post.getPostid());
                 intent.putExtra("publisherid",post.getPublisher());
                 mcontext.startActivity(intent);

             }
         });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("profile_Id",post.getPublisher());
                editor.apply();
                (( FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new  ProfileFragment()).commit();

            }
        });
        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("profile_Id",post.getPublisher());
                editor.apply();
                (( FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new  ProfileFragment()).commit();

            }
        });
        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("profile_Id",post.getPublisher());
                editor.apply();
                (( FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new  ProfileFragment()).commit();

            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                (( FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PostDetaiksFragment()).commit();

            }
        });

        checkisSaved(holder.save,post.getPostid());
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);


                }else {FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getPostid()).removeValue();

                }

            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, FollowersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("Title","likes");
                mcontext.startActivity(intent);

            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(mcontext,v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit :
                                editepost(post.getPostid());
                                return true;
                            case R.id.delete :
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(post.getPostid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mcontext,"deleted",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                return true;
                            case R.id.report :  Toast.makeText(mcontext,"Reported it",Toast.LENGTH_LONG).show();
                                return true;

                            default: return
                                    false ;
                        }


                    }
                });
                popupMenu.inflate(R.menu.more_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();

            }
        });


        }

    @Override
    public int getItemCount() {
        return mposts.size();
    }

    public   class Viewholder extends RecyclerView.ViewHolder{

      public ImageView profile_image,post_image,like,comment,save,more;
      public TextView likes,comments,publisher,username,description;

      public Viewholder(@NonNull View itemView) {
          super(itemView);
          profile_image=itemView.findViewById(R.id.image_profle);
          post_image=itemView.findViewById(R.id.post_image);
          like=itemView.findViewById(R.id.img_like);
          comment=itemView.findViewById(R.id.img_comment);
          save=itemView.findViewById(R.id.img_save);
          likes=itemView.findViewById(R.id.txt_likes);
          comments=itemView.findViewById(R.id.txt_comments);
          publisher=itemView.findViewById(R.id.txt_pubisher);
          username=itemView.findViewById(R.id.txt_username);
          description=itemView.findViewById(R.id.txt_descreption);
          more=itemView.findViewById(R.id.more);



      }
  }
  private void getComments(String postid, final TextView comments){
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

  }

  private void isLiked(final ImageView imageView, String postid)
  {
      final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
   DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
   reference.addValueEventListener(new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot.child(firebaseUser.getUid()).exists()){
               imageView.setImageResource(R.drawable.ic_liked);
               imageView.setTag("isliked");

           }else {imageView.setImageResource(R.drawable.ic_like);
               imageView.setTag("like");}

       }

       @Override
       public void onCancelled(@NonNull DatabaseError databaseError) {

       }
   });

  }
    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" Likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void addnotifagation(String userid,String postid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifagations").child(userid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","is Liked your post ");
        hashMap.put("postid",postid);
        hashMap.put("ispost","true");
        reference.push().setValue(hashMap);
    }

    private void publisherInfo(final  ImageView image_profile, final TextView username, final TextView publisher, String userid)
               {
                   DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
                   reference.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           User user=dataSnapshot.getValue(User.class);
                           Glide.with(mcontext).load(user.getImageUrl()).into(image_profile);
                           username.setText(user.getUsername().toString());
                           publisher.setText(user.getUsername().toString());


                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

                  }

     private void checkisSaved(final ImageView imageSaved, final String postid)
                  {
                      FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                      DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Saves")
                              .child(firebaseUser.getUid());
                      reference.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                  if (dataSnapshot.child(postid).exists())
                                  {
                                      imageSaved.setImageResource(R.drawable.ic_saved);
                                      imageSaved.setTag("saved");

                                  }else {
                                      imageSaved.setImageResource(R.drawable.ic_save);
                                      imageSaved.setTag("save");
                                  }



                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });

                  }
         private  void editepost(final String postid){
                      final AlertDialog.Builder alertDialog=new AlertDialog.Builder(mcontext);
                      alertDialog.setTitle("Edit post");

                      final EditText editText=new EditText(mcontext);
                      LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(
                              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                      );
                      editText.setLayoutParams(p);
                      alertDialog.setView(editText);
                      gettext(editText,postid);
                      alertDialog.setPositiveButton("edit", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              HashMap<String,Object>hashMap=new HashMap<>();
                              hashMap.put("description",editText.getText().toString());
                              FirebaseDatabase.getInstance().getReference("Posts").child(postid)
                                      .updateChildren(hashMap);

                          }
                      });
                      alertDialog.setNegativeButton("canacel", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                          }
                      });
                      alertDialog.show();

                  }


          private void gettext(final EditText editText, String userid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}

package com.example.instgramapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.R;
import com.example.instgramapp.fragment.PostDetaiksFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;


public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.Viewolder> {

    private Context mcontext;
    private List<Post> mMypostList;

    public MyPhotoAdapter(Context mcontext, List<Post> mMypostList) {
        this.mcontext = mcontext;
        this.mMypostList = mMypostList;

    }

    @NonNull
    @Override
    public Viewolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.photo_item,parent,false);
        return new Viewolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewolder holder, int position) {

        final Post post=mMypostList.get(position);
        Glide.with(mcontext).load(post.getPostimage()).into(holder.myphoto_imageView);

        holder.myphoto_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS",MODE_PRIVATE).edit();
               // editor.putString("postid",post.getPostid());
                //editor.apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PostDetaiksFragment()).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mMypostList.size();
    }


    public class Viewolder extends RecyclerView.ViewHolder {
        private     ImageView myphoto_imageView;

        public Viewolder(@NonNull View itemView) {
            super(itemView);
            myphoto_imageView=itemView.findViewById(R.id.photopost);



        }
    }
}

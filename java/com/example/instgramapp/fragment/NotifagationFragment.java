package com.example.instgramapp.fragment;

import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instgramapp.Adapter.NotifagationAdapter;
import com.example.instgramapp.Adapter.PostAdapter;
import com.example.instgramapp.Model.Notifagation;
import com.example.instgramapp.Model.Post;
import com.example.instgramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class NotifagationFragment extends Fragment {
    String postid;

    RecyclerView recyclerView_notifagation;
    List<Notifagation> mnotifagation_List;
    NotifagationAdapter mnotifagationAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notifagation, container, false);

        SharedPreferences preferences=getContext().getSharedPreferences("PREFS",MODE_PRIVATE);
        postid= preferences.getString("postid","none");
        // Toast.makeText(getContext(),postid,Toast.LENGTH_LONG).show();

        recyclerView_notifagation=view.findViewById(R.id.recycler_View_Notifagation);
        recyclerView_notifagation.setHasFixedSize(true);
        recyclerView_notifagation.setLayoutManager(new LinearLayoutManager(getContext()));
        mnotifagation_List=new ArrayList<>();
        mnotifagationAdapter=new NotifagationAdapter(getContext(),mnotifagation_List);
        recyclerView_notifagation.setAdapter(mnotifagationAdapter);

        read_Notifagation();


        return view;
    }
    private  void read_Notifagation (){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifagations").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mnotifagation_List.clear();
             //   Toast.makeText(getContext(),"count : "+dataSnapshot.getChildrenCount(),Toast.LENGTH_LONG).show();
              for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Notifagation notifagation=snapshot.getValue(Notifagation.class);
                    mnotifagation_List.add(notifagation);
                }
                 Collections.reverse(mnotifagation_List);
               mnotifagationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                  Toast.makeText(getContext(),"Failed",Toast.LENGTH_LONG).show();
            }
        });


    }
}

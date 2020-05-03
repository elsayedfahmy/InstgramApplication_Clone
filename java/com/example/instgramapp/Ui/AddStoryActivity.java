package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.instgramapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {
    StorageTask storageTask;
    StorageReference storageReference;
    Uri imageuri;
    String myuri="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        storageReference= FirebaseStorage.getInstance().getReference("story");
        CropImage.activity()
                .setAspectRatio(9,16)
                .start(AddStoryActivity.this);

    }

    private String getfileExtenation(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void publishstory()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("posting ..");
        pd.show();
        if(imageuri!=null)
        {
            final StorageReference imageRefrence=storageReference.child(System.currentTimeMillis()+"."+getfileExtenation(imageuri));
            storageTask=imageRefrence.putFile(imageuri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    //return null;
                    if (!task.isComplete()){
                        throw task.getException();
                    }
                    return imageRefrence.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadedUri=task.getResult();
                        myuri=downloadedUri.toString();

                        String myid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story").child(myid);


                         long endtime=System.currentTimeMillis()+8640000; //1 day 24*60*60*100
                        String storyid=reference.push().getKey();
                        Log.d("Add Story","storyid================================== >>>>>>>>>>>>>>"+storyid);


                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imagUrL",myuri);
                        hashMap.put("starttime", ServerValue.TIMESTAMP);
                        hashMap.put("endtime",endtime);
                        hashMap.put("userid", myid);
                        hashMap.put("storyid",storyid);

                        reference.child(storyid).setValue(hashMap);
                        pd.dismiss();
                        finish();
                    }else
                    {
                        Toast.makeText(AddStoryActivity.this,"failed",Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {Toast.makeText(AddStoryActivity.this,"No image Selected",Toast.LENGTH_SHORT).show();}


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode== RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri =result.getUri();
           publishstory();
        }else {
            Toast.makeText(AddStoryActivity.this, "someThing gone Wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
            finish();
        }


    }


}

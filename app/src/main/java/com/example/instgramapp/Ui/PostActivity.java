package com.example.instgramapp.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instgramapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
 ImageView close,image_added;
 TextView post;
 EditText description;

 StorageTask uploadTask;
 StorageReference storageReference;
 Uri imageuri;
 String myuri="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        image_added=findViewById(R.id.image_added);
        close=findViewById(R.id.imageclose);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);

        storageReference= FirebaseStorage.getInstance().getReference("Posts");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               UploadImage();
            }
        });

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);

    }

    private String getfileExtenation(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("posting ..");
        pd.show();
        if(imageuri!=null)
        {
            final StorageReference fileRefrence=storageReference.child(System.currentTimeMillis()+"."+getfileExtenation(imageuri));
            uploadTask=fileRefrence.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    //return null;
                    if (!task.isComplete()){
                       throw task.getException();
                    }
                    return fileRefrence.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadedUri=task.getResult();
                        myuri=downloadedUri.toString();

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
                        String postid=reference.push().getKey();

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("postimage",myuri);
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);
                        pd.dismiss();
                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                        finish();



                    }else
                    {
                        Toast.makeText(PostActivity.this,"failed",Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {Toast.makeText(PostActivity.this,"No image Selected",Toast.LENGTH_SHORT).show();}


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode== RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri =result.getUri();
            image_added.setImageURI(imageuri);
        }else {
            Toast.makeText(PostActivity.this, "someThing gone Wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }


    }




}

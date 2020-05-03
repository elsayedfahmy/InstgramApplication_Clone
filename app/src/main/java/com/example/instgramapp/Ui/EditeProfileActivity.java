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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instgramapp.Model.User;
import com.example.instgramapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditeProfileActivity extends AppCompatActivity {

    ImageView imgclose,imgProfile;
    TextView txt_save,txt_edtPhoto;
    MaterialEditText edt_username,edt_fullname,edt_Bio;

    FirebaseUser firebaseUser;

    StorageTask uploadTask;
    private Uri imgeuri;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_profile);

        imgclose =findViewById(R.id.image_close);
        imgProfile =findViewById(R.id.imageProfile);
        txt_save =findViewById(R.id.txt_save);
        txt_edtPhoto =findViewById(R.id.txt_ChangePhoto);
        edt_username =findViewById(R.id.edt_username);
        edt_fullname =findViewById(R.id.edt_fullname);
        edt_Bio =findViewById(R.id.edt_Bio);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                edt_username.setText(user.getUsername());
                edt_fullname.setText(user.getFullname());
                edt_Bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imgProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); }});

        txt_edtPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditeProfileActivity.this);

            }
        });

      imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditeProfileActivity.this);

            }
        });

        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               UpdateProdileData(edt_username.getText().toString(),
                       edt_fullname.getText().toString(),edt_Bio.getText().toString());
                finish();

            }



        });



    }
    private  void UpdateProdileData(String username,String fullname,String bio)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
       // String postid=reference.push().getKey();

        HashMap<String,Object> hashMap=new HashMap<>();
      //  hashMap.put("postid",firebaseUser.getUid());
        hashMap.put("username",username);
        hashMap.put("fullname",fullname);
        hashMap.put("bio",bio);
        reference.updateChildren(hashMap);


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
        pd.setMessage("Uploading ..");
        pd.show();
        if(imgeuri!=null)
        {
            final StorageReference fileRefrence=storageReference.child(System.currentTimeMillis()+"."+getfileExtenation(imgeuri));
            uploadTask=fileRefrence.putFile(imgeuri);
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
                       String myuri=downloadedUri.toString();

                        DatabaseReference reference= FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(firebaseUser.getUid());

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("ImageUrl",""+myuri);

                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }else
                    {
                        Toast.makeText(EditeProfileActivity.this,"failed",Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditeProfileActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {Toast.makeText(EditeProfileActivity.this,"No image Selected",Toast.LENGTH_SHORT).show();}


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode== RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imgeuri =result.getUri();
            UploadImage();

        }else {
            Toast.makeText(EditeProfileActivity.this, "someThing gone Wrong", Toast.LENGTH_SHORT).show();
        }
    }
}

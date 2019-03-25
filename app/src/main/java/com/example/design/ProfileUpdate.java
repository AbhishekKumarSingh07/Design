package com.example.design;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdate extends AppCompatActivity {
  private TextView pfName,pfEnroll,pfBrnch,pfGender,pfEmail;
  private Button bck;
  private CircleImageView iv;
  private FirebaseAuth mAuth;
  private DatabaseReference userRef;
  private FirebaseStorage storage;
  private StorageReference userProfileImageRef;
  private  ProgressDialog loadingBar;


    String currentUserId;
    final static int Gallery_Pick=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        iv=findViewById(R.id.imgup);
        pfName=findViewById(R.id.pfName);
        pfEnroll=findViewById(R.id.pfEnroll);
        pfBrnch=findViewById(R.id.pfBranch);
        pfGender=findViewById(R.id.pfGender);
        pfEmail=findViewById(R.id.pfEmail);
        bck=findViewById(R.id.bck);

       // loadUserInfo();

        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Student").child(currentUserId);
        userProfileImageRef=FirebaseStorage.getInstance().getReference().child("DP");

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileUpdate.this,NavigationDrawer.class));
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    //String pfImage=dataSnapshot.child("ProfileImage").getValue().toString();
                    String pName=dataSnapshot.child("Name").getValue().toString();
                    String pBrn=dataSnapshot.child("Branch").getValue().toString();
                    String pEnroll=dataSnapshot.child("Enrollment").getValue().toString();
                    String pGender=dataSnapshot.child("Gender").getValue().toString();
                    String pEmail=dataSnapshot.child("Email").getValue().toString();


                   // Picasso.get().load(pfImage).placeholder(R.drawable.profile).into(iv);

                    pfName.setText(pName);
                    pfEnroll.setText(pEnroll);
                    pfBrnch.setText(pBrn);
                    pfGender.setText(pGender);
                    pfEmail.setText(pEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileUpdate.this,NavigationDrawer.class));
        super.onBackPressed();
    }
    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(mAuth.getCurrentUser()==null)
//        {
//            finish();
//            startActivity(new Intent(ProfileUpdate.this,MainActivity.class));
//        }
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                loadingBar.setTitle("DP");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri=result.getUri();
                StorageReference filepath=userProfileImageRef.child(currentUserId + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            startActivity( new Intent(ProfileUpdate.this,ProfileUpdate.class));
                            Toast.makeText(ProfileUpdate.this,"Profile Pic succesfully stored",Toast.LENGTH_SHORT).show();

                            final String downloadurl=task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            userRef.child("DP").setValue(downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ProfileUpdate.this,"Pic Stored in DB",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        String msg=task.getException().getMessage();
                                        Toast.makeText(ProfileUpdate.this,"Error in pic Storage in DB : "+msg,Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            }

            else {

                loadingBar.dismiss();
                Toast.makeText(ProfileUpdate.this,"Error iN Image Crop ",Toast.LENGTH_SHORT).show();
            }
        }

    }

}

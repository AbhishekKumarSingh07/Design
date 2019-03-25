package com.example.design;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView greet,upsc,Dvocab,navName,navEnroll,nam;
    private String greeting=null,currentUser;
    private CircleImageView navimage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UsersRef;



    private StorageReference userProfileImageRef;
    final static int Gallery_Pick=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        greet=findViewById(R.id.grtn);
        upsc=findViewById(R.id.upsc);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);


        Dvocab=findViewById(R.id.dvcb);
        nam=findViewById(R.id.nam);

        firebaseAuth=FirebaseAuth.getInstance();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Student");
        currentUser=firebaseAuth.getCurrentUser().getUid();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navView=navigationView.inflateHeaderView(R.layout.header);
        navimage=navView.findViewById(R.id.imgshw);
        navName=navView.findViewById(R.id.nvName);
        navEnroll=navView.findViewById(R.id.nvEnroll);



        userProfileImageRef= FirebaseStorage.getInstance().getReference();


        userProfileImageRef.child(firebaseAuth.getUid()).child("DP").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(navimage);
            }
        });

        UsersRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("Name"))
                    {
                        String fullname = dataSnapshot.child("Name").getValue().toString();
                        navName.setText(fullname);
                        nam.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("Enrollment"))
                    {
                        String enrol = dataSnapshot.child("Enrollment").getValue().toString();
                        navEnroll.setText(enrol);
                    }

//                    if(dataSnapshot.hasChild("DP"))
//                    {
//
//                        String image = dataSnapshot.child("DP").getValue().toString();
//                       // Toast.makeText(NavigationDrawer.this, image, Toast.LENGTH_SHORT).show();
//
//                       // Picasso.with(NavigationDrawer.this).load(image).placeholder(R.drawable.profile).into(navimage);
//                            //Picasso.get().setLoggingEnabled(true);
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        userProfileImageRef.child(firebaseAuth.getCurrentUser().getUid()).child("Profile images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).centerCrop().fit().into(navimage);
//            }
//        });



        Dvocab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationDrawer.this,DailyVocab.class));
            }
        });


        upsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String main="http://www.froshbuddy.xyz";
                Intent intent=new Intent(NavigationDrawer.this,WebActivity.class);
                intent.putExtra("url",main);
                startActivity(intent);
            }
        });



        Date date=new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);

        if(hour>= 12 && hour < 17){
            greeting = "Good Afternoon";
        } else if(hour >= 17 && hour < 21){
            greeting = "Good Evening";
        } else if(hour >= 21 && hour < 24){
            greeting = "Good Night";
        } else {
            greeting = "Good Morning";
        }









        greet.setText(greeting);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder= new AlertDialog.Builder(NavigationDrawer.this);
            builder.setTitle("Exit App")
                    .setMessage("Do you want to Exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(new Intent(NavigationDrawer.this,MainActivity.class));


                        }
                    })
                    .setNegativeButton("No",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.show();

            //  super.onBackPressed();
        }
    }


    @Override
    protected void onStart() {

        FirebaseUser cuser=firebaseAuth.getCurrentUser();

        if(cuser==null)
        {
            startActivity(new Intent(NavigationDrawer.this,MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));

        }

        super.onStart();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.pf) {
            startActivity(new Intent(NavigationDrawer.this,ProfileUpdate.class));
        } else if (id == R.id.dsh) {

        } else if (id == R.id.ntf) {

        } else if (id == R.id.mem) {
            startActivity(new Intent(NavigationDrawer.this,AFamily.class));

        } else if (id == R.id.shr) {

        } else if (id == R.id.thnk) {

        }
        else if (id == R.id.lgot) {
            AlertDialog.Builder builder= new AlertDialog.Builder(NavigationDrawer.this);
            builder.setTitle("Exit App")
                    .setMessage("Do you want to Log Out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(NavigationDrawer.this,MainActivity.class));

                        }
                    })
                    .setNeutralButton("Exit App", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                            finish();
                        }
                    })
                    .setNegativeButton("No",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.show();


        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

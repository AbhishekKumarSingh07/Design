package com.example.design;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView reg;
    private EditText email,password;
    private Button logn,fpsw;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Sign In");
        reg=findViewById(R.id.tvreg);
        logn=findViewById(R.id.btnSignIn);
        email=findViewById(R.id.logEtEml);
        password=findViewById(R.id.logEtPsw);
        dialog=new ProgressDialog(this);
        fpsw=findViewById(R.id.frgtps);


        firebaseAuth=FirebaseAuth.getInstance();

        fpsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter your registered email ID", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "Error in sending password reset email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



        logn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setTitle("Loading");
                dialog.setMessage("Please wait...");
                dialog.show();
                String emal=email.getText().toString().trim();
                String psw=password.getText().toString().trim();
                if(validate(emal,psw))
                {   email.setText(null);
                    password.setText(null);
                    firebaseAuth.signInWithEmailAndPassword(emal,psw).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,"Succesfull",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,NavigationDrawer.class));
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent= new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(MainActivity.this,NavigationDrawer.class));
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit App")
                .setMessage("Do you want to exit app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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

    private Boolean validate(String em, String ps){
        Boolean res=false;

        if(em.isEmpty() || ps.isEmpty())
        {
            Toast.makeText(MainActivity.this,"Enter the credentials",Toast.LENGTH_SHORT).show();
        }
        else {
            res=true;
        }
        return res;
    }
}

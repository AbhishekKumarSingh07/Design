package com.example.design;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private  TextView logn;
    private String gender,email,pass,name,enrol,brn;
    private EditText etName,etEnr,etBrn,etEmail,etPass;
    private RadioButton rbtnMale,rBtnFem;
    private Button reg;
    private RadioGroup gen;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        setupUIViews();

        dialog=new ProgressDialog(this);




        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference("Student");


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.setTitle("Loading");
                    dialog.setMessage("Please Wait...");
                    dialog.show();
                  gender=checkGender();
                  email=etEmail.getText().toString().trim();
                  pass=etPass.getText().toString().trim();
                  name=etName.getText().toString().trim();
                  enrol=etEnr.getText().toString().trim();
                  brn= etBrn.getText().toString().trim();

                if(validate(email,pass,name,enrol,brn))
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(SignUp.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {

                                Student info =new Student(name,enrol,brn,email,gender);

                                FirebaseDatabase.getInstance().getReference("Student")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task2) {
                                        if (task2.isSuccessful()) {
                                            dialog.dismiss();
                                            finish();

                                            Toast.makeText(SignUp.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        }
                                    }
                                });



                            }
                            else {
                                    dialog.dismiss();
                                    Toast.makeText(SignUp.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });



        logn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(SignUp.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(SignUp.this,MainActivity.class);
       startActivity(intent);
    }

    private String checkGender()
    {
        String gen="";
        if(rbtnMale.isChecked())
        {
            gen="Male";
        }
        else if (rBtnFem.isChecked())
        {
            gen="Female";
        }
        else
        {
            Toast.makeText(SignUp.this,"Gender not selected",Toast.LENGTH_SHORT).show();
        }
        return gen;
    }

    private Boolean validate(String em,String ps,String nm,String enr,String brn)
    {
        Boolean res=false;
        if(em.isEmpty() || ps.isEmpty() || nm.isEmpty() || enr.isEmpty() || brn.isEmpty())
        {
            Toast.makeText(SignUp.this,"Fill all the form",Toast.LENGTH_SHORT).show();
        }
        else {
            res=true;
        }
        return res;

    }

    private void setupUIViews()
    {
        logn=findViewById(R.id.tvLogin);
        etName=findViewById(R.id.etname);
        etEnr=findViewById(R.id.etenroll);
        etBrn=findViewById(R.id.etbrnch);
        etEmail=findViewById(R.id.etemail);
        etPass=findViewById(R.id.etpass);
        rbtnMale=findViewById(R.id.rbtnma);
        rBtnFem=findViewById(R.id.rbtnfem);
        gen=findViewById(R.id.gend);
        reg=findViewById(R.id.btnSignup);

    }
}

package com.example.design;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DailyVocab extends AppCompatActivity {

    private TextView textView;
    private Button nxt,means;
    private String[] words,meaning;
    private String ab="";
    private String bc="";
    private  int i=1;
    private int k=0;
    private Document word,mean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_vocab);
        textView=findViewById(R.id.tv);
        nxt=findViewById(R.id.Nbtn);
        means=findViewById(R.id.Mbtn);

        nxt.setVisibility(View.GONE);
        new start().execute();


        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(i<words.length) {
                    textView.setText(words[i]);
                    i++;
                    means.setVisibility(View.VISIBLE);
                    nxt.setVisibility(View.GONE);
                }
                else
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(DailyVocab.this);
                    builder.setTitle("Do You Want to Repeat?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DailyVocab.this,DailyVocab.class));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(DailyVocab.this,NavigationDrawer.class));
                                }
                            });
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }

            }
        });

        means.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(k<meaning.length)
                {
                    textView.setText(meaning[k]);
                    k++;
                    nxt.setVisibility(View.VISIBLE);
                    means.setVisibility(View.GONE);
                }
            }
        });
    }
    public void second()
    {
        textView.setText(words[0]);
    }

    public class start extends AsyncTask<Void,Void,Void> {




        @Override
        protected Void doInBackground(Void... voids) {
            try {
                word= Jsoup.connect("http://www.froshbuddy.xyz").get();
                mean=Jsoup.connect("http://www.froshbuddy.xyz/meaning.html").get();
                ab=word.text();
                bc=mean.text();

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            words=ab.split(",");
            meaning=bc.split(",");
            second();
            super.onPostExecute(aVoid);
        }
    }


}

package com.example.android.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    int pos, pia,score,total;
    TextView Score;
    ImageView Photo;
    Button Fc, Sc, Tc, Fthc;
    String[] v = new String[4];
    ArrayList<String> CelebritiesPhotosUrls = new ArrayList<String>(), CelebritiesNames = new ArrayList<String>();

    public class DownloadHTMLContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String res = "";
            URL url;
            HttpURLConnection con = null;
            try {
                url = new URL(urls[0]);
                con = (HttpURLConnection) url.openConnection();
                InputStream IS = con.getInputStream();
                InputStreamReader ISR = new InputStreamReader(IS);
                int data = ISR.read();

                while (data != -1) {

                    char c = (char) data;
                    res += c;
                    data = ISR.read();
                }
                return res;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();
                InputStream IS = con.getInputStream();

                Bitmap image = BitmapFactory.decodeStream(IS);

                return image;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void Do_This() {


        Random r = new Random();
        pos = r.nextInt(CelebritiesNames.size());

        DownloadImage img = new DownloadImage();
        Bitmap bm = null;

        try {
            bm = img.execute(CelebritiesPhotosUrls.get(pos)).get();
            Photo.setImageBitmap(bm);

            HashMap<Integer,Boolean> map=new HashMap<Integer, Boolean>(CelebritiesNames.size());
            for (int i = 0; i <CelebritiesNames.size() ; i++) {
                map.put(i,false);
            }

            pia = r.nextInt(4);
            for (int i = 0; i < 4; i++) {

                if (i == pia) {
                    v[i] = CelebritiesNames.get(pos);
                    map.put(pos,true);
                } else {

                    int x = r.nextInt(CelebritiesNames.size());
                    while (map.get(x)==true)
                        x = r.nextInt(CelebritiesNames.size());

                    map.put(x,true);
                    v[i] = CelebritiesNames.get(x);
                }
            }
            Fc.setText(v[0]);
            Sc.setText(v[1]);
            Tc.setText(v[2]);
            Fthc.setText(v[3]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void ChosenButtonPressed(View view) {

        String Message = "Wrong This is ";
        if (view.getTag().toString().equals(Integer.toString(pia))) {
            Message = "Correct!";
            score++;
        }
        else
            Message += CelebritiesNames.get(pos);

        total++;

        Score.setText(Integer.toString(score)+"/" + Integer.toString(total));

        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();

        Do_This();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total=score=0;
        Fc = (Button) findViewById(R.id.FirstChoicebutton);
        Sc = (Button) findViewById(R.id.SecondChoicebutton);
        Tc = (Button) findViewById(R.id.ThirdChoicebutton);
        Fthc = (Button) findViewById(R.id.FourthChoicebutton);
        Score = (TextView) findViewById(R.id.ScoreTextView);
        Photo = (ImageView) findViewById(R.id.CelebrityImage);

        String res = null;
        DownloadHTMLContent dhc = new DownloadHTMLContent();

        try {
            res = dhc.execute("http://www.posh24.se/kandisar/").get();
            String[] Arr = res.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(Arr[0]);

            while (m.find()) {
                CelebritiesPhotosUrls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(Arr[0]);

            while (m.find()) {
                CelebritiesNames.add(m.group(1));
            }

            Do_This();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

}

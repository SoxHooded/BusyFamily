package com.gmail.epsilon1011.busyfamily;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageFull extends AppCompatActivity {

    private android.support.v7.widget.Toolbar ImageFullToolbar;
    private ImageView view;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);

        ImageFullToolbar = findViewById(R.id.imageFullToolbar);
        setSupportActionBar(ImageFullToolbar);
        getSupportActionBar().setTitle("Family Album");
        ImageFullToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyAlbum();
            }
        });

        view = findViewById(R.id.image_view_full);

        mStorage = FirebaseStorage.getInstance();
        mStore = FirebaseFirestore.getInstance();

        String url = getIntent().getStringExtra("url");

        if (url != null) {
            Picasso.get()
                    .load(url)
                    .placeholder(R.mipmap.img)
                    .fit()
                    .centerCrop()
                    .into(view);
        } else {
            sendToFamilyAlbum();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.imagefull_menu, menu);

        return true;
    }

    //Menu options

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_download:

                downloadImage();

                return true;

            case R.id.menu_delete:

                deleteImageAlert();

                return true;


            default:
                return false;
        }
    }

    private void deleteImageAlert() {

        AlertDialog.Builder dial = new AlertDialog.Builder(this);
        dial.setMessage("Are you sure you want to delete this image?").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage();
                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dial.create();
        alert.setTitle("Deleting Image");
        alert.show();

    }

    private void deleteImage() {

        final String url = getIntent().getStringExtra("url");

        mStorage.getReferenceFromUrl(url).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                mStore.collection("Family_Calendar_Album").whereEqualTo("ImageUrl", url).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final String doc_id = document.getId();

                            mStore.collection("Family_Calendar_Album").document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(ImageFull.this, "Image deleted!", Toast.LENGTH_LONG).show();
                                    sendToFamilyAlbum();
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    private void downloadImage() {
        final String url = getIntent().getStringExtra("url");

        DownloadTask task = new DownloadTask();
        task.execute(url);
    }

    private void sendToFamilyAlbum() {
        Intent albumIntent = new Intent(ImageFull.this, FamilyAlbum.class);
        startActivity(albumIntent);
        finish();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            int permission = ActivityCompat.checkSelfPermission(ImageFull.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        ImageFull.this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

            progressDialog = new ProgressDialog(ImageFull.this);
            progressDialog.setTitle("Downloading image");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String path = params[0];
            int size = 0;


            try {
                URL url = new URL(path);
                URLConnection con = url.openConnection();
                con.connect();
                size = con.getContentLength();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");
                if (!(file.exists())) {
                    file.mkdir();
                }

                File input = new File(file, "BF"+ System.currentTimeMillis() + ".jpg");
                InputStream stream = new BufferedInputStream(url.openStream(), 8192);
                byte[] data = new byte[1024];

                int total = 0;
                int count = 0;


                OutputStream stream2 = new FileOutputStream(input);

                while ((count = stream.read(data)) != -1) {
                    total += count;
                    stream2.write(data, 0, count);

                    int progress = (int) total * 100 / size;
                    publishProgress(progress);
                }

                stream.close();
                stream2.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download Complete.";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String res) {

            progressDialog.dismiss();
            Toast.makeText(ImageFull.this, res, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

        sendToFamilyAlbum();
    }

}

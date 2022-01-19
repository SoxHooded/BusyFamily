package com.gmail.epsilon1011.busyfamily;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyAlbum extends AppCompatActivity {

    private RecyclerView FamilyAlbumView;
    private FamilyAlbumAdapter familyAlbumAdapter;
    private Toolbar FamilyAlbumToolbar;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageTask mUploadTask;
    private List<FamilyAlbumItem> albumList;
    private String user_id;
    private static final int requestImage = 1;
    private Uri imageUri ;
    private ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_album);

        FamilyAlbumToolbar = findViewById(R.id.familyAlbumToolbar);
        setSupportActionBar(FamilyAlbumToolbar);
        getSupportActionBar().setTitle("Family Album");
        FamilyAlbumToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        progBar = (ProgressBar) findViewById(R.id.albumProg);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        FamilyAlbumView = findViewById(R.id.familyAlbumView);
        FamilyAlbumView.setHasFixedSize(true);
        FamilyAlbumView.setLayoutManager(new LinearLayoutManager(this));

        albumList = new ArrayList<>();
        familyAlbumAdapter = new FamilyAlbumAdapter(albumList);
        FamilyAlbumView.setAdapter(familyAlbumAdapter);

        user_id = mAuth.getCurrentUser().getUid();

        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String serv_id = task.getResult().getString("Server_id");

                mStore.collection("Family_Calendar_Album").whereEqualTo("Server_id", serv_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (documentSnapshots != null) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    FamilyAlbumItem upload = doc.getDocument().toObject(FamilyAlbumItem.class);
                                    albumList.add(upload);
                                    familyAlbumAdapter.notifyDataSetChanged();

                                }
                            }
                        }
                    }
                });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.familyalbum_menu, menu);

        return true;
    }

    //Menu options

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_upload:

                chooseImage();

                return true;


            default:
                return false;
        }
    }

    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(FamilyAlbum.this,FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    private void chooseImage(){
        Intent chooseImageIntent = new Intent();
        chooseImageIntent.setType("image/*");
        chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseImageIntent , requestImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == requestImage && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (imageUri != null) {

                progBar.setVisibility(View.VISIBLE);

                StorageReference ref = mStorage.getReference("images").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                mUploadTask = ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            final String url = task.getResult().getDownloadUrl().toString();

                        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String serv_id = task.getResult().getString("Server_id");

                                Map<String, String> imageMap = new HashMap<>();

                                imageMap.put("Server_id", serv_id);
                                imageMap.put("ImageUrl", url);

                                mStore.collection("Family_Calendar_Album").add(imageMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Toast.makeText(FamilyAlbum.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                                        progBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        });
                    }
                });

            } else {
                Toast.makeText(FamilyAlbum.this, "Please select an image", Toast.LENGTH_LONG).show();
                progBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }

}

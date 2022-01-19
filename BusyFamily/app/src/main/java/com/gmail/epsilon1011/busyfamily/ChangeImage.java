package com.gmail.epsilon1011.busyfamily;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class ChangeImage extends AppCompatActivity {

    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private static final int requestImage = 1;
    private Uri imageUri ;
    private StorageTask mUploadTask;
    private String id ;
    private ProgressBar progBar;
    private Toolbar changeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_image);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        progBar = (ProgressBar) findViewById(R.id.changeProg);

        changeToolbar = findViewById(R.id.changeImageToolbar);
        setSupportActionBar(changeToolbar);
        getSupportActionBar().setTitle("Changing Image");

        id = getIntent().getStringExtra("id");

        if(id!=null){
            if(!id.isEmpty()) {
                chooseImage();
            }
        } else {
            sendToUsers();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == requestImage && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            if (imageUri != null) {

                progBar.setVisibility(View.VISIBLE);

                mStore.collection("Family_Calendar_Users").whereEqualTo("user_id" , id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final String doc_id = document.getId();

                            mStore.collection("Family_Calendar_Users").document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    String url = task.getResult().getString("ImageUrl");

                                    if(url!=null){
                                        if(!url.equals("none")){
                                            mStorage.getReferenceFromUrl(url).delete();
                                        }
                                    }

                                    StorageReference ref = mStorage.getReference("images").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                                    mUploadTask = ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            final String url = task.getResult().getDownloadUrl().toString();

                                              mStore.collection("Family_Calendar_Users").document(doc_id).update("ImageUrl",url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {

                                                      Toast.makeText(ChangeImage.this, "Image Changed", Toast.LENGTH_LONG).show();
                                                      sendToUsers();
                                                      progBar.setVisibility(View.INVISIBLE);
                                                  }
                                              });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

            } else {
                progBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ChangeImage.this, "Please select an image", Toast.LENGTH_LONG).show();
                sendToUsers();
            }
        }
        else{
            progBar.setVisibility(View.INVISIBLE);
            sendToUsers();
        }
    }

    @Override
    public void onBackPressed() {

        Toast.makeText(ChangeImage.this, "Please wait..", Toast.LENGTH_LONG).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void chooseImage(){
        Intent chooseImageIntent = new Intent();
        chooseImageIntent.setType("image/*");
        chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseImageIntent , requestImage);
    }

    private void sendToUsers(){
        Intent usersIntent = new Intent(ChangeImage.this , FamilyUsers.class);
        startActivity(usersIntent);
        finish();
    }
}

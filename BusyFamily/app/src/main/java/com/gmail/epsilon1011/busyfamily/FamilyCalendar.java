package com.gmail.epsilon1011.busyfamily;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.work.Data;

public class FamilyCalendar extends AppCompatActivity implements DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener {

    private Toolbar FamilyCalendarToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private Dialog colorDialog;
    private String color;
    private String user_id;
    private CalendarView FamilyCalendarView;
    private Button viewDate;
    private Button add;
    private String theDate;
    private Dialog addDialog;
    private String notificationItem;
    private String Date;
    private String Time;
    private String userItem;
    public ArrayList<String> options=new ArrayList<String>();
    public String id;
    public boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_calendar);


        String languageToLoad  = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        FamilyCalendarToolbar = findViewById(R.id.familyCalendarToolbar);
        setSupportActionBar(FamilyCalendarToolbar);
        getSupportActionBar().setTitle("");
        FamilyCalendarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMain();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        colorDialog = new Dialog(this);
        addDialog = new Dialog(this);

        notificationItem = "No Notification";

        viewDate= findViewById(R.id.familyViewbtn);
        add = findViewById(R.id.familyAddbtn);

        FamilyCalendarView = findViewById(R.id.familyCalendarView);

        SimpleDateFormat showformat = new SimpleDateFormat("dd-MM-yyyy");
        theDate = showformat.format(new Date());

        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String username = task.getResult().getString("Username");
                String serv_id = task.getResult().getString("Server_id");
                options.add(username);
                userItem=username;

                mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",serv_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final String doc_id = document.getId();

                            mStore.collection("Family_Calendar_Users").document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    String type = task.getResult().getString("Type");
                                    String id = task.getResult().getString("user_id");

                                    if(type.equals("Fake")){
                                        options.add(id);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });


        FamilyCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                String day1;
                String month1;

                day1 = String.valueOf(dayOfMonth);
                month1 = String.valueOf(month + 1);

                if (dayOfMonth <= 9) {
                    day1 = "0" + String.valueOf(dayOfMonth);
                }
                if (month + 1 <= 9) {
                    month1 = "0" + String.valueOf(month + 1);
                }

                theDate = day1 + "-" + month1 + "-" + year;

            }
        });

        viewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewDate.setEnabled(false);
                add.setEnabled(false);

                Intent sendToDate = new Intent(FamilyCalendar.this,FamilyViewDate.class);
                sendToDate.putExtra("date", theDate);
                startActivity(sendToDate);
                finish();

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewDate.setEnabled(false);
                add.setEnabled(false);

               addActivity2();

            }
        });

        checkActivities();

    }

    protected void onStart(){
        super.onStart();

        String leaving = getIntent().getStringExtra("Leaving");
        if(leaving!=null){
            if(leaving.equals("true")){
                leaveCalendar();
            }
        }

        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                final String res = task.getResult().getString("Server_id");

                if (res.equals("none")) {

                    AlertDialog.Builder dial = new AlertDialog.Builder(FamilyCalendar.this);
                    dial.setMessage("Would you like to create a family calendar?").setCancelable(false).
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mStore.collection("Users").document(user_id).update("Server_id", user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            colorPopupOwner();
                                        }
                                    });
                                }
                            }).
                            setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    sendToMain();
                                }
                            });

                    AlertDialog alert = dial.create();
                    alert.setTitle("Family Calendar");
                    alert.show();

                } else if (!res.equals(user_id)){

                    mStore.collection("Family_Calendar_Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                colorPopupGuest(res);
                            }
                        }
                    });

                }
            }});


    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            //inflate menu
            getMenuInflater().inflate(R.menu.familycalendar_menu, menu);

            return true;
        }

        //Menu options

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_familyActivities:

                    sendToFamilyActivities();

                    return true;

                case R.id.menu_users:

                    sendToFamilyUsers();

                    return true;

                case R.id.menu_chat:

                    sendToChat();

                    return true;

                case R.id.menu_Shopping:

                    sendToFamilyShopping();

                    return true;

                case R.id.menu_album:

                    sendToFamilyAlbum();

                    return true;

                case R.id.menu_leave:

                    leaveCalendarAlert();

                    return true;

                case R.id.menu_Create:

                    sendToCreateUser();

                    return true;

                case R.id.menu_invite:

                    sendToFamilyInvite();

                    return true;

                default:
                    return false;
            }
        }

    private void colorPopupOwner(){
        final Button cancelbtn;
        final Button nextbtn ;
        colorDialog.setContentView(R.layout.create_choose_color_popout);
        colorDialog.setCancelable(false);

        cancelbtn = colorDialog.findViewById(R.id.cancel2btn);
        nextbtn = colorDialog.findViewById(R.id.donebtn);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelbtn.setEnabled(false);
                nextbtn.setEnabled(false);
                mStore.collection("Users").document(user_id).update("Server_id","none").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        colorDialog.dismiss();
                        sendToMain();
                    }
                });
            }
        });


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cancelbtn.setEnabled(false);
                nextbtn.setEnabled(false);
                if (color!=null) {

                                     Map<String, String> ownerMap = new HashMap<>();

                                     ownerMap.put("user_id", user_id);
                                     ownerMap.put("Color", color);
                                     ownerMap.put("Type", "Owner");
                                     ownerMap.put("Server_id", user_id);
                                     ownerMap.put("ImageUrl", "none");

                                     mStore.collection("Family_Calendar_Users").document(user_id).set(ownerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             mStore.collection("Users").document(user_id).update("Server_id",user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     Toast.makeText(FamilyCalendar.this, "Family calendar created", Toast.LENGTH_LONG).show();
                                                     colorDialog.dismiss();
                                                     recreate();
                                                 }
                                             });

                                         }
                                     });
                }

                else{
                    cancelbtn.setEnabled(true);
                    nextbtn.setEnabled(true);
                    Toast.makeText(FamilyCalendar.this, "Select a color", Toast.LENGTH_LONG).show();
                }
            }
        });


        colorDialog.show();
    }

    private void colorPopupGuest(final String res){
        final Button cancelbtn;
        final Button nextbtn ;
        colorDialog.setContentView(R.layout.create_choose_color_popout);
        colorDialog.setCancelable(false);

        cancelbtn = colorDialog.findViewById(R.id.cancel2btn);
        nextbtn = colorDialog.findViewById(R.id.donebtn);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelbtn.setEnabled(false);
                nextbtn.setEnabled(false);

                mStore.collection("Users").document(user_id).update("Server_id","none").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                colorDialog.dismiss();
                sendToMain();
                    }
                });

            }
        });


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cancelbtn.setEnabled(false);
                nextbtn.setEnabled(false);

                if (color!=null) {

                    mStore.collection("Family_Calendar_Users").whereEqualTo("Color",color).whereEqualTo("Server_id",res).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean result = task.getResult().isEmpty();

                            if(!result){
                                Toast.makeText(FamilyCalendar.this,"This color is taken",Toast.LENGTH_LONG).show();
                                cancelbtn.setEnabled(true);
                                nextbtn.setEnabled(true);
                            }else {

                                Map<String, String> guestMap = new HashMap<>();

                                guestMap.put("user_id", user_id);
                                guestMap.put("Color", color);
                                guestMap.put("Type", "Guest");
                                guestMap.put("Server_id", res);
                                guestMap.put("ImageUrl", "none");


                                mStore.collection("Family_Calendar_Users").document(user_id).set(guestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(FamilyCalendar.this, "You joined the Family Calendar", Toast.LENGTH_LONG).show();
                                        colorDialog.dismiss();
                                        recreate();

                                    }
                                });
                            }
                }
            });
                             }


                    //and insert activities from private calendar
                else{
                    cancelbtn.setEnabled(true);
                    nextbtn.setEnabled(true);
                    Toast.makeText(FamilyCalendar.this, "Select a color", Toast.LENGTH_LONG).show();
                }
            }});


        colorDialog.show();
    }

    private void leaveCalendarAlert(){

        AlertDialog.Builder dial = new AlertDialog.Builder(FamilyCalendar.this);
        dial.setMessage("Are you sure you want to leave this Calendar?").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveCalendar();
                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dial.create();
        alert.setTitle("Leaving Calendar");
        alert.show();

    }

    public void leaveCalendar(){

        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               final String serv_id = task.getResult().getString("Server_id");

               if (!serv_id.equals(user_id)){

                   mStore.collection("Users").document(user_id).update("Server_id","none").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           mStore.collection("Family_Calendar_Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                   String url = task.getResult().getString("ImageUrl");

                                   if(url!=null){
                                       if(!url.equals("none")){
                                           mStorage.getReferenceFromUrl(url).delete();
                                       }
                                   }

                                   mStore.collection("Family_Calendar_Users").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {


                                           new CountDownTimer(1000, 1000) {

                                               public void onTick(long millisUntilFinished) {

                                               }

                                               public void onFinish() {
                                                   Toast.makeText(FamilyCalendar.this, "You left the Calendar", Toast.LENGTH_LONG).show();
                                                   sendToMain();
                                               }
                                           }.start();
                                       }
                                   });
                               }
                           });
                       }
                   });

               } else {

                        mStore.collection("Users").document(user_id).update("Server_id","none");

                   mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           for (QueryDocumentSnapshot document : task.getResult()) {

                               final String doc_id = document.getId();

                               mStore.collection("Family_Calendar_Users").document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                   @Override
                                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                      String url = task.getResult().getString("ImageUrl");
                                      String type = task.getResult().getString("Type");

                                       if(url!=null){
                                           if(!url.equals("none")){
                                               mStorage.getReferenceFromUrl(url).delete();
                                           }
                                       }

                                       mStore.collection("Family_Calendar_Users").document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               mStore.collection("Users").document(doc_id).update("Server_id","none");
                                           }
                                       });

                                       if (type.equals("Fake")) {
                                           mStore.collection("Activities").whereEqualTo("user_id", doc_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                   for (QueryDocumentSnapshot documents : task.getResult()) {

                                                       String doc = documents.getId();

                                                       mStore.collection("Activities").document(doc).delete();

                                                   }
                                               }
                                           });
                                       }
                                   }
                               });
                           }
                       }
                   });


                   mStore.collection("Family_Calendar_Shopping").whereEqualTo("Server_id", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                    String doc_id = document.getId();

                                   mStore.collection("Family_Calendar_Shopping").document(doc_id).delete();

                               }

                       }
                   });

                            mStore.collection("Family_Calendar_Invites").document(user_id).collection(user_id).whereEqualTo("req_type", "sent").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        final String doc_id = document.getId();

                                        mStore.collection("Family_Calendar_Invites").document(user_id).collection(user_id).document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                mStore.collection("Family_Calendar_Invites").document(doc_id).collection(doc_id).document(user_id).delete();

                                            }
                                        });
                                    }
                                }
                            });

                   mStore.collection("Family_Calendar_Chat").whereEqualTo("Server_id", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               String doc_id = document.getId();

                               mStore.collection("Family_Calendar_Chat").document(doc_id).delete();

                           }

                       }
                   });

                    mStore.collection("Family_Calendar_Album").whereEqualTo("Server_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String doc_id = document.getId();

                                mStore.collection("Family_Calendar_Album").document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        String url = task.getResult().getString("ImageUrl");

                                        if(url!=null){
                                            if(!url.equals("none")){
                                                mStorage.getReferenceFromUrl(url).delete();
                                            }
                                        }

                                        mStore.collection("Family_Calendar_Album").document(doc_id).delete();

                                    }
                                });

                            }
                        }
                    });

                   new CountDownTimer(2000, 1000) {

                       public void onTick(long millisUntilFinished) {

                       }

                       public void onFinish() {
                           Toast.makeText(FamilyCalendar.this, "Calendar Deleted", Toast.LENGTH_LONG).show();
                           sendToMain();
                       }
                   }.start();
               }
            }
        });

    }


    public void colorRadio(View v){
        RadioGroup group = colorDialog.findViewById(R.id.radiogroup);
        RadioButton btn;
        int radioButtonid = group.getCheckedRadioButtonId();
        btn = colorDialog.findViewById(radioButtonid);

        color= btn.getText().toString();

    }

    private void sendToMain(){
        Intent mainIntent = new Intent(FamilyCalendar.this , MainMenu.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToFamilyActivities(){
        Intent activitiesIntent = new Intent(FamilyCalendar.this , FamilyActivities.class);
        startActivity(activitiesIntent);
        finish();

    }

    private void sendToFamilyUsers() {
        Intent usersIntent = new Intent(FamilyCalendar.this, FamilyUsers.class);
        startActivity(usersIntent);
        finish();
    }

    private void sendToChat() {
        Intent chatIntent = new Intent(FamilyCalendar.this, FamilyChat.class);
        startActivity(chatIntent);
        finish();
    }

    private void sendToFamilyShopping() {
        Intent shoppingIntent = new Intent(FamilyCalendar.this, FamilyShopping.class);
        startActivity(shoppingIntent);
        finish();
    }

    private void sendToFamilyAlbum() {
        Intent albumIntent = new Intent(FamilyCalendar.this, FamilyAlbum.class);
        startActivity(albumIntent);
        finish();
    }

    private void sendToFamilyInvite() {
        Intent inviteIntent = new Intent(FamilyCalendar.this , FamilyInvite.class);
        startActivity(inviteIntent);
        finish();
    }
    private void sendToCreateUser(){
        Intent createUser = new Intent(FamilyCalendar.this,CreateUser.class);
        startActivity(createUser);
        finish();
    }

    public void addActivity2(){
        final Button cancelbtn;
        final Button addbtn ;
        final ImageButton datebtn;
        final ImageButton timebtn;
        final EditText descText;
        final TextView dateText;

        addDialog.setContentView(R.layout.activities_popup_user);
        addDialog.setCancelable(false);

        Spinner activitySpinner = addDialog.findViewById(R.id.activitySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.activitySpinner,R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                notificationItem = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        Spinner activitySpinner2 = addDialog.findViewById(R.id.activitySpinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.spinner_item2,options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner2.setAdapter(adapter2);
        activitySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                userItem = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        addbtn= addDialog.findViewById(R.id.activityDonebtn);
        cancelbtn= addDialog.findViewById(R.id.activityCancelbtn);
        datebtn= addDialog.findViewById(R.id.datebtn);
        timebtn= addDialog.findViewById(R.id.frombtn);
        descText= addDialog.findViewById(R.id.descText);
        dateText= addDialog.findViewById(R.id.dateView);

        dateText.setText(theDate);
        Date = theDate;

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datebtn.setEnabled(false);
                timebtn.setEnabled(false);
                cancelbtn.setEnabled(false);
                addbtn.setEnabled(false);

                final String desc = descText.getText().toString();

                mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String serv_id = task.getResult().getString("Server_id");

                        mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",serv_id).whereEqualTo("user_id",userItem).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.getResult().getDocuments().isEmpty()){
                                        id = user_id;
                                        check=true;
                                    }else{
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            final String doc_id = document.getId();
                                            id = doc_id;
                                            check=false;
                                        }
                                    }

                                    if (Date!=null&&Time!=null&&desc!=null) {

                                        final Calendar cal = Calendar.getInstance();

                                        java.util.Date date = null;
                                        final Date currentDate = new Date();

                                        final String dateandtime = Date + "/" + Time;

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm");

                                        try {
                                            date = dateFormat.parse(dateandtime);//catch exception
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                        if(date.before(currentDate)){
                                            datebtn.setEnabled(true);
                                            timebtn.setEnabled(true);
                                            cancelbtn.setEnabled(true);
                                            addbtn.setEnabled(true);

                                            Toast.makeText(getApplicationContext(),"This date has passed",Toast.LENGTH_LONG).show();
                                        }

                                        else {

                                            Date Date2 = null;

                                            try {
                                                Date2 = dateFormat.parse(dateandtime);//catch exception
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                            if (notificationItem.equals("One Hour Before")) {
                                                cal.setTime(Date2);
                                                cal.add(Calendar.HOUR, -1);
                                            } else if (notificationItem.equals("Two Hours Before")) {
                                                cal.setTime(Date2);
                                                cal.add(Calendar.HOUR, -2);
                                            } else if (notificationItem.equals("One Day Before")) {
                                                cal.setTime(Date2);
                                                cal.add(Calendar.DATE, -1);
                                            } else if (notificationItem.equals("Two Days Before")) {
                                                cal.setTime(Date2);
                                                cal.add(Calendar.DATE, -2);
                                            }

                                            mStore.collection("Activities").whereEqualTo("user_id", user_id).whereEqualTo("desc", desc).whereEqualTo("DateTime", dateandtime).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (!task.getResult().getDocuments().isEmpty()) {

                                                        Toast.makeText(FamilyCalendar.this, "This activity already exists on this time! Please choose a different description.", Toast.LENGTH_LONG).show();

                                                        datebtn.setEnabled(true);
                                                        timebtn.setEnabled(true);
                                                        cancelbtn.setEnabled(true);
                                                        addbtn.setEnabled(true);


                                                    } else {

                                                    final String tag = id + desc + dateandtime;

                                                        if (!notificationItem.equals("No Notification")) {

                                                            if (cal.getTime().before(currentDate)) {
                                                                datebtn.setEnabled(true);
                                                                timebtn.setEnabled(true);
                                                                cancelbtn.setEnabled(true);
                                                                addbtn.setEnabled(true);

                                                                Toast.makeText(getApplicationContext(), "Notification date has passed", Toast.LENGTH_LONG).show();
                                                            }

                                                            Calendar today = Calendar.getInstance();

                                                            long diff = (cal.getTimeInMillis() - today.getTimeInMillis());

                                                            long mins = diff / (60 * 1000);

                                                            final int random = (int) (Math.random() * 50 + 1);

                                                            final long alertTime = getAlertTime((int) mins) - System.currentTimeMillis();

                                                            if(!check) {
                                                                //Data
                                                                Data data = createWorkInputData(desc, userItem+" - " +desc + " at : " + dateandtime, random);

                                                                NotificationHandler.scheduleReminder(alertTime + 30000, data, tag);
                                                            }
                                                            else{
                                                                mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                        String name = task.getResult().getString("Username");

                                                                        //Data
                                                                        Data data = createWorkInputData(desc, name+" - "+desc + " at : " + dateandtime, random);

                                                                        NotificationHandler.scheduleReminder(alertTime + 30000, data, tag);
                                                                    }
                                                                });
                                                            }


                                                        }

                                                        Map<String, String> addMap = new HashMap<>();

                                                        addMap.put("user_id", id);
                                                        addMap.put("Date", Date);
                                                        addMap.put("Time", Time);
                                                        addMap.put("desc", desc);
                                                        addMap.put("DateTime", dateandtime);


                                                        mStore.collection("Activities").add(addMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                datebtn.setEnabled(true);
                                                                timebtn.setEnabled(true);
                                                                cancelbtn.setEnabled(true);
                                                                addbtn.setEnabled(true);

                                                                Toast.makeText(getApplicationContext(), "Activity added!", Toast.LENGTH_LONG).show();

                                                                addDialog.dismiss();
                                                                recreate();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        datebtn.setEnabled(true);
                                        timebtn.setEnabled(true);
                                        cancelbtn.setEnabled(true);
                                        addbtn.setEnabled(true);

                                        Toast.makeText(getApplicationContext(), "Please complete all fields", Toast.LENGTH_LONG).show();
                                    }
                            }
                        });
                    }
                });
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datebtn.setEnabled(false);
                timebtn.setEnabled(false);
                cancelbtn.setEnabled(false);
                addbtn.setEnabled(false);
                viewDate.setEnabled(true);
                add.setEnabled(true);

                addDialog.dismiss();
            }
        });

        addDialog.show();
    }

    private void pickDate(){

        DatePickerDialog dialog = new DatePickerDialog(this,this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String day1;
        String month1;

        day1=String.valueOf(dayOfMonth);
        month1=String.valueOf(month+1);

        if(dayOfMonth<=9){
            day1="0"+String.valueOf(dayOfMonth);
        }
        if (month+1<=9){
            month1="0" + String.valueOf(month+1);
        }
        String date = day1+"-"+month1+"-"+year;
        TextView showDate = addDialog.findViewById(R.id.dateView);
        showDate.setText(date);
        Date= day1+"-"+month1+"-"+year;

    }

    private void pickTime(){
        Calendar calendar =  Calendar.getInstance();

        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this,this,hour,min,android.text.format.DateFormat.is24HourFormat(this));
        dialog.show();

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        TextView fromView = addDialog.findViewById(R.id.fromView);

        String hour1;
        String min1;

        hour1=String.valueOf(hourOfDay);
        min1=String.valueOf(minute);

        if(hourOfDay<=9){
            hour1="0"+String.valueOf(hourOfDay);
        }

        if(minute<=9){
            min1="0"+String.valueOf(minute);
        }

        fromView.setText(hour1 + ":" + min1);
        Time = hour1 + ":" + min1;
    }

    private Data createWorkInputData(String title, String text, int id){
        return new Data.Builder()
                .putString(NotificationConst.EXTRA_TITLE, title)
                .putString(NotificationConst.EXTRA_TEXT, text)
                .putInt(NotificationConst.EXTRA_ID, id)
                .build();
    }

    private long getAlertTime(int userInput){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, userInput);
        return cal.getTimeInMillis();
    }


    @Override
    public void onBackPressed() {
        sendToMain();
    }

    private void checkActivities(){

        mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    final String doc_id = document.getId();

                    mStore.collection("Family_Calendar_Users").document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String type = task.getResult().getString("Type");

                            if (type.equals("Fake")) {
                                mStore.collection("Activities").whereEqualTo("user_id", doc_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot documents : task.getResult()) {

                                            final String doc = documents.getId();

                                            mStore.collection("Activities").document(doc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    String date = task.getResult().getString("DateTime");

                                                    Date thisDate = null;
                                                    Date currentDate = new Date();

                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm");

                                                    try {
                                                        thisDate = dateFormat.parse(date);//catch exception
                                                    } catch (Exception e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                    if(thisDate.before(currentDate)){
                                                        mStore.collection("Activities").document(doc).delete();
                                                    }

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


}


package com.gmail.epsilon1011.busyfamily;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.work.Data;

public class FamilyViewActivities extends AppCompatActivity implements AdapterView.OnItemSelectedListener , DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener {

    private Toolbar FamilyViewActivitiesToolbar;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private Dialog addDialog;
    private String notificationItem;
    private String Date;
    private String Time;
    private String user_id;
    private String type;
    private String server_id;
    private FloatingActionButton floatbtn;
    private RecyclerView FamilyActivitiesView;
    private List<FamilyViewActivitiesItem> activitiesList;
    private FamilyViewActivitiesAdapter myActivitiesAdapter;
    public boolean check2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_view_activities);

        String languageToLoad  = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        FamilyViewActivitiesToolbar = findViewById(R.id.familyViewActivitiesToolbar);
        setSupportActionBar(FamilyViewActivitiesToolbar);
        FamilyViewActivitiesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyActivities();
            }
        });

        floatbtn= findViewById(R.id.addActivitybtn2);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        addDialog = new Dialog(this);

        notificationItem = "No Notification";

        user_id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        server_id = getIntent().getStringExtra("serv_id");

        FamilyActivitiesView = findViewById(R.id.familyActivitiesView);
        FamilyActivitiesView.setLayoutManager(new LinearLayoutManager(this));
        FamilyActivitiesView.setHasFixedSize(true);

        //model
        activitiesList = new ArrayList<>();
        //adapter
        myActivitiesAdapter = new FamilyViewActivitiesAdapter(activitiesList);
        //set adapter to recycler view
        FamilyActivitiesView.setAdapter(myActivitiesAdapter);

        if(type!=null) {
            if (type.equals("Fake")) {
                getSupportActionBar().setTitle(user_id + "'s Activities");

            } else {
                if(type.equals("Guest")) {
                    floatbtn.hide();
                }

                mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String username = task.getResult().getString("Username");
                        getSupportActionBar().setTitle(username + "'s Activities");
                    }
                });
            }
        }
        if(server_id!=null) {
            mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id", server_id).whereEqualTo("user_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        final String doc_id = document.getId();

                        mStore.collection("Activities").whereEqualTo("user_id",doc_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent( QuerySnapshot documentSnapshots,  FirebaseFirestoreException e) {

                                if (documentSnapshots != null) {
                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                        if (doc.getType() == DocumentChange.Type.ADDED) {

                                            FamilyViewActivitiesItem list = doc.getDocument().toObject(FamilyViewActivitiesItem.class);
                                            activitiesList.add(list);
                                            myActivitiesAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    private void sendToFamilyActivities(){
        Intent activitiesIntent = new Intent(FamilyViewActivities.this,FamilyActivities.class);
        startActivity(activitiesIntent);
        finish();
    }

    public void addActivity2(View v){
        final Button cancelbtn;
        final Button addbtn ;
        final ImageButton datebtn;
        final ImageButton timebtn;
        final EditText descText;

        addDialog.setContentView(R.layout.activities_popup);
        addDialog.setCancelable(false);

        Spinner activitySpinner = addDialog.findViewById(R.id.activitySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.activitySpinner,R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);
        activitySpinner.setOnItemSelectedListener(this);

        addbtn= addDialog.findViewById(R.id.activityDonebtn);
        cancelbtn= addDialog.findViewById(R.id.activityCancelbtn);
        datebtn= addDialog.findViewById(R.id.datebtn);
        timebtn= addDialog.findViewById(R.id.frombtn);
        descText= addDialog.findViewById(R.id.descText);

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

                    mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id", server_id).whereEqualTo("user_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String id;

                                if(!type.equals("Fake")){
                                    id = user_id;
                                    check2=true;
                                }else {
                                    id = document.getId();
                                    check2=false;
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

                                            mStore.collection("Activities").whereEqualTo("user_id", id).whereEqualTo("desc", desc).whereEqualTo("DateTime", dateandtime).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (!task.getResult().getDocuments().isEmpty()) {

                                                        Toast.makeText(FamilyViewActivities.this, "This activity already exists on this time! Please choose a different description.", Toast.LENGTH_LONG).show();

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

                                                                if(user_id.equals(mAuth.getCurrentUser().getUid())) {

                                                                    mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                            String name = task.getResult().getString("Username");

                                                                            //Data
                                                                            Data data = createWorkInputData(desc, name+" - "+desc + " at : " + dateandtime, random);

                                                                            NotificationHandler.scheduleReminder(alertTime + 30000, data, tag);
                                                                        }
                                                                    });
                                                                }else{

                                                                    Data data = createWorkInputData(desc, user_id+" - "+desc + " at : " + dateandtime, random);

                                                                    NotificationHandler.scheduleReminder(alertTime + 30000, data, tag);
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
                                else{
                                    datebtn.setEnabled(true);
                                    timebtn.setEnabled(true);
                                    cancelbtn.setEnabled(true);
                                    addbtn.setEnabled(true);

                                    Toast.makeText(getApplicationContext(),"Please complete all fields",Toast.LENGTH_LONG).show();
                                }
                            }
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

                addDialog.dismiss();
            }
        });

        addDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        notificationItem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        sendToFamilyActivities();
    }

}

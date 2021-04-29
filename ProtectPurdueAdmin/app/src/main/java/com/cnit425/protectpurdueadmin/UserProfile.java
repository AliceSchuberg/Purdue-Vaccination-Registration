package com.cnit425.protectpurdueadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    private String uid;
    private Boolean vaccinated;
    private Integer vaccineCount;
    private ArrayList<Dose> doseList;

    private ValueEventListener vaccinationListener;
    private ValueEventListener locationValueListener;
    private Dose_LL_Adapter listViewAdapter;

    private DatabaseReference vaccinationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //get uid
        uid = getIntent().getStringExtra("uid");

        //display email
        String email = getIntent().getStringExtra("email");
        ((TextView)findViewById(R.id.txtEmail)).setText(email);

        //set vaccinated_checked listener
        CheckBox chkBoxVaccinated = findViewById(R.id.chkBoxVaccinated);
        chkBoxVaccinated.setOnCheckedChangeListener((buttonView, isChecked) -> vaccinated = isChecked);

        //set vaccine_count listener
        EditText txtCount = findViewById(R.id.txtVaccineCount);
        txtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    vaccineCount = Integer.parseInt(s.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        doseList = new ArrayList<>();
        listViewAdapter = new Dose_LL_Adapter(this,doseList);
        ((ListView)findViewById(R.id.listView)).setAdapter(listViewAdapter);

        vaccinationRef = FirebaseDatabase.getInstance().getReference("user").child(uid).child("Vaccination");
        //defined ValueListener for location/<location_Serial> which contains "name" \ "address"
        locationValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //retrieve the location_name & address from location_serial node
                String location_name = snapshot.child("name").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                //find the dose info object, and fill in the specific location name and address
                for (Dose dose: doseList){
                    if(dose.getLocation().equals(snapshot.getKey())){
                        dose.setLocation_name(location_name);
                        dose.setAddress(address);
                    }
                }
                //notify the adapter the dataList has changed
                listViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        FirebaseDatabase.getInstance().getReference("user").child(uid).child("Vaccination")
                .get().addOnCompleteListener(task -> {
            for (DataSnapshot shot: Objects.requireNonNull(task.getResult()).getChildren()){
                if (Objects.equals(shot.getKey(), "Vaccinated")){
                    //get vaccinated data and set it to text
                    vaccinated = shot.getValue(Boolean.class);
                    ((CheckBox)findViewById(R.id.chkBoxVaccinated)).setChecked(vaccinated);
                }else if(Objects.equals(shot.getKey(), "VaccineCount")){
                    //get vaccineCount data and set it to text
                    vaccineCount = shot.getValue(Integer.class);
                    System.out.println(vaccineCount);
                    ((TextView)findViewById(R.id.txtVaccineCount)).setText(String.valueOf(vaccineCount));
                }else{
                    //get Dose node data and convert it to a Dose object, add to arrayList to update the adapter
                    Dose newDose = shot.getValue(Dose.class);
                    newDose.setDose_num(shot.getKey());
                    doseList.add(newDose);
                    //notify the adapter the dataList has changed
                    listViewAdapter.notifyDataSetChanged();
                    //run on another thread to retrieve specific info for location
                    new Thread(() -> {
                        String location_Serial = newDose.getLocation();
                        //string check
                        if(location_Serial == null || !location_Serial.startsWith("location")){
                            Toast.makeText(getApplicationContext(),
                                    "location_serial_error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseDatabase.getInstance().getReference("location").child(location_Serial)
                                .addValueEventListener(locationValueListener);
                    }).start();
                }
            }
        });
        //define childValueListener for user/<uid>/Vaccination which contains "Vaccinated" \ "VaccineCount" \ "Dose<i>"


    }

    @Override
    protected void onPause() {
//        if(vaccinationRef!=null && vaccinationListener != null){
//            vaccinationRef.removeEventListener(vaccinationListener);
//        }
        if (locationValueListener!= null){
            for (Dose dose: doseList){
                String location = dose.getLocation();
                FirebaseDatabase.getInstance().getReference("location").child(location)
                        .removeEventListener(locationValueListener);
            }
        }
        super.onPause();
    }

    public void btnConfirmOnClick(View view){
        vaccinationRef.child("Vaccinated").setValue(vaccinated);
        vaccinationRef.child("VaccineCount").setValue(vaccineCount);

        for (Dose dose :doseList){
            String dose_serial = dose.getDose_num();
            vaccinationRef.child(dose_serial).child("date").setValue(dose.getDate());
            vaccinationRef.child(dose_serial).child("time").setValue(dose.getTime());
            vaccinationRef.child(dose_serial).child("completed").setValue(dose.getCompleted());
            vaccinationRef.child(dose_serial).child("vaccineType").setValue(dose.getVaccineType());
            vaccinationRef.child(dose_serial).child("vaccineSerial").setValue(dose.getVaccineSerial());
            vaccinationRef.child(dose_serial).child("vaccineExpDate").setValue(dose.getVaccineExpDate());
        }

        finish();
    }
}
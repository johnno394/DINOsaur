package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText firstName, lastName, email, password, parentsEmail;
    Button registerBtn;
    CheckBox isTeacherBox, isStudentBox;
    TextView haveAccountTv;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        firstName = findViewById(R.id.registerFirstName);
        lastName = findViewById(R.id.registerLastName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        registerBtn = findViewById(R.id.registerBtn);
        haveAccountTv = findViewById(R.id.haveAccountTV);
        parentsEmail = findViewById(R.id.registerParents);

        isTeacherBox = findViewById(R.id.isTeacher);
        isStudentBox =findViewById(R.id.isStudent);

        isStudentBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    isTeacherBox.setChecked(false);
                }
            }
        });

        isTeacherBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    isStudentBox.setChecked(false);
                }
            }
        });


        haveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //can add if statements to make sure all fields are filled here later or in the create user method

                if(!(isTeacherBox.isChecked() || isStudentBox.isChecked())){
                    Toast.makeText(RegisterActivity.this, "Please select teacher or student", Toast.LENGTH_SHORT).show();
                    return;
                }


                createUser();
            }
        });


    }

    private void createUser(){

        fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                 FirebaseUser user = fAuth.getCurrentUser();
                 Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();

                 DocumentReference df = fStore.collection("Users").document(user.getUid());
                 Map<String,Object> userInfo = new HashMap<>();
                 userInfo.put("FirstName", firstName.getText().toString());
                 userInfo.put("LastName", lastName.getText().toString());
                 userInfo.put("Email", email.getText().toString());
                 userInfo.put("UID", fAuth.getUid());

                 //teacher vs student
                if(isTeacherBox.isChecked()){
                    userInfo.put("isTeacher", "1");

                }else if(isStudentBox.isChecked()) {
                    userInfo.put("isTeacher", "0");
                    userInfo.put("MoodProgress", 0);
                    userInfo.put("Parents Email", parentsEmail.getText().toString());
                    userInfo.put("latest_emotional_rating",0);
                    userInfo.put("latest_intensity_of_emotion", 0);
                    userInfo.put("latest_Timestamp", null);
                    userInfo.put("latest_additional_comments", null);

                }

                df.set(userInfo);



                 startActivity(new Intent(getApplicationContext(), MainActivity.class));
                 finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
            }
        });

    }



}
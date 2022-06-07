package com.example.infs3605_group_assignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class teacher_profileFragment extends Fragment {

    public FirebaseFirestore fStore;
    public FirebaseAuth mAuth;
    EditText updateEmail, updatePassword, oldPassword;
    Button saveBtn;
    String newEmail, newPassword, oldpwd;
    TextView profileName, logouttv;

    public static final String TAG = "teacher_ProfileFragment";

    FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateEmail = view.findViewById(R.id.updateEmail2);
        updatePassword = view.findViewById(R.id.newpassword2);
        saveBtn = view.findViewById(R.id.btnSave2);
        oldPassword = view.findViewById(R.id.oldpassword2);
        profileName = view.findViewById(R.id.profileName2);


        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        DocumentReference documentReference = db.collection("Users").document(fAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, documentSnapshot.getString("FirstName"));
                profileName.setText(documentSnapshot.getString("FirstName") + " "  + documentSnapshot.getString("LastName"));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newEmail = updateEmail.getText().toString();
                oldpwd = oldPassword.getText().toString();
                newPassword = updatePassword.getText().toString();

                DocumentReference docRef = db.collection("Users").document(fAuth.getCurrentUser().getUid());



                Log.d(TAG, user.getEmail());
                Log.d(TAG, oldpwd);

                AuthCredential credential =  EmailAuthProvider.getCredential(user.getEmail(), oldpwd);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(newEmail.matches("")){
                                user.updatePassword(newPassword);
                                Toast.makeText(getActivity(), "Password Changed Successfully", Toast.LENGTH_LONG ).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));

                            }else {


                                Log.d("NEW EMAIL", newEmail);
                                user.updateEmail(newEmail);
                                user.updatePassword(newPassword);
                                docRef.update("Email", newEmail);

                                Toast.makeText(getActivity(), "Details updated successfully, returning to login screen", Toast.LENGTH_LONG).show();

                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }

                        }else{
                            Toast.makeText(getActivity(), "Failed to update details", Toast.LENGTH_LONG ).show();
                        }
                    }
                });




            }
        });


        logouttv = view.findViewById(R.id.logoutTV);

        logouttv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                fAuth.getInstance().signOut();
                                startActivity(new Intent(getActivity(), MainActivity.class));

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to log out?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });








    }

}
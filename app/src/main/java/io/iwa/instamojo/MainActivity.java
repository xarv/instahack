package io.iwa.instamojo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class MainActivity extends BaseActivity {

    private AppCompatButton saveInfoButton;
    private EditText nameView;
    private EditText phoneView;
    private String email;
    private String name;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        saveInfoButton = (AppCompatButton)findViewById(R.id.update_info_button);
        final TextView emailTextView = (TextView)findViewById(R.id.email);
        nameView = (EditText)findViewById(R.id.name);
        phoneView = (EditText)findViewById(R.id.phone);


        Button editNameButton = (Button)findViewById(R.id.edit_name_button);
        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameView.setEnabled(true);
                nameView.requestFocus();
                saveInfoButton.setVisibility(View.VISIBLE);
            }
        });

        Button editPhoneButton = (Button)findViewById(R.id.edit_phone_button);
        editPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneView.setEnabled(true);
                phoneView.requestFocus();
                saveInfoButton.setVisibility(View.VISIBLE);
            }
        });
        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        Firebase usersRef = CustomApplication.firebase.child("users").child(CustomApplication.firebase.getAuth().getUid());


        usersRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               User user = null;
               try {
                  user = dataSnapshot.getValue(User.class);
               }
               catch (Exception e){
                   e.printStackTrace();
               }
               if(user != null) {
                   email = user.getEmail();
                   if(email != null) {
                       emailTextView.setText(email);
                   }
                   name = user.getName();
                   if(name != null){
                       nameView.setText(name);
                   }
                   phone = user.getPhoneNumber();
                   if(phone != null){
                       phoneView.setText(phone);
                   }
               }
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
       });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ContactDisplayActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomApplication.firebase.unauth();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateUserData() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Updating details.");
        pd.show();
        final User user = new User(email);
        user.setName(nameView.getText().toString());
        user.setPhoneNumber(phoneView.getText().toString());
        final Firebase usersRef = CustomApplication.firebase.child("users").child(CustomApplication.firebase.getAuth().getUid());
        usersRef.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError == null) {
                    usersRef.setValue(user, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) {
                                Toast.makeText(MainActivity.this, "Info Updated Successfully", Toast.LENGTH_SHORT).show();
                                nameView.setEnabled(false);
                                phoneView.setEnabled(false);
                                saveInfoButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Info Updated Failed,Try Again", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                        }
                    });
                }
            }
        });


    }

}

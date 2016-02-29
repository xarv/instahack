package io.iwa.instamojo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class LoginActivity extends BaseActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(Constants.SIGN_IN);
                    return true;
                }
                return false;
            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin(Constants.SIGN_IN);
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(Constants.SIGN_UP);
            }
        });
    }

    private void attemptLogin(int mode) {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            if(mode == Constants.SIGN_UP) {
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setIndeterminate(true);
                pd.setMessage("Creating User.");
                pd.show();
                CustomApplication.firebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Toast.makeText(LoginActivity.this, "Successfully created user account", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        hideSoftKeyBoard();

                        User user = new User(email);
                        Firebase usersRef = CustomApplication.firebase.child("users").child((String) result.get("uid"));
                        usersRef.setValue(user);
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra(Constants.MODE,Constants.SIGN_UP);
                        startActivity(intent);
                        finish();

                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast.makeText(LoginActivity.this, "There was an error creating user account try again.", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        hideSoftKeyBoard();
                    }
                });
            }
            else{
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setIndeterminate(true);
                pd.setMessage("Logging User In.");
                pd.show();
                CustomApplication.firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Toast.makeText(LoginActivity.this, "Successfully signed in.", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        hideSoftKeyBoard();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra(Constants.MODE,Constants.SIGN_IN);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(LoginActivity.this, "There was an error signing in.", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        hideSoftKeyBoard();
                    }
                });
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}

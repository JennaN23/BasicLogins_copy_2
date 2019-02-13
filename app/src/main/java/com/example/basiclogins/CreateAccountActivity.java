package com.example.basiclogins;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonCreateAccount;
    public static final String EXTRA_USERNAME = "";
    public static final String EXTRA_PASSWORD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        wireWidgets();
        prefillUserName();

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    registerAccountOnBackendless();
                }

        });


    }

    private void registerAccountOnBackendless() {
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String name = editTextName.getText().toString();

        if (AllFieldsValid(password, confirmPassword, username, email, name)){
            BackendlessUser user = new BackendlessUser();
            user.setProperty("email", email);
            user.setPassword(password);
            user.setProperty("username", username);
            user.setProperty("name", name);

            Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>()
            {
                public void handleResponse( BackendlessUser registeredUser )
                {
                    // user has been registered and now can login
                    Toast.makeText(CreateAccountActivity.this, registeredUser.getUserId() + " has registered", Toast.LENGTH_SHORT).show();
                    finish();
                }

                public void handleFault( BackendlessFault fault )
                {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                    Toast.makeText(CreateAccountActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } );
        }
    }

    private boolean AllFieldsValid(String password, String confirmPassword, String username, String email, String name) {
        return password.equals(confirmPassword) && username.length() > 0;
    }

    private void prefillUserName() {
        String username = getIntent().getStringExtra(LoginActivity.EXTRA_USERNAME);
        if(username != null) {
            editTextUsername.setText(username);
        }
    }

    private void wireWidgets() {
        editTextName = findViewById(R.id.editText_create_name);
        editTextUsername = findViewById(R.id.editText_create_username);
        editTextEmail = findViewById(R.id.editText_create_email);
        editTextPassword = findViewById(R.id.editText_create_password);
        editTextConfirmPassword = findViewById(R.id.editText_create_confirmpassword);
        editTextUsername.setText(getIntent().getStringExtra(LoginActivity.EXTRA_USERNAME));
        buttonCreateAccount = findViewById(R.id.button_create_createaccount);
    }
}

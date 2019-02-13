package com.example.basiclogins;

import android.app.Activity;
import android.content.Intent;
import android.net.Credentials;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonCreateAccount;
    private EditText editTextUsername;
    private EditText editTextPassword;
    public static final String EXTRA_USERNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wireWidgets();

        Backendless.initApp(this, com.example.basiclogins.Credentials.APP_ID, com.example.basiclogins.Credentials.API_KEY);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToBackendless();
            }
        });
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreateNewAccount = new Intent(LoginActivity.this, CreateAccountActivity.class);
                if(editTextUsername.getText().toString().length() > 0) {
                    intentCreateNewAccount.putExtra(EXTRA_USERNAME, editTextUsername.getText().toString()); }
                else {
                    Toast.makeText(LoginActivity.this, "enter a username", Toast.LENGTH_SHORT).show();
                }
                if(editTextPassword.getText().toString().length() <= 0) {
                    Toast.makeText(LoginActivity.this, "enter a password", Toast.LENGTH_SHORT).show();
                }
                startActivity(intentCreateNewAccount);

            }
        });

    }

    private void loginToBackendless() {
        String login = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        Backendless.UserService.login(login, password,
                new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(LoginActivity.this, response.getEmail() + " logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, RestaurantListActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void wireWidgets() {
        buttonLogin = findViewById(R.id.button_login_login);
        editTextUsername = findViewById(R.id.editText_login_username);
        editTextPassword = findViewById(R.id.editText_login_password);
        buttonCreateAccount = findViewById(R.id.button_login_createAccount);
    }
}

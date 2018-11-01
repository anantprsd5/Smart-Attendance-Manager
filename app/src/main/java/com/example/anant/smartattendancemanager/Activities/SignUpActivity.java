package com.example.anant.smartattendancemanager.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.Presenter.LoginPresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.LoginView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements LoginView {

    // UI references.

    @BindView(R.id.email_sign_in_button)
    AppCompatButton mSignUpView;
    @BindView(R.id.input_name)
    EditText mNameView;
    @BindView(R.id.input_email)
    EditText mEmailView;
    @BindView(R.id.input_password)
    EditText mPasswordView;
    @BindView(R.id.link_signin)
    TextView mSingInTextView;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private LoginPresenter loginPresenter;
    View focusView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginPresenter = new LoginPresenter(mAuth, this, this);
        loginPresenter.setBackgroundResource();

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                if (isInternetConnected())
                    attemptLogin();
                else
                    Toast.makeText(SignUpActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        mSignUpView.setOnClickListener(view -> {
            if (isInternetConnected())
                attemptLogin();
            else
                Toast.makeText(SignUpActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
        });

        mSingInTextView.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        if (isInternetConnected())
            loginPresenter.signUp(email, password, name, mDatabase);
        else
            Toast.makeText(SignUpActivity.this, R.string.check_internet,
                    Toast.LENGTH_SHORT).show();
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void OnSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailed() {
        Toast.makeText(this, R.string.account_creation_error
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInvalidPassword() {
        mPasswordView.setError(getString(R.string.error_invalid_password));
        focusView = mPasswordView;
        focusView.requestFocus();

    }

    @Override
    public void showInvalidEmail() {
        mEmailView.setError(getString(R.string.error_invalid_email));
        focusView = mEmailView;
        focusView.requestFocus();
    }

    @Override
    public void showEmailFieldRequired() {
        mEmailView.setError(getString(R.string.error_field_required));
        focusView = mEmailView;
        focusView.requestFocus();
    }

    @Override
    public void showNameFieldRequired() {
        mNameView.setError(getString(R.string.error_field_required));
        focusView = mNameView;
        focusView.requestFocus();
    }

    @Override
    public void toggleProgressVisibility(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSignUpView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSignUpView.setVisibility(View.VISIBLE);
        }
    }
}


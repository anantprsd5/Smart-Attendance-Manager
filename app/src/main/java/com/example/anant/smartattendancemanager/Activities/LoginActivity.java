package com.example.anant.smartattendancemanager.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.Presenter.LoginPresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.LoginView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginView {

    // UI references.
    @BindView(R.id.input_email)
    EditText mEmailView;
    @BindView(R.id.input_password)
    EditText mPasswordView;
    @BindView(R.id.link_forgot_password)
    TextView mForgotPassView;
    @BindView(R.id.link_signup)
    TextView mSignUpView;
    @BindView(R.id.email_sign_in_button)
    AppCompatButton mEmailSignInButton;
    @BindView(R.id.logo_image_view)
    ImageView mImageView;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private LoginPresenter loginPresenter;
    View focusView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        loginPresenter = new LoginPresenter(mAuth, this, this);

        loginPresenter.setBackgroundResource();

        mEmailSignInButton.setOnClickListener(v -> {

            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            attemptLogin();

        });

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mForgotPassView.setOnClickListener(view -> {

            String email = mEmailView.getText().toString();
            loginPresenter.sendResetPasswordEmail(email);

        });

        mSignUpView.setOnClickListener(v -> loginPresenter.startSignUpActivity(mImageView));
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (isInternetConnected())
            loginPresenter.signIn(email, password);
        else
            Toast.makeText(LoginActivity.this, R.string.check_internet,
                    Toast.LENGTH_SHORT).show();
    }

    private void startApplication() {
        loginPresenter.getSubjects();

    }

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void OnSuccess() {
        startApplication();
    }

    @Override
    public void onFailed() {
        Toast.makeText(this, R.string.error_incorrect_password,
                Toast.LENGTH_SHORT).show();
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
        //Not required for logging In
    }

    @Override
    public void toggleProgressVisibility(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
            mEmailSignInButton.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmailSignInButton.setVisibility(View.VISIBLE);
        }
    }

}


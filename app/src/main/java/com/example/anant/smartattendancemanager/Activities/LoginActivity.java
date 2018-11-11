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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.Presenter.LoginPresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.LoginView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final int RC_SIGN_IN = 1000;
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
    @BindView(R.id.google_sign_in)
    GoogleSignInButton googleSignInButton;

    private FirebaseAuth mAuth;
    private LoginPresenter loginPresenter;
    View focusView = null;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginPresenter = new LoginPresenter(mAuth, this, this, mDatabase);

        loginPresenter.setBackgroundResource();

        mGoogleSignInClient = GoogleSignIn.getClient(this, loginPresenter.configueGoogleClient());

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

        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
            toggleProgressVisibility(true);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginPresenter.firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                toggleProgressVisibility(false);
                Toast.makeText(LoginActivity.this, R.string.login_failed_message, Toast.LENGTH_SHORT)
                        .show();
                // ...
            }
        }
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
            mProgressBar.requestFocus();
            mEmailSignInButton.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmailSignInButton.setVisibility(View.VISIBLE);
        }
    }

}


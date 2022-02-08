package ca.cheems.iotproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private Button btnlog, btnreg;
    private EditText etxtedituser, etxteditpass;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private ImageButton btnglogin;
    private CheckBox checkboxrem;
    private boolean sw, user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        res();
        createRequest();
        SaveState();
        btnlog.setOnClickListener(view ->
        {
            login();



        });
        btnreg.setOnClickListener(view ->
        {
            register();
        });
        /*findViewById(R.id.txtforgetpass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPassActivity.class));
            }
        });

         */


    }

    private void SaveState()
    {
        sharedPreferences = this.getSharedPreferences("SmartCity", Context.MODE_PRIVATE);
        sw = sharedPreferences.getBoolean("Switch", false);
        user = sharedPreferences.getBoolean("checkboxrem", false);
        String email, pass;
        email = sharedPreferences.getString("email", "");
        pass = sharedPreferences.getString("pass", "");
        if (sw == false)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (user == false)
        {
            etxtedituser.setText("");
            etxteditpass.setText("");
            checkboxrem.setChecked(false);
        } else
        {
            checkboxrem.setChecked(true);
            etxtedituser.setText(email);
            etxteditpass.setText(pass);
        }
    }

    private void createRequest()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        btnglogin.setOnClickListener(view ->
        {
            signIn();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null)
            {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
            }
            // Signed in successfully, show authenticated UI.
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("signInResult:failed code=", e.toString());
        }
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void res()
    {
        etxtedituser = (EditText) findViewById(R.id.txteditusername);
        etxteditpass = (EditText) findViewById(R.id.txteditpassword);
        btnlog = (Button) findViewById(R.id.bntlogin);
        btnreg = (Button) findViewById(R.id.bntregister);
        btnglogin = (ImageButton) findViewById(R.id.imgbntglogin);
        checkboxrem = (CheckBox) findViewById(R.id.checkboxremember);
    }

    private void login()
    {
        sharedPreferences = this.getSharedPreferences("SmartCity", Context.MODE_PRIVATE);
        String email, pass;
        email = etxtedituser.getText().toString();
        pass = etxteditpass.getText().toString();
        if (email.isEmpty() && pass.isEmpty())
        {
            etxtedituser.setError("This email can not be blank");
            etxteditpass.setError("This password can not be blank");
        }
        if (email.isEmpty())
        {
            etxtedituser.setError("This email can not be blank");
        }
        if (pass.isEmpty())
        {
            etxteditpass.setError("This password can not be blank");
        } else
        {
            if (!isEmailValid(email))
            {
                etxtedituser.setError("This look like an incorrect address");
                return;
            }
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("pass", pass);
                        if (checkboxrem.isChecked())
                        {
                            editor.putBoolean("checkboxrem", true);
                        }
                        else
                        {
                            editor.putBoolean("checkboxrem", false);
                        }
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else
                    {
                        AlertDialog.Builder dialogb = Dialogb(task.getException().getMessage().toString());
                        dialogb.show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            NotificationChannel channel = new NotificationChannel("Login", "Login", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }
                    }
                }
            });
        }
    }


    //Build Dialog
    private AlertDialog.Builder Dialogb(String mess)
    {
        AlertDialog.Builder builderd = new AlertDialog.Builder(LoginActivity.this);
        builderd.setCancelable(true);
        builderd.setMessage(mess);
        builderd.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        return builderd;
    }

    //use to check email valid or not
    private static boolean isEmailValid(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Function to load intent register
    private void register()
    {
        Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent1);
    }

}
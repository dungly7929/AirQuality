package ca.cheems.iotproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    private EditText editTextfullname, editTextpassword, editTextpassword1, editTextemail, editTextphone;
    private TextView txtpasswords;
    private boolean pass, passcof, emailc, phonev, fname;
    private String password, fullname, phone, email, password1;
    private Button bntSubmit;
    private DatabaseReference reff;
    private FirebaseAuth mAuth;
    private User user;
    private ProgressBar progressBar;
    long maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_register);
        reff = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        user = new User();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channen1 = new NotificationChannel("Smart City", "Smart City", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channen1);
        }
        regid();
        ValidFullnamecheck();
        Validpasswordcheck();
        Validpasswordconfirm();
        ValidEmailcheck();
        ValidPhone();
        //Use to control the submit button on Register form
        bntSubmit.setOnClickListener(view ->
        {
            AESCrypt aesCrypt = new AESCrypt();
            if (fname == true && pass == true && passcof == true && emailc == true && phonev == true)
            {
                user.setEmail(email);
                user.setFullname(fullname);
                try
                {
                    user.setPassword(aesCrypt.encrypt(password));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                user.setPhoneNo(phone);
                emailreg(user);
            } else
            {
                Toast.makeText(this, "Failed to submitted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ValidFullnamecheck()
    {
        fname = false;
        editTextfullname.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.toString().isEmpty())
                {
                    editTextfullname.setError("This feild can not empty");
                    fname = false;
                } else
                {
                    fullname = editable.toString();
                    fname = true;
                }
            }
        });
    }

    private void ValidEmailcheck()
    {
        emailc = false;
        editTextemail.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (!editable.toString().isEmpty())
                {
                    try
                    {
                        if (!!isEmailValid(editable.toString()))
                        {
                            email = editable.toString();
                            emailc = true;
                        } else
                        {
                            editTextemail.setError("The email type not correct !");
                            emailc = false;
                        }
                    } catch (Exception e)
                    {

                    }
                }
            }
        });
    }

    private void ValidPhone()
    {
        phonev = false;
        editTextphone.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.toString().isEmpty())
                {
                    editTextphone.setError("This feild can not be empty");
                    phonev = false;
                } else
                {
                    if (editable.length() < 10)
                    {
                        editTextphone.setError("Invalid Phone number !");
                        phonev = false;
                    } else
                    {
                        phone = editable.toString();
                        phonev = true;
                    }
                }
            }
        });
    }

    private void Validpasswordcheck()
    {
        pass = false;
        editTextpassword.addTextChangedListener(new TextWatcher()
        {
            int temp;

            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

                int number = s.length();
                switch (number)
                {
                    case 0:
                        progressBar.setProgress(0);
                        editTextpassword.setError("This field can not be empty !");
                        txtpasswords.setText("Invalid password");
                        break;
                    case 1:
                        progressBar.setProgress(10);
                        editTextpassword.setError("Password need more than 8 character.");
                        txtpasswords.setText("Invalid password");
                        break;
                    case 2:
                        progressBar.setProgress(20);
                        editTextpassword.setError("Password need more than 8 character.");
                        txtpasswords.setText("Invalid password");
                        break;
                    case 3:
                        progressBar.setProgress(30);
                        editTextpassword.setError("Password need more than 8 character.");
                        txtpasswords.setText("Weak Password");
                        break;
                    case 4:
                        progressBar.setProgress(40);
                        editTextpassword.setError("Password need more than 8 character.");
                        txtpasswords.setText("Weak Password");
                        break;
                    case 5:
                        progressBar.setProgress(50);
                        editTextpassword.setError("Password need more than 8 character.");
                        txtpasswords.setText("Weak Password");
                        break;
                    case 6:
                        progressBar.setProgress(60);
                        editTextpassword.setError("Password need more than 6 character.");
                        txtpasswords.setText("Weak Password");
                        break;
                    case 7:
                        progressBar.setProgress(70);
                        editTextpassword.setError("Password need more than 6 character.");
                        txtpasswords.setText("Weak Password");
                        break;
                    case 8:
                        validatePassword(String.valueOf(s));
                        progressBar.setProgress(80);
                        break;
                    case 9:
                        validatePassword(String.valueOf(s));
                        progressBar.setProgress(90);
                        break;
                    case 10:
                        validatePassword(String.valueOf(s));
                        progressBar.setProgress(100);
                        break;
                    default:
                        validatePassword(String.valueOf(s));
                        progressBar.setProgress(100);
                        break;
                }
            }
        });
    }

    private void Validpasswordconfirm()
    {
        passcof = false;
        editTextpassword1.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.toString().equals(password))
                {
                    password1 = editable.toString();
                    passcof = true;
                } else
                {

                    editTextpassword1.setError("Password not match !");
                    passcof = false;
                }
            }
        });
    }

    //This method use to create User on method login with email when user register complete!
    private void emailreg(User user)
    {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                if (task.isSuccessful())
                {
                    reff.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast register_success = Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT);
                                register_success.show();
                                notibuild("Registered Successed !");
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }

                        }
                    });
                } else
                {
                    AlertDialog.Builder dialog = Dialogb(task.getException().getMessage());
                    dialog.show();
                }
            }
        });
    }

    private void validatePassword(String s)
    {
        String passwordInput = editTextpassword.getText().toString().trim();

        if (!PASSWORD_PATTERN.matcher(passwordInput).matches())
        {
            txtpasswords.setText("Weak Password");
            password = "";
            pass = false;
        } else
        {
            txtpasswords.setText("Strong Password");
            pass = true;
            password = s;
        }
    }


    private void notibuild(String mess)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(RegisterActivity.this, "Smart City");
        builder.setContentTitle("Smart City");
        builder.setContentText(mess);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(RegisterActivity.this);
        managerCompat.notify(1, builder.build());

    }

    //Method use to show dialog with custom mess
    private AlertDialog.Builder Dialogb(String mess)
    {
        AlertDialog.Builder builderd = new AlertDialog.Builder(RegisterActivity.this);
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

    //Method use to check email is correct type or not
    private static boolean isEmailValid(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Method use to find ID of each variable need to set on xml
    private void regid()
    {
        editTextfullname = (EditText) findViewById(R.id.txteditregfullname);
        editTextpassword = (EditText) findViewById(R.id.txteditregpassword);
        editTextpassword1 = findViewById(R.id.txteditregpasswordconfirm);
        editTextemail = (EditText) findViewById(R.id.txteditregemail);
        editTextphone = (EditText) findViewById(R.id.txteditphoneno);
        bntSubmit = (Button) findViewById(R.id.bntsubmitreg);
        progressBar = (ProgressBar) findViewById(R.id.passprogressbar);
        txtpasswords = findViewById(R.id.txtpasswords);
    }

}
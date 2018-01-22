package al_muntaqimcrescent2018.com.secureshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    ArrayList<String> emailA ,userNameA;

    ArrayAdapter<String > arrayAdapter;

    AutoCompleteTextView emailAc,passwordAc,ac2UserName;

    Button get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        passwordAc= (AutoCompleteTextView)findViewById(R.id.password);

        emailA = new ArrayList<String >();
        userNameA = new ArrayList<String>();
        get = (Button) findViewById(R.id.login);



        displayDataInfo();


        int i =1 ;

        if(i==1)
        {
            i=2;
            arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,emailA);
            final AutoCompleteTextView actvGetEmail= (AutoCompleteTextView)findViewById(R.id.email);
            actvGetEmail.setThreshold(1);
            actvGetEmail.setAdapter(arrayAdapter);
            actvGetEmail.setTextColor(Color.BLACK);
            emailAc = actvGetEmail;
        }
        if(i==2)
        {
            i=1;
            arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,userNameA);
            final AutoCompleteTextView actvGetFullname= (AutoCompleteTextView)findViewById(R.id.FullText);
            actvGetFullname.setThreshold(1);//will start working from first character
            actvGetFullname.setAdapter(arrayAdapter);//setting the adapter data into the AutoCompleteTextView
            actvGetFullname.setTextColor(Color.BLACK);
            ac2UserName = actvGetFullname;
        }


        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailAc.getText().toString().trim();
                String userName = ac2UserName.getText().toString().trim();
                String password = passwordAc.getText().toString().trim();

                boolean check  =   emailChecker(email);

                SharedPreferences.Editor editor = getSharedPreferences("Email",MODE_PRIVATE).edit();
                editor.putString("email",email);
                editor.putString("uName",userName);
                Toast.makeText(getApplicationContext() ,""+email,Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext() ,""+userName,Toast.LENGTH_SHORT).show();
                editor.apply();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "SignUp Successfull", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUp.this,Push_and_pull.class));
                                    finish();
                                }else if (!task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "Failed to SignUp!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                String djvb = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // check email and password here

//                Intent intent = new Intent(getApplicationContext(),Push_and_pull.class);
//                intent.putExtra("email",email);
//                intent.putExtra("password",password);
//                startActivity(intent);


                Toast.makeText(getApplicationContext(),"Signed in",Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void displayDataInfo() {

        SharedPreferences getShare = getSharedPreferences("Email",MODE_PRIVATE);
        String mail = getShare.getString("email","example@gmail.com");
        String user = getShare.getString("uName","Master");
        Toast.makeText(getApplicationContext() ,""+mail,Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext() ,""+user,Toast.LENGTH_SHORT).show();
        emailA.add(""+mail+"");
        userNameA.add(""+user+"");

    }

    private boolean emailChecker(String email) {


        return  true;
    }
}

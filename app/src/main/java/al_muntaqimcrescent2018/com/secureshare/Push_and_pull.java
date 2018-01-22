package al_muntaqimcrescent2018.com.secureshare;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String.*;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Push_and_pull extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "Imran";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;

    private static final int RC_PHOTO_PICKER = 2 ;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ChildEventListener childEventListener;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;

    private String Master = "",pwd = "" ,FullName = "";

    private TextView linktext;
    Boolean isFirst = true;

//    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private FirebaseStorage mfireStr;
    private StorageReference mstorgeRef;

    private  TextView longText,email,fullname,messageTextView;
    public ArrayList<String> links;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String mUsername;
    private static final int RC_FILE_CHOOSER =2;
    ArrayList<FriendlyMessage> fmList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_and_pull);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

         FullName = "Shaik Mohammad Imran".trim();
         Master = "smdimran838@gmail.com";
         pwd = "imran$#123";


//        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);



        longText = (TextView) headerView.findViewById(R.id.onback1);
        email = (TextView) headerView.findViewById(R.id.textView);
        fullname = (TextView) headerView.findViewById(R.id.textView2);

         char chari  =    getTheChar(FullName);

         longText.setText(""+chari);
         email.setText(Master);
         fullname.setText(FullName);


         String dataRef =  Corrector(Master);
         String strRef =   Corrector(pwd);

        messageTextView = (TextView) findViewById(R.id.messageTextView);
        linktext = (TextView) findViewById(R.id.messageTextView);



        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

              if(charSequence.toString().trim().length() > 0)
              {
                  mSendButton.setEnabled(true);
              }
              else
              {
                  mSendButton.setEnabled(false);
              }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT )});
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(),firebaseUser.getDisplayName(),  null);
                databaseReference.push().setValue(friendlyMessage);

                // Clear input box
                mMessageEditText.setText("");

            }
        });

        firebaseDatabase =FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mfireStr = FirebaseStorage.getInstance();



        databaseReference = firebaseDatabase.getReference().child(dataRef);
        mstorgeRef = mfireStr.getReference().child(strRef);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);




        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        this.registerForContextMenu(mMessageListView);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser!=null)
                {


                    onSignedIn(firebaseUser.getDisplayName());

                }
                else
                {
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                            .build(), RC_SIGN_IN);


                    onSignedOut();
                }
            }
        };


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if(v.getId() == R.id.messageListView){
            this.getMenuInflater().inflate(R.menu.menu,menu);
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo  info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        FriendlyMessage friendlyMessage = fmList.get(index);
        String url = friendlyMessage.getText();

        boolean containHttp = getHttp(url);

        switch(item.getItemId())
        {
            case R.id.pull :



             if(containHttp) {

                 Toast.makeText(getApplicationContext() ,""+url,Toast.LENGTH_SHORT).show();
                 DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                 request.allowScanningByMediaScanner();

                 Toast.makeText(getApplicationContext(), "pulling visiblity", Toast.LENGTH_SHORT).show();

                 Toast.makeText(getApplicationContext(), "downloading file", Toast.LENGTH_SHORT).show();

                 request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                 request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "secureshare");

                 Toast.makeText(getApplicationContext(), "checking viruses", Toast.LENGTH_SHORT).show();

                 DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                 dm.enqueue(request);
             }
             else{

                describer();

             }

                break;
            case R.id.view :


              if(containHttp) {
                  Toast.makeText(getApplicationContext(), "Loading Media", Toast.LENGTH_SHORT).show();

                  Intent intent = new Intent(getApplicationContext(), Fundamentals.class);
                  intent.putExtra("link", url);
                  startActivity(intent);
              }
              else {
                  describer();
              }
                break;

            case R.id.viewpdf :

               if(containHttp) {
                   Toast.makeText(getApplicationContext(), "Loading Pdf", Toast.LENGTH_SHORT).show();

                   Intent in1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                   startActivity(in1);
               }
               else {
                   describer();
               }
                break;

            case R.id.delete :


                 Toast.makeText(getApplicationContext() ,"deleting link",Toast.LENGTH_SHORT).show();

                break;

            case R.id.share :

                if (containHttp) {

                    Toast.makeText(getApplicationContext(), "sharing link", Toast.LENGTH_SHORT).show();


                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Secure Share");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + " \n " + url;
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                }
                else {
                    describer();
                }

                    break;


        }
        return super.onContextItemSelected(item);
    }

    private void describer() {

        Toast.makeText(getApplicationContext(), "Description", Toast.LENGTH_SHORT).show();
    }

    private boolean getHttp(String url) {

        if(url.contains("https"))
        {

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.push_and_pull, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_settings:
//                AuthUI.getInstance().signOut(this);
                Toast.makeText(getApplicationContext()," deleting all files ",Toast.LENGTH_SHORT).show();

                StorageReference strref = mfireStr.getReference().child("files");

                strref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        detachlistener();
                        mMessageAdapter.clear();

                    }



                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                       int errorcode = ((StorageException) exception ).getErrorCode();
                       String except = exception.getMessage();
                        Toast.makeText(getApplicationContext(),except + " " + errorcode ,Toast.LENGTH_SHORT).show();
                    }
                });




                return true;
            default:

                finish();
                return super.onOptionsItemSelected(item);


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "signed in", Toast.LENGTH_SHORT).show();
            }
           else  if (requestCode == RESULT_CANCELED) {

                Toast.makeText(getApplicationContext(), "sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            Uri photouri = data.getData();

            Toast.makeText(getApplicationContext(),""+photouri,Toast.LENGTH_SHORT).show();

            StorageReference mref = mstorgeRef.child(photouri.getLastPathSegment());

            Toast.makeText(getApplicationContext(),""+mref,Toast.LENGTH_SHORT).show();
            mref.putFile(photouri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                    Uri downloadurl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

                    Toast.makeText(getApplicationContext(),""+downloadUri,Toast.LENGTH_SHORT).show();

                    FriendlyMessage friendlyMessage = new FriendlyMessage(downloadUri.toString(), mUsername, downloadurl.toString());
                    databaseReference.push().setValue(friendlyMessage);

                    Toast.makeText(getApplicationContext(),"push",Toast.LENGTH_SHORT).show();
//                    mProgressBar.setVisibility(View.VISIBLE);

                }
            });

            mref.putFile(photouri).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            Toast.makeText(getApplicationContext(),"out loop",Toast.LENGTH_SHORT).show();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void onSignedIn(String displayName) {
//        mProgressBar.setVisibility(View.VISIBLE);
//        mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.GREEN));
        mUsername = displayName;
        attchDatabaseListener();

//            isFirst = false;


    }

    private void onSignedOut() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachlistener();
    }


    public void attchDatabaseListener()
    {
        if(childEventListener==null) {

            childEventListener = new ChildEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                    fmList.add(friendlyMessage);
                    mProgressBar.setVisibility(View.GONE);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            databaseReference.addChildEventListener(childEventListener);

        }
    }

    public void  detachlistener()
    {

        if(childEventListener!=null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener=null;
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("images/png");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "images"), RC_FILE_CHOOSER);
        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "documents"), RC_FILE_CHOOSER);

        } else if (id == R.id.nav_slideshow) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("videos/mp3");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "videos"), RC_FILE_CHOOSER);

        }
//        else if (id == R.id.nav_manage) {
//
//        }
        else if (id == R.id.nav_share) {

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Secure Share");
            String sAux = "\nLet me recommend you this application\n\n";
//                    sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));

        }
        else if (id == R.id.nav_send) {

            AuthUI.getInstance().signOut(this);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (authStateListener!=null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachlistener();
        mMessageAdapter.clear();
    }
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }



    public char  getTheChar(String fullName) {


        String []spliti = fullName.split("\\s");
        String Name;
        int getl = spliti.length;

        if( getl > 0) {


            Name = spliti[spliti.length - 1];
            System.out.println("" + Name.charAt(0));

            return Name.charAt(0);
        }
        else
        {
            System.out.println("" +fullName.charAt(0));

            return  fullName.charAt(0);
        }
    }

    private String Corrector(String master) {

        String fin = "";

        String mod = "" ,mod1 = "",mod2 = "",mod3 = "",mod4="",mod5 = "";



        if(master.contains("."))
        {
            mod = ""+master.replace(".","dot") ;
        }
        else{
            mod = master;
        }
        if(mod.contains("$"))
        {


            mod1 = ""+mod.replace("$","dollar");
        }
        else{
            mod1 = mod;
        }
        if(mod1.contains("["))
        {

            mod2 = ""+mod1.replace("[","lsb");
        }
        else{
            mod2 = mod1;
        }
        if(mod2.contains("]"))
        {

            mod3 = ""+mod2.replace("]","rsb");
        }
        else{
            mod3 = mod2;
        }
        if(mod3.contains("#"))
        {

            mod4 = ""+mod3.replace("#","hash");
        }
        else{
            mod4 = mod3;
        }
        if(mod4.contains("/"))
        {

            mod5 = ""+mod4.replace("/","fs");

            fin = mod5;
        }
        else{
            mod5 = mod4;

            fin = mod5;

        }


        System.out.println(""+fin);

        return   fin;
    }

}

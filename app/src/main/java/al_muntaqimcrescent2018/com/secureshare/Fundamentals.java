package al_muntaqimcrescent2018.com.secureshare;

import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Fundamentals extends AppCompatActivity {

    boolean web = true , text = true;

     String url;
    WebView webView ;
    TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fundamentals);



        url = getIntent().getExtras().getString("link");

;

                    webView = (WebView) findViewById(R.id.my_web_view);
                    webView.setWebViewClient(new MyBrowser());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                    webView.getSettings().setBuiltInZoomControls(true);

                   this.registerForContextMenu(webView);
    }

    private class MyBrowser extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if(v.getId() == R.id.my_web_view){
            this.getMenuInflater().inflate(R.menu.download,menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case  R.id.pull :


                Toast.makeText(getApplicationContext() ,"Pulling link",Toast.LENGTH_SHORT).show();
                String url = "https://firebasestorage.googleapis.com/v0/b/share-13f4a.appspot.com/o/files%2F251?alt=media&token=7b939ad3-2e17-4ae9-b24b-306d568dd490";

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.allowScanningByMediaScanner();

                Toast.makeText(getApplicationContext(),"pulling visiblity",Toast.LENGTH_SHORT).show();

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();

                request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS,"secureshare");

                Toast.makeText(getApplicationContext(),"checking viruses",Toast.LENGTH_SHORT).show();

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                dm.enqueue(request);

                Toast.makeText(getApplicationContext(),"downloading file",Toast.LENGTH_SHORT).show();


                break;
             default:

                 Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                   break;
        }

        return super.onContextItemSelected(item);
    }


}

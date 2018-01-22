package al_muntaqimcrescent2018.com.secureshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class MessageAdapter extends ArrayAdapter<FriendlyMessage> {

    private  ListView mMessageListView;

    public MessageAdapter(Context context, int resource, List<FriendlyMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);

        ImageView photoImageViewpdf = (ImageView) convertView.findViewById(R.id.photoImageViewpdf);

        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);

        TextView  nameTextViewbeside = (TextView) convertView.findViewById(R.id. nameTextViewbeside);

        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);
//        TextView imageNameT = (TextView) convertView.findViewById(R.id.imageName);



//        mMessageListView = (ListView) convertView.findViewById(R.id.messageListView);

        final FriendlyMessage message = getItem(position);

        final ArrayList<String> linkArray = new ArrayList<String>(100);

//        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
////                WebView webView = (WebView) view.findViewById(R.id.webView1);
////                webView.getSettings().setJavaScriptEnabled(true);
////                webView.loadUrl( messageTextView.getText().toString());
//try {
//    Toast.makeText(getContext(), i + "" , Toast.LENGTH_SHORT).show();
//}
//catch (Exception e){e.printStackTrace();}
//
//            }
//        });




        boolean isPhoto = message.getPhotoUrl() != null;

   if(isPhoto) {

       String str = new String(message.getText());

//       messageTextView.setText();
//
//       messageTextView.setText(message.getText());
//
//       Glide.with(photoImageView.getContext())
//               .load(R.drawable.video)
//               .into(photoImageView);

         if(str.contains("video")){

             messageTextView.setText("video");

             Glide.with(photoImageView.getContext())
                     .load(R.drawable.video)
                     .into(photoImageView);


         }else if(str.contains("pdf"))
         {

             Glide.with(photoImageView.getContext())
                     .load(R.drawable.img_pdf)
                     .into(photoImageView);

             messageTextView.setText("pdf");

         }
       else {

             Glide.with(photoImageView.getContext())
                     .load(message.getPhotoUrl())
                     .into(photoImageView);

             messageTextView.setText("image");
         }


       authorTextView.setText(message.getName());



   }

   else{

       messageTextView.setVisibility(View.VISIBLE);
       photoImageView.setVisibility(View.GONE);
       messageTextView.setTextSize(15);
       messageTextView.setText(message.getText());
       photoImageViewpdf.setVisibility(View.GONE);
       nameTextViewbeside.setVisibility(View.GONE);

   }

        return convertView;
    }


}

/**

License GPLv3: GNU GPL Version 3
<http://gnu.org/licenses/gpl.html>.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ca.cmput301w14t09;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import ca.cmput301w14t09.Controller.LocationController;
import ca.cmput301w14t09.Controller.PictureController;
import ca.cmput301w14t09.FileManaging.CreateComment;
import ca.cmput301w14t09.FileManaging.FileSaving;
import ca.cmput301w14t09.FileManaging.SerializableBitmap;
import ca.cmput301w14t09.Model.Comment;
import ca.cmput301w14t09.Model.CommentAdapter;
import ca.cmput301w14t09.Model.PictureModelList;
import ca.cmput301w14t09.Model.User;
import ca.cmput301w14t09.elasticSearch.ElasticSearchOperations;

/**
 * 
 * @author Conner
 * @editor Chun-Han Lee
 *This activity shows the top comment that was selected in a 
 *previous activity and displays all the replies to that comment
 *
 */
public class CommentListActivity extends ListActivity {

    //Activity request codes to take pictures
    public static final int OBTAIN_PIC_REQUEST_CODE = 117;
    public static final int MEDIA_TYPE_IMAGE = 1;

    //Directory name to store captured images
    private static final String IMAGE_DIRECTORY_NAME = "CAMERA";

    //File uri to store Images
    private Uri fileUri;


    PictureController pictureController;

    protected Intent intent;
    protected User user;
    protected Dialog dialog;
    protected ListView aCommentList;
    CommentAdapter adapter;
    Comment comment;

    ImageButton addPicImageButton;
    ImageView picImagePreview;
    SerializableBitmap picture = null;

    PictureModelList pictureModel;

    EditText authorText;
    EditText commentText;

    protected ListView favList;
    protected String firstComment;
    protected CommentListActivity commentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        favList = (ListView) findViewById(android.R.id.list);

        favList.setOnItemClickListener(new OnItemClickListener(){

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

                Comment thread = (Comment)(favList.getItemAtPosition(arg2)); 

                user.profile.add(thread);

                FileSaving.saveUserFile(user, commentActivity);

            }

        });

        commentActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment_list, menu);

        intent = getIntent();
        user = (User) intent.getSerializableExtra("CURRENT_USER");
        firstComment = (String) getIntent().getSerializableExtra("THREAD_ID");
        System.out.println(firstComment);
        onStart();


        return true;
    }

    /**
     * onStart populates the listview with results from the elasticSearch
     *  query found in ElasticSearchOperations.pullOneThread(firstComment)
     */
    @Override
    protected void onStart() {
        super.onStart();

        try {
            ArrayList<Comment> comment = ElasticSearchOperations.pullOneThread(firstComment);

            adapter = new CommentAdapter(this,
                    R.layout.comment_view, comment);
            favList.setAdapter(adapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //@SuppressLint("NewApi")
    public void replyComment(View v) {


        dialog = new Dialog(this);

        dialog.setContentView(R.layout.pop_up_comment);
        dialog.setTitle("New Top Comment");

        authorText=(EditText)dialog.findViewById(R.id.authorText);
        commentText=(EditText)dialog.findViewById(R.id.commentText);
        final EditText tv2 = (EditText)dialog.findViewById(R.id.longtext3);
        final EditText tv3 = (EditText)dialog.findViewById(R.id.lattext3);

        //new Location Controller 
        final LocationController lc = new LocationController();
        this.pictureController = new PictureController();

        //https://github.com/baoliangwang/CurrentLocation
        //setup location manager
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        authorText.setText(user.getProfile().getAuthorName());
        Button save=(Button)dialog.findViewById(R.id.save);
        Button btnCancel=(Button)dialog.findViewById(R.id.cancel);

        //update location button
        Button btnSetLocation = (Button)dialog.findViewById(R.id.changebutton);

        picImagePreview = (ImageView)dialog.findViewById(R.id.picImagePreview);  
        addPicImageButton = (ImageButton) dialog.findViewById(R.id.takePicture);

        dialog.show();

        //Capture image button click event              
        this.addPicImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
                //      attachment = true;
            }
        });

        // Checks camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "No Camera Detected.", Toast.LENGTH_LONG).show();
        }

        // Retrieve location updates through LocationListener interface
        //https://github.com/baoliangwang/CurrentLocation
        LocationListener locationListener = new LocationListener() {                            

            public void onProviderDisabled (String provider) {

            }

            public void onProviderEnabled (String provider) {


            }

            public void onStatusChanged (String provider, int status, Bundle extras) {


            }

            @Override
            public void onLocationChanged(android.location.Location location) {

                lc.locationchanged(location, tv2, tv3);


            }
        };

        dialog.show();

        //request location update
        //https://github.com/baoliangwang/CurrentLocation
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);


        //update location button
        btnSetLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { 
                String latString;
                String lngString;

                lc.updatelocation(dialog.getContext(), tv2.getText().toString(), tv3.getText().toString());


            }
        });

        //cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        //save button
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text1 = commentText.getText().toString();
                String text2 = authorText.getText().toString();
                user.getProfile().setAuthorName(text2);
                FileSaving.saveUserFile(user, commentActivity );

                picture = pictureController.finalizePicture(picture, commentActivity);
                comment = CreateComment.newReplyComment(lc, text2, text1, false, picture, firstComment);

                try
                {
                    ElasticSearchOperations.postThread(comment);
                    Thread.sleep(1000);
                    adapter.notifyDataSetChanged();
                    recreate();

                } catch (InterruptedException e)
                {

                    e.printStackTrace();
                }

                dialog.dismiss();

            }
        });
        onStart();

    }


    /**
     * isDeviceSupportCamera does a check to see
     * if device hardware camera is present or not
     * @return
     */
    public boolean isDeviceSupportCamera() {
        if(getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)){
            //returns true if device has a camera
            return true;
        }else {
            //returns false if device doesn't have a camera
            return false;
        }
    }

    /**
     * captureImage will launch camera app request image capture
     * Creates the intent to take a picture, and then starts it
     */
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = pictureController.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // starts the image capture intent
        startActivityForResult(intent, OBTAIN_PIC_REQUEST_CODE);

    }


}

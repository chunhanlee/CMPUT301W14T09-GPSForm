package ca.cmput301w14t09;




import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import ca.cmput301w14t09.Controller.LocationController;
import ca.cmput301w14t09.Controller.PictureController;
import ca.cmput301w14t09.FileManaging.CreateComment;
import ca.cmput301w14t09.FileManaging.FileSaving;
import ca.cmput301w14t09.elasticSearch.ElasticSearchOperations;
import ca.cmput301w14t09.model.Comment;
import ca.cmput301w14t09.model.CommentAdapter;
import ca.cmput301w14t09.model.ThreadAdapter;

import ca.cmput301w14t09.model.PictureModelList;

import ca.cmput301w14t09.model.User;
import ca.cmput301w14t09.view.PictureAdapter;

/**
 * This class handles all the functions that the pop_up_comment.xml has to offer.
 * ~ Controlling the longitude & latitude 
 * ~ Attaching a picture to a comment
 * 	 Most of the code referring to handling the picture is taken from
 * 	 http://www.androidhive.info/2013/09/android-working-with-camera-api/
 * ~ Making a comment with an author
 * @author ssowemim
 *
 */
public class TopCommentsActivity extends ListActivity {
	
	//Activity request codes to take pictures
	public static final int OBTAIN_PIC_REQUEST_CODE = 117;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	//Directory name to store captured images
	private static final String IMAGE_DIRECTORY_NAME = "CAMERA";
	
	//File uri to store Images
	private Uri fileUri;
	
	private TopCommentsActivity topActivity;
	
	protected Intent intent;
	protected User user;
	protected Dialog dialog;
	protected ListView aCommentList;
	Comment comment;
	
	ImageButton addPicImageButton;
	ImageView picImagePreview;


	PictureModelList pictureModel;
	PictureController pictureController;
	PictureAdapter pictureAdapter;
	
	EditText authorText;
	EditText commentText;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_comments);
		topActivity = this;
		
		aCommentList = (ListView) findViewById(android.R.id.list);

		aCommentList.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

				Comment thread = (Comment)(aCommentList.getItemAtPosition(arg2)); 
				
				// Pass in comment object
				commentThread(thread);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top_comments, menu);

		intent = getIntent();
		user = (User) intent.getSerializableExtra("CURRENT_USER");	
		
		return true;


	}

	@Override
	protected void onStart(){
		super.onStart();
		ArrayList<Comment> topComments;
		try {
			topComments = ElasticSearchOperations.pullThreads();
			ThreadAdapter adapter = new ThreadAdapter(this,
					R.layout.thread_view, topComments);
			aCommentList.setAdapter(adapter);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
 
	public void popUp(View v){

		dialog = new Dialog(this);

		dialog.setContentView(R.layout.pop_up_comment);
		dialog.setTitle("New Top Comment");

		authorText=(EditText)dialog.findViewById(R.id.authorText);
		commentText=(EditText)dialog.findViewById(R.id.commentText);
		final EditText tv2 = (EditText)dialog.findViewById(R.id.longtext3);
		final EditText tv3 = (EditText)dialog.findViewById(R.id.lattext3);
		
		//new Location Controller 
		final LocationController lc = new LocationController();
		
		//get Location Manager setup

		//lc.setLocationManager(dialog.getContext());
	
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

						
			
		//addPicImageView = (ImageView)this.findViewById(R.id.add_pic_image_view);
		//System.out.println(user.getAuthorName());

		
		authorText.setText(user.getProfile().getAuthorName());
		Button save=(Button)dialog.findViewById(R.id.save);
		Button btnCancel=(Button)dialog.findViewById(R.id.cancel);
		//update location button
		Button btnSetLocation = (Button)dialog.findViewById(R.id.changebutton);
		
		picImagePreview = (ImageView)dialog.findViewById(R.id.picImagePreview);  
		addPicImageButton = (ImageButton) dialog.findViewById(R.id.takePicture);
		
		dialog.show();
		
		/**
		 * Capture image button click event
		 */
		this.addPicImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// capture picture
				captureImage();
			}
		});
		
		// Checks camera availability
		if (!isDeviceSupportCamera()){
			Toast.makeText(getApplicationContext(),
								"No Camera Detected.", Toast.LENGTH_LONG).show();
		}
		
		// Retrieve location updates through LocationListener interface
		LocationListener locationListener = new LocationListener(){				
								
				public void onProviderDisabled (String provider){
									
					}
								
				public void onProviderEnabled (String provider){
									
									
					}
								
				public void onStatusChanged (String provider, int status, Bundle extras){
									
									
					}

				@Override
				public void onLocationChanged(android.location.Location location) {
									
				    lc.locationchanged(location, tv2, tv3);
									
									
					}
				};
				
				//set up location update request.
				//lc.requestLocationUpdates(locationListener);
		
				// Retrieve location updates through LocationListener interface
				
						
						//set up location update request.
						
		
						dialog.show();
		
		
					//lc.requestLocationUpdates(locationListener);
					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
			
				
				//update location button
				btnSetLocation.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) { 
					    String latString;
					    String lngString;
					    
					    lc.updatelocation(dialog.getContext(), tv2.getText().toString(), tv3.getText().toString());
					   // tv2.setText(lc.getGeoLocation().getLatitude()));
					    
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
				FileSaving.saveUserFile(user, topActivity);
				
				comment = CreateComment.newComment(lc, text2, text1, true);
				
				
				
				ElasticSearchOperations.postThread(comment);
				dialog.dismiss();

			}
		});

	}

	/**
	 * Checking device hardware if camera is present or not
	 * @return
	 */
	private boolean isDeviceSupportCamera() {
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
	 * Capturing camera image will launch camera app request image capture
	 * Creates the intent to take a picture, and then starts it
	 */
	public void captureImage(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		
		// starts the image capture intent
		startActivityForResult(intent, OBTAIN_PIC_REQUEST_CODE);
		
	}
	
	/**
	 * Storing the file url as it will be null after returning from camera app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		
		// save file url in bundle as it will be null on screen orientation changes
		outState.putParcelable("file_uri",fileUri);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		fileUri = savedInstanceState.getParcelable("file_uri");
	}
	
	
	/**
	 * Receiving activity result method will be called after closing the camera
	 */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // if the result is capturing Image
            if (requestCode == OBTAIN_PIC_REQUEST_CODE) {
                    if (resultCode == RESULT_OK) {
                            // successfully captured the image
                            // display it in image view
                            previewCapturedImage();
                    } else if (resultCode == RESULT_CANCELED) {
                            // user cancelled Image capture
                            Toast.makeText(getApplicationContext(),
                                            "User cancelled image capture", Toast.LENGTH_SHORT)
                                            .show();
                    } else {
                            // failed to capture image
                            Toast.makeText(getApplicationContext(),
                                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                                            .show();
                    }
            }
    }

	/**
	 * Display the image taken into an ImageView for preview
	 */
	private void previewCapturedImage(){
		try{
			picImagePreview.setVisibility(View.VISIBLE);
			
			//bitmap factory
			BitmapFactory.Options options = new BitmapFactory.Options();
			
			//downsizing image into a smaller size and will throw exception for larger images
			options.inSampleSize = 8;
			
			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
			
			picImagePreview.setImageBitmap(bitmap);
			
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Creating the File Uri that will be used to store images
	 * @param type
	 * @return
	 */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
}
	
	/**
	 * Returning Images
	 * returning Image
	 * @param v
	 */
	private static File getOutputMediaFile(int type){

		//External Sdcard Location
		File mediaStorageDir = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	public void viewFavorites(View v){
		if(user.getUserName().equals("Guest")){

			dialog = new Dialog(this);
			dialog.setContentView(R.layout.guest_box);
			dialog.setTitle("ALERT!");

			Button button =(Button)dialog.findViewById(R.id.favorite1);
			dialog.show();
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();

				}
			});
		}

		else{
			Intent intent = new Intent(this, FavoritesActivity.class);
			startActivity(intent);
		}
	}

	// Sends comment object to new activity
	public void commentThread(Comment thread) {
	        
	        String stringId = new String();
	        stringId = thread.getThreadId();
	        
		Intent intent = new Intent(this, CommentListActivity.class);
		intent.putExtra("THREAD_ID", stringId);
		startActivity(intent);

	}
}
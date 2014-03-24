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
import java.util.List;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ca.cmput301w14t09.Controller.LocationController;
import ca.cmput301w14t09.Controller.LocationFactory;
import ca.cmput301w14t09.Controller.PictureController;
import ca.cmput301w14t09.FileManaging.CreateComment;
import ca.cmput301w14t09.FileManaging.FileSaving;
import ca.cmput301w14t09.Model.Comment;
import ca.cmput301w14t09.Model.GeoLocation;
import ca.cmput301w14t09.Model.PictureModelList;
import ca.cmput301w14t09.Model.ThreadAdapter;
import ca.cmput301w14t09.Model.User;
import ca.cmput301w14t09.elasticSearch.ElasticSearchOperations;

/**
 * 
 * @author ssowemim, Conner
 * TopCommentsActivity handles all the functions that the pop_up_comment.xml has to offer.
 * Controlling the longitude & latitude. 
 * Attaching a picture to a comment.
 * Most of the code referring to handling the picture is taken from
 * http://www.androidhive.info/2013/09/android-working-with-camera-api/
 * Making a comment with an author.
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
	
	PictureController pictureController;

	protected Intent intent;
	protected User user;
	protected Dialog dialog;
	protected ListView aCommentList;
	Comment comment;

	ImageButton addPicImageButton;
	ImageView picImagePreview;
	Bitmap picture = null;

	PictureModelList pictureModel;

	EditText authorText;
	EditText commentText;
	ThreadAdapter adapter;
	
	List<GeoLocation> objList = new ArrayList<GeoLocation>();
	List<String> Locationstring = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_comments);
		topActivity = this;
	//	attachment = false;

		aCommentList = (ListView) findViewById(android.R.id.list);

		aCommentList.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

				Comment thread = (Comment)(aCommentList.getItemAtPosition(arg2)); 

				// Pass in comment object
				commentThread(thread);
			}

		});
		
		LocationFactory lf = new LocationFactory();
		lf.buildlocations();
		objList = lf.getObjList();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top_comments, menu);

		intent = getIntent();
		user = (User) intent.getSerializableExtra("CURRENT_USER");	

		return true;

	}

	/**
	 * onStart popluates the listview with results from
	 * elasticSearch, finding all of the top comments
	 * @param thread
	 */
	@Override
	public void onResume() {
		super.onResume();
		ArrayList<Comment> topComments = null;
			try {
				topComments = ElasticSearchOperations.pullThreads();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter = new ThreadAdapter(this,
					R.layout.thread_view, topComments);
			aCommentList.setAdapter(adapter);
			adapter.notifyDataSetChanged();

	}

	/**
	 * popUp is a dialog that is invoked when the new
	 * comment button is pressed.
	 * It allows for most attributes of a comment to be
	 * modified and then pushed to elasticSearch
	 * @param v
	 */
	@SuppressLint("NewApi")
	public void popUp(View v) {

		dialog = new Dialog(this);

		dialog.setContentView(R.layout.pop_up_comment);
		dialog.setTitle("New Top Comment");

		authorText=(EditText)dialog.findViewById(R.id.authorText);
		commentText=(EditText)dialog.findViewById(R.id.commentText);
		final ListView LocationList = (ListView)dialog.findViewById(R.id.list22);
		
		

		//new Location Controller 
		final LocationController lc = new LocationController();
		
		lc.getnameslocation(objList);
		Locationstring =lc.getLocationstringlist();
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_view, Locationstring);
		LocationList.setAdapter(adapter);
		
		
		this.pictureController = new PictureController(this);

		//https://github.com/baoliangwang/CurrentLocation
		//setup location manager
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		authorText.setText(user.getProfile().getAuthorName());
		Button save=(Button)dialog.findViewById(R.id.save);
		Button btnCancel=(Button)dialog.findViewById(R.id.cancel);


		picImagePreview = (ImageView)dialog.findViewById(R.id.picImagePreview);  
		addPicImageButton = (ImageButton) dialog.findViewById(R.id.takePicture);

		dialog.show();

		//Capture image button click event		
		this.addPicImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				captureImage();
			//	attachment = true;
			}
		});

		// Checks camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),"No Camera Detected.", Toast.LENGTH_LONG).show();
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

				lc.locationchanged(location);

			}
		};
		
		//http://android.okhelp.cz/start-activity-from-listview-item-click-android-example/
		 //list view item click 
	     LocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		 
	               // When clicked, show a toast with the TextView text Game, Help, Home
	              // Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();  
	              
	               //http://stackoverflow.com/questions/801193/modify-view-static-variables-while-debugging-in-eclipse
	               String selectedFromList =(String) (LocationList.getItemAtPosition(position));
	               
	               lc.updatelocation(selectedFromList, dialog.getContext());
	               
	               
	    	 }

	     });

		dialog.show();

		//request location update
		//https://github.com/baoliangwang/CurrentLocation
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		
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

			//	picture = comment.getPicture();
				picture = pictureController.finalizePicture(picture);
				comment = CreateComment.newComment(lc, text2, text1, true, picture);

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

	}

	
	public void saveComment(){
		
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

	/**
	 * onSaveInstanceState stores the file url as
	 * it will be null after returning from camera app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on screen orientation changes
		outState.putParcelable("file_uri",fileUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		fileUri = savedInstanceState.getParcelable("file_uri");
	}

	/**
	 * onActivityResult will Receive the activity result
	 * method and will be called after closing the camera, this method 
	 * is always called when camera is closed.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// if the result is capturing Image
		if (requestCode == OBTAIN_PIC_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				picture = pictureController.previewCapturedImage(fileUri, picture, picImagePreview, comment);
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
	 * viewFavorites checks to see if you are guest or not
	 * since guest cannot have favorites.
	 * Then the method starts up the FavoritesListActivity.
	 * This activity has yet to be written
	 * @param v
	 */
	public void viewFavorites(View v) {
		if(user.getUserName().equals("Guest")) {

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

		else {

		}
	}

	/**
	 * commentThread takes in thread and then starts a new
	 * activity while passing the contents of thread to the 
	 * activity
	 * @param thread
	 */
	public void commentThread(Comment thread) {

		String stringId = new String();
		stringId = thread.getThreadId();

		Intent intent = new Intent(this, CommentListActivity.class);
		intent.putExtra("THREAD_ID", stringId);
		startActivity(intent);

	}

}
package ensias.um5.com.event_gl2;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1 ;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private EditText mEditTextFileplace;
    private EditText mEditTextFiletime;
    private EditText mEditTextFiletype;
    private EditText mEditTextFileemail;
    private ImageView mImageView ;
    private ProgressBar mProgressBar ;

    private Uri  mImageUri ;

    private StorageReference mStorageRef ;
    private DatabaseReference mDatabaseRef ;
    private StorageTask  mUploadtask ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload =findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mEditTextFileplace = findViewById(R.id.edit_text_file_place);
        mEditTextFiletype = findViewById(R.id.edit_text_file_type);
        mEditTextFiletime = findViewById(R.id.edit_text_file_time);
        mEditTextFileemail = findViewById(R.id.edit_text_file_email);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progess_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openFileChooser();
            }
        });
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mUploadtask !=null && mUploadtask.isInProgress()) {
                   Toast.makeText(MainActivity.this,"upload in progress",Toast.LENGTH_SHORT).show();
               }
               else { uploadfile();}

            }
        });
        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              openImagesActivity();
            }
        });
    }
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri)) ;

    }
     private void uploadfile(){

         if(mImageUri == null){
             Toast.makeText(this,"No file Selected",Toast.LENGTH_SHORT).show();
         }
         else {

           StorageReference fileRefence = mStorageRef.child(System.currentTimeMillis()+"." +getFileExtension(mImageUri));
             mUploadtask = fileRefence.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Handler handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           mProgressBar.setProgress(0);
                       }
                   },500);

                   Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                   Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                   while (!urlTask.isSuccessful());
                   Uri downloadUrl = urlTask.getResult();
                   Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),downloadUrl.toString(),mEditTextFiletime.getText().toString().trim(),mEditTextFileplace.getText().toString().trim(),mEditTextFileemail.getText().toString().trim(),mEditTextFiletype.getText().toString().trim());

                   String uploadId = mDatabaseRef.push().getKey();
                   mDatabaseRef.child(uploadId).setValue(upload);
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                   double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                   mProgressBar.setProgress((int)progress);
               }
           });
       }
     }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST  && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData() ;

            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }
    private void  openImagesActivity(){
        Intent intent = new Intent(this,imagesActivity.class);
        startActivity(intent);
    }
}

package cc.cunbm.android.fbdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        /*
        // Or Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference =
            storage.getReferenceFromUrl("gs://bucket/images/stars.jpg");

         */
        findViewById(R.id.myImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // file picker
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 100);
            }
        });

        /** uploading **/
        findViewById(R.id.btnUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // store the image
                StorageReference _storageRef = storageRef.child("uploadedfile.png");
                (findViewById(R.id.myImg)).setDrawingCacheEnabled(true);
                (findViewById(R.id.myImg)).buildDrawingCache();
                Bitmap bitmap = (findViewById(R.id.myImg)).getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = _storageRef.putBytes(data);

                uploadTask.addOnFailureListener(exception -> {
                    Toast.makeText(MainActivity.this, "TASK FAILED", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(MainActivity.this, "TASK SUCCEEDED", Toast.LENGTH_SHORT).show();

                    String DOWNLOAD_URL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    Log.v("DOWNLOAD URL", DOWNLOAD_URL);
                    Toast.makeText(MainActivity.this, DOWNLOAD_URL, Toast.LENGTH_SHORT).show();
                });
            }
        });

        /** downloading **/
        findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Download file in Memory
                StorageReference islandRef = storageRef.child("images/ro.jpeg");

                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Log.d("***","downloaded " + bytes.length + " bytes");

                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ImageView image = (ImageView) findViewById(R.id.myImg);
                        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==100){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    ((ImageView)findViewById(R.id.myImg)).setImageURI(selectedImageUri);
                }
            }
        }
    }





}
package pt.ipp.estg.cmu_exerciseyourself.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import android.graphics.drawable.BitmapDrawable
import pt.ipp.estg.cmu_exerciseyourself.R


class ImageCaptureActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var imageView: ImageView

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)

        imageView = findViewById(R.id.imageView)

        dispatchTakePictureIntent()

    }

    private fun dispatchTakePictureIntent() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if (takePictureIntent.resolveActivity(packageManager)) {
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        //}
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var extras = data?.extras
            var bitmap = extras?.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)
        }

        var storage = FirebaseStorage.getInstance();

        var storageRef = storage.getReference();

        var imagesRef = storageRef.child("images");

        var filename = "space.jpg";
        var spaceRef = imagesRef.child(filename);


        // [START upload_memory]
        // Get the data from an ImageView as bytes
        // [START upload_memory]
        // Get the data from an ImageView as bytes
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask: UploadTask = spaceRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...
        }
    }
}
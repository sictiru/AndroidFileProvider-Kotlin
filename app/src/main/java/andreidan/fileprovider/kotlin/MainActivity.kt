package andreidan.fileprovider.kotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val RC_PERM_READ_EXTERNAL_STORAGE: Int = 1
        val RC_PICK_PHOTO_GALLERY: Int = 2
        val RC_PICK_PHOTO_CAMERA: Int = 3
    }

    @BindView(R.id.button_gallery)
    lateinit var btnGallery: Button
    @BindView(R.id.button_camera)
    lateinit var btnCamera: Button
    @BindView(R.id.imageView)
    lateinit var imageView: ImageView

    var cameraFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        btnCamera.setOnClickListener { openCamera(getLocalImageFileName()) }
        btnGallery.setOnClickListener { openGallery() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraPermissionResult(requestCode, grantResults, cameraFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_PICK_PHOTO_CAMERA ->
                if (resultCode == Activity.RESULT_OK) {
                    val cameraOutput = getCameraOutput(cameraFile)
                    val selectedImageUri = Uri.fromFile(cameraOutput)
                    imageView.setImageURI(selectedImageUri)
                }
            RC_PICK_PHOTO_GALLERY ->
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = data?.data
                    imageView.setImageURI(selectedImageUri)
                }
        }
    }

    fun cameraPermissionResult(requestCode: Int, grantResults: IntArray, fileName: String) {
        if (PermissionUtils.permissionGranted(requestCode, RC_PERM_READ_EXTERNAL_STORAGE, grantResults)) {
            openCamera(fileName)
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_PICK_PHOTO_GALLERY)
    }

    fun openCamera(fileName: String) {
        if (PermissionUtils.requestPermission(this, RC_PERM_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val cameraOutput = getCameraOutput(fileName)
            val outPut = getUriFromFile(cameraOutput)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPut)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, RC_PICK_PHOTO_CAMERA)
        }
    }

    fun getCameraOutput(fileName: String): File {
        if (fileName.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid file name", Toast.LENGTH_SHORT).show()
        }

        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, fileName)
    }

    fun getUriFromFile(cameraOutput: File): Uri {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(this, packageName + ".provider", cameraOutput)
        } else {
            return Uri.fromFile(cameraOutput)
        }
    }

    fun getLocalImageFileName(): String {
        cameraFile = "test-" + UUID.randomUUID().toString() + ".jpeg"
        return cameraFile as String
    }
}

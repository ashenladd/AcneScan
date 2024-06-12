package com.dicoding.asclepius.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.AcneClassifierHelper
import com.dicoding.asclepius.utils.toBitmap
import com.dicoding.asclepius.view.CameraActivity.Companion.CAMERAX_RESULT
import com.dicoding.asclepius.view.model.ClassificationResult
import com.dicoding.asclepius.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private lateinit var acneClassifierHelper: AcneClassifierHelper


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    "Permission request granted",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission request denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            val uri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            if (uri != null) {
                currentImageUri = uri
                showImage()
            }
        }
    }

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(
                inputUri,
                outputUri
            )
                .withAspectRatio(
                    1f,
                    1f
                )
                .withMaxResultSize(
                    1920,
                    1080
                )

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            if (resultCode == RESULT_OK) {
                return UCrop.getOutput(intent!!)!!
            }
            return Uri.parse("")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermission()
        setupClick()
        setupToolbar()
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun setupToolbar() {
        binding.apply {
            lytMain.toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            lytMain.toolbar.title = getString(R.string.label_cancer_detection)
        }
    }

    private fun setupClick() {
        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }
            analyzeButton.setOnClickListener {
                analyzeImage()
            }
            cameraButton.setOnClickListener {
                startCamera()
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val outputUri = Uri.fromFile(
            File.createTempFile(
                "temp",
                ".jpg"
            )
        )

        if (uri != null) {
            cropImage.launch(
                listOf(
                    uri,
                    outputUri
                )
            )
        } else {
            Log.d(
                "Photo Picker",
                "No media selected"
            )
        }
    }

    private val cropImage = registerForActivityResult(uCropContract) { uri: Uri? ->
        if (uri != null && uri != Uri.parse("")) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d(
                "Photo Picker",
                "No media selected"
            )
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d(
                "Image URI",
                "showImage: $it"
            )
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        if (currentImageUri == null) {
            showEmptyWarning()
            return
        }
        showLoading(true)
        acneClassifierHelper = AcneClassifierHelper(
            context = this,
            classifierListener = object : AcneClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        showToast(error)
                        showLoading(false)
                    }
                }

                override fun onResults(results: FloatArray) {
                    runOnUiThread {
                        if (results.isNotEmpty()) {
                            var highest = 0
                            val displayResult = results.mapIndexed { index, value ->
                                if (value > results[highest]) {
                                    highest = index
                                }
                                "Level ${index}: ${
                                    NumberFormat.getPercentInstance().format(value)
                                }"
                            }
                            val classificationResult = ClassificationResult(
                                imageUri = currentImageUri!!,
                                classifications = displayResult,
                                highest = highest
                            )
                            moveToResultAcne(classificationResult)
                        } else {
                            showLoading(false)
                            showToast("No result")
                        }
                    }
                }
            }
        )

        currentImageUri?.let {
            acneClassifierHelper.classifyStaticImage(
                toBitmap(
                    this,
                    it
                )
            )
        }
    }

    private fun startCamera() {
        val intent = Intent(
            this,
            CameraActivity::class.java
        )
        launcherIntentCameraX.launch(intent)
    }

    private fun moveToResultAcne(classificationResult: ClassificationResult) {
        val intent = Intent(
            this,
            ResultActivity::class.java
        )
        intent.putExtra(
            ResultActivity.EXTRA_RESULT_ACNE,
            classificationResult
        )
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showEmptyWarning() {
        showToast(getString(R.string.empty_image_warning))
    }

    companion object {
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}
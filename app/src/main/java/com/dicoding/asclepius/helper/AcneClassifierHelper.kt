package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

class AcneClassifierHelper(
    private val context: Context,
    private val modelName: String = "model.tflite", // Ganti dengan nama model Anda
    private val classifierListener: ClassifierListener?
) {

    private var interpreter: Interpreter? = null
    private val inputImageSize = 150 // Sesuaikan dengan ukuran input model Anda

    init {
        setupInterpreter()
    }

    private fun setupInterpreter() {
        try {
            val model = FileUtil.loadMappedFile(context, modelName)
            val options = Interpreter.Options()
            // Anda bisa menambahkan pengaturan opsional di sini
            interpreter = Interpreter(model, options)
        } catch (e: IOException) {
            classifierListener?.onError("Error loading model: ${e.message}")
            Log.e(TAG, "Error loading model: ${e.message}")
        }
    }

    fun classifyStaticImage(bitmap: Bitmap) {
        if (interpreter == null) {
            setupInterpreter()
        }

        // Check bitmap dimensions and resize if necessary
        val resizedBitmap = if (bitmap.width != inputImageSize || bitmap.height != inputImageSize) {
            Bitmap.createScaledBitmap(bitmap, inputImageSize, inputImageSize, false)
        } else {
            bitmap
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputImageSize, inputImageSize, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(resizedBitmap))
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap, inputImageSize)

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, outputClasses), DataType.FLOAT32)

        interpreter?.run(inputBuffer, outputBuffer.buffer)

        // Proses output untuk mendapatkan hasil klasifikasi
        val probabilities = outputBuffer.floatArray
        // ... (Interpretasikan hasil probabilitas sesuai kebutuhan Anda)
        classifierListener?.onResults(probabilities) // Mengirimkan hasil
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap, inputImageSize: Int): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputImageSize * inputImageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputImageSize * inputImageSize)

        // Get the minimum of inputImageSize and bitmap dimensions
        val width = min(inputImageSize, bitmap.width)
        val height = min(inputImageSize, bitmap.height)
        bitmap.getPixels(intValues, 0, width, 0, 0, width, height)

        var pixel = 0
        for (i in 0 until inputImageSize) {
            for (j in 0 until inputImageSize) {
                val value = if (pixel < intValues.size) intValues[pixel++] else 0
                byteBuffer.putFloat((value shr 16 and 0xFF) / 255.0f)
                byteBuffer.putFloat((value shr 8 and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(results: FloatArray) // FloatArray untuk probabilitas
    }

    companion object {
        private const val TAG = "AcneClassifierHelper"
        private const val outputClasses = 3
    }
}
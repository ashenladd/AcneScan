package com.dicoding.asclepius.view.result

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.ClassificationHistory
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.utils.ViewModelFactory
import com.dicoding.asclepius.view.model.ClassificationResult

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private var data: ClassificationResult? = null

    private val factory by lazy { ViewModelFactory.getInstance(this) }

    private val resultViewModel: ResultViewModel by viewModels() {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchData()
        setupData()
        setupToolbar()
        setupObserver()
    }

    private fun setupObserver() {
        resultViewModel.isSaved.observe(this) {
            showSaved(it)
        }
    }

    private fun showSaved(isSaved: Boolean) {
        if (isSaved) {
            binding.apply {
                lytResult.toolbar.menu.findItem(R.id.save).isEnabled = false
                lytResult.toolbar.menu.findItem(R.id.save).setIcon(R.drawable.ic_save_unactive_bg)
            }
            Toast.makeText(
                this,
                getString(R.string.label_data_saved),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            binding.apply {
                lytResult.toolbar.menu.findItem(R.id.save).isEnabled = true
                lytResult.toolbar.menu.findItem(R.id.save).setIcon(R.drawable.ic_save_bg)
            }
        }

    }

    private fun setupToolbar() {
        binding.apply {
            lytResult.toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            lytResult.toolbar.title = getString(R.string.result)
            lytResult.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.save -> {
                        val savedData = ClassificationHistory(
                            imageUri = data?.imageUri.toString(),
                            classifications = data?.classifications?.joinToString("\n") ?: "",
                            timestamp = System.currentTimeMillis().toString()
                        )
                        resultViewModel.saveResult(savedData)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun setupData() {
        data?.let {
            binding.apply {
                val resultAcneResult = it.classifications.joinToString("\n")
                resultImage.setImageURI(it.imageUri)
                resultText.text = resultAcneResult
                if (it.highest == 2) {
                    predictionImage.setImageResource(R.drawable.ic_sad)
                }else {
                    predictionImage.setImageResource(R.drawable.ic_smile)
                }
            }
        }
    }

    private fun fetchData() {
        data = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                EXTRA_RESULT_ACNE,
                ClassificationResult::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RESULT_ACNE)
        }
    }

    companion object {
        const val EXTRA_RESULT_ACNE = "extra_result_acne"
    }

}
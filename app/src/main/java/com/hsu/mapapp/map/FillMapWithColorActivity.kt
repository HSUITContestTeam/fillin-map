package com.hsu.mapapp.map

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityFillMapWithColorBinding

class FillMapWithColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFillMapWithColorBinding

    /*val btnClickListener : View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.red_btn -> println("red")
                R.id.yellow_btn -> println("yello")
                R.id.blue_btn -> println("blue")
            }
            Toast.makeText(applicationContext, "${v?.id}", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFillMapWithColorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pathData = intent.getStringExtra("pathData")

        binding.redBtn.setOnClickListener {
            binding.imageView5.setBackgroundColor(Color.parseColor("#CC0000"))
            intent.putExtra("color", "#CC0000")
        }

        binding.yellowBtn.setOnClickListener {
            binding.imageView5.setBackgroundColor(Color.parseColor("#FFCC00"))
            intent.putExtra("color", "#FFCC00")
        }

        binding.blueBtn.setOnClickListener {
            binding.imageView5.setBackgroundColor(Color.parseColor("#0000CC"))
            intent.putExtra("color", "#0000CC")
        }

        binding.saveBtn.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }

        /*binding.redBtn.setOnClickListener { btnClickListener }
        binding.yellowBtn.setOnClickListener { btnClickListener }
        binding.blueBtn.setOnClickListener { btnClickListener }*/
    }
}
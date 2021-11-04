package com.github.h4de5ing.startactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h4de5ing.startactivity.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startAFR.setOnClickListener {
            setResult(RESULT_OK, Intent().putExtra("data", "SecondActivity返回来的数据"))
            finish()
        }
    }
}
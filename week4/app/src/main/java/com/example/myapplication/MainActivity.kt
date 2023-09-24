package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity() {
    private lateinit var motionLayout: MotionLayout
    private lateinit var radioGroup: RadioGroup
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        motionLayout = findViewById(R.id.motionLayout)
        radioGroup = findViewById(R.id.radioGroup)

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextAddr = findViewById<EditText>(R.id.editTextAddr)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.name = editTextName.text.toString()
            viewModel.address = editTextAddr.text.toString()

            when (checkedId) {
                R.id.radioStudent ->  {
                    motionLayout.transitionToStart()
                }
                R.id.radioStaff -> {
                    motionLayout.transitionToEnd()
                }
            }

            editTextName.setText(viewModel.name)
            editTextAddr.setText(viewModel.address)
        }
    }

    class MyViewModel : ViewModel() {
        var name: String = ""
        var address: String = ""
    }
}
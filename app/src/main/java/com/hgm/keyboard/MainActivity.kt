package com.hgm.keyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hgm.keyboard.widget.Keyboard

class MainActivity : AppCompatActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val keyboard = findViewById<Keyboard>(R.id.keyBoard)
            keyboard.setOnKeyClickListener(object : Keyboard.OnKeyClickListener {
                  override fun onNumKeyClick(number: Int) {
                        Toast.makeText(this@MainActivity, number.toString(), Toast.LENGTH_SHORT).show()
                  }

                  override fun onDeleteKeyClick() {
                        Toast.makeText(this@MainActivity, "删除", Toast.LENGTH_SHORT).show()
                  }
            })
      }
}
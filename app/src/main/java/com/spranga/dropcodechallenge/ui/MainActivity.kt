package com.spranga.dropcodechallenge.ui

import android.os.Bundle
import com.spranga.dropcodechallenge.R.layout
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : DaggerAppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidInjection.inject(this)
    setContentView(layout.activity_main)
    setSupportActionBar(toolbar)
  }



  companion object {
    val TAG = "MainActivity"
  }
}

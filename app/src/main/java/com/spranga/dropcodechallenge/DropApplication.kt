package com.spranga.dropcodechallenge


import com.facebook.drawee.backends.pipeline.Fresco
import com.spranga.dropcodechallenge.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Created by nietzsche on 02/09/17.
 */
class DropApplication : DaggerApplication() {

  override fun onCreate() {
    super.onCreate()
    Fresco.initialize(this)
  }

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
    return DaggerAppComponent.builder().create(this)
  }
}
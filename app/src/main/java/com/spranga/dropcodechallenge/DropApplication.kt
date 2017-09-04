package com.spranga.dropcodechallenge


import com.facebook.drawee.backends.pipeline.Fresco
import com.spranga.dropcodechallenge.di.AppComponent
import com.spranga.dropcodechallenge.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Created by nietzsche on 02/09/17.
 */
class DropApplication : DaggerApplication() {

  private lateinit var appComponent: AppComponent
  override fun onCreate() {
    appComponent = DaggerAppComponent.builder().create(this) as AppComponent
    appComponent.inject(this)
    super.onCreate()
    Fresco.initialize(this)
  }

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
    return appComponent
  }
}
package com.spranga.dropcodechallenge.di

import com.spranga.dropcodechallenge.DropApplication
import com.spranga.dropcodechallenge.network.ApiService
import com.spranga.dropcodechallenge.ui.BeerFragment
import com.spranga.dropcodechallenge.ui.MainActivity
import com.spranga.dropcodechallenge.ui.behaviours.Behaviour
import com.spranga.dropcodechallenge.ui.behaviours.Generator
import com.spranga.dropcodechallenge.ui.behaviours.HopBehaviour
import com.spranga.dropcodechallenge.ui.behaviours.HopsGenerator
import com.spranga.dropcodechallenge.ui.behaviours.MaltsGenerator
import com.spranga.dropcodechallenge.ui.behaviours.MethodsGenerator
import com.spranga.dropcodechallenge.ui.behaviours.SimpleBehaviour
import com.spranga.dropcodechallenge.ui.behaviours.TimerBehaviour
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Created by nietzsche on 02/09/17.
 */
@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, ActivityModule::class,
    NetworkModule::class))
interface AppComponent : AndroidInjector<DropApplication> {

  @Component.Builder
  abstract class Builder : AndroidInjector.Builder<DropApplication>()
}


@Module
abstract class ActivityModule {

  @ContributesAndroidInjector(modules = arrayOf(BeerFragmentModule::class))
  internal abstract fun mainActivity(): MainActivity
}

@Module
abstract class BeerFragmentModule {
  @ContributesAndroidInjector(modules = arrayOf(BehaviourModule::class, GeneratorModule::class))
  internal abstract fun beerFragment(): BeerFragment
}


@Module
object BehaviourModule {
  @Provides
  @IntoSet
  @JvmStatic
  internal fun simpleBehaviour(view: BeerFragment): Behaviour = SimpleBehaviour(view)

  @Provides
  @IntoSet
  @JvmStatic
  internal fun timerBehaviour(view: BeerFragment): Behaviour = TimerBehaviour(
      view)

  @Provides
  @IntoSet
  @JvmStatic
  internal fun hopCoordinator(view: BeerFragment): Behaviour = HopBehaviour(
      view)
}

@Module
object GeneratorModule {

  @Provides
  @IntoSet
  @JvmStatic
  internal fun hopsGenerator(view: BeerFragment): Generator = HopsGenerator(
      view)

  @Provides
  @IntoSet
  @JvmStatic
  internal fun maltsGenerator(view: BeerFragment): Generator = MaltsGenerator(
      view)

  @Provides
  @IntoSet
  @JvmStatic
  internal fun methodsGenerator(view: BeerFragment): Generator = MethodsGenerator(
      view)

}


@Module
object NetworkModule {

  @Provides
  @JvmStatic
  fun provideOkHttp(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder().addInterceptor(interceptor).build()
  }

  @Provides
  @JvmStatic
  fun provideApiService(okHttpClient: OkHttpClient): ApiService {
    val builder = Retrofit.Builder().client(okHttpClient)
        .baseUrl("https://api.punkapi.com/v2/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
    return builder.build().create(ApiService::class.java)
  }
}
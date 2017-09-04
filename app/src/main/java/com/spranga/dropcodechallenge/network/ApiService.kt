package com.spranga.dropcodechallenge.network

import com.spranga.dropcodechallenge.model.BeerModel
import io.reactivex.Maybe
import retrofit2.http.GET

/**
 * Created by nietzsche on 02/09/17.
 */
interface ApiService{

  @GET("beers/random")
  fun getRandomBeer(): Maybe<List<BeerModel>>
}
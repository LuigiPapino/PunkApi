package com.spranga.dropcodechallenge.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.spranga.dropcodechallenge.R.layout
import com.spranga.dropcodechallenge.model.BeerModel
import com.spranga.dropcodechallenge.network.ApiService
import com.spranga.dropcodechallenge.ui.behaviours.Behaviour
import com.spranga.dropcodechallenge.ui.behaviours.Generator
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.beer_fragment.beer_flag
import kotlinx.android.synthetic.main.beer_fragment.beer_image
import kotlinx.android.synthetic.main.beer_fragment.beer_name
import kotlinx.android.synthetic.main.beer_fragment.recipe_recycler
import javax.inject.Inject

/**
 * Created by nietzsche on 02/09/17.
 */
class BeerFragment : Fragment() {


  @Inject
  internal lateinit var presenter: BeerPresenter

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(layout.beer_fragment, container, false)
  }

  override fun onAttach(context: Context?) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  private lateinit var adapter: ItemsAdapter
  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recipe_recycler.layoutManager = LinearLayoutManager(context)
    adapter = ItemsAdapter { it, pos ->
      presenter.action(it, pos)
    }
    recipe_recycler.adapter = adapter
    beer_image.hierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
        .setProgressBarImage(ProgressBarDrawable())
        .build()

    presenter.start()

  }

  override fun onDestroyView() {
    presenter.destroy()
    super.onDestroyView()
  }

  fun bind(imageUri: String, name: String) {
    beer_image.setImageURI(imageUri)
    beer_name.text = name
  }


  fun updateHop(item: ListItem, pos: Int) {
    adapter.update(pos, item)
  }

  fun loadList(items: MutableList<ListItem>) {
    adapter.update(items)
  }

  fun showError(s: String) {
    AlertDialog.Builder(context).setMessage(s).show()
  }

  fun getItems() = adapter.items
  fun showFlag(it: String) {
    beer_flag.text = it
  }
}


class BeerPresenter @Inject constructor(private val view: BeerFragment,
    private val apiService: ApiService,
    private val generators: Set<@JvmSuppressWildcards Generator>,
    private val behaviours: Set<@JvmSuppressWildcards Behaviour>) {

  private var disposable: CompositeDisposable = CompositeDisposable()

  fun start() {
    disposable = CompositeDisposable()
    disposable += apiService.getRandomBeer().subscribeOn(Schedulers.io()).observeOn(
        AndroidSchedulers.mainThread()).subscribe({
      Log.d(MainActivity.TAG, it.toString())
      loadBeer(it[0])
    }, { view.showError("Error in retrieving the beer") })
  }

  private fun loadBeer(beer: BeerModel) {
    view.bind(beer.image_url, "${beer.name} ABV:${beer.abv} IBU:${beer.ibu ?: "none"}")
    val items: MutableList<ListItem> = ArrayList()
    generators.forEach({
      items.addAll(it.generate(beer))
    })
    view.loadList(items)

    var flag: String? = null
    if (beer.abv >= 10 && beer.ibu != null && beer.ibu >= 90) {
      flag = "Strong & Bitter"
    } else if (beer.abv >= 10) {
      flag = "Strong"
    } else if (beer.ibu != null && beer.ibu >= 90) {
      flag = "Bitter"
    }

    flag?.let { view.showFlag(it) }
  }


  fun destroy() {
    disposable.dispose()
  }

  fun action(item: ListItem, pos: Int) {
    behaviours.asSequence()
        .filter { it.shouldApply(item) }
        .forEach({ it.action(items, pos) })
  }


  private val items: List<ListItem>
    get() = view.getItems()

}

enum class ButtonState { IDLE, DONE, RUNNING, PAUSED }
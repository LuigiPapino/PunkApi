package com.spranga.dropcodechallenge.ui.behaviours

import com.spranga.dropcodechallenge.model.BeerModel
import com.spranga.dropcodechallenge.ui.BeerFragment
import com.spranga.dropcodechallenge.ui.ButtonState.IDLE
import com.spranga.dropcodechallenge.ui.ListItemData
import com.spranga.dropcodechallenge.ui.ListItemType.Hop
import com.spranga.dropcodechallenge.ui.ListItemType.Method

interface Generator {
  val view: BeerFragment
  fun generate(beer: BeerModel): List<ListItemData>
}


class HopsGenerator(override val view: BeerFragment) : Generator {
  override fun generate(beer: BeerModel): List<ListItemData> {
    return beer.ingredients.hops.asSequence()
        .map {
          ListItemData(name = "Hop: " + it.name,
              actionName = IDLE.toString(), type = Hop,
              hopAdd = it.add)
        }
        .toList()
  }
}

class MaltsGenerator(override val view: BeerFragment) : Generator {
  override fun generate(beer: BeerModel): List<ListItemData> {
    return beer.ingredients.malt.asSequence()
        .map {
          ListItemData(
              "Malt:  ${it.name} ${it.amount.value}${it.amount.unit}", IDLE.toString(), Hop)
        }
        .toList()
  }
}

class MethodsGenerator(override val view: BeerFragment) : Generator {
  override fun generate(beer: BeerModel): List<ListItemData> {
    val items = ArrayList<ListItemData>()
    beer.method.fermentation.let {
      items += ListItemData(
          "Fermentation at ${it.temp.value}${it.temp.unit}", IDLE.toString(), Method)
    }
    beer.method.twist?.let {
      items += ListItemData("Twist $it", IDLE.toString(), Method)
    }
    items += beer.method.mash_temp.asSequence()
        .map {
          ListItemData(
              "Mash temp: ${it.duration} at ${it.temp.value.toInt()}${it.temp.unit}",
              IDLE.toString(),
              Method, it.duration)
        }
        .toList()

    return items
  }
}

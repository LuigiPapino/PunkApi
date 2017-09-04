package com.spranga.dropcodechallenge.ui.behaviours

import com.spranga.dropcodechallenge.ui.BeerFragment
import com.spranga.dropcodechallenge.ui.ButtonState.DONE
import com.spranga.dropcodechallenge.ui.ButtonState.IDLE
import com.spranga.dropcodechallenge.ui.ButtonState.PAUSED
import com.spranga.dropcodechallenge.ui.ButtonState.RUNNING
import com.spranga.dropcodechallenge.ui.ButtonState.valueOf
import com.spranga.dropcodechallenge.ui.ListItemData
import com.spranga.dropcodechallenge.ui.ListItemType
import com.spranga.dropcodechallenge.ui.ListItemType.Malt
import com.spranga.dropcodechallenge.ui.ListItemType.Method
import com.spranga.dropcodechallenge.utils.CountDownTimerWithPause


/**
 * Created by nietzsche on 03/09/17.
 */
interface Behaviour {

  val view: BeerFragment
  fun action(items: List<ListItemData>, pos: Int)
  fun shouldApply(item: ListItemData): Boolean

}


class SimpleBehaviour(override val view: BeerFragment) : Behaviour {
  override fun shouldApply(
      item: ListItemData) = item.type == Malt || (item.type == Method && item.duration == null)

  override fun action(items: List<ListItemData>, pos: Int) {
    val state = DONE
    view.updateHop(items[pos].copy(actionName = state.toString()), pos)
  }
}


class TimerBehaviour(override val view: BeerFragment) : Behaviour {
  override fun shouldApply(item: ListItemData) = item.type == Method && item.duration != null
  override fun action(items: List<ListItemData>, pos: Int) {
    val item = items[pos]
    val state = valueOf(item.actionName)
    val newState = when (state) {
      IDLE -> {
        countdownMap[pos] = object : CountDownTimerWithPause(item.duration?.times(1000L) ?: 0L,
            1000L,
            true) {
          override fun onTick(millisUntilFinished: Long) {
            view.updateHop(item.copy(actionName = RUNNING.toString(),
                countdownLabel = (millisUntilFinished / 1000).toString()), pos)
          }

          override fun onFinish() {
            val newItem = item.copy(actionName = DONE.toString(), countdownLabel = null)
            view.updateHop(newItem, pos)
          }
        }
        countdownMap[pos]?.create()
        RUNNING
      }
      DONE -> DONE
      RUNNING -> {
        countdownMap[pos]?.pause()
        PAUSED
      }
      PAUSED -> {
        countdownMap[pos]?.resume()
        RUNNING
      }
    }

    view.updateHop(item.copy(actionName = newState.toString()), pos)
  }


  private val countdownMap = HashMap<Int, CountDownTimerWithPause>()

  companion object {
    val TAG = "TimerBehaviour"
  }
}


class HopBehaviour(override val view: BeerFragment) : Behaviour {

  override fun action(items: List<ListItemData>, pos: Int) {
    val item = items[pos]

    val itemAdd = item.hopAdd

    val targetAdd = when (itemAdd) {
      "middle" -> "start"
      "end" -> "middle"
      else -> null
    }

    if (targetAdd != null) {
      val completed = items.asSequence()
          .filter { it.hopAdd != null && it.hopAdd.contentEquals(targetAdd) }
          .all { it.actionName == DONE.toString() }
      if (!completed) {
        view.showError("Not all previous hops are done")
        return
      }
    }
    view.updateHop(item.copy(actionName = DONE.toString()), pos)

  }

  override fun shouldApply(item: ListItemData): Boolean {
    return item.type == ListItemType.Hop
  }

}
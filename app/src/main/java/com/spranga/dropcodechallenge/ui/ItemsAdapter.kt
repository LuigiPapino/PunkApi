package com.spranga.dropcodechallenge.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spranga.dropcodechallenge.R.layout
import kotlinx.android.synthetic.main.list_item.view.item_action
import kotlinx.android.synthetic.main.list_item.view.item_name
import java.util.Collections


/**
 * Created by nietzsche on 02/09/17.
 */
class ItemsAdapter(items: List<ListItem> = Collections.emptyList(),
    private val listener: (ListItem, Int) -> Unit = { _, _ -> }) : RecyclerView.Adapter<ItemViewHolder>() {

  val items: MutableList<ListItem>

  init {
    this.items = ArrayList(items)
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(
      items[position], position)

  override fun onCreateViewHolder(parent: ViewGroup,
      viewType: Int): ItemViewHolder = ItemViewHolder(
      parent.inflate(layout.list_item), listener)

  override fun getItemCount(): Int = items.size

  fun update(pos: Int, item: ListItem) {
    items[pos] = item
    notifyItemChanged(pos)
  }

  fun update(items: MutableList<ListItem>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }


}

class ItemViewHolder(itemView: View,
    private val listener: (ListItem, Int) -> Unit) : RecyclerView.ViewHolder(itemView) {

  fun bind(item: ListItem, position: Int) {
    itemView.item_name.text = "${item.name} ${item.hopAdd ?: ""}"
    itemView.item_action.text = "${item.actionName} ${item.countdownLabel ?: ""}"
    itemView.item_action.setOnClickListener({ listener.invoke(item, position) })

  }
}

data class ListItem(val name: String, val actionName: String, val type: ListItemType,
    val duration: Int? = null, val countdownLabel: String? = null, val hopAdd: String? = null)

enum class ListItemType { Hop, Malt, Method }

fun ViewGroup.inflate(layoutRes: Int): View {
  return LayoutInflater.from(context).inflate(layoutRes, this, false)
}
package com.example.fitnesstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(
    private val itemList: MutableList<Item>,
    private val onItemClickListener: (Item, Int) -> Unit
): RecyclerView.Adapter<MyAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.item_list_name)
        val itemDistance: TextView = itemView.findViewById(R.id.item_list_distance)
        val itemTime: TextView = itemView.findViewById(R.id.item_list_time)
        val itemKcal: TextView = itemView.findViewById(R.id.item_list_kcal)
        val itemDesc: TextView = itemView.findViewById(R.id.item_list_desc)
        val itemIntensity: TextView = itemView.findViewById(R.id.item_list_intensity)
        val itemType: TextView = itemView.findViewById(R.id.item_list_type)
        val itemIcon: ImageView = itemView.findViewById(R.id.item_list_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.name
        holder.itemDistance.text = currentItem.distance
        holder.itemTime.text = currentItem.time
        holder.itemKcal.text = currentItem.kcal
        holder.itemDesc.text = currentItem.desc
        holder.itemIntensity.text = currentItem.intensity.toString()
        holder.itemType.text = currentItem.type
        holder.itemIcon.setImageResource(currentItem.iconRes)

        holder.itemView.setOnClickListener{
            onItemClickListener(currentItem, position)
        }

        holder.itemIntensity.text = when(currentItem.intensity){
            0 -> "Lekki"
            1 -> "Średni"
            2 -> "Intensywny"
            else -> "Nieznany"
        }
    }

    override fun getItemCount(): Int = itemList.size

}
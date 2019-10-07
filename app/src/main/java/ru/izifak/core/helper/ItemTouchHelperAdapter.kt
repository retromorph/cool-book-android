package ru.izifak.core.helper

import androidx.recyclerview.widget.RecyclerView


interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemDismiss(position: Int)
}
package vn.sharkdms.util

import androidx.recyclerview.widget.RecyclerView

class AdapterDataObserver(private val recyclerView: RecyclerView) :
    RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        recyclerView.scrollToPosition(0)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        recyclerView.scrollToPosition(0)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        recyclerView.scrollToPosition(0)
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        recyclerView.scrollToPosition(0)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        recyclerView.scrollToPosition(0)
    }
}
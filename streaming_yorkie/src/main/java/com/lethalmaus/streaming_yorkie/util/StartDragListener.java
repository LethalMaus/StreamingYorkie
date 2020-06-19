package com.lethalmaus.streaming_yorkie.util;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Interface for applying recycler view row movements to an image instead of the row itself.
 * @author LethalMaus
 */
public interface StartDragListener {
    /**
     * Method for requesting drag
     * @author LethalMaus
     * @param viewHolder Adapter.ViewHolder
     */
    void requestDrag(RecyclerView.ViewHolder viewHolder);
}

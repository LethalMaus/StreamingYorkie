package com.lethalmaus.streaming_yorkie.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lethalmaus.streaming_yorkie.adapter.HostAdapter;

/**
 * Utility class for recycler view row movement callbacks
 * @author LethalMaus
 */
public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    /**
     * Constructor for the callback
     * @author LethalMaus
     * @param adapter recyclerview.adapter
     */
    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {/* Do nothing */}

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof HostAdapter.HostViewHolder) {
                HostAdapter.HostViewHolder myViewHolder=
                        (HostAdapter.HostViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof HostAdapter.HostViewHolder) {
            HostAdapter.HostViewHolder myViewHolder=
                    (HostAdapter.HostViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    /**
     * Interface
     * @author LethalMaus
     */
    public interface ItemTouchHelperContract {
        /**
         * Logic after the row has been moved
         * @author LethalMaus
         * @param fromPosition int
         * @param toPosition int
         */
        void onRowMoved(int fromPosition, int toPosition);

        /**
         * Update UI on selection
         * @author LethalMaus
         * @param viewHolder Adapter.ViewHolder
         */
        void onRowSelected(HostAdapter.HostViewHolder viewHolder);

        /**
         * Update UI on release
         * @author LethalMaus
         * @param viewHolder Adapter.ViewHolder
         */
        void onRowClear(HostAdapter.HostViewHolder viewHolder);
    }
}
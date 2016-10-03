package org.k.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.IllegalFormatCodePointException;

/**
 * Created by khangnt on 9/19/16.
 * Email: khang.neon.1997@gmail.com
 */
public class KRecyclerView extends RecyclerView {
    protected int focusPosition;
    protected WrappedLinearLayoutManager layoutManager;
    protected EndlessAdapter adapter;
    protected float focusedItemHeight, defaultItemHeight, flingScrollSpeedFactor;
    protected float minimumChange;

    public KRecyclerView(Context context) {
        this(context, null);
    }

    public KRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setLayoutManager(layoutManager = new WrappedLinearLayoutManager(context));
        this.focusPosition = 0;
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KRecyclerView, defStyle, 0);
        if (ta != null) {
            focusedItemHeight = ta.getDimension(R.styleable.KRecyclerView_focusedItemHeight, 400);
            defaultItemHeight = ta.getDimension(R.styleable.KRecyclerView_defaultItemHeight, 200);
            flingScrollSpeedFactor = ta.getDimension(R.styleable.KRecyclerView_flingScrollSpeedFactor, 1f);
            minimumChange = ta.getDimension(R.styleable.KRecyclerView_updateWhenPixelChangedLargerThan, 2f);
            ta.recycle();
        } else {
            focusedItemHeight = 400f;
            defaultItemHeight = 200f;
            flingScrollSpeedFactor = 1f;
            minimumChange = 2f;
        }
        super.addOnScrollListener(new OnScrollListener() {
            int dy = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                this.dy += dy;
                float change = this.dy * getScaleFactor();
                if (change > minimumChange || change < -minimumChange) {    // improve performance
                    Log.d("TAG", "onScrolled: " + this.dy + " --- " + dy);
                    processOnScrolled(-this.dy);
                    this.dy = 0;
                }
            }
        });
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        // empty
    }

    @Override
    public void setAdapter(Adapter adapter) throws IllegalArgumentException {
        if (adapter instanceof EndlessAdapter) {
            this.adapter = ((EndlessAdapter) adapter);
            super.setAdapter(adapter);
        } else
            throw new IllegalArgumentException("Adapter must be instance of KRecyclerView.EndlessAdapter");
    }

    public void setFocusPosition(int focusPosition) {
        this.focusPosition = focusPosition;
        this.correctHeight = -1;
        this.adapter.notifyDataSetChanged();
        layoutManager.scrollToPositionWithOffset(focusPosition, 0);
    }

    public int getFocusPosition() {
        return focusPosition;
    }

    protected float getScaleFactor() {
        return (getFocusedItemHeight() - getDefaultHeight()) / getFocusedItemHeight();
    }



    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= flingScrollSpeedFactor; // slow down fling scroll speed.
        return super.fling(velocityX, velocityY);
    }

    float correctHeight = -1;
    private void processOnScrolled(int dy) {
        ViewHolder vh = findViewHolderForAdapterPosition(focusPosition + 1);
        if (dy != 0 && vh != null) {
            ViewGroup.LayoutParams lp = vh.itemView.getLayoutParams();
            if (correctHeight == -1)
                correctHeight = lp.height;
            correctHeight -= dy * getScaleFactor();
            int unProcessed = 0;
            if (correctHeight > getFocusedItemHeight()) {
                unProcessed = (int) -((correctHeight - getFocusedItemHeight()) / getScaleFactor());
                correctHeight = getFocusedItemHeight();
            } else if (correctHeight < getDefaultHeight()) {
                unProcessed = (int) -((correctHeight - getDefaultHeight()) / getScaleFactor());
                correctHeight = getDefaultHeight();
            }
            lp.height = (int) correctHeight;
            vh.itemView.setLayoutParams(lp);
            //noinspection unchecked
            adapter.onScaled(vh, focusPosition + 1, (correctHeight - getDefaultHeight()) / (getFocusedItemHeight() - getDefaultHeight()));
            if (focusPosition != 0 && correctHeight == getDefaultHeight()) {
                focusPosition = focusPosition > 0 ? focusPosition - 1 : 0;
                correctHeight = -1;
            } else if (correctHeight == getFocusedItemHeight()) {
                focusPosition++;
                correctHeight = -1;
            }
            if (unProcessed != 0) {
                Log.e("TAG", "processOnScrolled: -------------------" );
                processOnScrolled(unProcessed);
            }
        }
    }

    public float getDefaultHeight() {
        return defaultItemHeight;
    }

    public float getFocusedItemHeight() {
        return focusedItemHeight;
    }

    private class WrappedLinearLayoutManager extends LinearLayoutManager {

        WrappedLinearLayoutManager(Context context) {
            super(context, VERTICAL, false);
            setStackFromEnd(true);
        }

        @Override
        public void addView(View child) {
            super.addView(child);
        }

        @Override
        public void addView(View child, int index) {
            super.addView(child, index);
        }

        @Override
        public void setStackFromEnd(boolean stackFromEnd) {
            super.setStackFromEnd(false);
        }

        @Override
        public void setOrientation(int orientation) {
            super.setOrientation(VERTICAL);
        }

        @Override
        public void setReverseLayout(boolean reverseLayout) {
            super.setReverseLayout(false);
        }

        @Override
        public LayoutParams generateDefaultLayoutParams() {
            RecyclerView.LayoutParams lp = super.generateDefaultLayoutParams();
            lp.height = (int) getDefaultHeight();
            return lp;
        }

        @Override
        public LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
            LayoutParams layoutParams = super.generateLayoutParams(c, attrs);
            layoutParams.height = (int) getDefaultHeight();
            return layoutParams;
        }

        @Override
        public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
            LayoutParams layoutParams = super.generateLayoutParams(lp);
            layoutParams.height = (int) getDefaultHeight();
            return layoutParams;
        }
    }

    public static abstract class EndlessAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        KRecyclerView kRecyclerView;

        public EndlessAdapter(KRecyclerView kRecyclerView) {
            this.kRecyclerView = kRecyclerView;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            onBindViewHolder_(holder, getRealPosition(position));
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = (int) (position <= kRecyclerView.getFocusPosition() ?
                                kRecyclerView.getFocusedItemHeight() : kRecyclerView.getDefaultHeight());
            holder.itemView.setLayoutParams(layoutParams);
            onScaled(holder, position, position <= kRecyclerView.getFocusPosition() ? 1 : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return getItemViewType_(getRealPosition(position));
        }

        @Override
        public int getItemCount() {
            return getItemCount_() * 10000;  // infinity scroll
        }

        public abstract int getItemViewType_(int position);

        public abstract int getItemCount_();

        public abstract void onBindViewHolder_(VH holder, int position);

        public abstract void onScaled(VH holder, int position, float f);

        private int getRealPosition(int position) {
            if (getItemCount_() == 0)
                return -1;  // invalid state
            int realCount = getItemCount_();
            return position % realCount;
        }
    }
}

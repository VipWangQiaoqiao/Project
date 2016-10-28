package net.oschina.app.improve.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thanatosx on 16/10/27.
 */

public class TabPickerView extends FrameLayout {

    private ImageView mViewArrow;

    private RecyclerView mRecyclerActive;
    private RecyclerView mRecyclerInactive;
    private TabAdapter<SubTab, TabAdapter.ViewHolder> mActiveAdapter;
    private TabAdapter<SubTab, TabAdapter.ViewHolder> mInactiveAdapter;

    private TabPickerDataManager mTabManager;
    private OnTabPickingListener mTabPickingListener;

    private int mSelectedIndex = 0;

    public TabPickerView(Context context) {
        this(context, null);
    }

    public TabPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidgets();
        initData();
    }

    /**
     * The Tab Picking Listener Interface
     */
    public interface OnTabPickingListener{
        /**
         * 单击选择某个tab
         * @param position select a tab
         */
        void onSelected(int position);

        /**
         * 删除某个tab
         * @param position the moved tab's position
         * @param tab the moved tab
         */
        void onRemove(int position, SubTab tab);

        /**
         * 添加某个tab
         * @param tab the inserted tab
         */
        void onInsert(SubTab tab);

        /**
         * 交换tab
         * @param op the mover's position
         * @param mover the moving tab
         * @param np the swapper's position
         * @param swapper the swapped tab
         */
        void onMove(int op, SubTab mover, int np, SubTab swapper);

        /**
         * 重新持久化活动的tabs
         * @param activeTabs the actived tabs
         */
        void onRestore(List<SubTab> activeTabs);
    }

    private void initWidgets() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.view_tab_picker, this, false);

        mViewArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        mRecyclerActive = (RecyclerView) view.findViewById(R.id.view_recycler_active);
        mRecyclerInactive = (RecyclerView) view.findViewById(R.id.view_recycler_inactive);

        addView(view);

        mViewArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO turn back
                hide();
            }
        });

    }

    public void show(int selectedIndex) {
        int tempIndex = mSelectedIndex;
        mSelectedIndex = selectedIndex;
        mActiveAdapter.notifyItemChanged(tempIndex);
        mActiveAdapter.notifyItemChanged(mSelectedIndex);

        setVisibility(VISIBLE);
        setTranslationY(-getHeight() * 0.2f);
        setAlpha(0);
        animate()
                .translationY(0)
                .alpha(1)
                .setDuration(380)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setTranslationY(0);
                    }
                }).start();
    }

    public void hide(){
        if (mTabPickingListener != null){
            mTabPickingListener.onRestore(mTabManager.mActiveDataSet);
        }
        animate()
                .translationY(-getHeight())
                .setDuration(500)
                .alpha(0)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(GONE);
                    }
                }).start();
    }


    private void initData() {

    }

    private void initRecyclerView() {
        if (mRecyclerActive.getAdapter() != null && mRecyclerInactive.getAdapter() != null) return;

        /* - set up Active Recycler - */
        mActiveAdapter = new TabAdapter<SubTab, TabAdapter.ViewHolder>(mTabManager.mActiveDataSet) {
            @Override
            public TabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TabAdapter.ViewHolder(createItemView());
            }

            @Override
            public void onBindViewHolder(TabAdapter.ViewHolder holder, int position) {
                SubTab item = items.get(position);
                holder.mViewTab.setText(item.getName());
                if (item.isFixed()) {
                    holder.mViewTab.setActivated(false);
                }else {
                    holder.mViewTab.setActivated(true);
                }
                if (mSelectedIndex == position){
                    holder.mViewTab.setSelected(true);
                }else {
                    holder.mViewTab.setSelected(false);
                }
                if (!TextUtils.isEmpty(item.getTag())) {
                    holder.mViewBubble.setText(item.getTag());
                    holder.mViewBubble.setVisibility(VISIBLE);
                } else {
                    holder.mViewBubble.setVisibility(GONE);
                }
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        };
        mActiveAdapter.setOnClickItemListener(new TabAdapter.OnSelectItemListener() {
            @Override
            public void onSelect(int position) {
                // 先调用隐藏, 因为隐藏有保存,更新tab的动作
                hide();
                if (mTabPickingListener != null){
                    mTabPickingListener.onSelected(position);
                }
            }
        });
        mActiveAdapter.setOnDoubleClickItemListener(new TabAdapter.OnSelectItemListener() {
            @Override
            public void onSelect(int position) {
                SubTab tab = mActiveAdapter.getItem(position);
                if (tab.isFixed()) return;
                int oldCount = mActiveAdapter.getItemCount();
                tab = mActiveAdapter.removeItem(position);
                // 放到下面需要根据Original DataSet重排序
                for (SubTab item : mTabManager.mOriginalDataSet) {
                    if (!item.getToken().equals(tab.getToken())) continue;
                    tab.setOrder(item.getOrder());
                    break;
                }

                int i = 0;
                for (; i < mTabManager.mInactiveDataSet.size(); i++) {
                    SubTab item = mTabManager.mInactiveDataSet.get(i);
                    if (item.getOrder() < tab.getOrder()) continue;
                    break;
                }
                mTabManager.mInactiveDataSet.add(i, tab);
                mInactiveAdapter.notifyItemInserted(i);

                if (mSelectedIndex == position){
                    mSelectedIndex = position == oldCount - 1 ? mSelectedIndex - 1 : mSelectedIndex;
                    mActiveAdapter.notifyItemChanged(mSelectedIndex);
                }
                if (mTabPickingListener != null){
                    mTabPickingListener.onRemove(position, tab);
                }
            }
        });
        mRecyclerActive.setAdapter(mActiveAdapter);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = 0;
                if (!mActiveAdapter.getItem(viewHolder.getAdapterPosition()).isFixed()) {
                    dragFlag = ItemTouchHelper.UP
                            | ItemTouchHelper.DOWN
                            | ItemTouchHelper.LEFT
                            | ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFlag, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int fromTargetIndex = viewHolder.getAdapterPosition();
                int toTargetIndex = target.getAdapterPosition();
                if (mActiveAdapter.getItem(toTargetIndex).isFixed()) return true;
                if (fromTargetIndex < toTargetIndex) {
                    for (int i = fromTargetIndex; i < toTargetIndex; i++) {
                        Collections.swap(mTabManager.mActiveDataSet, i, i + 1);
                    }
                } else {
                    for (int i = fromTargetIndex; i > toTargetIndex; i--) {
                        Collections.swap(mTabManager.mActiveDataSet, i, i - 1);
                    }
                }
                mRecyclerActive.getAdapter().notifyItemMoved(fromTargetIndex, toTargetIndex);
                if (mTabPickingListener != null){
                    mTabPickingListener.onMove(fromTargetIndex, mActiveAdapter.getItem(fromTargetIndex),
                            toTargetIndex, mActiveAdapter.getItem(toTargetIndex));
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // pass
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder == null) return;
                viewHolder.itemView.setSelected(true);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setSelected(false);
            }
        }).attachToRecyclerView(mRecyclerActive);
        mRecyclerActive.setLayoutManager(new GridLayoutManager(getContext(), 4));

        /* - set up Inactive Recycler - */
        mRecyclerInactive.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mInactiveAdapter = new TabAdapter<SubTab, TabAdapter.ViewHolder>(mTabManager.mInactiveDataSet) {
            @Override
            public TabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(createItemView());
            }

            @Override
            public void onBindViewHolder(TabAdapter.ViewHolder holder, int position) {
                holder.mViewTab.setText(items.get(position).getName());
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        };
        mInactiveAdapter.setOnClickItemListener(new TabAdapter.OnSelectItemListener() {
            @Override
            public void onSelect(int position) {
                SubTab tab = mInactiveAdapter.removeItem(position);
                mActiveAdapter.addItem(tab);
                if (mTabPickingListener != null){
                    mTabPickingListener.onInsert(tab);
                }
            }
        });
        mRecyclerInactive.setAdapter(mInactiveAdapter);
    }

    private View createItemView() {
        final int dp8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getContext().getResources().getDisplayMetrics());
        final int dp6 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getContext().getResources().getDisplayMetrics());

        /*layout*/
        RelativeLayout layout = new RelativeLayout(getContext());
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        /*bubble view*/
        TextView bubble = new TextView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.setMargins(0, 0, dp8 / 8, 0);
        bubble.setLayoutParams(params);

        bubble.setTextColor(0XFFFFFFFF);
        bubble.setPadding(dp6 / 2, 0, dp6 / 2, 0);
        bubble.setVisibility(GONE);
        bubble.setTag("mViewBubble");
        bubble.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_bubble));
        bubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        bubble.setGravity(Gravity.CENTER);

        /*content text view*/
        TextView view = new TextView(getContext());
        view.setTag("mViewTab");
        view.setActivated(true);
        RelativeLayout.LayoutParams mTextParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        mTextParams.setMargins(dp8, dp6, dp8, dp6);
        view.setLayoutParams(mTextParams);
        view.setText("软件");
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        view.setTextColor(new ColorStateList(new int[][]{
                        new int[]{android.R.attr.state_selected},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{}
                }, new int[]{
                        0XFFFFFFFF, 0XFF9A9A9A, 0XFF4E4E4E})
        );
        view.setGravity(Gravity.CENTER);
        int dp4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getContext().getResources().getDisplayMetrics());
        view.setPadding(0, dp4, 0, dp4);
        view.setClickable(true);
        view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_dynamic_tab));

        layout.addView(view);
        layout.addView(bubble);

        return layout;
    }

    public void setTabPickerManager(TabPickerDataManager manager) {
        if (manager == null) return;
        mTabManager = manager;
        initRecyclerView();
    }

    public TabPickerDataManager getTabPickerManager(){
        return mTabManager;
    }

    public void setOnTabPickingListener(OnTabPickingListener l){
        mTabPickingListener = l;
    }

    /**
     * Class TabAdapter
     *
     * @param <VH>
     */
    public abstract static class TabAdapter<T, VH extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<VH> {

        private OnSelectItemListener onClickItemListener;
        private OnSelectItemListener onDoubleClickItemListener;
        private OnTabTouchListener mTouchListener;
        List<T> items;

        public TabAdapter(List<T> items) {
            this.items = items;
        }

        public interface OnSelectItemListener {
            void onSelect(int position);
        }

        T removeItem(int position) {
            T b = items.remove(position);
            notifyItemRemoved(position);
            return b;
        }

        void addItem(T bean) {
            items.add(bean);
            notifyItemInserted(items.size() - 1);
        }

        void addItem(T bean, int index) {
            items.add(index, bean);
            notifyItemInserted(index);
        }

        T getItem(int position) {
            return items.get(position);
        }

        OnTabTouchListener getTouchListener() {
            if (mTouchListener == null) {
                mTouchListener = new OnTabTouchListener();
            }
            return mTouchListener;
        }

        /**
         * Tab View Holder
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            TextView mViewTab;
            TextView mViewBubble;

            ViewHolder(View view) {
                super(view);
                mViewTab = (TextView) view.findViewWithTag("mViewTab");
                mViewBubble = (TextView) view.findViewWithTag("mViewBubble");
                mViewTab.setOnTouchListener(getTouchListener());
                mViewTab.setTag(this);
            }
        }

        /**
         * Inner Tab Touch Listener
         */
        private class OnTabTouchListener implements OnTouchListener {
            private GestureDetector detector;
            private int position;

            OnTabTouchListener() {
                this.detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (onDoubleClickItemListener != null) {
                            onDoubleClickItemListener.onSelect(position);
                        }
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (onClickItemListener != null) {
                            onClickItemListener.onSelect(position);
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });
            }

            @Override
            @SuppressWarnings("all")
            public boolean onTouch(View v, MotionEvent event) {
                ViewHolder holder = (ViewHolder) v.getTag();
                if (holder == null) return false;
                position = holder.getAdapterPosition();
                return detector.onTouchEvent(event);
            }
        }

        void setOnClickItemListener(OnSelectItemListener l) {
            this.onClickItemListener = l;
        }

        void setOnDoubleClickItemListener(OnSelectItemListener l) {
            this.onDoubleClickItemListener = l;
        }
    }

    /**
     * Tab Data Picker Manager, Manager the Tab Data's behavior
     *
     * @param
     */
    public abstract static class TabPickerDataManager {

        List<SubTab> mActiveDataSet;
        List<SubTab> mInactiveDataSet;
        List<SubTab> mOriginalDataSet;

        public TabPickerDataManager() {
            mActiveDataSet = setupActiveDataSet();
            mOriginalDataSet = setupOriginalDataSet();

            if (mActiveDataSet == null || mActiveDataSet.size() == 0) {
                throw new RuntimeException("Active Data Set can't be null or empty");
            }

            if (mOriginalDataSet == null || mOriginalDataSet.size() == 0) {
                throw new RuntimeException("Original Data Set can't be null or empty");
            }

            mInactiveDataSet = new ArrayList<>();
            for (SubTab item : mOriginalDataSet) {
                if (mActiveDataSet.contains(item)) continue;
                mInactiveDataSet.add(item);
            }
        }

        public List<SubTab> getActiveDataSet() {
            return mActiveDataSet;
        }

        public List<SubTab> getInActiveDataSet() {
            return mInactiveDataSet;
        }

        public List<SubTab> getOriginalDataSet() {
            return mOriginalDataSet;
        }

        public abstract List<SubTab> setupActiveDataSet();

        public abstract List<SubTab> setupOriginalDataSet();
    }

}

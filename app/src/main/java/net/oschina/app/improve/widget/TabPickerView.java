package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 动态栏目View 请通过{@link #setTabPickerManager(TabPickerDataManager)}来设置活动数据和原始数据，数据
 * 对象根据需要实现{@link Object#hashCode()}和{@link Object#equals(Object)}方法，因为非活动数据是通过使用
 * {@link List#contains(Object)}方法从原始数据剔除活动数据实现的。
 * <p>
 * <p>活动动态栏目的添加、删除、移动、选择通过{@link OnTabPickingListener}来实现的，你可以通过方法
 * {@link #setOnTabPickingListener(OnTabPickingListener)}来监听。
 * <p>
 * <p>通过{@link #show(int)}和{@link #hide()}方法来显示隐藏动态栏目界面。
 * <p>
 * <p>通过{@link #onTurnBack()}响应回退事件。
 * <p>
 * <p>Created by thanatosx on 16/10/27.
 */
@SuppressWarnings("all")
public class TabPickerView extends FrameLayout {

    private ImageView mViewArrow;
    private TextView mViewDone;
    private TextView mViewOperator;
    private RecyclerView mRecyclerActive;
    private RecyclerView mRecyclerInactive;
    private LinearLayout mLayoutWrapper;
    private ItemTouchHelper mItemTouchHelper;

    private TabAdapter<TabAdapter.ViewHolder> mActiveAdapter;
    private TabAdapter<TabAdapter.ViewHolder> mInactiveAdapter;

    private TabPickerDataManager mTabManager;
    private OnTabPickingListener mTabPickingListener;

    private Action1<ViewPropertyAnimator> mOnShowAnimator;
    private Action1<ViewPropertyAnimator> mOnHideAnimator;

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
    }

    /**
     * The Tab Picking Listener Interface
     */
    public interface OnTabPickingListener {
        /**
         * 单击选择某个tab
         *
         * @param position select a tab
         */
        void onSelected(int position);

        /**
         * 删除某个tab
         *
         * @param position the moved tab's position
         * @param tab      the moved tab
         */
        void onRemove(int position, SubTab tab);

        /**
         * 添加某个tab
         *
         * @param tab the inserted tab
         */
        void onInsert(SubTab tab);

        /**
         * 交换tab
         *
         * @param op      the mover's position
         * @param mover   the moving tab
         * @param np      the swapper's position
         * @param swapper the swapped tab
         */
        void onMove(int op, SubTab mover, int np, SubTab swapper);

        /**
         * 重新持久化活动的tabs
         *
         * @param activeTabs the actived tabs
         */
        void onRestore(List<SubTab> activeTabs);
    }

    public interface Action1<T> {
        void call(T t);
    }

    private void initWidgets() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.view_tab_picker, this, false);

        mViewArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        mRecyclerActive = (RecyclerView) view.findViewById(R.id.view_recycler_active);
        mRecyclerInactive = (RecyclerView) view.findViewById(R.id.view_recycler_inactive);
        mViewDone = (TextView) view.findViewById(R.id.tv_done);
        mViewOperator = (TextView) view.findViewById(R.id.tv_operator);
        mLayoutWrapper = (LinearLayout) view.findViewById(R.id.layout_wrapper);
        mViewDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewDone.getText().toString().equals("排序删除")) {
                    mActiveAdapter.startEditMode();
                } else {
                    mActiveAdapter.cancelEditMode();
                }
            }
        });

        addView(view);

        mViewArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTurnBack();
            }
        });

    }

    public void setOnShowAnimation(Action1<ViewPropertyAnimator> l) {
        this.mOnShowAnimator = l;
    }

    public void setOnHideAnimator(Action1<ViewPropertyAnimator> l) {
        this.mOnHideAnimator = l;
    }

    public void show(int selectedIndex) {
        final int tempIndex = mSelectedIndex;
        mSelectedIndex = selectedIndex;
        mActiveAdapter.notifyItemChanged(tempIndex);
        mActiveAdapter.notifyItemChanged(mSelectedIndex);

        if (mOnShowAnimator != null) {
            mOnShowAnimator.call(animate());
            animate().start();
        } else {
            setVisibility(VISIBLE);
        }
    }

    public void hide() {
        if (mTabPickingListener != null) {
            mTabPickingListener.onSelected(mSelectedIndex);
            mTabPickingListener.onRestore(mTabManager.mActiveDataSet);
        }
        if (mOnHideAnimator != null) {
            mOnHideAnimator.call(animate());
            animate().start();
        } else {
            setVisibility(GONE);
        }
    }

    private void initRecyclerView() {
        if (mRecyclerActive.getAdapter() != null && mRecyclerInactive.getAdapter() != null) return;

        /* - set up Active Recycler - */
        mActiveAdapter = new TabAdapter<TabAdapter.ViewHolder>(mTabManager.mActiveDataSet) {
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
                } else {
                    holder.mViewTab.setActivated(true);
                }
                if (mSelectedIndex == position) {
                    holder.mViewTab.setSelected(true);
                } else {
                    holder.mViewTab.setSelected(false);
                }
                if (!TextUtils.isEmpty(item.getTag())) {
                    holder.mViewBubble.setText(item.getTag());
                    holder.mViewBubble.setVisibility(VISIBLE);
                } else {
                    holder.mViewBubble.setVisibility(GONE);
                }
                if (isEditMode() && !item.isFixed()) {
                    holder.mViewDel.setVisibility(VISIBLE);
                } else {
                    holder.mViewDel.setVisibility(GONE);
                }
                if (mBindViewObserver != null) {
                    mBindViewObserver.call(holder);
                }
            }

            @Override
            public int getItemCount() {
                return items.size();
            }

        };

        mActiveAdapter.setOnClickItemListener(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                // 先调用隐藏, 因为隐藏有保存,更新tab的动作
                int tempIndex = mSelectedIndex;
                mSelectedIndex = position;
                mActiveAdapter.notifyItemChanged(tempIndex);
                mActiveAdapter.notifyItemChanged(mSelectedIndex);
                hide();
            }
        });

        mActiveAdapter.setOnDeleteItemListener(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
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

                if (mSelectedIndex == position) {
                    mSelectedIndex = position == oldCount - 1 ? mSelectedIndex - 1 : mSelectedIndex;
                    mActiveAdapter.notifyItemChanged(mSelectedIndex);
                } else if (mSelectedIndex > position) {
                    --mSelectedIndex;
                    mActiveAdapter.notifyItemChanged(mSelectedIndex);
                }
                if (mTabPickingListener != null) {
                    mTabPickingListener.onRemove(position, tab);
//                    mTabPickingListener.onSelected(mSelectedIndex);
                }
            }
        });
        mRecyclerActive.setAdapter(mActiveAdapter);

        mItemTouchHelper = new ItemTouchHelper(mActiveAdapter.newItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(mRecyclerActive);
        mRecyclerActive.setLayoutManager(new GridLayoutManager(getContext(), 4));

        /* - set up Inactive Recycler - */
        mRecyclerInactive.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mInactiveAdapter = new TabAdapter<TabAdapter.ViewHolder>(mTabManager.mInactiveDataSet) {
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
        mInactiveAdapter.setOnClickItemListener(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                SubTab tab = mInactiveAdapter.removeItem(position);
                mActiveAdapter.addItem(tab);
                if (mTabPickingListener != null) {
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
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params1.setMargins(0, 0, dp8 / 8, 0);
        bubble.setLayoutParams(params1);

        bubble.setTextColor(0XFFFFFFFF);
        bubble.setPadding(dp6 / 2, 0, dp6 / 2, 0);
        bubble.setVisibility(GONE);
        bubble.setTag("mViewBubble");
        bubble.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_bubble));
        bubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        bubble.setGravity(Gravity.CENTER);

        /*delete image view*/
        ImageView delView = new ImageView(getContext());
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        delView.setPadding(10, 10, 16, 16);
        delView.setLayoutParams(params2);
        delView.setImageResource(R.mipmap.ic_unsubscribe);
        delView.setTag("mViewDel");
        delView.setVisibility(GONE);

        /*content text view*/
        TextView view = new TextView(getContext());
        view.setTag("mViewTab");
        view.setActivated(true);
        RelativeLayout.LayoutParams mTextParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        mTextParams.setMargins(dp6, dp6, dp6, dp6);
        view.setLayoutParams(mTextParams);
        view.setText("软件");
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        view.setTextColor(new ColorStateList(new int[][]{
                        new int[]{-android.R.attr.state_activated},
                        new int[]{}
                }, new int[]{
                        0XFF24CF5F, 0XFF6A6A6A})
        );
        view.setGravity(Gravity.CENTER);
        int dp4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getContext().getResources().getDisplayMetrics());
        view.setPadding(0, dp4, 0, dp4);
        view.setClickable(true);
        view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_dynamic_tab));

        layout.addView(view);
        layout.addView(bubble);
        layout.addView(delView);

        return layout;
    }

    public void setTabPickerManager(TabPickerDataManager manager) {
        if (manager == null) return;
        mTabManager = manager;
        initRecyclerView();
    }

    public TabPickerDataManager getTabPickerManager() {
        return mTabManager;
    }

    public void setOnTabPickingListener(OnTabPickingListener l) {
        mTabPickingListener = l;
    }

    public boolean onTurnBack() {
        if (mActiveAdapter.isEditMode()) {
            mActiveAdapter.cancelEditMode();
            return true;
        }
        if (getVisibility() == VISIBLE) {
            hide();
            return true;
        }
        return false;
    }

    /**
     * Class TabAdapter
     *
     * @param <VH>
     */
    private abstract class TabAdapter<VH extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<VH> {

        private View.OnClickListener mClickDeleteListener;
        private View.OnClickListener mClickTabItemListener;
        private View.OnTouchListener mTouchTabItemListener;

        private Action1<Integer> mDeleteItemAction;
        private Action1<Integer> mSelectItemAction;
        Action1<ViewHolder> mBindViewObserver;

        private boolean isEditMode = false;
        List<SubTab> items;

        TabAdapter(List<SubTab> items) {
            this.items = items;
        }

        SubTab removeItem(int position) {
            SubTab b = items.remove(position);
            notifyItemRemoved(position);
            return b;
        }

        void addItem(SubTab bean) {
            items.add(bean);
            notifyItemInserted(items.size() - 1);
        }

        void addItem(SubTab bean, int index) {
            items.add(index, bean);
            notifyItemInserted(index);
        }

        SubTab getItem(int position) {
            return items.get(position);
        }

        void startEditMode() {
            mViewOperator.setText("拖动排序");
            mViewDone.setText("完成");
            mLayoutWrapper.setVisibility(GONE);
            mRecyclerActive.getHeight();
            float nh = mRecyclerActive.getHeight() + TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRecyclerActive.getLayoutParams();
            params.height = (int) nh;
            mRecyclerActive.setLayoutParams(params);
            isEditMode = true;
            notifyDataSetChanged();
        }

        void cancelEditMode() {
            mViewOperator.setText("切换栏目");
            mViewDone.setText("排序删除");
            mLayoutWrapper.setVisibility(VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRecyclerActive.getLayoutParams();
            params.height = LayoutParams.WRAP_CONTENT;
            mRecyclerActive.setLayoutParams(params);
            isEditMode = false;
            notifyDataSetChanged();
        }

        boolean isEditMode() {
            return isEditMode;
        }

        void registerBindViewObserver(Action1<ViewHolder> l) {
            this.mBindViewObserver = l;
        }

        void unRegisterBindViewObserver() {
            this.mBindViewObserver = null;
        }

        OnClickListener getClickTabItemListener() {
            if (mClickTabItemListener == null) {
                mClickTabItemListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHolder holder = (ViewHolder) v.getTag();
                        if (holder == null) return;
                        if (mSelectItemAction != null) {
                            mSelectItemAction.call(holder.getAdapterPosition());
                        }
                    }
                };
            }
            return mClickTabItemListener;
        }

        OnClickListener getDeleteItemListener() {
            if (mClickDeleteListener == null) {
                mClickDeleteListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHolder holder = (ViewHolder) v.getTag();
                        if (holder == null) return;
                        if (mDeleteItemAction != null) {
                            mDeleteItemAction.call(holder.getAdapterPosition());
                        }
                    }
                };
            }
            return mClickDeleteListener;
        }

        OnTouchListener getTouchTabItemListener() {
            if (mTouchTabItemListener == null) {
                mTouchTabItemListener = new OnTabTouchListener();
            }
            return mTouchTabItemListener;
        }

        void setOnClickItemListener(Action1<Integer> l) {
            this.mSelectItemAction = l;
        }

        void setOnDeleteItemListener(Action1<Integer> l) {
            this.mDeleteItemAction = l;
        }

        TabItemTouchHelperCallback newItemTouchHelperCallback() {
            return new TabItemTouchHelperCallback();
        }

        /**
         * Tab View Holder
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            TextView mViewTab;
            TextView mViewBubble;
            ImageView mViewDel;

            ViewHolder(View view) {
                super(view);
                mViewTab = (TextView) view.findViewWithTag("mViewTab");
                mViewBubble = (TextView) view.findViewWithTag("mViewBubble");
                mViewDel = (ImageView) view.findViewWithTag("mViewDel");
                mViewTab.setTag(this);
                mViewDel.setTag(this);
                mViewDel.setOnClickListener(getDeleteItemListener());
                mViewTab.setOnClickListener(getClickTabItemListener());
                mViewTab.setOnTouchListener(getTouchTabItemListener());
            }
        }

        /**
         * Inner Tab Touch Listener
         */
        private class OnTabTouchListener implements OnTouchListener {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewHolder holder = (ViewHolder) v.getTag();
                if (holder == null) return false;
                if (isEditMode() && MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mItemTouchHelper.startDrag(holder);
                    return true;
                }
                return false;
            }
        }

        class TabItemTouchHelperCallback extends ItemTouchHelper.Callback {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = 0;
                int position = viewHolder.getAdapterPosition();
                if (!items.get(position).isFixed()) {
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
                if (fromTargetIndex == toTargetIndex) return true;
                if (items.get(toTargetIndex).isFixed()) return true;

                if (fromTargetIndex < toTargetIndex) {
                    for (int i = fromTargetIndex; i < toTargetIndex; i++) {
                        Collections.swap(items, i, i + 1);
                    }
                } else {
                    for (int i = fromTargetIndex; i > toTargetIndex; i--) {
                        Collections.swap(items, i, i - 1);
                    }
                }

                if (mSelectedIndex == fromTargetIndex) {
                    mSelectedIndex = toTargetIndex;
                } else if (mSelectedIndex == toTargetIndex) {
                    mSelectedIndex = fromTargetIndex;
                } else if (toTargetIndex < mSelectedIndex && mSelectedIndex < fromTargetIndex) {
                    ++mSelectedIndex;
                }

                notifyItemMoved(fromTargetIndex, toTargetIndex);
                if (mTabPickingListener != null) {
                    mTabPickingListener.onMove(fromTargetIndex, items.get(fromTargetIndex),
                            toTargetIndex, items.get(toTargetIndex));
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // pass
            }

            @Override
            public void onSelectedChanged(final RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder == null) return;
                ((ViewHolder) viewHolder).mViewTab.setSelected(true);
                if (isEditMode()) return;
                final int position = viewHolder.getAdapterPosition();

                // onBindViewHolder之后，ViewHolder.itemView.getParent() != RecycleView
                // 估计是在onBindViewHolder之后绑定了ViewParent的，延迟500，暂时没什么好办法
                registerBindViewObserver(new Action1<ViewHolder>() {
                    @Override
                    public void call(final ViewHolder viewHolder) {
                        int index = viewHolder.getAdapterPosition();
                        if (index != position) return;
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mItemTouchHelper.startDrag(viewHolder);
                            }
                        }, 500);
                        unRegisterBindViewObserver();
                    }
                });
                startEditMode();
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (mSelectedIndex == viewHolder.getAdapterPosition()) return;
                ((ViewHolder) viewHolder).mViewTab.setSelected(false);
            }
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

package net.oschina.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.util.ImageLoader;
import net.oschina.app.util.TLog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class RecentContactsView extends LinearLayout implements View.OnClickListener {
    private static final String CACHE_FILE = "RecentContactsCache";
    private List<Model> models;
    private RecentContactsListener listener;

    public RecentContactsView(Context context) {
        super(context);
        init();
    }

    public RecentContactsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecentContactsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        List<Author> authors = loadCache(getContext());
        if (authors.size() == 0)
            setVisibility(GONE);
        else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(R.layout.lay_recent_contacts, this, true);

            models = new ArrayList<>();
            for (Author author : authors) {
                Model model = new Model(author);
                models.add(model);
                View view = createView(inflater, model);
                addView(view, view.getLayoutParams());
                refreshView(model);
            }
        }
    }

    private View createView(LayoutInflater inflater, Model model) {
        View view = inflater.inflate(R.layout.activity_item_select_friend, this, false);
        // 双向绑定
        model.tag = view;
        view.setTag(model);
        return view;
    }

    private void refreshView(Model model) {
        View root = model.tag;
        final Author author = model.author;

        CircleImageView portrait = (CircleImageView) root.findViewById(R.id.iv_portrait);
        TextView name = (TextView) root.findViewById(R.id.tv_name);
        ImageView isSelected = (ImageView) root.findViewById(R.id.iv_select);
        View line = root.findViewById(R.id.line);

        ImageLoader.loadImage(Glide.with(getContext()), portrait, author.getPortrait(), R.mipmap.widget_default_face);

        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherUserHomeActivity.show(v.getContext(), author.getId());
            }
        });
        name.setText(author.getName());

        if (model.isSelected) {
            isSelected.setVisibility(View.VISIBLE);
        } else {
            isSelected.setVisibility(View.INVISIBLE);
        }
        line.setVisibility(View.GONE);


        TLog.error("refreshView:" + model.toString());
    }

    public boolean hasData() {
        return models != null && models.size() > 0;
    }

    public void setSelected(Author author, boolean selected) {
        if (!hasData() || author == null)
            return;

        for (Model model : models) {
            if (model.author.getId() == author.getId()) {
                model.isSelected = selected;
                refreshView(model);
            }
        }
    }

    public void setListener(RecentContactsListener listener) {
        this.listener = listener;
    }

    public static void add(Author... authors) {
        saveCache(AppContext.getInstance(), authors);
    }


    private static LinkedList<Author> loadCache(Context context) {
        List<User> cache = CacheManager.readListJson(context, CACHE_FILE, User.class);
        LinkedList<Author> linkedList = new LinkedList<>();
        if (cache != null)
            linkedList.addAll(cache);
        return linkedList;
    }

    private static void saveCache(Context context, Author... authors) {
        final LinkedList<Author> localCache = loadCache(context);

        // 避免重复添加
        for (Author author : authors) {
            if (author != null && author.getId() > 0 && !TextUtils.isEmpty(author.getName())
                    && checkNotInList(localCache, author))
                localCache.addFirst(author);
        }

        // 至多存储15条
        while (localCache.size() > 15) {
            localCache.removeLast();
        }

        CacheManager.saveToJson(context, CACHE_FILE, localCache);
    }

    private static boolean checkNotInList(List<Author> list, Author user) {
        for (Author author : list) {
            if (author.getId() == user.getId())
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (listener == null ||
                v.getTag() == null ||
                !(v.getTag() instanceof Model))
            return;

        Model model = (Model) v.getTag();
        if (listener.onSelectChanged(model.author)) {
            model.isSelected = !model.isSelected;
            refreshView(model);
        }
    }

    private static class Model {
        Model(Author author) {
            this.author = author;
        }

        Author author;
        boolean isSelected = false;
        View tag;

        @Override
        public String toString() {
            return "Model{" +
                    "author=" + author +
                    ", isSelected=" + isSelected +
                    ", tag=" + tag +
                    '}';
        }
    }


    public interface RecentContactsListener {
        boolean onSelectChanged(Author author);
    }
}

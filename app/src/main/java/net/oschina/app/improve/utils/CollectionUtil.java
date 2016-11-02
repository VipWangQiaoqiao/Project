package net.oschina.app.improve.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JuQiu
 * on 16/7/29.
 */

public class CollectionUtil {

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0)
            return null;
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Set<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0)
            return null;
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> HashSet<T> toHashSet(T[] items) {
        if (items == null || items.length == 0)
            return null;
        HashSet<T> set = new HashSet<>();
        Collections.addAll(set, items);
        return set;
    }

    public static <T> ArrayList<T> toArrayList(T[] items) {
        if (items == null || items.length == 0)
            return null;
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }

    /**
     * 移动一个列表中的元素位置
     * <p>
     * A B C D 四个元素，移动2坐标移动到0坐标，
     * 结果： C A B D
     *
     * @param collection   列表
     * @param fromPosition 起始位置
     * @param toPosition   目标位置
     * @param <T>          元素
     * @return 列表
     */
    public static <T> Collection<T> move(List<T> collection, int fromPosition, int toPosition) {
        int maxPosition = collection.size() - 1;
        if (fromPosition == toPosition || fromPosition > maxPosition || toPosition > maxPosition)
            return collection;

        if (fromPosition < toPosition) {
            T fromModel = collection.get(fromPosition);
            T toModel = collection.get(toPosition);

            collection.remove(fromPosition);
            collection.add(collection.indexOf(toModel) + 1, fromModel);
        } else {
            T fromModel = collection.get(fromPosition);
            collection.remove(fromPosition);
            collection.add(toPosition, fromModel);
        }

        return collection;
    }
}

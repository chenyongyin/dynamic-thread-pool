package com.threadpool.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于LinkedHashMap实现可淘汰的Lru缓存
 * @author cyy
 * @date 2021/04/12 10:41
 **/
public class LruHashMap<K,V> extends LinkedHashMap<K, V> implements Map<K, V> {
    /**
     * LRU中最大元素数量
     */
    private final int maxSize;
    private final static float LOAD_FACTOR = 0.75f;
    /**
     *
     * @author cyy
     * @date 2021/04/12 10:45
     * @param maxSize 最大容量
     */
    public LruHashMap(int maxSize) {
        super((int) Math.ceil(maxSize / 0.75) + 1, LOAD_FACTOR, true);
        this.maxSize = maxSize;
    }

    /**
     * 此方法为钩子方法，map插入元素时会调用此方法
     * 此方法返回true则证明删除最老的因子
     * @author cyy
     * @date 2021/04/12 10:45
     * @param eldest 最老元素
     * @return boolean
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;
    }
}

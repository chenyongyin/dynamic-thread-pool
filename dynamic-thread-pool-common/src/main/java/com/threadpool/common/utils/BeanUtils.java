package com.threadpool.common.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * the beanCopierMap
     */
    private static final ConcurrentMap<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();

    /**
     * @description 两个类对象之间转换
     * @param source
     * @param target
     * @return
     * @return T
     */
    public static <T> T convert(Object source, Class<T> target) {
        T ret = null;
        if (source != null) {
            try {
                ret = target.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("create class[" + target.getName()
                        + "] instance error", e);
            }
            BeanCopier beanCopier = getBeanCopier(source.getClass(), target);
            beanCopier.copy(source, ret, new DeepCopyConverter(target));
        }
        return ret;
    }



    public static <S, T> List<T> convertListTo(List<S> sources, Supplier<T> targetSupplier) {
        return convertListTo(sources, targetSupplier, null);
    }

    /**
     * 转换对象
     *
     * @param sources        源对象list
     * @param targetSupplier 目标对象供应方
     * @param callBack       回调方法
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     * @return 目标对象list
     */
    public static <S, T> List<T> convertListTo(List<S> sources, Supplier<T> targetSupplier, ConvertCallBack<S, T> callBack) {
        if (null == sources || null == targetSupplier) {
            return null;
        }

        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T target = targetSupplier.get();
            copyProperties(source, target);
            if (callBack != null) {
                callBack.callBack(source, target);
            }
            list.add(target);
        }
        return list;
    }

    /**
     * 回调接口
     *
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     */
    @FunctionalInterface
    public interface ConvertCallBack<S, T> {
        void callBack(S t, T s);
    }




    public static class DeepCopyConverter implements Converter {

        /**
         * The Target.
         */
        private Class<?> target;

        /**
         * Instantiates a new Deep copy converter.
         *
         * @param target
         *            the target
         */
        public DeepCopyConverter(Class<?> target) {
            this.target = target;
        }

        @Override
        public Object convert(Object value, Class targetClazz, Object methodName) {
            if (value instanceof List) {
                List values = (List) value;
                List retList = new ArrayList<>(values.size());
                for (final Object source : values) {
                    String tempFieldName = methodName.toString().replace("set",
                            "");
                    String fieldName = tempFieldName.substring(0, 1)
                            .toLowerCase() + tempFieldName.substring(1);
                    Class clazz = ClassUtils.getElementType(target, fieldName);
                    retList.add(BeanUtils.convert(source, clazz));
                }
                return retList;
            } else if (value instanceof Map) {
                // TODO 暂时用不到，后续有需要再补充
            } else if (!ClassUtils.isPrimitive(targetClazz)) {
                return BeanUtils.convert(value, targetClazz);
            }
            return value;
        }
    }

    /**
     * @description 获取BeanCopier
     * @param source
     * @param target
     * @return
     * @return BeanCopier
     */
    public static BeanCopier getBeanCopier(Class<?> source, Class<?> target) {
        String beanCopierKey = generateBeanKey(source, target);
        if (beanCopierMap.containsKey(beanCopierKey)) {
            return beanCopierMap.get(beanCopierKey);
        } else {
            BeanCopier beanCopier = BeanCopier.create(source, target, true);
            beanCopierMap.putIfAbsent(beanCopierKey, beanCopier);
        }
        return beanCopierMap.get(beanCopierKey);
    }

    /**
     * @description 生成两个类的key
     * @param source
     * @param target
     * @return
     * @return String
     */
    public static String generateBeanKey(Class<?> source, Class<?> target) {
        return source.getName() + "@" + target.getName();
    }
    /**
     * 对象复制(忽略值为null的属性)
     * @author cyy
     * @date 2021/03/16 17:58
     * @param source
     * @param target
     * @return void
     */
    public static void copyIgnoreNullProperties(Object source, Object target){
        copyProperties(source,target,getNullPropertyNames(source));
    }
    /**
     * 获取对象中值为null的属性名
     * @author cyy
     * @date 2021/03/16 17:59
     * @param source
     * @return java.lang.String[]
     */
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if(srcValue instanceof String){
                if(StringUtils.isEmpty(String.valueOf(srcValue))){
                    emptyNames.add(pd.getName());
                }
                continue;
            }
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 比较两个实体属性值，返回一个boolean,true则表时两个对象中的属性值有差异
     * @author cyy
     * @date 2021/03/30 13:58
     * @param oldObject 进行属性比较的对象1
     * @param newObject 进行属性比较的对象2
     * @return boolean 
     */
    public static boolean compareObject(Object oldObject, Object newObject) {
        Map<String, Map<String,Object>> resultMap=compareFields(oldObject,newObject,Boolean.FALSE);
        return resultMap.size() > 0;
    }
    /**
     * 忽略新对象空属性之间的对比
     * @author cyy
     * @date 2021/05/13 15:04
     * @param oldObject 进行属性比较的对象1
     * @param newObject 进行属性比较的对象2
     * @return boolean
     */
    public static boolean compareIgnoreNullObject(Object oldObject, Object newObject) {
        Map<String, Map<String,Object>> resultMap=compareFields(oldObject,newObject,Boolean.TRUE);
        return resultMap.size() > 0;
    }

    /**
     * 比较两个实体属性值，返回一个map以有差异的属性名为key，value为一个Map分别存oldObject,newObject此属性名的值
     * @author cyy
     * @date 2021/03/30 14:06
     * @param oldObject 进行属性比较的对象1
     * @param newObject 进行属性比较的对象2
     * @return java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Object>> 
     */
    public static Map<String, Map<String,Object>> compareFields(Object oldObject, Object newObject,boolean ignoreNull) {
        Map<String, Map<String, Object>> map = null;
        try{
            /**
             * 只有两个对象都是同一类型的才有可比性
             */
            if (oldObject.getClass() == newObject.getClass()) {
                Class clazz = oldObject.getClass();
                //获取object的所有属性
                PropertyDescriptor[] pds = Introspector.getBeanInfo(clazz,Object.class).getPropertyDescriptors();
                map = new HashMap<>((int) (pds.length/0.75+1));
                for (PropertyDescriptor pd : pds) {
                    //遍历获取属性名
                    String name = pd.getName();
                    //获取属性的get方法
                    Method readMethod = pd.getReadMethod();
                    // 在oldObject上调用get方法等同于获得oldObject的属性值
                    Object oldValue = readMethod.invoke(oldObject);
                    // 在newObject上调用get方法等同于获得newObject的属性值
                    Object newValue = readMethod.invoke(newObject);
                    if(oldValue == null){
                        continue;
                    }
                    if(ignoreNull && newValue == null){
                        continue;
                    }
                    if(oldValue instanceof List){
                        continue;
                    }
                    if(newValue instanceof List){
                        continue;
                    }
                    if(oldValue instanceof Timestamp){
                        oldValue = new Date(((Timestamp) oldValue).getTime());
                    }
                    if(newValue instanceof Timestamp){
                        newValue = new Date(((Timestamp) newValue).getTime());
                    }
                    // 比较这两个值是否相等,不等就可以放入map了
                    if (!oldValue.equals(newValue)) {
                        Map<String,Object> valueMap = new HashMap<>(4);
                        valueMap.put("oldValue",oldValue);
                        valueMap.put("newValue",newValue);
                        map.put(name, valueMap);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return map;
    }
}

package it.bluebird.bluebirdlib.utils;

import it.bluebird.bluebirdlib.utils.annotations.AutoSerialize;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NbtUtils {
    public static CompoundTag writeVector3f(Vector3f vec) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", vec.x());
        tag.putFloat("y", vec.y());
        tag.putFloat("z", vec.z());
        return tag;
    }

    public static Vector3f readVector3f(CompoundTag tag) {
        return new Vector3f(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
    }

    public static CompoundTag serialize(Object instance) {
        CompoundTag tag = new CompoundTag();
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(AutoSerialize.class)) {
                AutoSerialize annotation = field.getAnnotation(AutoSerialize.class);
                String key = annotation.value().isEmpty() ? field.getName() : annotation.value();

                try {
                    Object value = field.get(instance);
                    if (value instanceof Integer) {
                        tag.putInt(key, (int) value);
                    } else if (value instanceof Boolean) {
                        tag.putBoolean(key, (boolean) value);
                    } else if (value instanceof String) {
                        tag.putString(key, (String) value);
                    } else if (value instanceof List) {
                        ListTag listTag = new ListTag();
                        for (Object obj : (List<?>) value) {
                            if (obj instanceof INBTSerializable) {
                                listTag.add(((INBTSerializable<?>) obj).serializeNBT());
                            } else if (obj instanceof Enum) {
                                listTag.add(StringTag.valueOf(((Enum<?>) obj).name()));
                            }
                        }
                        tag.put(key, listTag);
                    } else if (value instanceof Map) {
                        CompoundTag mapTag = new CompoundTag();
                        Map<?, ?> map = (Map<?, ?>) value;
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            String mapKey = entry.getKey().toString();
                            Object mapValue = entry.getValue();
                            if (mapValue instanceof INBTSerializable) {
                                mapTag.put(mapKey, ((INBTSerializable<?>) mapValue).serializeNBT());
                            } else if (mapValue instanceof Enum) {
                                mapTag.put(mapKey, StringTag.valueOf(((Enum<?>) mapValue).name()));
                            }
                        }
                        tag.put(key, mapTag);
                    } else if (value instanceof Enum) {
                        tag.putString(key, ((Enum<?>) value).name());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return tag;
    }

    public static void deserialize(Object instance, CompoundTag tag) {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(AutoSerialize.class)) {
                AutoSerialize annotation = field.getAnnotation(AutoSerialize.class);
                String key = annotation.value().isEmpty() ? field.getName() : annotation.value();

                try {
                    if (tag.contains(key)) {
                        Class<?> type = field.getType();
                        if (type == int.class || type == Integer.class) {
                            field.set(instance, tag.getInt(key));
                        } else if (type == boolean.class || type == Boolean.class) {
                            field.set(instance, tag.getBoolean(key));
                        } else if (type == String.class) {
                            field.set(instance, tag.getString(key));
                        } else if (List.class.isAssignableFrom(type)) {
                            ListTag listTag = tag.getList(key, 10);
                            List<Object> list = new ArrayList<>();
                            Class<?> componentType = getGenericComponentType(field);
                            for (Tag itemTag : listTag) {
                                if (itemTag instanceof StringTag) {
                                    String name = ((StringTag) itemTag).getAsString();
                                    if (componentType != null && componentType.isEnum()) {
                                        list.add(Enum.valueOf((Class<Enum>) componentType, name));
                                    }
                                } else if (itemTag instanceof CompoundTag) {
                                    Object item = createInstance(componentType);
                                    if (item instanceof INBTSerializable<?>) {
                                        ((INBTSerializable<CompoundTag>) item).deserializeNBT((CompoundTag) itemTag);
                                        list.add(item);
                                    }
                                }
                            }
                            field.set(instance, list);
                        } else if (Map.class.isAssignableFrom(type)) {
                            CompoundTag mapTag = tag.getCompound(key);
                            Map<Object, Object> map = new HashMap<>();
                            Class<?> keyType = getGenericMapKeyType(field);
                            Class<?> valueType = getGenericMapValueType(field);
                            for (String mapKey : mapTag.getAllKeys()) {
                                Tag mapValueTag = mapTag.get(mapKey);
                                if (mapValueTag instanceof StringTag) {
                                    String name = ((StringTag) mapValueTag).getAsString();
                                    if (keyType != null && keyType.isEnum()) {
                                        map.put(mapKey, Enum.valueOf((Class<Enum>) keyType, name));
                                    }
                                } else if (mapValueTag instanceof CompoundTag) {
                                    Object value = createInstance(valueType);
                                    if (value instanceof INBTSerializable<?>) {
                                        ((INBTSerializable<CompoundTag>) value).deserializeNBT((CompoundTag) mapValueTag);
                                        map.put(mapKey, value);
                                    }
                                }
                            }
                            field.set(instance, map);
                        } else if (type.isEnum()) {
                            String name = tag.getString(key);
                            field.set(instance, Enum.valueOf((Class<Enum>) type, name));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    private static Object createInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private static Class<?> getGenericMapValueType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypeArguments = paramType.getActualTypeArguments();
            if (actualTypeArguments.length > 1) {
                Type typeArgument = actualTypeArguments[1];
                if (typeArgument instanceof Class<?>) {
                    return (Class<?>) typeArgument;
                }
            }
        }
        return null;
    }

    private static Class<?> getGenericComponentType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType paramType) {
            Type[] actualTypeArguments = paramType.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Type typeArgument = actualTypeArguments[0];
                if (typeArgument instanceof Class<?>) {
                    return (Class<?>) typeArgument;
                }
            }
        }
        return null;
    }

    private static Class<?> getGenericMapKeyType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType paramType) {
            Type[] actualTypeArguments = paramType.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Type typeArgument = actualTypeArguments[0];
                if (typeArgument instanceof Class<?>) {
                    return (Class<?>) typeArgument;
                }
            }
        }
        return null;
    }

}

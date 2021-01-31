import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import javax.json.*;

@SuppressWarnings("rawtypes")
public class Serializer {

    public static JsonObject serializeObject(Object object) throws Exception {
        JsonArrayBuilder object_list = Json.createArrayBuilder();
        IdentityHashMap map = new IdentityHashMap();

        serializeHelper(object, object_list, map);

        JsonObjectBuilder json_base_object = Json.createObjectBuilder();
        json_base_object.add("objects", object_list);

        return json_base_object.build();
    }

    private static void serializeHelper(Object sourceObj, JsonArrayBuilder object_list, IdentityHashMap<Object, String> object_tracking_map) throws Exception {

        String object_id = Integer.toString(object_tracking_map.size());
        object_tracking_map.put(sourceObj, object_id);

        try {
            Class object_class = sourceObj.getClass();
            if (object_class != null) {
                System.out.println(object_class.getName());
                JsonObjectBuilder object_info = Json.createObjectBuilder();
                object_info.add("class", object_class.getName());
                object_info.add("id", object_id);
                System.out.println("obj is primitive " + object_class.isPrimitive());
                if (object_class.isPrimitive()) {
                    object_info.add("type", "object");
                    // add list of fields to our object list
                    object_info.add("fields", serializeFields(sourceObj, object_list, object_tracking_map, object_class));

                } else if (object_class.isArray()) {
                    object_info.add("type", "array");
                    object_info.add("length", Integer.toString(Array.getLength(sourceObj)));
                    object_info.add("entries", serializeArray(sourceObj, object_list, object_tracking_map));
                } else {
                    object_info.add("type", "object");
                    // could need a serialize ArrayList option
                    object_info.add("fields", serializeFields(sourceObj, object_list, object_tracking_map, object_class));
                }
                object_list.add(object_info);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    private static JsonArrayBuilder serializeArray(Object array, JsonArrayBuilder
            object_list, IdentityHashMap<Object, String> object_tracking_map) throws Exception {
        JsonArrayBuilder array_list = Json.createArrayBuilder();
        JsonObjectBuilder array_entry = Json.createObjectBuilder();
        int length = Array.getLength(array);
        Class componentType = array.getClass().getComponentType();

        for (int i = 0; i < length; i++) {
            Object arrayObject = Array.get(array, i);
            if (componentType.isPrimitive()) {
                array_entry.add("value", arrayObject.toString());
            } else {
                if (object_tracking_map.containsKey(arrayObject)) {
                    array_entry.add("reference", object_tracking_map.get((arrayObject)));
                } else {
                    array_entry.add("reference", Integer.toString(object_tracking_map.size()));
                    serializeHelper(arrayObject, object_list, object_tracking_map);
                }
            }

            array_list.add(array_entry);
        }

        return array_list;
    }


    private static JsonArrayBuilder serializeFields(Object sourceObj, JsonArrayBuilder
            object_list, IdentityHashMap<Object, String> object_tracking_map, Class objClass) throws Exception {


        JsonArrayBuilder field_list = Json.createArrayBuilder();
        for (Field f : objClass.getDeclaredFields()) {
            JsonObjectBuilder json_fields = Json.createObjectBuilder();
            if (!Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                json_fields.add("name", f.getName());
                json_fields.add("declaringclass", f.getDeclaringClass().getName());

                Class fieldType = f.getType();
                Object fieldObject = null;
                try {
                    fieldObject = f.get(sourceObj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (fieldType.isPrimitive()) {
                    assert fieldObject != null;
                    json_fields.add("value", fieldObject.toString());
                } else {
                    if (object_tracking_map.containsKey(fieldObject)) {
                        json_fields.add("reference", object_tracking_map.get(fieldObject));
                    } else {
                        json_fields.add("reference", Integer.toString(object_tracking_map.size()));
                        serializeHelper(fieldObject, object_list, object_tracking_map);
                    }
                }
            }

            field_list.add(json_fields);
        }
        return field_list;

    }

}

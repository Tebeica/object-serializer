import java.lang.reflect.*;
import javax.json.*;
import java.util.*;

public class Deserializer {

    public static Object deserialize(JsonObject document) {
        //get root element and list of nested object elements
        JsonArray objList = document.getJsonArray("objects");
        HashMap objMap = new HashMap();
        //Object to be instantiated via deserialization
        Object obj = null;

        try {
            createObjectInstances(objList, objMap);
            setFieldValues(objList, objMap);
            obj = objMap.get("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static Object deserializeFieldValue(Class fieldType, JsonObject valueElement) {

        Object valueObject = null;

        if (fieldType.equals(int.class))
            valueObject = Integer.valueOf(valueElement.getString("value"));
        else if (fieldType.equals(byte.class))
            valueObject = Byte.valueOf(valueElement.getString("value"));
        else if (fieldType.equals(short.class))
            valueObject = Short.valueOf(valueElement.getString("value"));
        else if (fieldType.equals(long.class))
            valueObject = Long.valueOf(valueElement.getString("value"));
        else if (fieldType.equals(float.class))
            valueObject = Float.valueOf(valueElement.getString("value"));
        else if (fieldType.equals(double.class))
            valueObject = Double.valueOf(valueElement.getString("value"));
        else if (fieldType.equals(boolean.class)) {

            String boolString = valueElement.getString("value");

            if (boolString.equals("true"))
                valueObject = Boolean.TRUE;
            else
                valueObject = Boolean.FALSE;
        }

        return valueObject;
    }


    private static Object deserializeContentElement(Class classType, JsonObject contentElement, HashMap objMap) {
        Object contentObject;

        if (contentElement.containsKey("reference")) {
            contentObject = objMap.get(contentElement.getString("reference"));
        } else //if (contentElement.containsKey("value")) {
            contentObject = deserializeFieldValue(classType, contentElement);
//        } else
//            contentObject = contentElement.get;


        return contentObject;
    }

    private static void createObjectInstances(List objList, HashMap objMap) {
        for (Object o : objList) {
            try {
                JsonObject objElement = (JsonObject) o;

                //create uninitialized instance using element attribute
                Class objClass = Class.forName(objElement.getString("class"));
                //System.out.println(objClass.getName());

                //check for class type then create new instance
                Object objInstance;
                if (objClass.isArray()) {
                    //get length (via element attributes) and component type of array object instantiation
                    int arrayLength = Integer.parseInt(objElement.getString("length"));
                    Class arrayType = objClass.getComponentType();
                    objInstance = Array.newInstance(arrayType, arrayLength);

                } else {
                    //non-array object, instantiate with no arg constructor
                    Constructor constructor = objClass.getConstructor(null);
                    //check constructor modifiers, just in case
                    if (!Modifier.isPublic(constructor.getModifiers())) {
                        constructor.setAccessible(true);
                    }
                    objInstance = constructor.newInstance(null);
                }
                //associate the new instance with the object's unique id
                String objId = objElement.getString("id");
                objMap.put(objId, objInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void setFieldValues(List objList, HashMap objMap) {
        for (int i = 0; i < objList.size(); i++) {
            try {
                JsonObject objElement = (JsonObject) objList.get(i);

                Object objInstance = objMap.get(objElement.getString("id"));
                //get list of all children of object element (fields if non-array, elements if array)
                JsonArray objChildrenList;

                // if array object, set value of each element
                // if non-array object, assign values to all fields/instance variables
                Class objClass = objInstance.getClass();
                System.out.println(objClass.getName());
                if (objClass.isArray()) {
                    objChildrenList = objElement.getJsonArray("entries");
                    //set values for each array element
                    Class arrayType = objClass.getComponentType();
                    for (int j = 0; j < objChildrenList.size(); j++) {
                        JsonObject arrayContentElement = (JsonObject) objChildrenList.get(j);
                        Object arrayContent = deserializeContentElement(arrayType, arrayContentElement, objMap);
                        Array.set(objInstance, j, arrayContent);
                    }
                } else {
                    objChildrenList = objElement.getJsonArray("fields");
                    //non-array object, assign values to all fields
                    for (int j = 0; j < objChildrenList.size(); j++) {
                        JsonObject fieldElement = (JsonObject) objChildrenList.get(j);

                        //get declaring class and field name (via field attributes) and load Field metaobject dynamically
                        Class declaringClass = Class.forName(fieldElement.getString("declaringclass"));
                        String fieldName = fieldElement.getString("name");
                        Field field = declaringClass.getDeclaredField(fieldName);

                        if (!Modifier.isPublic(field.getModifiers())) {
                            field.setAccessible(true);

                            //in case field also has final modifier, reset modifiers field with bitwise negation
                            Field modifiersField = Field.class.getDeclaredField("modifiers");
                            modifiersField.setAccessible(true);
                            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        }
                        //check field element content for value/reference and set accordingly
                        Class fieldType = field.getType();
                        JsonObject fieldContentElement = (JsonObject) objChildrenList.get(j);
                        Object fieldContent = deserializeContentElement(fieldType, fieldContentElement, objMap);
                        field.set(objInstance, fieldContent);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //end of loop, object list should be deserialized and instantiated
    }
}

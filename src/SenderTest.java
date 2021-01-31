import org.junit.Before;
import org.junit.Test;

import javax.json.*;

import static org.junit.Assert.*;

import java.lang.reflect.*;
import java.io.*;

public class SenderTest {
    Class senderClass;

    @Before
    public void createSenderClass() {
        try {
            senderClass = Class.forName("Sender");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSimpleObjectJson() {
        try {
            Method m = senderClass.getDeclaredMethod("createJsonFile", new Class[]{Json.class});
            m.setAccessible(true);

            SimpleObject obj = new SimpleObject(1, 2);
            JsonObject doc = Serializer.serializeObject(obj);
            assertNotNull(m.invoke(null, new Object[]{doc}));
            File file = (File) m.invoke(null, new Object[]{doc});
            assertNotNull(file);
            assertTrue(file.length() > 0);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            while (bufferedReader.ready()) {
                stringBuffer.append((char) bufferedReader.read());
            }

            String result = stringBuffer.toString();
            String expectedResult = "{\"objects\":[{\"class\":\"SimpleObject\",\"id\":\"0\",\"type\":\"object\",\"fields\":[{\"name\":\"fieldInt\",\"declaringclass\":\"SimpleObject\",\"value\":\"1\"},{\"name\":\"fieldDouble\",\"declaringclass\":\"SimpleObject\",\"value\":\"2.0\"}]}]}";

            assertEquals(expectedResult, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReferenceObjectJson() {
        try {
            Method m = senderClass.getDeclaredMethod("createJsonFile", new Class[]{Json.class});
            m.setAccessible(true);

            ReferenceObject obj = new ReferenceObject(new SimpleObject(5,7));
            JsonObject doc = Serializer.serializeObject(obj);
            assertNotNull(m.invoke(null, new Object[]{doc}));
            File file = (File) m.invoke(null, new Object[]{doc});
            assertNotNull(file);
            assertTrue(file.length() > 0);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            while (bufferedReader.ready()) {
                stringBuffer.append((char) bufferedReader.read());
            }

            String result = stringBuffer.toString();
            String expectedResult = "{\"objects\":[{\"class\":\"SimpleObject\",\"id\":\"1\",\"type\":\"object\",\"fields\":[{\"name\":\"fieldInt\",\"declaringclass\":\"SimpleObject\",\"value\":\"5\"},{\"name\":\"fieldDouble\",\"declaringclass\":\"SimpleObject\",\"value\":\"7.0\"}]},{\"class\":\"ReferenceObject\",\"id\":\"0\",\"type\":\"object\",\"fields\":[{\"name\":\"fieldObj\",\"declaringclass\":\"ReferenceObject\",\"reference\":\"1\"}]}]}";
            assertEquals(expectedResult, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleArrayObject() {
        try {
            Method m = senderClass.getDeclaredMethod("createJsonFile", new Class[]{Json.class});
            m.setAccessible(true);

            int[] testArray = new int[]{1,2,3,4};
            SimpleArrayObject obj = new SimpleArrayObject(testArray);
            JsonObject doc = Serializer.serializeObject(obj);
            assertNotNull(m.invoke(null, new Object[]{doc}));
            File file = (File) m.invoke(null, new Object[]{doc});
            assertNotNull(file);
            assertTrue(file.length() > 0);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();

            while (bufferedReader.ready()) {
                stringBuffer.append((char) bufferedReader.read());
            }

            String result = stringBuffer.toString();
            String expectedResult = "{\"objects\":[{\"class\":\"[I\",\"id\":\"1\",\"type\":\"array\",\"length\":\"4\",\"entries\":[{\"value\":\"1\"},{\"value\":\"2\"},{\"value\":\"3\"},{\"value\":\"4\"}]},{\"class\":\"SimpleArrayObject\",\"id\":\"0\",\"type\":\"object\",\"fields\":[{\"name\":\"fieldIntArray\",\"declaringclass\":\"SimpleArrayObject\",\"reference\":\"1\"}]}]}";
            assertEquals(expectedResult, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

import java.util.ArrayList;
import java.util.Scanner;

public class ObjectBuilder {

    static SimpleObject createSimpleObject() {
        System.out.println("SimpleObject(int paramInt, double paramDouble)");
        SimpleObject simpleObj = null;
        try {
            Scanner input = new Scanner(System.in);

            //prompt user to set paramInt
            System.out.print("Enter an integer: ");
            while (!input.hasNextInt()) {
                input.next();
                System.out.println("Enter a valid integer for paramInt:");
            }
            int paramInt = input.nextInt();

            //prompt user to set paramDouble
            System.out.print("Enter a double: ");
            while (!input.hasNextDouble()) {
                input.next();
                System.out.println("Enter a valid double");
            }
            double paramDouble = input.nextDouble();

            simpleObj = new SimpleObject(paramInt, paramDouble);
            System.out.println("SimpleObject created!");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return simpleObj;

    }

    static ReferenceObject createReferenceObject() {
        System.out.println("ReferenceObject(SimpleObject simpleObj)");

        //create simpleObj to pass to referenceObj
        SimpleObject simpleObj = createSimpleObject();
        ReferenceObject referenceObj = new ReferenceObject(simpleObj);
        System.out.println("ReferenceObject created!");

        return referenceObj;

    }

    static int getInt() {
        Scanner scan = new Scanner(System.in);
        int i = 0;
        while (true) {
            System.out.print("Int value: ");
            String option = scan.nextLine();
            if (option.equals("")) break;
            try {
                i = Integer.parseInt(option);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid int!");
            }
        }
        return i;
    }

    static CircularReference createCircularReference() {
        System.out.println("\nTo create two objects with circular reference to one another, please specify the id of the first object");
        CircularReference obj1 = new CircularReference();
        obj1.int_id = getInt();

        System.out.println("Now, specify the id of the second object");
        CircularReference obj2 = new CircularReference();
        obj2.int_id = getInt();

        obj1.partner = obj2;
        obj2.partner = obj1;

        return obj1;
    }

    static SimpleArrayObject createSimpleArrayObject() {
        System.out.println("SimpleArrayObject(int[] paramIntArray)");

        Scanner input = new Scanner(System.in);

        //prompt user for size of array
        System.out.println("Enter array length for paramIntArray:");
        while (!input.hasNextInt()) {
            input.next();
            System.out.print("Enter a valid integer: ");
        }
        int arrayLength = input.nextInt();
        int[] paramIntArray = new int[arrayLength];

        //prompt users to set values for each index
        for (int i = 0; i < arrayLength; i++) {
            System.out.printf("Enter an integer for index %d:\n", i);
            while (!input.hasNextInt()) {
                input.next();
                System.out.print("Enter a valid integer: ");
            }
            paramIntArray[i] = input.nextInt();
        }

        SimpleArrayObject simpleArrayObj = new SimpleArrayObject(paramIntArray);
        System.out.println("SimpleArrayObject created!");
        return simpleArrayObj;

    }

    static ReferenceArrayObject createRefArrayObject() {
        System.out.println("ReferenceArrayObject(Object[] paramObjArray)");

        Scanner input = new Scanner(System.in);

        //prompt user for size of array
        System.out.print("Enter an integer for the length of the array: ");
        while (!input.hasNextInt()) {
            input.next();
            System.out.print("Enter a valid integer: ");
        }
        int arrayLength = input.nextInt();

        Object[] paramObjArray = new Object[arrayLength];

        //instantiate array with simpleObjects
        for (int i = 0; i < arrayLength; i++) {
            System.out.printf("index %d:\n", i);
            SimpleObject simpleObj = createSimpleObject();
            paramObjArray[i] = simpleObj;
        }

        ReferenceArrayObject refArrayObj = new ReferenceArrayObject(paramObjArray);
        System.out.println("ReferenceArrayObject created!");
        return refArrayObj;

    }

    static CollectionObject createCollectionObject() {
        System.out.println("CollectionObject(ArrayList paramCollection)");

        Scanner input = new Scanner(System.in);

        CollectionObject collectionObj = null;
        ArrayList<SimpleObject> paramCollection = new ArrayList<SimpleObject>();
        //continue adding to collection until user decides to quit
        boolean quit = false;
        while (!quit) {

            System.out.println("Add an object to the collection (yes/no)?");
            String collectionChoice = input.nextLine();
            if (collectionChoice.equalsIgnoreCase("yes")) {
                SimpleObject simpleObj = createSimpleObject();
                paramCollection.add(simpleObj);
            } else if (collectionChoice.equalsIgnoreCase("no")) {
                collectionObj = new CollectionObject(paramCollection);
                quit = true;
            } else {
                System.out.println("Invalid input. Enter yes or no.");
            }
        }
        System.out.println("CollectionObject created!");
        return collectionObj;
    }
}
import java.lang.Object;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;

/*
 * CPSC 501
 * Inspector starter class
 *
 * @author Jonathan Hudson
 * @completed by Teodor Tebeica
 * @UCID 30046038
 */

public class Inspector {

    private HashSet<Integer> objectHash;

    public Inspector() {
        this.objectHash = new HashSet<Integer>();
    }

    public void inspect(Object obj, boolean recursive) throws IllegalArgumentException, IllegalAccessException {
        Class c = obj.getClass();
        HashSet<Integer> objectHash = this.getObjectHash();
        objectHash.add(obj.hashCode());
        inspectClass(c, obj, recursive, 0);
    }

    /**
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws ArrayIndexOutOfBoundsException
     */
    private Object[] populateArray(Object obj) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        int arrayLength = Array.getLength(obj);
        Object[] objArray = new Object[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            objArray[i] = (Object) Array.get(obj, i);
        }
        return objArray;
    }

    /**
     * @param depth
     * @return
     */
    private String indent(int depth) {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i <= depth; i++) {
            res.append("\t");
        }
        String s = res.toString();
        return s;
    }

    /**
     * @param recursive
     * @param depth
     * @param fType
     * @param objArray
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void handleArrayRecursive(Boolean recursive, int depth, Class fType, Object[] objArray)
            throws IllegalArgumentException, IllegalAccessException {
        for (Object entry : objArray) {
            if (fType.getComponentType().isPrimitive()) {
                System.out.println(indent(depth) + "   Value: " + entry);
            } else if (!fType.getComponentType().isPrimitive() && recursive == false) {
                if (entry == null) {
                    System.out.println(indent(depth) + "  Value: null");
                } else {
                    System.out.println(indent(depth) + "   Value (ref): " + entry + "@"
                            + Integer.toHexString(entry.hashCode()));
                }
            } else if (entry == null) {
                System.out.println(indent(depth) + "   Value: null");
            } else {
                System.out.println(indent(depth) + "   Value (ref)-> " + entry + "@"
                        + Integer.toHexString(entry.hashCode()));
                if (alreadyInspected(entry.getClass())) {
                    System.out.println("Already inspected this object...");
                } else {
                    System.out.println(indent(depth) + "     -> Recursively inspect");
                    inspectClass(entry.getClass(), entry, recursive, depth + 1);
                }

            }
        }
    }

    /**
     * @param c
     * @param depth
     * @param objArray
     */
    private void printArrayHeader(Class c, int depth, Object[] objArray) {
        System.out.println(indent(depth) + "  Component Type: " + c.getComponentType());
        System.out.println(indent(depth) + "  Length: " + objArray.length);
        System.out.println(indent(depth) + "  Entries->");
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth)
            throws IllegalArgumentException, IllegalAccessException {
        System.out.println(indent(depth) + "CLASS");
        System.out.println(indent(depth) + "Class: " + c.getName());
        if (!c.isArray()) {
            // check for superclass
            if (c.getSuperclass() != null) {
                System.out.println(indent(depth) + "SuperClass: " + c.getSuperclass().getCanonicalName());
                if (alreadyInspected(c.getSuperclass())) {
                    System.out.println("Already inspected this object...");
                } else {
                    System.out.println(indent(depth) + "SUPERCLASS -> Recursively Inspect");
                    inspectClass(c.getSuperclass(), obj, recursive, depth + 1);
                }
            } else {
                System.out.println(indent(depth) + "SUPERCLASS: NONE");
            }
//			inspectInterfaces(c, obj, depth);
//			inspectConstructors(c, depth);
//			inspectMethods(c, depth);

            inspectFields(c, obj, recursive, depth);

        } else if (c.isArray() && recursive == true) {
            Object[] objArray = populateArray(obj);
            printArrayHeader(c, depth, objArray);
            handleArrayRecursive(recursive, depth, c, objArray);

        } else if (c.isArray() && recursive == false) {
            Object[] objArray = populateArray(obj);
            printArrayHeader(c, depth, objArray);

            for (Object entry : objArray) {
                if (c.getComponentType().isPrimitive()) {
                    System.out.println(indent(depth) + "   Value: " + entry);
                } else if (!c.getComponentType().isPrimitive() && recursive == false) {
                    if (entry == null) {
                        System.out.println(indent(depth) + "   Value: null");
                    } else {
                        System.out.println(indent(depth) + "   Value (ref): " + Integer.toHexString(entry.hashCode()));
                    }
                } else if (entry == null) {
                    System.out.println(indent(depth) + "   Value: null");
                }
            }
        }
    }

//	private void inspectInterfaces(Class c, Object obj, int depth)
//			throws IllegalArgumentException, IllegalAccessException {
//		System.out.println(indent(depth) + "INTERFACES( " + c.getCanonicalName() + " )");
//		Class[] objInterface = c.getInterfaces();
//
//		if (objInterface.length > 0) {
//			System.out.println(indent(depth) + "Interfaces->");
//			for (Class entry : objInterface) {
//				System.out.println(indent(depth) + " INTERFACE -> Recursively Inspect");
//				System.out.println(indent(depth) + " " + entry.getCanonicalName());
//				inspectClass(entry, obj, true, depth + 1);
//			}
//		} else {
//			System.out.println(indent(depth) + "Interfaces-> NONE");
//		}
//
//	}

//	private void inspectConstructors(Class c, int depth) {
//		System.out.println(indent(depth) + "CONSTRUCTORS( " + c.getCanonicalName() + " )");
//		if (c.getDeclaredConstructors().length > 0) {
//			System.out.println(indent(depth) + "Constructors->");
//
//			for (int i = 0; i < c.getDeclaredConstructors().length; i++) {
//				System.out.println(indent(depth) + " CONSTRUCTOR");
//				System.out.println(indent(depth) + "  Name: " + c.getDeclaredConstructors()[i].getName());
//				Constructor currentCons = c.getDeclaredConstructors()[i];
//
//				Class[] parameterTypes = currentCons.getParameterTypes();
//				int parameterCount = currentCons.getParameterCount();
//				if (parameterCount > 0) {
//					System.out.println(indent(depth) + "  Parameter types->");
//					for (Class parameterType : parameterTypes) {
//						System.out.println(indent(depth) + "   " + parameterType);
//					}
//				} else {
//					System.out.println(indent(depth) + "  Parameter types-> NONE");
//				}
//				if (currentCons.getModifiers() > 0) {
//					System.out.println(indent(depth) + "  Modifiers: " + Modifier.toString(currentCons.getModifiers()));
//				} else {
//					System.out.println(indent(depth) + "  Modifiers: NONE");
//				}
//			}
//
//		} else {
//			System.out.println(indent(depth) + "Constructors-> NONE");
//		}
//	}

//	private void inspectMethods(Class c, int depth) {
//		System.out.println(indent(depth) + "METHODS( " + c.getCanonicalName() + " )");
//		if (c.getDeclaredMethods().length > 0) {
//			System.out.println(indent(depth) + "Methods->");
//			for (int i = 0; i < c.getDeclaredMethods().length; i++) {
//				System.out.println(indent(depth) + " METHOD");
//				System.out.println(indent(depth) + "  Name: " + c.getDeclaredMethods()[i].getName());
//				Method instanceOfMethod = c.getDeclaredMethods()[i];
//				// print exceptions
//				if (c.getDeclaredMethods()[i].getExceptionTypes().length > 0) {
//					System.out.println(indent(depth) + "  Exceptions->");
//					for (Class exceptionType : instanceOfMethod.getExceptionTypes()) {
//						System.out.println(indent(depth) + "   " + exceptionType);
//					}
//				} else {
//					System.out.println(indent(depth) + "  Exceptions-> NONE");
//				}
//				// ----
//				// print parameter types
//				if (instanceOfMethod.getParameterTypes().length > 0) {
//					System.out.println(indent(depth) + "  Parameter types->");
//					for (Class parameterType : instanceOfMethod.getParameterTypes()) {
//						System.out.println(indent(depth) + "   " + parameterType);
//					}
//				} else {
//					System.out.println(indent(depth) + "  Parameter types-> NONE");
//				}
//				// ----
//				// print return type
//				System.out.println(indent(depth) + "  Return type: " + instanceOfMethod.getReturnType());
//				// ----
//				// print modifiers
//				System.out
//						.println(indent(depth) + "  Modifiers: " + Modifier.toString(instanceOfMethod.getModifiers()));
//			}
//		} else {
//			System.out.println(indent(depth) + "Methods-> NONE");
//		}
//
//	}

    private void inspectFields(Class c, Object obj, Boolean recursive, int depth)
            throws IllegalArgumentException, IllegalAccessException {

        System.out.println(indent(depth) + "FIELDS( " + c.getCanonicalName() + " )");
        if (c.getDeclaredFields().length > 0) {

            System.out.println(indent(depth) + "Fields->");
            for (Field field : c.getDeclaredFields()) {
                field.setAccessible(true);
                if (alreadyInspected(field.get(obj))) {
                    System.out.println("Already inspected this object...");
                } else {
                    Class fType = field.getType();
                    Object fValue = field.get(obj);

                    System.out.println(indent(depth) + " FIELD");
                    System.out.println(indent(depth) + "  Name: " + field.getName());
                    System.out.println(indent(depth) + "  Type: " + fType);
                    System.out.println(indent(depth) + "  Modifiers: " + Modifier.toString(field.getModifiers()));


                    if (fType.isPrimitive()) {
                        System.out.println(indent(depth) + "  Value: " + fValue);
                    } else if (fType.isArray()) {
                        Object[] objArray = populateArray(fValue);
                        printArrayHeader(fType, depth, objArray);
                        handleArrayRecursive(recursive, depth, fType, objArray);
                    } else if (recursive == false) {
                        if (fValue == null) {
                            System.out.println(indent(depth) + "  Value: null");
                        } else {
                            System.out.println(indent(depth) + "  Value (ref)-> " + fValue + "@"
                                    + Integer.toHexString(fValue.hashCode()));
                        }
                    } else if (fValue == null) {
                        System.out.println(indent(depth) + "  Value: null");
                    } else {
                        System.out.println(
                                indent(depth) + "  Value (ref)-> " + fValue + "@" + Integer.toHexString(fValue.hashCode()));
                        if (alreadyInspected(fValue.getClass())) {
                            System.out.println("Already inspected this object...");
                        } else {
                            System.out.println(indent(depth) + "     -> Recursively inspect");
                            inspectClass(fValue.getClass(), fValue, recursive, depth + 1);
                        }
                    }
                }
            }
        } else {
            System.out.println(indent(depth) + "Fields-> NONE");
        }
    }

    public boolean alreadyInspected(Object obj) {
        HashSet<Integer> objHash = this.getObjectHash();
        return objHash.contains(obj.hashCode());
    }

    public HashSet<Integer> getObjectHash() {
        return this.objectHash;
    }
}

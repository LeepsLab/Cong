/**
 * Copyright (c) 2012, University of California All rights reserved.
 *
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 *
 */
package edu.ucsc.leeps.fire.reflection;

import edu.ucsc.leeps.fire.config.Configurator.ConfigStore;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * OMG why is this all static methods...
 *
 * @author jpettit
 */
public class ObjectMapper {

    private int row;
    private int currColumn;

    public ObjectMapper(int row) {
        this.row = row;
        this.currColumn = -1;
    }

    public static void load(Object object, int row, ConfigStore store) throws ObjectMapException {
        ObjectMapper om = new ObjectMapper(row);
        om.loadComplexField(0, store.keys.length - 1, store.keys, store.rows.get(row), object);
    }
    private static Set<String> trueValues = new HashSet<String>() {

        {
            add("TRUE");
            add("YES");
            add("T");
            add("Y");
        }
    };
    private static Set<String> falseValues = new HashSet<String>() {

        {
            add("FALSE");
            add("NO");
            add("F");
            add("N");
        }
    };

    private void loadPrimitiveField(Field field, String value, Object object) throws ObjectMapException {
        Object typedValue;
        if (field.getType() == boolean.class) {
            if (trueValues.contains(value.toUpperCase())) {
                typedValue = true;
            } else if (falseValues.contains(value.toUpperCase())) {
                typedValue = false;
            } else {
                throw new ObjectMapException(row, String.format("Invalid choice for boolean (%s): %s", field.getName(), value));
            }
        } else if (field.getType() == int.class) {
            try {
                typedValue = Integer.decode(value.trim());
            } catch (NumberFormatException ex) {
                throw new ObjectMapException(row, String.format("Cannot format integer field (%s): %s", field.getName(), value));
            }
        } else if (field.getType() == float.class) {
            try {
                typedValue = Float.parseFloat(value.trim());
            } catch (NumberFormatException ex) {
                throw new ObjectMapException(row, String.format("Cannot format integer field (%s): %s", field.getName(), value));
            }
        } else {
            throw new ObjectMapException(row, String.format("Unknown field type. (%s): %s", field.getName(), value));
        }
        try {
            field.set(object, typedValue);
        } catch (IllegalAccessException ex) {
            throw new ObjectMapException(row, String.format("Error accessing %s.%s", object.getClass().getCanonicalName(), field.getName()));
        } catch (IllegalArgumentException ex) {
            throw new ObjectMapException(row, String.format("Error accessing %s.%s", object.getClass().getCanonicalName(), field.getName()));
        }
    }

    private void loadEnumField(Field field, String fieldValue, Object object) throws ObjectMapException {
        for (Object enumConstant : field.getType().getEnumConstants()) {
            if (enumConstant.toString().toLowerCase().equals(fieldValue.toLowerCase())) {
                try {
                    field.set(object, enumConstant);
                } catch (IllegalAccessException ex) {
                    throw new ObjectMapException(row, String.format("Error assiging %s to %s. Code: IllegalAccessException", fieldValue, field.getName()));
                } catch (IllegalArgumentException ex) {
                    throw new ObjectMapException(row, String.format("Error assiging %s to %s. Code: IllegalAccessException", fieldValue, field.getName()));
                }
                return;
            }
        }
        throw new ObjectMapException(row, String.format("No enum constant found for %s", fieldValue));
    }

    private void loadComplexField(int begin, int end, String[] keys, String[] values, Object object) throws ObjectMapException {
        for (int i = begin; i <= end; i++) {
            currColumn++;
            if (!values[i].equals("")) {
                try {
                    Field field = object.getClass().getField(keys[i]);
                    if (field.getType().isPrimitive()) {
                        loadPrimitiveField(field, values[i], object);
                    } else if (field.getType().isEnum()) {
                        loadEnumField(field, values[i], object);
                    } else if (field.getType() == String.class) {
                        field.set(object, values[i]);
                    } // If none of the previous if-statements are tripped, it's a new complex field.
                    else {
                        Object child = newChild(values[i], object);
                        int j = i + 1;
                        while (j <= end) {
                            if (!values[j].equals("")) {
                                try {
                                    child.getClass().getField(keys[j]);
                                } catch (NoSuchFieldException ex) {
                                    break;
                                }
                            }
                            j++;
                        }
                        field.set(object, child);
                        loadComplexField(i + 1, j - 1, keys, values, child);
                        i = j - 1;
                    }
                } catch (IllegalAccessException ex) {
                    throw new ObjectMapException(row, String.format("Error loading column %s. Code: IllegalAccessException", keys[i]));
                } catch (IllegalArgumentException ex) {
                    throw new ObjectMapException(row, String.format("Error loading column %s. Code: IllegalArgumentException", keys[i]));
                } catch (InstantiationException ex) {
                    throw new ObjectMapException(row, String.format("Error loading column %s. Code: InstantiationException", keys[i]));
                } catch (NoSuchFieldException ex) {
                    try {
                        String key = keys[i];
                        if (key.contains("Array")) {
                            String[] vvalues = values[i].split(";");
                            Float[] array = new Float[vvalues.length];
                            for (int j = 0; j < vvalues.length; j++) {
                                float value = Float.parseFloat(vvalues[j]);
                                array[j] = value;
                            }
                            try {
                                Field f = object.getClass().getField("paramArrayMap");
                                Map<String, Float[]> paramArrayMap = (Map<String, Float[]>) f.get(object);
                                if (paramArrayMap == null) {
                                    paramArrayMap = new HashMap<String, Float[]>();
                                    f.set(object, paramArrayMap);
                                }
                                paramArrayMap.put(key, array);
                            } catch (NoSuchFieldException ex2) {
                                throw new ObjectMapException(row, String.format("No field named %s", keys[i]));
                            } catch (IllegalAccessException ex2) {
                                throw new ObjectMapException(row, String.format("Error loading column %s. Code: IllegalAccessException", keys[i]));
                            } catch (IllegalArgumentException ex2) {
                                throw new ObjectMapException(row, String.format("Error loading column %s. Code: IllegalArgumentException", keys[i]));
                            }

                        } else {
                            float value = Float.parseFloat(values[i]);
                            try {
                                Field f = object.getClass().getField("paramMap");
                                Map<String, Float> paramMap = (Map<String, Float>) f.get(object);
                                if (paramMap == null) {
                                    paramMap = new HashMap<String, Float>();
                                    f.set(object, paramMap);
                                }
                                paramMap.put(key, value);
                            } catch (NoSuchFieldException ex2) {
                                throw new ObjectMapException(row, String.format("No field named %s", keys[i]));
                            } catch (IllegalAccessException ex2) {
                                throw new ObjectMapException(row, String.format("Error loading column %s. Code: IllegalAccessException", keys[i]));
                            } catch (IllegalArgumentException ex2) {
                                throw new ObjectMapException(row, String.format("Error loading column %s. Code: IllegalArgumentException", keys[i]));
                            }
                        }
                    } catch (NumberFormatException numFormatEx) {
                        throw numFormatEx;
                    }
                } catch (SecurityException ex) {
                    throw new ObjectMapException(row, String.format("Error loading column %s. Code: SecurityException", keys[i]));
                }
            }
        }
    }

    private Object newChild(String value, Object parent) throws IllegalAccessException, IllegalArgumentException, InstantiationException, SecurityException {
        for (Field field : parent.getClass().getFields()) {
            if (field.getName().equals(value) && field.getType() == Class.class) {
                return ((Class) field.get(parent)).newInstance();
            }
        }
        return null;
    }

    public static void writeHeader(Object o, BufferedWriter w, String delimiter) throws IOException, IllegalAccessException, IllegalArgumentException, SecurityException {
        List<String> header = getHeader(o);
        int i = 0;
        for (String key : header) {
            w.write(key);
            if (i != header.size() - 1) {
                w.write(delimiter);
            }
            i++;
        }
        w.newLine();
    }

    public static void write(Object o, BufferedWriter w, String delimiter) throws IOException, IllegalAccessException, IllegalArgumentException, SecurityException {
        List<String> line = getLine(o);
        int i = 0;
        for (String key : line) {
            w.write(key);
            if (i != line.size() - 1) {
                w.write(delimiter);
            }
            i++;
        }
        w.newLine();
        w.flush();
    }

    private static List<String> getHeader(Object o) throws IllegalAccessException, IllegalArgumentException, SecurityException {
        List<String> header = new LinkedList<String>();
        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && !Modifier.isTransient(field.getModifiers())) {
                if (field.getType().isArray()) {
                    if (field.get(o) == null) {
                        header.add(field.getName() + "(null)");
                    } else {
                        for (int i = 0; i < Array.getLength(field.get(o)); i++) {
                            header.add(field.getName() + i);
                        }
                    }
                } else {
                    header.add(field.getName());
                    if (!(field.getType().isPrimitive()
                            || field.getType().isEnum()
                            || field.getType() == String.class)) {
                        if (field.get(o) != null) {
                            header.addAll(getHeader(field.get(o)));
                        }
                    }
                }
            }
        }
        return header;
    }

    private static List<String> getLine(Object o) throws IllegalAccessException, IllegalArgumentException, SecurityException {
        List<String> line = new LinkedList<String>();
        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && !Modifier.isTransient(field.getModifiers())) {
                line.addAll(map(field.get(o), field.getType()));
            }
        }
        return line;
    }

    private static List<String> map(Object o, Class type) throws ArrayIndexOutOfBoundsException, IllegalAccessException, IllegalArgumentException, SecurityException {
        List<String> line = new LinkedList<String>();
        if (o == null) {
            line.add("null");
        } else if (type.isPrimitive()) {
            line.add(o.toString());
        } else if (type.isEnum()) {
            line.add(o.toString());
        } else if (type == String.class) {
            line.add(o.toString());
        } else if (type.isArray()) {
            for (int i = 0; i < Array.getLength(o); i++) {
                Object o1 = Array.get(o, i);
                line.add(o1.toString());
            }
        } else {
            line.add(o.getClass().getSimpleName());
            line.addAll(getLine(o));
        }
        return line;
    }

    public static class ObjectMapException extends Exception {

        public ObjectMapException(int row, String string) {
            super(String.format("Row %d: %s", row + 2, string));
        }
    }
}

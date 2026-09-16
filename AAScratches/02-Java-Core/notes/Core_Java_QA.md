# Core Java Q&A

Interview notes on core Java internals: how the JVM loads classes, and the different ways an object can be created.

## 1. What is a Class Loader?

A `ClassLoader` is a part of the Java Virtual Machine (JVM) that loads `.class` files into memory when they are first referenced in code.

You don't have to load all classes manually — Java does it automatically and lazily (on demand).

### Class Loader Hierarchy (Parent Delegation Model)

Java uses a hierarchical delegation model to ensure security and avoid class conflicts.

| ClassLoader | Responsibility | Loads From |
| --- | --- | --- |
| Bootstrap ClassLoader | Core JVM classes | `$JAVA_HOME/jre/lib` (e.g., `rt.jar`, `java.base`) |
| Extension (Platform) ClassLoader | Extension libraries | `$JAVA_HOME/jre/lib/ext` |
| System (Application) ClassLoader | Application-level classes | `classpath` (e.g., `target/classes`, `libs/*.jar`) |
| Custom ClassLoader | User-defined logic | You implement it (e.g., load from DB, network) |

## 2. In how many ways can we create an object in Java?

### 1. Using the `new` keyword (most common)

```java
MyClass obj = new MyClass();
```

### 2. Using `Class.forName()` and `newInstance()` (Reflection)

```java
MyClass obj = (MyClass) Class.forName("MyClass").newInstance();
```

Note: `newInstance()` is deprecated since Java 9. Recommended alternative:

```java
MyClass obj = MyClass.class.getDeclaredConstructor().newInstance();
```

### 3. Using the `Constructor` class from Reflection

```java
Constructor<MyClass> constructor = MyClass.class.getConstructor();
MyClass obj = constructor.newInstance();
```

### 4. Using the `clone()` method

The class must implement `Cloneable` and override `clone()`.

```java
MyClass obj2 = (MyClass) obj1.clone();
```

### 5. Using Deserialization

The object is created without calling a constructor.

```java
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("file.ser"));
MyClass obj = (MyClass) ois.readObject();
```

### 6. Using Factory Methods

For example:

```java
Number n = Integer.valueOf(10);
MyClass obj = MyClass.create();
```

### 7. Using Object Streams / Copying libraries

Frameworks like Jackson, Gson, Kryo create objects internally. Example with Gson:

```java
MyClass obj = new Gson().fromJson(json, MyClass.class);
```

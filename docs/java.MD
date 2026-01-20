# Java Related Code Cheat Sheet

## `Static Keyword`

> [!NOTE] Static Definition
> `Static` = belongs to the class, not an instance. If no object exists, static still works.

---

There are 7-8 use cases for the keyword `static`, they are:

1. [Static Import](#static-import)
2. [Static Field/Variable (Class Variable)](#)




### Static Import

```java
import static org.junit.jupiter.api.Assertions.*;
```

- Use: Call static members without the class name
- Think: Syntax convenience
- Does NOT: Change scope or visibility

### Static Field/Variable (Class Variable)
static int count;


Use: Shared state across all instances
One copy total
Danger: Shared mutable state (tests!)

### Static Method

```java
static int add(int a, int b)
```

Use: Utility / helper logic
Rules:
Can use static fields
Cannot use instance fields or this

### Static Nested Class

static class Helper { }

Use: Helper class that doesn’t need an outer instance

### Static in Interfaces


interface X {
    static void helper() {}
    static final int VALUE = 1;
}


Use: Constants + utility methods
Note: Interface fields are implicitly public static final

### Static Block
static {
    // runs once at class load
}


Use: One-time setup logic

### Static Constants
public static final String BASE_URL = "...";


Use: Shared, immutable values
Safest static usage

### Static Context Rule
static void f() { ... }


Rule:

❌ no this

❌ no instance members

✅ static only

Static quick test (exam rule)

If it can exist without new, it must be static



1) Plain English first
Static (keyword)

Plain English: “This belongs to the class, so everyone shares one copy.”

Technical term (class-level / shared member): static member

Theater analogy:

Class = the theater company

Object = a specific actor in that company

Static = the company’s shared props (one prop closet for everyone)

Non-static = the actor’s personal costume (each actor has their own)

Why it matters for the exam:
Static is a top-5 source of trick questions (scope, this, calling methods, and shared state bugs).

2) Static import vs static variable (they’re different things)
import static org.junit.jupiter.api.Assertions.*;

Plain English: “Bring in those static methods so I can call them without the class name.”

You can write:

assertEquals(2, x);


instead of:

Assertions.assertEquals(2, x);


This does NOT change scope or make things local.
It only removes the need to type the class name.

Theater: You stop saying “From the Critic’s Handbook…” before every critique.

3) static on a variable (field)
Inside a class:
class User {
    static int count = 0;
}

Plain English

There is one count for the whole class, shared by all instances.

Not local

This is NOT a local variable. It’s a field (member) that lives as long as the class is loaded.

How you use it
User.count++;

Theater

One shared “ticket counter” for the whole theater company, not one per actor.

Why it matters

If you use static fields in tests, you can get test pollution (one test affects another).

Static field vs instance field
class User {
    static int shared = 0;  // one shared copy
    int personal = 0;       // each object gets its own
}

User a = new User();
User b = new User();

a.shared++;   // shared becomes 1
b.shared++;   // shared becomes 2 (same variable)

a.personal++; // a.personal = 1
b.personal++; // b.personal = 1 (different per object)

4) “What about outside the class?”

You can’t write:

static String x;


just floating around in Java (top-level). Java requires variables to be inside:

a class

an interface

a method/block (local)

So “outside the class” only means: from another class, how do I access it?

Access a static field from another class
SomeClass.x


Example:

class A {
    static String name = "A";
}

class B {
    void test() {
        System.out.println(A.name);
    }
}

5) static on a method
class MathUtil {
    static int add(int a, int b) { return a + b; }
}

Plain English

You can call it without creating an object.

MathUtil.add(1, 2);

Key rule

A static method:

can use static fields

cannot directly use instance fields (because there might be no object)

Theater: A company-wide announcement, not an actor speaking from their own costume.

6) static on a nested class
class Outer {
    static class Inner { }
}

Plain English

This inner class does NOT need an instance of Outer to exist.

7) static in interfaces (common in Java)
interface X {
    static void helper() { }
    static final int VALUE = 10;
}

Plain English

Interface constants are effectively shared and class-level.
You call interface static methods like:

X.helper();

8) static blocks (runs once)
class App {
    static {
        // runs once when class loads
    }
}

Plain English

One-time setup code for the class.

9) Static + this (big confusion point)
this = the current object (instance)

So inside a static context, there is no “current object.”

That’s why this is illegal:

static void f() {
    System.out.println(this); // ❌ no instance
}


Exam hook: “Why can’t a static method use this?”
Because this requires an instance.

10) Your exact question: “static means treat it like local?”
static String variableName;

Plain English: “This is a shared variable for the entire class.”

NOT local

NOT per method

NOT per object

It’s one shared copy

Local would look like:

void f() {
    String variableName; // local
}

Theater comparison

static String x; = shared prop closet item

String x; (instance field) = actor’s personal costume piece

String x; (inside method) = something you wrote on a sticky note during rehearsal (temporary)

11) Most common “context situations” you’ll run into
A) Tests / Step Definitions (Cucumber)

Static can accidentally carry state between scenarios

That causes flaky tests

Rule of thumb:
Use static only if it’s truly constant, like:

static final String BASE_URL = "...";

B) Constants
public static final int TIMEOUT = 5;


Plain English: one shared constant for everyone.

C) Utility methods
public static String normalize(String s) { ... }


No object needed, just a helper.

If you paste the AdminLoginSteps file (or just the static lines you’re seeing), I’ll label each one as:

“shared constant”

“shared mutable state” (danger)

“utility method”
and tell you what to change (or not change) for clean Cucumber tests.

So 8 use cases? Static import, static variable aka static field, static method, static nested class, static in interfaces, and static block?

Yes — you’ve got it 👍
Those are the core use cases. I’ll tighten it, count them cleanly, and give you one-line meanings so it’s exam-ready.

The 8 practical uses of static in Java
1) Static import
import static org.junit.jupiter.api.Assertions.*;


Meaning:
Use static members without qualifying the class name.

Why it exists:
Cleaner syntax, especially in tests.

2) Static field (class variable)
static int count;


Meaning:
One shared variable for the entire class.

Key rule:
All instances see the same value.

3) Static method
static int add(int a, int b) { ... }


Meaning:
Method belongs to the class, not an object.

Key rule:

Can access static fields

Cannot access instance fields or this

4) Static nested class
static class Helper { }


Meaning:
Nested class that does not depend on an outer instance.

5) Static in interfaces
interface X {
    static void helper() { }
    static final int VALUE = 10;
}


Meaning:
Utility methods or constants tied to the interface itself.

6) Static block
static {
    // runs once when the class is loaded
}


Meaning:
One-time initialization logic for the class.

7) Static constants (static final)
public static final String BASE_URL = "...";


Meaning:
Shared, immutable constant.

Most common safe use of static.

8) Static context (scope rule)
static void f() { ... }


Meaning:
Code runs without an instance, so:

❌ no this

❌ no instance fields

✅ only static members

(This is less a feature and more a rule, but exams treat it as its own concept.)

One-sentence mental model (memorize this)

static means “belongs to the class, not to an instance.”

Quick exam traps to watch for

Static methods using instance variables ❌

Static fields used to hold test state ❌

Forgetting that static fields are shared ❌

Thinking static = local ❌



## Integer

### intValue()

expectedStatus is an Integer object (wrapper).
.intValue() converts it to primitive int.

Modern Java usually lets you skip this because of auto-unboxing, but it’s fine.

Other useful Integer things:

Integer.parseInt("123")

toString()

comparisons, etc.
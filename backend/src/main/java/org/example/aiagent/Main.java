class Person {
    public abstract void run();
}

abstract class Person {
    public abstract void run();
}

abstract class Person {
    public abstract void run();
}

Person s = new Student();
Person t = new Teacher();

abstract class Person {
    public abstract void run();
    public abstract String getName();
}

interface Person {
    void run();
    String getName();
}

class Student implements Person {
    private String name;

    public Student(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(this.name + " run");
    }

    @Override
    public String getName() {
        return this.name;
    }
}

// interface
public class Main {
    public static void main(String[] args) {
        Person p = new Student("Xiao Ming");
        p.run();
    }
}

interface Person {
    String getName();
    default void run() {
        System.out.println(getName() + " run");
    }
}






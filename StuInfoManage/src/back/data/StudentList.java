package back.data;

import java.util.ArrayList;

public class StudentList {

    private final ArrayList<Student> students = new ArrayList<>();

    public StudentList() {
    }

    public void add(Student student) {
        this.students.add(student);
    }

    public boolean has(Student student) {
        for (Student value : students) {
            if (student.getS_number().equals(value.getS_number())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (Student i : this.students) {
            buffer.append(i.toString());
        }
        return buffer.toString();
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public Student getStudent(int index) {
        return students.get(index);
    }

    public int getLength() {
        return students.size();
    }

}

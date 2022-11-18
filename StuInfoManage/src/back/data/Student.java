package back.data;

public class Student {
    private final String s_name;
    private final String s_number;
    private final String s_grade;
    private final String s_class;
    private int s_version;

    public Student(String s_name, String s_number, String s_grade, String s_class, int s_version) {
        this.s_name = s_name;
        this.s_number = s_number;
        this.s_grade = s_grade;
        this.s_class = s_class;
        this.s_version = s_version;
    }

    //大于时说明客户端已改过该条数据，小于时说明服务器端已改过该数据
    public boolean OptimisticLocking(int version) {
        return this.s_version == version;
    }

    public boolean equals(Student student) {
        return s_name.equals(student.s_name) && s_number.equals(student.s_number)
                && s_grade.equals(student.s_grade) && s_class.equals(student.s_class);
    }

    @Override
    public String toString() {
        return s_name + " " + s_number + " " + s_grade + " " + s_class + " " + s_version + "\n";
    }

    public String getS_number() {
        return s_number;
    }

    public int getS_version() {
        return s_version;
    }

    public void setS_version(int s_version) {
        this.s_version = s_version;
    }
}

package back.utils;

import back.data.Command;
import back.data.Student;
import back.data.StudentList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

public class Utils {
    private static Utils instance;

    //�ù��캯��Ϊ private����������Ͳ��ᱻʵ����
    private Utils() {
    }

    //��ȡΨһ���õĶ���
    public static synchronized Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    //��������ʱ����
    public String RchangeCode(String content) {
        return URLDecoder.decode(content, StandardCharsets.UTF_8);
    }

    //��������ʱ�ڷ���json��ʽ��ǰ���¸������ַ�����
    public String FchangeCode(String content) {
        content = URLEncoder.encode(content, StandardCharsets.UTF_8);
        content = content.replaceAll("\\+", "");
        content = content.replaceAll("%22", "\"");
        content = content.replaceAll("%3A", ":");
        content = content.replaceAll("%2C", ",");
        content = content.replaceAll("%5B", "[");
        content = content.replaceAll("%7B", "{");
        content = content.replaceAll("%7D", "}");
        content = content.replaceAll("%5D", "]");
        return content;
    }

    //json�ַ�����ѧ����Ϣ
    public String jsonSL(StudentList studentList) {
        Gson gson = new Gson();
        return gson.toJson(studentList);
    }

    //��ѧ����Ϣ���ļ��ж�ȡ����
    public StudentList readStuFile() throws IOException {

        String content;
        StringBuilder builder = new StringBuilder();
        File file = new File("StuInfoManage/src/resource/json/StuInfo.json");
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "GBK");
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while ((content = bufferedReader.readLine()) != null) {
            builder.append(content);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Object>>() {
        }.getType();

        StudentList sl = new StudentList();

        ArrayList<Object> al = gson.fromJson(builder.toString(), type);

        for (Object i : al) {
            String istr = i.toString();
            Student j = new Student(
                    StringUtils.substringBetween(istr, "s_name=", ", s_number"),
                    StringUtils.substringBetween(istr, "s_number=", ", s_grade"),
                    StringUtils.substringBetween(istr, "s_grade=", ", s_class"),
                    StringUtils.substringBetween(istr, "s_class=", ", s_version"),
                    (int) Double.parseDouble(StringUtils.substringBetween(istr, "s_version=", "}")));
            sl.add(j);
        }

        System.out.println("studentlist.length = " + sl.getLength() + "\n");

        return sl;
    }

    public void writeStuFile(StudentList studentList) {
        Gson gson = new Gson();
        String filename;
        try {
            filename = new String("StuInfoManage/src/resource/json/StuInfo.json".getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        File file = new File(filename);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(file);
            writer.println(gson.toJson(studentList.getStudents()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //������ָ����Ϣ���ļ��ж�ȡ����
    public Stack<Command> readComFile() throws IOException {

        String content;
        StringBuilder builder = new StringBuilder();

        File file = new File("StuInfoManage/src/resource/json/ComInfo.json");
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), "GBK");
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while ((content = bufferedReader.readLine()) != null) {
            builder.append(content);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Stack<Command>>() {
        }.getType();

        Stack<Command> cl = gson.fromJson(builder.toString(), type);

        if (cl == null) {
            cl = new Stack<>();
        }

        System.out.println("commands.size() = " + cl.size());

        return cl;
    }

    //д�����ָ��
    public void writeComFile(Stack<Command> commands) {
        Gson gson = new Gson();
        String filename;
        try {
            filename = new String("StuInfoManage/src/resource/json/ComInfo.json".getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        File file = new File(filename);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(file);
            writer.println(gson.toJson(commands));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createStudent(StudentList studentList, Command command, Stack<Command> commands) {
        //����Ѿ����˸�ѧ�����򴴽���Ч������false
        if (studentList.has(command.getCurstu())) {
            return false;
        }
        for (int i = 0; i < studentList.getLength(); i++) {
            if (Integer.parseInt(studentList.getStudent(i).getS_number())
                    > Integer.parseInt(command.getCurstu().getS_number())) {
                studentList.getStudents().add(i, command.getCurstu());
                //���°汾��
                studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                writeStuFile(studentList);
                //���泷��ָ��
                commands.push(new Command("delete", command.getCurstu(), command.getPrestu()));
                writeComFile(commands);
                return true;
            }
        }
        //���°汾��
        studentList.getStudent(studentList.getLength() - 1).
                setS_version(studentList.getStudent(studentList.getLength() - 1).getS_version() + 1);
        studentList.add(command.getCurstu());
        writeStuFile(studentList);
        //���泷��ָ��
        commands.push(new Command("delete", command.getCurstu(), command.getPrestu()));
        writeComFile(commands);
        return true;
    }

    public boolean deleteStudent(StudentList studentList, Command command, Stack<Command> commands) {
        //�����ҵ�Ҫɾ�ļ�¼
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //�����������version<=�ͻ���version������Ը�������
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    writeStuFile(studentList);
                    //���泷��ָ��
                    commands.push(new Command("create", command.getCurstu(), command.getPrestu()));
                    writeComFile(commands);
                    return true;
                } else {
                    return false;
                }
            }
        }
        //δ�ҵ���˵���������ѱ�ɾ��������false
        return false;
    }

    public boolean changeStudent(StudentList studentList, Command command, Stack<Command> commands) {
        //�����ҵ�Ҫ�ĵļ�¼
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //�����������version<=�ͻ���version������Ը�������
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    studentList.getStudents().add(i, command.getCurstu());
                    //���°汾��
                    studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                    writeStuFile(studentList);
                    //���泷��ָ��
                    commands.push(new Command("change", command.getCurstu(), command.getPrestu()));
                    writeComFile(commands);
                    return true;
                } else {
                    return false;
                }
            }
        }
        //δ�ҵ���˵���������ѱ�ɾ��������false
        return false;
    }

    public void createStudent(StudentList studentList, Command command) {
        //����Ѿ����˸�ѧ�����򴴽���Ч������false
        if (studentList.has(command.getCurstu())) {
            return;
        }
        for (int i = 0; i < studentList.getLength(); i++) {
            if (Integer.parseInt(studentList.getStudent(i).getS_number())
                    > Integer.parseInt(command.getCurstu().getS_number())) {
                studentList.getStudents().add(i, command.getCurstu());
                //���°汾�� �������������ø���
//                studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                writeStuFile(studentList);
                return;
            }
        }
        //���°汾�� �������������ø���
//        studentList.getStudent(studentList.getLength() - 1).
//                setS_version(studentList.getStudent(studentList.getLength() - 1).getS_version() + 1);
        studentList.add(command.getCurstu());
        writeStuFile(studentList);
    }

    public void deleteStudent(StudentList studentList, Command command) {
        //�����ҵ�Ҫɾ�ļ�¼
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //�����������version<=�ͻ���version������Ը�������
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    writeStuFile(studentList);
                }
                return;
            }
        }
        //δ�ҵ���˵���������ѱ�ɾ��������false
    }

    public void changeStudent(StudentList studentList, Command command) {
        //�����ҵ�Ҫ�ĵļ�¼
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //�����������version<=�ͻ���version������Ը�������
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    studentList.getStudents().add(i, command.getCurstu());
                    //���°汾�� �������������ø���
//                    studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                    writeStuFile(studentList);
                }
                return;
            }
        }
        //δ�ҵ���˵���������ѱ�ɾ��������false
    }

    //��ʼ������
    public void initFrame(JFrame frame) {
        //���ùر�ʱ�˳�����
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //���ô���Ĵ�С
        frame.setSize(320, 180);
        //���ô��ھ���
        frame.setLocationRelativeTo(null);
        //���ò�������
        frame.setResizable(false);
        //���ñ���ͼƬ
        ImageIcon image = new ImageIcon("StuInfoManage/src/resource/image/gui.jpg");
        JLabel background = new JLabel(image);
        frame.add(background);
        background.setBounds(0, 0, 320, 180);
        frame.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));
        ((JPanel) frame.getContentPane()).setOpaque(false); //����͸��

        frame.addComponentListener(new ComponentAdapter() {//�ô�����Ӧ��С�ı��¼�
            @Override
            public void componentResized(ComponentEvent e) {
                int fraWidth = frame.getWidth();//��ȡ�����
                int fraHeight = frame.getHeight();//��ȡ���߶�
                background.setBounds(0, 0, fraWidth, fraHeight);
            }
        });
    }

    public void beautifyFrame() {

        // ���ô��ڵķ��
        String outLookAndFeel = "com.jtattoo.plaf.luna.LunaLookAndFeel";
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(outLookAndFeel);
        } catch (Exception e) {
            System.out.println();
        }

        //�ı�ϵͳĬ������
        Font font = new Font("��Բ", Font.BOLD, 15);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    //������ָ����嵽ָ�������
    public void opCom(Stack<Command> commands, StudentList studentList) {
        if (commands.peek().getType().equals("create")) {
            Utils.getInstance().createStudent(studentList, commands.peek());
        } else if (commands.peek().getType().equals("delete")) {
            Utils.getInstance().deleteStudent(studentList, commands.peek());
        } else if (commands.peek().getType().equals("change")) {
            Utils.getInstance().changeStudent(studentList, commands.peek());
        }
        commands.pop();
        writeComFile(commands);
    }
}

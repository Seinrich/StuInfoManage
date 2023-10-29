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

    //让构造函数为 private，这样该类就不会被实例化
    private Utils() {
    }

    //获取唯一可用的对象
    public static synchronized Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    //接收数据时解码
    public String RchangeCode(String content) {
        return URLDecoder.decode(content, StandardCharsets.UTF_8);
    }

    //反馈数据时在符合json格式的前提下给中文字符加码
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

    //json字符串化学生信息
    public String jsonSL(StudentList studentList) {
        Gson gson = new Gson();
        return gson.toJson(studentList);
    }

    //将学生信息从文件中读取出来
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

    //将回退指令信息从文件中读取出来
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

    //写入回退指令
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
        //如果已经有了该学生，则创建无效，返回false
        if (studentList.has(command.getCurstu())) {
            return false;
        }
        for (int i = 0; i < studentList.getLength(); i++) {
            if (Integer.parseInt(studentList.getStudent(i).getS_number())
                    > Integer.parseInt(command.getCurstu().getS_number())) {
                studentList.getStudents().add(i, command.getCurstu());
                //更新版本号
                studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                writeStuFile(studentList);
                //储存撤销指令
                commands.push(new Command("delete", command.getCurstu(), command.getPrestu()));
                writeComFile(commands);
                return true;
            }
        }
        //更新版本号
        studentList.getStudent(studentList.getLength() - 1).
                setS_version(studentList.getStudent(studentList.getLength() - 1).getS_version() + 1);
        studentList.add(command.getCurstu());
        writeStuFile(studentList);
        //储存撤销指令
        commands.push(new Command("delete", command.getCurstu(), command.getPrestu()));
        writeComFile(commands);
        return true;
    }

    public boolean deleteStudent(StudentList studentList, Command command, Stack<Command> commands) {
        //遍历找到要删的记录
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //如果服务器端version<=客户端version，则可以更新数据
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    writeStuFile(studentList);
                    //储存撤销指令
                    commands.push(new Command("create", command.getCurstu(), command.getPrestu()));
                    writeComFile(commands);
                    return true;
                } else {
                    return false;
                }
            }
        }
        //未找到，说明该数据已被删除，返回false
        return false;
    }

    public boolean changeStudent(StudentList studentList, Command command, Stack<Command> commands) {
        //遍历找到要改的记录
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //如果服务器端version<=客户端version，则可以更新数据
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    studentList.getStudents().add(i, command.getCurstu());
                    //更新版本号
                    studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                    writeStuFile(studentList);
                    //储存撤销指令
                    commands.push(new Command("change", command.getCurstu(), command.getPrestu()));
                    writeComFile(commands);
                    return true;
                } else {
                    return false;
                }
            }
        }
        //未找到，说明该数据已被删除，返回false
        return false;
    }

    public void createStudent(StudentList studentList, Command command) {
        //如果已经有了该学生，则创建无效，返回false
        if (studentList.has(command.getCurstu())) {
            return;
        }
        for (int i = 0; i < studentList.getLength(); i++) {
            if (Integer.parseInt(studentList.getStudent(i).getS_number())
                    > Integer.parseInt(command.getCurstu().getS_number())) {
                studentList.getStudents().add(i, command.getCurstu());
                //更新版本号 服务器撤销不用更新
//                studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                writeStuFile(studentList);
                return;
            }
        }
        //更新版本号 服务器撤销不用更新
//        studentList.getStudent(studentList.getLength() - 1).
//                setS_version(studentList.getStudent(studentList.getLength() - 1).getS_version() + 1);
        studentList.add(command.getCurstu());
        writeStuFile(studentList);
    }

    public void deleteStudent(StudentList studentList, Command command) {
        //遍历找到要删的记录
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //如果服务器端version<=客户端version，则可以更新数据
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    writeStuFile(studentList);
                }
                return;
            }
        }
        //未找到，说明该数据已被删除，返回false
    }

    public void changeStudent(StudentList studentList, Command command) {
        //遍历找到要改的记录
        for (int i = 0; i < studentList.getLength(); i++) {
            if (studentList.getStudent(i).equals(command.getPrestu())) {
                //如果服务器端version<=客户端version，则可以更新数据
                if (studentList.getStudent(i).OptimisticLocking(command.getPrestu().getS_version())) {
                    studentList.getStudents().remove(i);
                    studentList.getStudents().add(i, command.getCurstu());
                    //更新版本号 服务器撤销不用更新
//                    studentList.getStudent(i).setS_version(studentList.getStudent(i).getS_version() + 1);
                    writeStuFile(studentList);
                }
                return;
            }
        }
        //未找到，说明该数据已被删除，返回false
    }

    //初始化界面
    public void initFrame(JFrame frame) {
        //设置关闭时退出程序
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //设置窗体的大小
        frame.setSize(320, 180);
        //设置窗口居中
        frame.setLocationRelativeTo(null);
        //设置不可缩放
        frame.setResizable(false);
        //设置背景图片
        ImageIcon image = new ImageIcon("StuInfoManage/src/resource/image/gui.jpg");
        JLabel background = new JLabel(image);
        frame.add(background);
        background.setBounds(0, 0, 320, 180);
        frame.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));
        ((JPanel) frame.getContentPane()).setOpaque(false); //设置透明

        frame.addComponentListener(new ComponentAdapter() {//让窗口响应大小改变事件
            @Override
            public void componentResized(ComponentEvent e) {
                int fraWidth = frame.getWidth();//获取面板宽度
                int fraHeight = frame.getHeight();//获取面板高度
                background.setBounds(0, 0, fraWidth, fraHeight);
            }
        });
    }

    public void beautifyFrame() {

        // 设置窗口的风格
        String outLookAndFeel = "com.jtattoo.plaf.luna.LunaLookAndFeel";
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(outLookAndFeel);
        } catch (Exception e) {
            System.out.println();
        }

        //改变系统默认字体
        Font font = new Font("幼圆", Font.BOLD, 15);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    //将撤销指令具体到指令的类型
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

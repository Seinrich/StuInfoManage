package back.server;

import back.data.Command;
import back.data.StudentList;
import back.utils.Utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        //监听端口号8088
        ServerSocket serverSocket = new ServerSocket(8088);
//        ExecutorService threadPool = new ThreadPoolExecutor(6, 12,
//                1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(6), Executors.defaultThreadFactory());
        //定容线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);
        //学生信息数据
        StudentList sl = Utils.getInstance().readStuFile();
        //撤销指令数据
        Stack<Command> cl = Utils.getInstance().readComFile();

//        //将学生信息读入文件
//        Utils.getInstance().writeStuFile(sl);
//        //将学生信息读入文件
//        Utils.getInstance().writeComFile(cl);

        //服务器端在控制台发送指令可进行回退（rollback回退一步,rollbackall回退到初始状态）
        fixedThreadPool.execute(new Thread(new Rollback(sl, cl), "thread" + 0));

        //服务器端图形化界面
        Utils.getInstance().beautifyFrame();

        int i = 1;
        while (true) {

            //socket对象创建
            Socket socket = serverSocket.accept();
            //输入输出流
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            //打印socket信息
            System.out.println(socket);

            //执行此线程
            fixedThreadPool.execute(new Thread(new Action(br, pw, sl, cl), "thread" + i));

            //给接收和反馈分别创建两个线程可能会造成反馈先于接收
//            //receive
//            fixedThreadPool.execute(new Thread(new ReceiveAction(br, sl, command), "thread" + i));
//            //feedback
//            fixedThreadPool.execute(new Thread(new FeedBackAction(pw, sl, command), "thread" + i + 1));

            //线程数++
            i++;
        }
    }

}
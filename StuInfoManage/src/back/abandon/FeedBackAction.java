package back.abandon;

import back.data.Command;
import back.data.StudentList;
import back.utils.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;

public class FeedBackAction implements Runnable {
    PrintWriter pw;
    StudentList sl;

    Command command;

    public FeedBackAction(PrintWriter pw, StudentList sl, Command command) {
        this.pw = pw;
        this.sl = sl;
        this.command = command;
    }

    public void feedback() throws IOException {
        Gson gson = new Gson();
        String content = gson.toJson("null");

        switch (command.getType()) {
            case "init" -> content = Utils.getInstance().FchangeCode(
                    Utils.getInstance().jsonSL(sl));
            case "create" -> content = gson.toJson("create");
            case "delete" -> content = gson.toJson("delete");
            case "change" -> content = gson.toJson("change");
        }
        System.out.println(command.getType());
        String html = "http/1.1 200 ok\r\n" +
                "Access-Control-Allow-Origin:*\r\n" +
                "Access-Control-Allow-Headers:*\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length:" + content.length() +
                "\r\n\r\n" + content;
        pw.println(html);
        pw.flush();

        System.out.println("return " + content.length() + "\n");
    }

    public void run() {
        try {
            feedback();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

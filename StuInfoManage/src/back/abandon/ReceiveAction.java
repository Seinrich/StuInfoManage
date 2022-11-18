package back.abandon;

import back.data.Command;
import back.data.StudentList;
import back.utils.Utils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiveAction implements Runnable {
    BufferedReader br;
    StudentList sl;

    Command command;

    public ReceiveAction(BufferedReader br, StudentList sl, Command command) {
        this.br = br;
        this.sl = sl;
        this.command = command;
    }

    public void receive() throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while (!(line = br.readLine()).startsWith("Host:")) {
            buffer.append(line);
        }
        Gson gson = new Gson();
        String json = Utils.getInstance().RchangeCode(
                StringUtils.substringBetween(buffer.toString(), "/?", " HTTP"));

        Command com = gson.fromJson(json, Command.class);
        command.setType(com.getType());
        command.setPrestu(com.getPrestu());
        command.setCurstu(com.getCurstu());

        System.out.println("receive " + json);
    }

    public void run() {
        try {
            receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


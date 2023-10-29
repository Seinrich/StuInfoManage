package back.server;

import back.data.Command;
import back.data.StudentList;
import back.gui.GUI;
import back.utils.Utils;

import java.util.Scanner;
import java.util.Stack;

public class Rollback implements Runnable {

    StudentList studentList;
    Stack<Command> commands;

    public Rollback(StudentList studentList, Stack<Command> commands) {
        this.studentList = studentList;
        this.commands = commands;
    }

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            if (in.hasNextLine()) {
                rollback(in.nextLine());
            }
        }
    }

    public void rollback(String command) {
        switch (command) {
            case "rollback" -> {
                if (!commands.empty()) {
                    Utils.getInstance().opCom(commands, studentList);
                    System.out.println("rollback success");
                } else {
                    System.out.println("rollback fail, stack commands is empty");
                }
            }
            case "rollbackall" -> {
                if (!commands.empty()) {
                    while (!commands.empty()) {
                        Utils.getInstance().opCom(commands, studentList);
                    }
                    System.out.println("rollbackall success");
                } else {
                    System.out.println("rollbackall fail, stack commands is empty");
                }
            }
            case "gui" -> {
                new GUI("·þÎñÆ÷¶Ë", commands, studentList);
                System.out.println("GUI has been created");
            }
            default -> System.out.println("incorrect command");
        }
        System.out.println();
    }
}

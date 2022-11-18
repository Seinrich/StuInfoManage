package back.gui;

import back.data.Command;
import back.data.StudentList;
import back.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class GUI extends JFrame implements ActionListener {

    JButton rollbackButton;
    JButton rollbackallButton;
    Stack<Command> commands;
    StudentList studentList;

    public GUI(String name, Stack<Command> commands, StudentList studentList) {
        super(name);
        this.commands = commands;
        this.studentList = studentList;

        //初始化界面
        Utils.getInstance().initFrame(this);

        //设置布局格式为弹簧式
        SpringLayout springLayout = new SpringLayout();
        this.getContentPane().setLayout(springLayout);

        rollbackButton = new JButton("撤销");
        rollbackButton.setToolTipText("撤销上一步成功提交到服务器的操作");
        rollbackallButton = new JButton("重置");
        rollbackallButton.setToolTipText("重置服务器数据");

        this.getContentPane().add(rollbackButton);
        springLayout.putConstraint(SpringLayout.NORTH, rollbackButton, 55, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, rollbackButton, 60, SpringLayout.WEST, this.getContentPane());

        this.getContentPane().add(rollbackallButton);
        springLayout.putConstraint(SpringLayout.NORTH, rollbackallButton, 55, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, rollbackallButton, -60, SpringLayout.EAST, this.getContentPane());

        //设置监听器
        rollbackallButton.addActionListener(this);
        rollbackButton.addActionListener(this);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(rollbackButton)) {
            ImageIcon icon = new ImageIcon("src/resource/image/NEU.png");
            if (!commands.empty()) {
                Utils.getInstance().opCom(commands, studentList);
                JOptionPane.showMessageDialog(null,
                        "撤销成功", "撤销", JOptionPane.ERROR_MESSAGE, icon);
                System.out.println("rollback success\n");
            } else {
                JOptionPane.showMessageDialog(null,
                        "已无操作可撤销", "撤销", JOptionPane.ERROR_MESSAGE, icon);
                System.out.println("rollback fail\n");
            }
        } else if (e.getSource().equals(rollbackallButton)) {
            ImageIcon icon = new ImageIcon("src/resource/image/NEU.png");
            if (!commands.empty()) {
                while (!commands.empty()) {
                    Utils.getInstance().opCom(commands, studentList);
                }
                JOptionPane.showMessageDialog(null,
                        "重置成功", "重置", JOptionPane.ERROR_MESSAGE, icon);
                System.out.println("rollbackall success\n");
            } else {
                JOptionPane.showMessageDialog(null,
                        "已无操作可撤销", "撤销", JOptionPane.ERROR_MESSAGE, icon);
                System.out.println("rollbackall fail\n");
            }
        }
    }
}

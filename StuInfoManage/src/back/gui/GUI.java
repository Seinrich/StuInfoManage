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

        //��ʼ������
        Utils.getInstance().initFrame(this);

        //���ò��ָ�ʽΪ����ʽ
        SpringLayout springLayout = new SpringLayout();
        this.getContentPane().setLayout(springLayout);

        rollbackButton = new JButton("����");
        rollbackButton.setToolTipText("������һ���ɹ��ύ���������Ĳ���");
        rollbackallButton = new JButton("����");
        rollbackallButton.setToolTipText("���÷���������");

        this.getContentPane().add(rollbackButton);
        springLayout.putConstraint(SpringLayout.NORTH, rollbackButton, 55, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, rollbackButton, 60, SpringLayout.WEST, this.getContentPane());

        this.getContentPane().add(rollbackallButton);
        springLayout.putConstraint(SpringLayout.NORTH, rollbackallButton, 55, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, rollbackallButton, -60, SpringLayout.EAST, this.getContentPane());

        //���ü�����
        rollbackallButton.addActionListener(this);
        rollbackButton.addActionListener(this);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(rollbackButton)) {
            if (!commands.empty()) {
                Utils.getInstance().opCom(commands, studentList);
                JOptionPane.showMessageDialog(null,
                        "�����ɹ�", "����", JOptionPane.ERROR_MESSAGE);
                System.out.println("rollback success\n");
            } else {
                JOptionPane.showMessageDialog(null,
                        "���޲����ɳ���", "����", JOptionPane.ERROR_MESSAGE);
                System.out.println("rollback fail\n");
            }
        } else if (e.getSource().equals(rollbackallButton)) {
            if (!commands.empty()) {
                while (!commands.empty()) {
                    Utils.getInstance().opCom(commands, studentList);
                }
                JOptionPane.showMessageDialog(null,
                        "���óɹ�", "����", JOptionPane.ERROR_MESSAGE);
                System.out.println("rollbackall success\n");
            } else {
                JOptionPane.showMessageDialog(null,
                        "���޲����ɳ���", "����", JOptionPane.ERROR_MESSAGE);
                System.out.println("rollbackall fail\n");
            }
        }
    }
}

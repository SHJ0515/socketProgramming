package Sender;

import javax.swing.*;
import java.awt.*;

public class SenderGUI {
    SMTPSender smtpSender = new SMTPSender();
    public JFrame frame = new JFrame();
    public JPanel senderPanel = new JPanel();

    public JLabel senderLabel = new JLabel(); // 메일 보내는 사람 담을 GUI
    public JTextField senderField = new JTextField();
    public JButton sendButton = new JButton();

    public JLabel receiverLabel = new JLabel(); // 메일 받는 사람 담을 GUI
    public JTextField receiverField = new JTextField();
    public JLabel SorF = new JLabel();

    public JLabel subjectLabel = new JLabel(); // 제목 입력 GUI
    public JTextField subjectField = new JTextField();
    public JLabel subjectLabel_right = new JLabel();

    public JLabel fileLabel = new JLabel(); // 파일 경로 입력 영역
    public JTextField filePathField = new JTextField();
    public JButton fileButton = new JButton();

    public JLabel contentLabel = new JLabel(); // 메일 본문 영역
    public JTextArea contentArea = new JTextArea();

    public GridBagLayout grid = new GridBagLayout();
    public GridBagConstraints gbc = new GridBagConstraints();

    SenderGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 닫기 버튼 누를 시 프로그램 종료 설정
        senderPanel.setLayout(grid); // 패널의 레이아웃을 gridBagLayout으로 설정한다
        gbc.fill = GridBagConstraints.HORIZONTAL; // 모든 요소를 가로 방향으로는 다 채운다

        senderPanel.setPreferredSize(new Dimension(800,500)); // 패널의 가로 세로 크기 픽셀 지정

        //////////////////////////////////////////////////// Sender 행 컴포넌트들
        // senderLabel 배치
        senderLabel.setText("Sender : ");
        makeLayout(senderLabel, 0, 0, 0.2,0.1);
        senderPanel.add(senderLabel);

        // senderField 배치
        makeLayout(senderField, 1, 0, 0.9, 0.1);
        senderPanel.add(senderField, gbc);

        // sendButton 배치
        sendButton.setText("Send");
        makeLayout(sendButton, 2, 0, 0.2, 0.1);
        senderPanel.add(sendButton, gbc);
        sendButton.addActionListener(new SenderEventHandler(this, this.smtpSender));
        //////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////// Receiver 행 컴포넌트들
        // recieverLabel 배치
        receiverLabel.setText("Receiver : ");
        makeLayout(receiverLabel, 0, 1, 0.2, 0.1);
        senderPanel.add(receiverLabel);

        // recieverField 배치
        makeLayout(receiverField,1,1,0.9,0.1);
        senderPanel.add(receiverField);

        // Success or Fail 버튼 배치
        SorF.setText("waiting..");
        makeLayout(SorF, 2,1,0.2,0.1);
        senderPanel.add(SorF);
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////// Subject 행 컴포넌트들
        // SubjectLabel 배치
        subjectLabel.setText("Subject : ");
        makeLayout(subjectLabel, 0, 2, 0.2, 0.1);
        senderPanel.add(subjectLabel);

        // subjectField 배치
        makeLayout(subjectField,1,2,0.9,0.1);
        senderPanel.add(subjectField);

        // sendButton 배치
        subjectLabel_right.setText("subject_test");
        makeLayout(subjectLabel_right, 2, 2, 0.2, 0.1);
        senderPanel.add(subjectLabel_right);

        /////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////// filePath행 컴포넌트들
        // filePath Label 배치
        fileLabel.setText("filePath : ");
        makeLayout(fileLabel,0,3,0.2,0.1);
        senderPanel.add(fileLabel);

        // filePath 입력하는 텍스트필드 배치
        makeLayout(filePathField,1,3,0.9,0.1);
        senderPanel.add(filePathField);

        // sendButton 배치
        fileButton.setText("add file");
        makeLayout(fileButton, 2, 3, 0.2, 0.1);
        senderPanel.add(fileButton);
        fileButton.addActionListener(new SenderEventHandler(this, this.smtpSender));
        ////////////////////////////////////////////////


        ////////////////////////////////////////////////////contentarea 행 컴포넌트들
        // contentLabel 배치
        contentLabel.setText("Content : ");
        makeLayout(contentLabel, 0, 4, 0.2, 0.1);
        senderPanel.add(contentLabel);

        // contentarea 배치
        makeLayout(contentArea, 1, 4, 0.9, 1.0);
        contentArea.setPreferredSize(new Dimension(400 ,300));
        senderPanel.add(contentArea);
        //////////////////////////////////////////////////////////////////////////

        frame.add(senderPanel);
        frame.pack();
        frame.setVisible(true);
    }

    // make함수를 내가 지정합니다.
    // jcomponent인 jbutton의 객체에 x,y의 좌표의 시작점에서 w,h 크기의 단추를 만듭니다
    public void makeLayout(JComponent c, int x, int y, double w, double h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = w;
        gbc.weighty = h;
        grid.setConstraints(c, gbc);
        // GridBagLayout의 GridBagConstraints의 set하는 방법
    }

    public static void main(String[] args) {
        new SenderGUI();
    }
}

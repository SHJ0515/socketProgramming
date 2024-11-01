package Sender;

import javax.swing.*;
import java.awt.*;

public class SenderGUI {
    private JFrame frame = new JFrame();
    private JPanel senderPanel = new JPanel();

    private JLabel senderLabel = new JLabel(); // 메일 보내는 사람 담을 GUI
    private JTextField senderField = new JTextField();
    private JButton sendButton = new JButton();

    private JLabel receiverLabel = new JLabel(); // 메일 받는 사람 담을 GUI
    private JTextField receiverField = new JTextField();
    private JLabel SorF = new JLabel();

    private JLabel subjectLabel = new JLabel(); // 제목 입력 GUI
    private JTextField subjectField = new JTextField();
    private JButton fileButton = new JButton();

    private JLabel contentLabel = new JLabel();
    private JTextArea contentArea = new JTextArea();

    private GridBagLayout grid = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();

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
        fileButton.setText("add file");
        makeLayout(fileButton, 2, 2, 0.2, 0.1);
        senderPanel.add(fileButton);
        /////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////contentarea 행 컴포넌트들
        // contentLabel 배치
        contentLabel.setText("Content : ");
        makeLayout(contentLabel, 0, 3, 0.2, 0.1);
        senderPanel.add(contentLabel);

        // contentarea 배치
        makeLayout(contentArea, 1, 3, 0.8, 0.8);
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

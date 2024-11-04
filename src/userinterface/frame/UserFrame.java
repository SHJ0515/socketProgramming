package userinterface.frame;

import userinterface.panel.EmailDisplay;
import userinterface.panel.EmailInfoDisplay;
import userinterface.panel.MailBoxDisplay;
import userinterface.panel.SenderGUI;

import javax.swing.*;
import java.awt.*;

public class UserFrame extends JFrame {
    private String email;
    private EmailDisplay emailDisplay;
    private MailBoxDisplay mailBoxDisplay;
    private EmailInfoDisplay emailInfo;
    private SenderGUI senderGUI;

    public UserFrame(String email) {
        this.email = email;

        setTitle("1 Group Email Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.lightGray);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9);
        int height = (int) (screenSize.height * 0.9);
        setSize(width, height);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        /// EmailDisplay 설정 - 왼쪽 상단 일부 영역
        emailDisplay = new EmailDisplay(this, email);
        gbc.gridx = 0; // (0, 0) 위치
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; // 왼쪽 상단 고정
        gbc.insets = new Insets(10, 10, 5, 5); // 패널 주변 여백 설정
        add(emailDisplay, gbc);

        // MailBoxDisplay 설정 - EmailDisplay 아래 위치
        mailBoxDisplay = new MailBoxDisplay(this);
        gbc.gridx = 0; // (0, 1) 위치
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH; // 수직, 수평 방향으로 확장
        gbc.insets = new Insets(5, 10, 10, 5);
        add(mailBoxDisplay, gbc);

        // EmailInfoDisplay 패널 설정 - 오른쪽 상단
        emailInfo = new EmailInfoDisplay(this, mailBoxDisplay);
        mailBoxDisplay.setEmailInfoDisplay(emailInfo); // MailBoxDisplay와 EmailInfoDisplay 연결
        gbc.gridx = 1; // (1, 0) 위치
        gbc.gridy = 0;
        gbc.weightx = 0.8; // 가로 확장 비율
        gbc.weighty = 0.4; // 세로 화장 비율
        gbc.insets = new Insets(10, 5, 5, 10);
        add(emailInfo, gbc);

        // SenderGUI 패널 설정 - EmailInfo 아래
        senderGUI = new SenderGUI(this);
        gbc.gridx = 1; // (1, 1) 위치
        gbc.gridy = 1;
        gbc.weighty = 0.6; // 세로 확장 비율
        gbc.insets = new Insets(5, 5, 10, 10);
        add(senderGUI, gbc);

        setVisible(true);
    }
}

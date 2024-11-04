package userinterface.panel;

import service.ConnectionManager;
import userinterface.frame.Main;
import userinterface.frame.UserFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EmailDisplay extends JPanel {
    private UserFrame userFrame;
    private String email;
    private JButton logoutButton;
    public EmailDisplay(UserFrame userFrame, String email) {
        this.userFrame = userFrame;
        this.email = email;

        initUI();
    }

    private void initUI() {
        // 패널 설정
        setLayout(new BorderLayout());
        setBackground(Color.white);

        // 이메일을 표시할 JLabel 생성
        JLabel emailLabel = new JLabel("Email: " + email);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setForeground(Color.BLUE);

        // 로그아웃 버튼 생성
        logoutButton = new JButton("로그 아웃");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(255, 69, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // 여백 추가

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // ConnectionManager를 통해 로그아웃 수행
                    ConnectionManager.getInstance().close();
                    JOptionPane.showMessageDialog(userFrame, "로그아웃합니다.");

                    // 로그아웃 후 로그인 화면으로 전환
                    Main main = new Main();
                    userFrame.dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(userFrame, "로그아웃 에러: " + ex.getMessage(),
                            "로그아웃 에러", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // 테두리 추가 (예: 라인 테두리)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 중앙에 이메일 라벨 추가
        add(emailLabel, BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(0, 15))); // 15픽셀 간격 추가
        add(logoutButton, BorderLayout.SOUTH);
    }

}
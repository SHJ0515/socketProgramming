package userinterface.panel;

import userinterface.frame.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class UserLoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public UserLoginPage(Main emailFrame) {

        // 배경색과 레이아웃 지정
        setBackground(Color.white);
        setLayout(new GridBagLayout());

        // 로그인 라벨 설정
        JLabel titleLabel = new JLabel("로그인");
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        /*
            사용자 입력 필드
            - email과 password는 30글자 제한
            - 데이터베이스에서 설정 필요
         */
        emailField = new JTextField(30);
        passwordField = new JPasswordField(30);

        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        // 로그인 버튼 설정
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 각 컴포넌트 주변 여백(상, 좌, 하, 우)
        gbc.fill = GridBagConstraints.HORIZONTAL; // 컴포넌트 화면 크기에 맞춰 수평 확장

        /*
            로그인 라벨 배치
         */
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipady = 20; // 높이 조정
        add(titleLabel, gbc);

        /*
            Email 라벨 배치
         */
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.ipady = 10; // 높이 조정
        add(emailLabel, gbc);

        /*
            Email 입력 필드 배치
         */
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipady = 15; // 높이 조정
        add(emailField, gbc);

        /*
            Password 라벨 배치
         */
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.ipady = 10; // 높이 조정
        add(passwordLabel, gbc);

        /*
            Password 입력 필드 배치
         */
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipady = 15; // 높이 조정
        add(passwordField, gbc);

        /*
            로그인 버튼 배치
         */
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipady = 20; // 버튼 높이 조정
        add(loginButton, gbc);

        // 로그인 버튼 이벤트 처리
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();

                // JPasswordField는 보안상 char[] 배열로 반환하기 때문에 문자열로 변환
                String password = new String(passwordField.getPassword());

                // EmailFrame에서 인증 로직 수행
                boolean loginSuccess = false;
                try {
                    loginSuccess = emailFrame.authenticate(email, password);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if(loginSuccess) {
                    emailFrame.showUserFrame(email, password);
                }
            }
        });

        // 엔터 키로 로그인 버튼 클릭
        KeyListener enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };
        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // 포커스 설정
        requestFocus();
        setFocusable(true);
    }
}

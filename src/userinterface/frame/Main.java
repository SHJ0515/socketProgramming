package userinterface.frame;

import service.IMAPLogin;
import userinterface.panel.UserLoginPage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {
    private UserLoginPage emailLoginPage;

    public Main() {
        setTitle("1 Group Email Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
            노트북의 너비와 높이에 맞게 실행창 조절
         */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9);
        int height = (int) (screenSize.height * 0.9);
        setSize(width, height);
        setLocationRelativeTo(null);

        emailLoginPage = new UserLoginPage(this);
        add(emailLoginPage);
        emailLoginPage.setVisible(true);
        setVisible(true);
    }

    /*
        인증 메서드
        UserLoginPate에서 아이디와 비밀번호를 받아 처리
     */
    public boolean authenticate(String email, String password) throws IOException {
        boolean loginSuccess = false;

        // 이메일이 @naver.com 도메일을 가질 경우 IMAPLogin의 login() 메서드를 통해 처리
        if(email.endsWith(("@naver.com"))) {
            IMAPLogin naverLogin = new IMAPLogin(email, password);
            loginSuccess = naverLogin.login();
        }
        /*
            다른 이메일 서버
         */
        else {
            JOptionPane.showMessageDialog(emailLoginPage, "지원되지 않는 이메일입니다.");
            return false;
        }

        // 로그인 결과에 따라 메시지 표시
        if (loginSuccess) {
            JOptionPane.showMessageDialog(emailLoginPage, "로그인합니다.");
            return true;
        }
        else {
            JOptionPane.showMessageDialog(emailLoginPage, "Email이나 Password 정보가 맞지 않습니다.");
            return false;
        }
    }

    // 로그인 성공 후 UserLoginPage에서 호출하여 UserFrame으로 변환
    public void showUserFrame(String email, String password) {
        UserFrame userFrame = new UserFrame(email);
        emailLoginPage.setVisible(false);
        this.dispose();
    }

    public static void main(String[] args) {
        new Main();
    }
}

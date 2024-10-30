import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
/*
    ** RFC 3501 **
    LOGIN: 6.2.3절

    Arguments:  user name, password
    Responses:  no specific responses for this command
    Result:     OK - login completed, now in authenticated state
                NO - login failure: user name or password rejected
                BAD - command unknown or arguments invalid

    Example : C: a001 LOGIN SMITH SESAME
              S: a001 OK LOGIN completed
 */
public class IMAPLogin {
    private String email;
    private String password;
    private String server;
    private int port;

    public IMAPLogin(
            String email, String password,
            String server, int port
    ) {
        this.email = email;
        this.password = password;
        this.server = server;
        this.port = port;
    }

    public boolean login() {
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port);
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 서버 초기 응답 읽기
                System.out.println("서버 초기 응답: " + reader.readLine());

                // 로그인 명령어 전송
                writer.println("a1 LOGIN " + email + " " + password);
                System.out.println("a1 LOGIN " + email + " " + password);

                // 응답에서 "OK"를 찾으면 성공으로 판단
                // 로그인 응답 확인
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("서버 응답: " + line);
                    if (line.contains("a1 OK")) { // 로그인 완료 메시지 확인
                        break;
                    }
                    if (line.contains("NO")) { // 로그인 실패 메시지 확인
                        System.out.println("로그인 실패: 아이디 또는 비밀번호 오류");
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        // 네이버 IMAP 서버 테스트
//        String naverEmail = "본인의 이메일";
//        String naverPassword = "본인의 비밀번호(2차 인증 활성화한 경우 앱 비밀번호를 알아야함)";
//        IMAPLogin naverLogin = new IMAPLogin(naverEmail, naverPassword, "imap.naver.com", 993);
//        naverLogin.login();

        // Gmail IMAP 서버 테스트
        /*
            gmail은 2차 인증을 활성한 후에 무조건 앱 비밀번호로 접근해야 가능
         */
        String gmailEmail = "audtnwldnjs@gmail.com";
        String gmailPassword = "mbfiafsahunskzyu";
        IMAPLogin gmailLogin = new IMAPLogin(gmailEmail, gmailPassword, "imap.gmail.com", 993);
        gmailLogin.login();
    }
}

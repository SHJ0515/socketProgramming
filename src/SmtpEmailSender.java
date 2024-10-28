import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class SmtpEmailSender {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final int SMTP_PORT = 465; // SSL 포트

    public static void main(String[] args) {
        String username = "hoo5152000@gmail.com";
        String password = "aaid pdlq wqbp fndu"; // 앱 비밀번호 사용
        String toEmail = "hoo5152000@naver.com";
        String subject = "Test Email";
        String body = "Hello, this is a test email sent from Java!";

        try {
            // 1. SSL 소켓 생성 및 서버 연결
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) factory.createSocket(SMTP_SERVER, SMTP_PORT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream()), true);

            // 2. 서버 응답 확인
            printResponse(reader); // 초기 서버 응답

            // 3. EHLO 메시지 전송
            writer.println("EHLO " + SMTP_SERVER);
            printResponse(reader); // EHLO 응답 읽기

            // 4. AUTH LOGIN 명령 전송 및 인증
            writer.println("AUTH LOGIN");
            printResponse(reader);

            // 5. MAIL FROM 명령 전송
            writer.println("MAIL FROM:<" + username + ">");
            printResponse(reader);

            // 6. RCPT TO 명령 전송
            writer.println("RCPT TO:<" + toEmail + ">");
            printResponse(reader);

            // 7. DATA 명령 전송
            writer.println("DATA");
            printResponse(reader);

            // 8. 이메일 헤더와 본문 전송 (CRLF로 구분)
            writer.println("From: " + username);
            writer.println("To: " + toEmail);
            writer.println("Subject: " + subject);
            writer.println("Content-Type: text/plain; charset=utf-8");
            writer.println(); // 헤더와 본문 사이의 빈 줄
            writer.println(body);
            writer.println(".");
            printResponse(reader);

            // 9. QUIT 명령으로 연결 종료
            writer.println("QUIT");
            printResponse(reader);

            // 리소스 정리
            writer.close();
            reader.close();
            sslSocket.close();

            System.out.println("Email sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SMTP 서버의 응답을 출력하는 메서드
    private static void printResponse(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Response: " + line);
            if (line.startsWith("220") || line.startsWith("250") || line.startsWith("334") ||
                    line.startsWith("235") || line.equals(".")) {
                break; // 중요한 응답 코드 도달 시 종료
            }
        }
    }
}


/*
import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SmtpEmailSender {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final int SMTP_PORT = 587; // TLS 포트

    public static void main(String[] args) {
        String username = "hoo5152000@gmail.com";
        String password = "aaid pdlq wqbp fndu"; // 앱 비밀번호 사용
        String toEmail = "hoo5152000@naver.com";
        String subject = "Test Email";
        String body = "Hello, this is a test email sent from Java!";

        try {
            // 1. 기본 소켓 연결
            Socket socket = new Socket(SMTP_SERVER, SMTP_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // 서버 응답 확인
            System.out.println("Response: " + reader.readLine());

            // 2. EHLO 전송
            writer.println("EHLO " + SMTP_SERVER);
            printResponse(reader); // 서버 응답 확인

            // 3. STARTTLS 명령 전송
            writer.println("STARTTLS");
            printResponse(reader); // TLS 시작 응답 확인

            // 4. 기존 소켓을 SSLSocket으로 업그레이드
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(SMTP_SERVER, SMTP_PORT);
            sslSocket.startHandshake(); // TLS 핸드셰이크 수행

            // TLS로 업그레이드된 소켓으로 새로운 Reader/Writer 생성
            BufferedReader sslReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter sslWriter = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream()), true);

            // 5. AUTH LOGIN 전송
            sslWriter.println("AUTH LOGIN");
            printResponse(sslReader);

            // 6. 사용자명과 비밀번호를 Base64로 인코딩하여 전송
            sslWriter.println(encodeBase64(username));
            printResponse(sslReader);
            sslWriter.println(encodeBase64(password));
            printResponse(sslReader);

            // 7. 메일 전송 명령
            sslWriter.println("MAIL FROM: <" + username + ">");
            printResponse(sslReader);

            sslWriter.println("RCPT TO: <" + toEmail + ">");
            printResponse(sslReader);

            sslWriter.println("DATA");
            printResponse(sslReader);

            // 8. 이메일 본문 전송
            sslWriter.println("Subject: " + subject);
            sslWriter.println();
            sslWriter.println(body);
            sslWriter.println(".");
            printResponse(sslReader);

            // 9. 연결 종료
            sslWriter.println("QUIT");
            printResponse(sslReader);

            // 리소스 정리
            sslWriter.close();
            sslReader.close();
            sslSocket.close();
            socket.close();

            System.out.println("Email sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 응답 읽기 및 출력
    private static void printResponse(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Response: " + line);
            if (line.startsWith("220") || line.startsWith("250") || line.startsWith("235") || line.equals(".")) {
                break; // SMTP 응답 완료
            }
        }
    }

    // Base64 인코딩 메서드
    private static String encodeBase64(String value) {
        return java.util.Base64.getEncoder().encodeToString(value.getBytes());
    }
}
*/


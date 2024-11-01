package Sender;// SMTPServer.java
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;
import java.util.Base64;

public class SMTPSender {
    public static void sendMail(
            String smtpServer, String sender, String recipient, String content, String password)
            throws Exception {

        // STMP서버에 소켓 연결
        Socket socket = new Socket(smtpServer, 587); // 소켓 생성

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 서버로부터 데이터 받는 버퍼 생성
        PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true); // 클라이언트에서 서버로 데이터 전송하는 버퍼 생성
        System.out.println("서버에 연결되었습니다.");

        String line = inFromServer.readLine();
        System.out.println("응답:" + line);
        if(!line.startsWith("220")) {
            //예외처리
            throw new Exception("SMTP 서버가 아닙니다");
        }

        System.out.println("HELO 명령을 전송합니다.");
        outToServer.print("HELO wndrhdyd8070@naver.com" + "\r\n");
        outToServer.flush();
        String lineFromServer = inFromServer.readLine();
        System.out.println("응답:" + lineFromServer);
        if(!lineFromServer.startsWith("250")) {
            throw new Exception("HELO 실패했습니다:" + lineFromServer);
        }

        System.out.println("STARTTLS 명령을 전송합니다.");
        outToServer.print("STARTTLS" + "\r\n");
        outToServer.flush();
        lineFromServer = inFromServer.readLine();
        System.out.println("응답:" + lineFromServer);
        if(!lineFromServer.startsWith("220")) {
            throw new Exception("STARTTLS 실패했습니다:" + lineFromServer);
        }

        // TLS로 업그레이드
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, smtpServer, 587, true);

        // TLS 1.2 또는 TLS 1.3을 사용하도록 설정
        sslSocket.setEnabledProtocols(new String[] {"TLSv1.2", "TLSv1.3"});

        // TLS 연결된 소켓으로 스트림을 재설정
        inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        outToServer = new PrintWriter(sslSocket.getOutputStream(), true);

        // AUTH LOGIN 명령을 통한 인증
        System.out.println("AUTH LOGIN 명령을 전송합니다.");
        outToServer.print("AUTH LOGIN\r\n");
        outToServer.flush();
        System.out.println("응답: " + inFromServer.readLine());

        // 사용자 이름과 비밀번호를 Base64로 인코딩하여 전송
        String encodedUsername = Base64.getEncoder().encodeToString(sender.getBytes());
        outToServer.print(encodedUsername + "\r\n");
        outToServer.flush();
        System.out.println("응답: " + inFromServer.readLine());

        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        outToServer.print(encodedPassword + "\r\n");
        outToServer.flush();
        System.out.println("응답: " + inFromServer.readLine());

        System.out.println("MAIL FROM 명령을 전송합니다.");
        outToServer.print("MAIL FROM:<" + sender + ">\r\n");
        outToServer.flush();
        String lineFromServer_3 = inFromServer.readLine();
        System.out.println("응답:" + lineFromServer_3);
        if (!lineFromServer_3.startsWith("250")) {
            throw new Exception("MAIL FROM 실패했습니다:" + lineFromServer_3);
        }

        System.out.println("RCPT 명령을 전송합니다.");
        outToServer.print("RCPT TO:<" + recipient + ">\r\n");
        outToServer.flush();
        line = inFromServer.readLine();
        System.out.println("응답:" + line);
        if(!line.startsWith("250")) {
            throw new Exception("RCPT TO 에서 실패했습니다:" + line);
        }

        System.out.println("DATA 명령을 전송합니다.");
        outToServer.print("DATA\r\n");
        outToServer.flush();
        line = inFromServer.readLine();
        System.out.println("응답:" + line);
        if(!line.startsWith("354")) {
            throw new Exception("DATA 에서 실패했습니다:" + line);
        }

        // 메일 본문 전송
        System.out.println("본문을 전송합니다.");
        outToServer.print(content + "\r\n"); // 본문 내용 전송
        outToServer.print(".\r\n"); // 본문 끝을 나타내는 점 한 줄 전송
        outToServer.flush();
        line = inFromServer.readLine();
        System.out.println("응답: " + line);
        if (!line.startsWith("250")) {
            throw new Exception("내용 전송에서 실패했습니다: " + line);
        }

        System.out.println("접속 종료합니다.");
        outToServer.println("quit");

        outToServer.close();
        inFromServer.close();
        socket.close();
    }

    public static void main(String args[]) {
        try {
            // RFC 5322 형식의 이메일 콘텐츠 작성
            String from = "wndrhdyd8070@naver.com"; // 여기에 보내는 사람 이메일을 넣으세요
            String to = "drumchris@naver.com"; // 여기에 받는사람 이메일을 넣으세요
            String subject = "Hello chanwoo"; // 메일 제목을 입력하세요
            String smtpServer = "smtp.naver.com";

            String body = "Hello chanwoo,\r\n"
                    + "\r\n"
                    + "I've succeed sending file\r\n"
                    + "Please check file\r\n";

            // RFC 5322 형식에 맞게 헤더와 본문을 생성
            // MailHeader mailHeaderObj = new MailHeader(from, to, subject, "text/plain"); // "text/plain" 또는 "multipart/mixed" 로 설정
            MailHeader mailHeaderObj = new MailHeader(from, to, subject, "multipart/mixed");
            //MailContent contentObj = new MailContent(body,false);
            MailContent contentObj = new MailContent(body, false, "/Users/sejunkwon/Hello world.docx");
            String content = mailHeaderObj.header + contentObj.result;
            System.out.println(content);

            Personal_Info infoObj = new Personal_Info();

            SMTPSender.sendMail(
                    smtpServer, // smtp 서버 설정
                    infoObj.from, // sender 주소 설정
                    to, // 받는사람 주소 설정
                    content,
                    infoObj.password // sender 메일주소의 비밀번호를 넣으세요
            );
            System.out.println("==========================");
            System.out.println("메일이 전송되었습니다.");
        } catch(Exception e) {
            System.out.println("==========================");
            System.out.println("메일이 발송되지 않았습니다.");
            System.out.println(e.toString());
        }
    }
}

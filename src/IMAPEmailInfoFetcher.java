package service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import static util.Decoder.decodeHeaderInfo;

public class IMAPEmailInfoFetcher {
    String email, password, emailID, server, boxType;
    int port;

    public IMAPEmailInfoFetcher(
            String email, String password, String emailID,
            String server, int port, String boxType
    ) {
        this.email = email;
        this.password = password;
        this.emailID = emailID;
        this.server = server;
        this.port = port;
        this.boxType = boxType;
    }

    public static void main(String[] args) {
        // 네이버 IMAP 서버 테스트
        String naverEmail = "audtn0099@naver.com";
        String naverPassword = "msjw0706";
        String emailID = "1";
        String server = "imap.naver.com";
        int port = 993;
        String boxType = "INBOX";

        for (int i = 1000; i >= 990; i--) {
            IMAPEmailInfoFetcher naverEmailInfoFetcher = new IMAPEmailInfoFetcher(
                    naverEmail, naverPassword, Integer.toString(i),
                    server, port, boxType
            );
            naverEmailInfoFetcher.fetchEmailInfo();
        }
    }

    /*
        특정 메일의 발신자 이메일, 수신 날짜, 메일 제목, 메일 내용 가져오기
     */
    public void fetchEmailInfo() {
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port);
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("\n************* " + emailID + " ID 이메일 *************\n");
                /*
                    서버 초기 응답
                 */
                System.out.println("S: " + reader.readLine());

                /*
                    로그인 명령어 전송 및 응답 확인
                  */
                writer.println("a1 LOGIN " + email + " " + password);
                System.out.println("C: a1 LOGIN " + email + " " + password);

                System.out.println("S: " + reader.readLine());

                /*
                    메일함 선택
                    EX) INBOX는 수신메일함, SENT는 발신메일함
                 */
                String line;
                writer.println("a2 SELECT " + boxType);
                System.out.println("C: a2 SELECT " + boxType);
                while ((line = reader.readLine()) != null) {
                    System.out.println("S: " + line);
                    if (line.contains("a2 OK")) {
                        break;
                    }
                }


                /*
                    이메일 헤더 정보 (FROM, DATE, SUBJECT) 가져오기
                 */
                writer.println("a4 FETCH " + emailID + " (BODY[HEADER.FIELDS (FROM DATE SUBJECT)])");
                System.out.println("C: a4 FETCH " + emailID + " (BODY[HEADER.FIELDS (FROM DATE SUBJECT)])");

                String from = null;
                String date = null;
                String[] encodedSubjectParts = null;
                String decodedSubjectPart = null;
                StringBuilder encodedSubjectBuilder = new StringBuilder();
                StringBuilder decodedSubjectBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    System.out.println("S: " + line);
                    if (line.contains("a4 OK"))
                        break;

                    if (line.startsWith("FROM:")) {
                        from = line.substring(5).trim();
                    } else if (line.startsWith("DATE:")) {
                        date = line.substring(5).trim();
                    } else if (line.startsWith("SUBJECT:")) {
                        encodedSubjectParts = line.substring(8).trim().split(" ");
                        for(String encodedSubjectpart : encodedSubjectParts) {
                            encodedSubjectBuilder.append(encodedSubjectpart).append(" ");
                        }
                    }
                }
                System.out.println("\n** 메일 헤더 정보(디코딩 전)================================");
                System.out.println("From: " + from);
                System.out.println("Date: " + date);
                System.out.println("Subject: " + encodedSubjectBuilder);
                System.out.println("========================================================\n");


                for(String encodedSubjectpart : encodedSubjectParts) {
                    decodedSubjectPart = decodeHeaderInfo(encodedSubjectpart);
                    decodedSubjectBuilder.append(decodedSubjectPart);
                }

                System.out.println("\n** 메일 헤더 정보(디코딩 후)================================");
                System.out.println("From: " + decodeHeaderInfo(from));
                System.out.println("Date: " + decodeHeaderInfo(date));
                System.out.println("Subject: " + decodedSubjectBuilder);
                System.out.println("========================================================\n");



                /*
                    르그아웃
                 */
                writer.println("a6 LOGOUT\r\n");
                System.out.println("C: a6 LOGOUT");

                System.out.println("S: " + reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

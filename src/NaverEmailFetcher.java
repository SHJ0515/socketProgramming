import java.io.*;
import java.net.*;
import java.util.Base64;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class NaverEmailFetcher {
    private static final String HOST = "imap.naver.com";  // 네이버 IMAP 서버
    private static final int PORT = 993;  // SSL 포트

    public static void main(String[] args) {
        try {
            // 1. SSL 소켓 생성 및 서버 연결
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(HOST, PORT);

            // 2. 서버와의 입출력 스트림 설정
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 3. 서버 초기 응답 확인
            System.out.println("S: " + reader.readLine());

            // 4. 로그인 요청 (앱 비밀번호 사용)
            String username = "본인 아이디@naver.com";  // 네이버 이메일 주소
            String password = "본인 비밀번호";  // 2차인증 시 앱 비밀번호
            writer.write("A001 LOGIN " + username + " " + password + "\r\n");
            writer.flush();
            System.out.println("C: A001 LOGIN " + username + " ********");

            // 5. 로그인 응답 확인
            System.out.println("S: " + reader.readLine());

            // 6. INBOX 선택
            writer.write("A002 SELECT INBOX\r\n");
            writer.flush();
            System.out.println("C: A002 SELECT INBOX");

            // 7. INBOX 응답 확인
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("S: " + line);  // 응답 출력
                if (line.startsWith("A002 OK")) break;  // INBOX 선택 완료
            }

            // 8. 안 읽은 메일 검색 (UNSEEN)
            writer.write("A003 SEARCH UNSEEN\r\n");
            writer.flush();
            System.out.println("C: A003 SEARCH UNSEEN");

            // 9. SEARCH 명령 응답 읽기 및 메일 ID 추출
            StringBuilder searchResultBuilder = new StringBuilder();
            System.out.println("----- SEARCH 명령 응답 시작 -----");
            while ((line = reader.readLine()) != null) {
                System.out.println("S: " + line);  // 응답 줄 출력

                // * SEARCH로 시작하는 줄을 누적
                if (line.startsWith("* SEARCH")) {
                    searchResultBuilder.append(line).append(" ");
                }

                // 응답 끝을 확인
                if (line.startsWith("A003 OK")) break;
            }
            System.out.println("----- SEARCH 명령 응답 끝 -----");

            // SEARCH 응답에서 첫 번째 메일 ID 추출
            String searchResult = searchResultBuilder.toString().trim();
            String[] parts = searchResult.split(" ");
            if (parts.length < 3) {  // "SEARCH" 다음에 ID가 있어야 함
                System.out.println("안 읽은 메일이 없습니다.");
                socket.close();
                return;
            }
            String firstMailId = parts[2];  // 첫 번째 메일 ID 추출
            System.out.println("첫 번째 안 읽은 메일 ID: " + firstMailId);

            // 10. 첫 번째 메일의 본문 요청
            writer.write("A004 FETCH " + firstMailId + " BODY[TEXT]\r\n");
            writer.flush();
            System.out.println("C: A004 FETCH " + firstMailId + " BODY[TEXT]");

            /*
            // 11. 메일 본문 읽기 및 디코딩
            StringBuilder encodedBody = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("A004 OK")) break;
                encodedBody.append(line).append("\n");
            }

            // 메일 본문을 Base64로 디코딩
            String decodedBody = decodeBase64(encodedBody.toString());
            System.out.println("----- 디코딩된 메일 본문 -----");
            System.out.println(decodedBody);
            */


            // 11. 메일 본문 출력
            System.out.println("----- 메일 본문 시작 -----");
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("A004 OK")) break;  // 본문 끝
                System.out.println(line);
            }
            System.out.println("----- 메일 본문 끝 -----");


            // 12. 로그아웃
            writer.write("A005 LOGOUT\r\n");
            writer.flush();
            System.out.println("C: A005 LOGOUT");

            // 로그아웃 응답 확인
            System.out.println("S: " + reader.readLine());

            // 소켓 닫기
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Base64 디코딩 메서드
    public static String decodeBase64(String encoded) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(encoded);
        return new String(decodedBytes);
    }
}



/*
import java.io.*;
import java.net.Socket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;

public class NaverEmailFetcher {
    private static final String SERVER = "imap.naver.com";
    private static final int PORT = 993; // IMAP SSL Port

    public static void main(String[] args) {
        try {
            // SSL 연결 설정
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    }
            }, new SecureRandom());

            SSLSocketFactory factory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(SERVER, PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // 서버로부터 응답 받기
                String response = in.readLine();
                System.out.println("Server: " + response);

                // 로그인 과정
                String username = "hoo5152000@naver.com"; // 이메일 주소
                String password = "ejddl20011556!"; // 비밀번호
                out.println("A001 LOGIN " + username + " " + password);
                response = in.readLine();
                System.out.println("Server: " + response);

                // INBOX 선택
                out.println("A002 SELECT INBOX");
                response = in.readLine();
                System.out.println("Server: " + response);

                // INBOX의 메일 수 확인
                out.println("A003 SEARCH ALL");
                response = in.readLine();
                System.out.println("Total emails in INBOX: " + response); // 총 메일 수 확인

                // 안 읽은 메일 검색
                out.println("A004 SEARCH UNSEEN");
                response = in.readLine();
                System.out.println("Server: " + response); // 응답을 확인

                // 안 읽은 메일 ID 파싱
                if (response.startsWith("* SEARCH")) {
                    String[] parts = response.split(" ");
                    if (parts.length > 2) { // ID가 존재하는 경우
                        String firstUnseenId = parts[2]; // 첫 번째 안 읽은 메일 ID
                        System.out.println("First unseen email ID: " + firstUnseenId);

                        // 첫 번째 안 읽은 메일 내용 가져오기
                        out.println("A005 FETCH " + firstUnseenId + " BODY[]");
                        response = in.readLine();
                        System.out.println("Server: " + response); // 메일 내용 출력
                    } else {
                        System.out.println("No unseen emails found.");
                    }
                } else {
                    System.out.println("No unseen emails found.");
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace(); // 다른 예외 처리
        }
    }
}
*/
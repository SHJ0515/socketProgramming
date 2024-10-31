/*
import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ShowNaverEmailList {
    private static final String HOST = "imap.naver.com";  // 네이버 IMAP 서버
    private static final int PORT = 993;  // SSL 포트
    private String id;                    // 네이버 아이디
    private String password;              // 네이버 비밀번호
    private String flag;                  //
    private String boxType;

    // private SSLSocket socket;
    // private BufferReader buffer;
    // private BufferWriter writer;
    // 이렇게 하고 메소드가 return ShowNaverEmailList 하게?

    public ShowNaverEmailList(String id, String password, String boxType, String flag) {
        this.id = id;
        this.password = password;
        this.boxType = boxType;
        this.flag = flag;
        returnList();
    }

    // String 배열이 아니라 모든 정보를 가지고 있는 ShowNaverEmailList 객체 자체를 return 하게 변경?
    // >>> socket, BufferReader, BufferWriter, boxType, flag, String[] 등등을 다 넘겨버려서 뒤에서 편히 처리할 수 있게?
    // 메일 박스마다 중복된 id가 있을 수 있다. e.g. 보낸 메일함의 id 12인 메일, 받은 메일함의 id 12인 메일이 공존 가능 -> boxType도 알아야 하거나 socket 통신
    // 진행 과정을 통째로 넘겨야 할 듯?

    public String[] returnList() {
        String[] b = new String[0];
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
            writer.write("A001 LOGIN " + id + " " + password + "\r\n");
            writer.flush();
            System.out.println("C: A001 LOGIN " + id + " ********");

            // 5. 로그인 응답 확인
            System.out.println("S: " + reader.readLine());

            // 6. BOX 선택  -> private String boxType 에 의해 결정.
            writer.write("A002 SELECT " + boxType + "\r\n");
            writer.flush();
            System.out.println("C: A002 SELECT " + boxType);

            // 7. BOX 응답 확인
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("S line: " + line);  // 응답 출력
                if (line.startsWith("A002 OK")) break;  // BOX 선택 완료
            }

            // 8. 메일 검색 (flag)
            writer.write("A003 SEARCH " + flag + "\r\n");
            writer.flush();
            System.out.println("C: A003 SEARCH " + flag);

            // 9. SEARCH 명령 응답 읽기 및 메일 ID 추출
            StringBuilder searchResultBuilder = new StringBuilder();
            System.out.println("----- SEARCH 명령 응답 시작 -----");
            while ((line = reader.readLine()) != null) {
                System.out.println("S searchLine: " + line);  // 응답 줄 출력

                // * SEARCH로 시작하는 줄을 누적
                if (line.startsWith("* SEARCH")) {
                    searchResultBuilder.append(line).append(" ");
                }

                // 응답 끝을 확인
                if (line.startsWith("A003 OK")) break;
            }
            System.out.println("----- SEARCH 명령 응답 끝 -----");

            //search 응답 String[]으로 가져오기.
            String searchResult = searchResultBuilder.toString().trim();
            String[] parts = searchResult.split(" ");
            System.out.println(parts.length);
            for (String part : parts) {
                System.out.println("S part = " + part);
            }

            // 마지막 100개 요소 또는 전체 요소를 복사 -> 가장 최신 메일 순으로 100개 가져오기.
            int length = parts.length;
            int startIndex = Math.max(0, length - 100);  // 마지막 100개 또는 시작 인덱스
            int size = length - startIndex;  // 복사할 요소 수


            // b 배열 생성 및 요소 복사  -> 최신메일 100개 리턴
            b = new String[size];
            for (int i = 0; i < size; i++) {
                b[i] = parts[length - 1 - i];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;   // 수정 필요
    }
}
 */

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ShowNaverEmailList {
    private static final String HOST = "imap.naver.com";  // 네이버 IMAP 서버
    private static final int PORT = 993;  // SSL 포트
    private String id;                    // 네이버 아이디
    private String password;              // 네이버 비밀번호
    private String flag;
    private String boxType;
    private String[] emailIds;            // 메일 ID 리스트를 저장할 필드
    private SSLSocket socket;             // 소켓 필드로 선언
    private BufferedReader reader;        // BufferedReader 필드로 선언
    private BufferedWriter writer;        // BufferedWriter 필드로 선언

    public ShowNaverEmailList(String id, String password, String boxType, String flag) {
        this.id = id;
        this.password = password;
        this.boxType = boxType;
        this.flag = flag;
        this.returnList();
    }

    // returnList 메서드에서 ShowNaverEmailList 객체를 반환하도록 변경
    public void returnList() {
        try {
            // 1. SSL 소켓 생성 및 서버 연결
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            this.socket = (SSLSocket) factory.createSocket(HOST, PORT);

            // 2. 서버와의 입출력 스트림 설정
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 3. 서버 초기 응답 확인
            System.out.println("S: " + reader.readLine());

            // 4. 로그인 요청
            writer.write("A001 LOGIN " + id + " " + password + "\r\n");
            writer.flush();
            System.out.println("C: A001 LOGIN " + id + " ********");

            // 5. 로그인 응답 확인
            System.out.println("S: " + reader.readLine());

            // 6. 메일함 선택
            writer.write("A002 SELECT " + boxType + "\r\n");
            writer.flush();
            System.out.println("C: A002 SELECT " + boxType);

            // 7. 메일함 응답 확인
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("S line: " + line);
                if (line.startsWith("A002 OK")) break;
            }

            // 8. 메일 검색
            writer.write("A003 SEARCH " + flag + "\r\n");
            writer.flush();
            System.out.println("C: A003 SEARCH " + flag);

            // 9. SEARCH 명령 응답 읽기 및 메일 ID 추출
            StringBuilder searchResultBuilder = new StringBuilder();
            System.out.println("----- SEARCH 명령 응답 시작 -----");
            while ((line = reader.readLine()) != null) {
                System.out.println("S searchLine: " + line);
                if (line.startsWith("* SEARCH")) {
                    searchResultBuilder.append(line).append(" ");
                }
                if (line.startsWith("A003 OK")) break;
            }
            System.out.println("----- SEARCH 명령 응답 끝 -----");

            // 검색 결과를 배열로 저장
            String searchResult = searchResultBuilder.toString().trim();
            String[] parts = searchResult.split(" ");
            int length = parts.length;
            int startIndex = Math.max(0, length - 100);
            int size = length - startIndex;

            // emailIds 필드에 최신 100개의 메일 ID를 저장
            emailIds = new String[size];
            for (int i = 0; i < size; i++) {
                emailIds[i] = parts[length - 1 - i];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 소켓을 종료하는 메서드 (추가)
    public void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getFlag() {
        return flag;
    }

    public String getBoxType() {
        return boxType;
    }

    public String[] getEmailIds() {
        return emailIds;
    }

    public SSLSocket getSocket() {
        return socket;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public BufferedWriter getWriter() {
        return writer;
    }
}

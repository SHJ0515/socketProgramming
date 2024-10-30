import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
                  
    SELECT: 6.3.1절
        Arguments:  mailbox name

        Responses:  REQUIRED untagged responses: FLAGS, EXISTS, RECENT
                    REQUIRED OK untagged responses:  UNSEEN,  PERMANENTFLAGS,
                    UIDNEXT, UIDVALIDITY

        Result:     OK - select completed, now in selected state
                    NO - select failure, now in authenticated state: no
                         such mailbox, can't access mailbox
                    BAD - command unknown or arguments invalid

    FETCH: 6.4.5절
        Arguments:  sequence set
                    message data item names or macro

        Responses:  untagged responses: FETCH

        Result:     OK - fetch completed
                    NO - fetch error: can't fetch that data
                    BAD - command unknown or arguments invalid
    LOGOUT: 6.1.3절
        Arguments:  none

        Responses:  REQUIRED untagged response: BYE

        Result:     OK - logout completed
                    BAD - command unknown or arguments invalid
 */
public class IMAPRecievedEmailFetcher {
    private String email;
    private String password;
    private String server;
    private int port;

    public IMAPRecievedEmailFetcher(
            String email, String password,
            String server, int port
    ) {
        this.email = email;
        this.password = password;
        this.server = server;
        this.port = port;
    }

    public String[] fetch(int emailCount) {
        String[] emailDetails = new String[emailCount * 4];
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port);
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 서버 초기 응답 읽기
                System.out.println("S: " + reader.readLine());

                // 로그인 명령어 전송 (아이디와 비밀번호 필요)
                writer.println("A001 LOGIN " + email + " " + password);
                System.out.println("C: A001 LOGIN " + email + " " + password);

                System.out.println("S: " + reader.readLine());

                // 메일함 선택 -> INBOX는 받은 메일함
                writer.println("A002 SELECT INBOX");
                System.out.println("C: A002 SELECT INBOX");

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("S: " + line);
                    if (line.startsWith("A002 OK")) break;
                }

                /*
                    받은 메일함에서 최신순으로 emailCount 에메일 ID 가져오기
                 */
                writer.println("A003 SEARCH ALL");
                System.out.println("C: A003 SEARCH ALL");

                String[] mailIds = new String[emailCount];
                while ((line = reader.readLine()) != null) {
                    System.out.println("a3 응답: " + line);
                    if (line.startsWith("* SEARCH")) {
                        String[] ids = line.split(" ");

                        int endId = Math.max(1, ids.length - emailCount);
                        int count = 0;
                        for (int i = ids.length - 1; i >= endId; i--) {
                            mailIds[count++] = ids[i];
                        }
                        break;
                    }
                }
                System.out.println("========================");
                for (String mailId : mailIds) {
                    System.out.println("메일 ID: " + mailId);
                }
                System.out.println("========================");

                for (int i = 0; i < mailIds.length; i++) {
                    writer.println("a4 FETCH " + mailIds[i] + " (BODY[HEADER] BODY[TEXT])");
                    System.out.println("C: A004 FETCH " + mailIds[i] + " (BODY[HEADER] BODY[TEXT])");

                    StringBuilder subjectBuilder = new StringBuilder();
                    boolean subjectStarted = false;
                    while ((line = reader.readLine()) != null) {
                        /*
                            발신자 이메일 가져오기
                         */
                        if (line.contains("From:")) {
                            System.out.println("S: A004 응답: " + line);
                            int start = line.indexOf("<") + 1;
                            int end = line.indexOf(">");
                            if (start > 0 && end > start) {
                                emailDetails[i * 4] = line.substring(start, end).trim();
                            } else {
                                emailDetails[i * 4] = line.substring(6).trim();
                            }
                            System.out.println("발신자 이메일: " + emailDetails[i * 4]);
                        }

                        /*
                            발신 날짜 가져오기
                         */
                        if (line.contains("Date:")) {
                            System.out.println("a4 응답: " + line);

                            String dateValue = line.substring(6).trim();
                            int lastSpaceIndex = dateValue.lastIndexOf(" ");
                            if (lastSpaceIndex != -1) {
                                dateValue = dateValue.substring(0, lastSpaceIndex);
                            }
                            emailDetails[i * 4 + 1] = dateValue;

                            System.out.println("수신 날짜: " + emailDetails[i * 4 + 1]);
                        }

                        /*
                            메일 제목 가져오기
                         */
                        if (line.contains("Subject:")) {
                            System.out.println("a4 응답: " + line);

                            subjectStarted = true;
                            subjectBuilder.append(line.substring("Subject:".length()).trim());
                        }
                        /*
                            제목이 여러 줄에 걸쳐있을 때 처리
                         */
                        else if (subjectStarted) {
                            if (line.startsWith(" ") || line.startsWith("\t")) {
                                subjectBuilder.append(line.trim());
                            } else {
                                break;
                            }
                        }
                        if (line.startsWith("a4 OK")) break;
                    }
                    String encodedSubject = subjectBuilder.toString();
                    emailDetails[i * 4 + 2] = decodeSubject(encodedSubject);
                    System.out.println("메일 제목: " + emailDetails[i * 4 + 2]);
                }

                // 연결 종료
                writer.println("a5 LOGOUT");
                System.out.println("a5 LOGOUT");

                System.out.println("로그아웃 응답: " + reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailDetails;
    }

    // 메일 제목을 디코딩하는 메서드
    private String decodeSubject(String encodedSubject) {
        StringBuilder decodedSubject = new StringBuilder();
        Pattern pattern = Pattern.compile("=\\?(.*?)\\?(B|Q)\\?(.*?)\\?=", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(encodedSubject);

        while (matcher.find()) {
            String charset = matcher.group(1);
            String encoding = matcher.group(2);
            String encodedText = matcher.group(3);

            if (encoding.equalsIgnoreCase("B")) {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
                decodedSubject.append(new String(decodedBytes, StandardCharsets.UTF_8));
            } else if (encoding.equalsIgnoreCase("Q")) {
                decodedSubject.append(decodeQuotedPrintable(encodedText, charset));
            }
        }

        return decodedSubject.toString();
    }

    private String decodeQuotedPrintable(String encoded, String charsetName) {
        StringBuilder decoded = new StringBuilder();
        try {
            for (int i = 0; i < encoded.length(); i++) {
                char current = encoded.charAt(i);
                if (current == '=' && i + 2 < encoded.length()) {
                    String hex = encoded.substring(i + 1, i + 3);
                    try {
                        int value = Integer.parseInt(hex, 16);
                        decoded.append((char) value);
                        i += 2; // 16진수 두 자리를 건너뜀
                    } catch (NumberFormatException e) {
                        decoded.append(current); // 잘못된 경우 '='을 그대로 추가
                    }
                } else {
                    decoded.append(current); // '='이 아니면 그대로 추가
                }
            }
            return decoded.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return encoded; // 오류 시 원본 반환
        }
    }

    public static void main(String[] args) {
        // 네이버 IMAP 서버 테스트
        String naverEmail = "audtn0099@naver.com";
        String naverPassword = "msjw0706";
        IMAPRecievedEmailFetcher naverRecievedEmailFetcher = new IMAPRecievedEmailFetcher(
                naverEmail, naverPassword,
                "imap.naver.com", 993);


        String[] emailDetails = naverRecievedEmailFetcher.fetch(10);
        if (emailDetails != null) {
            for (int i = 0; i < 10; i++) {
                int index = i * 4;
                System.out.println("이메일 " + (i + 1) + ":");
                System.out.println("발신자 이메일: " + emailDetails[index]);
                System.out.println("수신 날짜: " + emailDetails[index + 1]);
                System.out.println("메일 제목: " + emailDetails[index + 2]);
                System.out.println("메일 본문: " + emailDetails[index + 3]);
                System.out.println("---------------------------------");
            }
        } else {
            System.out.println("이메일을 가져오는 데 실패했습니다.");
        }
    }
}

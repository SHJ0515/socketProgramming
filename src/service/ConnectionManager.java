package service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class ConnectionManager {
    private static ConnectionManager instance;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int tagCounter = 0;

    private static final String HOST = "imap.naver.com";
    private static final int PORT = 993;

    private ConnectionManager() {
        // 비공개 생성자: 외부에서 직접 인스턴스를 생성할 수 없도록 제한
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void connect() throws IOException {
        // socket이 닫혀있거나 아직 연결되지 않은 경우
        if (socket == null || socket.isClosed()) {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket(HOST, PORT);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 서버의 초기 응답을 읽음
            readResponse();
        }
    }

    public String readResponse() throws IOException {
        String response = reader.readLine();
        System.out.println("S: " + response);
        return response;
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("C: " + command);
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            if (socket != null && !socket.isClosed()) {
                sendCommand("a" + tagCounter + " LOGOUT");
                readResponse();
                socket.close();
                reader.close();
                writer.close();
            }
        }
    }

    /*
        a1 LOGIN ... 식으로 명령을 보내고 다음 명령을 보낼 때
        a2로 보낼 수 있게 tagCounter 처리
     */
    public int getTagCounter() {
        return tagCounter;
    }

    public void setTagCounter(int tagCounter) {
        this.tagCounter = tagCounter;
    }
}

package service;

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
    private ConnectionManager connectionManager;

    public IMAPLogin(
            String email, String password
    ) {
        this.email = email;
        this.password = password;
        this.connectionManager = ConnectionManager.getInstance();
    }

    public boolean login() throws IOException {
        connectionManager.connect();
        connectionManager.sendCommand("a" + connectionManager.getTagCounter() + " LOGIN " + email + " " + password);

        String responseLine = connectionManager.readResponse();
        if(responseLine.contains("OK")) {
            System.out.println("로그인 성공");
            // 태크 카운터 증가시켜 다음 명령 준비
            connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);
            return true;
        }
        else if (responseLine.contains("NO") || responseLine.contains("BAD")) {
            System.out.println("로그인 실패");
            return false;
        }
        return false;
    }
}

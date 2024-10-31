
public class Main {
    public static void main(String[] args) {
        ShowNaverEmailList showNaverEmailList = new ShowNaverEmailList("hoo512000@naver.com", "ejddl20011556!", "INBOX", "UNSEEN");

        String[] a = showNaverEmailList.returnList();

        for (String s : a) {
            System.out.println("maidId = " + s);
        }

    }
}

public class Main {
    public static void main(String[] args) {
<<<<<<< Updated upstream
        ShowNaverEmailList showNaverEmailList = new ShowNaverEmailList("본인 아이디@naver.com","본인 비밀번호",
                "INBOX", "UNSEEN");

        showNaverEmailList.returnList();

        for (String s : showNaverEmailList.getEmailIds()){
            System.out.println("이메일 아이디 = " + s);
        }

        String[] a = showNaverEmailList.getEmailIds();


    }
}
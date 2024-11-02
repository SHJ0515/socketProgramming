
public class Main {

    private static final String ID = "본인 아이디";
    private static final String PASSWORD = "본인 비밀번호";
    private static final String INBOX = "INBOX";
    private static final String UNSEEN = "UNSEEN";

    public static void main(String[] args) {
        ShowNaverEmailList showNaverEmailList = new ShowNaverEmailList(ID,PASSWORD, INBOX, UNSEEN);
        showNaverEmailList.returnList();

        for (String s : showNaverEmailList.getEmailIds()){
            System.out.println("이메일 아이디 = " + s);
        }

        String[] emailIds = showNaverEmailList.getEmailIds();

        ShowNaverEmailTitle showNaverEmailTitle = new ShowNaverEmailTitle(showNaverEmailList);
        showNaverEmailTitle.displayAllEmailTitles();

        showNaverEmailList.closeConnection();

    }
}
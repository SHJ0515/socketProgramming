
public class Main {

    public static void main(String[] args) {
        ShowNaverEmailList showNaverEmailList = new ShowNaverEmailList("본인아이디","비번",
                "INBOX", "UNSEEN");
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
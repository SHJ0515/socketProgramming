package dto;

public class EmailInfo {

    private String fromOrTo;
    private String date;
    private String subject;

    public EmailInfo() {
    }

    public String getFromOrTo() {
        return fromOrTo;
    }

    public void setFromOrTo(String fromOrTo) {
        this.fromOrTo = fromOrTo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "EmailInfo{" +
                "fromOrTo='" + fromOrTo + '\'' +
                ", date='" + date + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
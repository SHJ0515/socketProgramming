public class EmailInfo {
    private String subject;
    private String from;
    private String date;
    private String body;

    public EmailInfo(String subject, String from, String date, String body) {
        this.subject = subject;
        this.from = from;
        this.date = date;
        this.body = body;
    }

    // Getters
    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    // Setters
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "EmailInfo{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", date='" + date + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

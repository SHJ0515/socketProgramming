public class EmailInfo {
    private String subject;
    private String from;
    private String date;
    private String body;

    public EmailInfo(
            String subject, String from,
            String date, String body
    ) {
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

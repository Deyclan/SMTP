package serveurSMTP;

import java.util.List;
import java.util.stream.Collectors;

public class Mail {
    private String from;
    private List<String> to;
    private String subject;
    private String date;
    private String message_id;
    private String content;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(from).append("\n")
                .append("To: ").append(to.stream().collect(Collectors.joining(";"))).append("\n")
                .append("Subject: ").append(subject).append("\n")
                .append("Date: ").append(date.toString()).append("\n")
                .append("Message-ID: ").append(message_id).append("\n\n")
                .append(content).append("\n.");
        return sb.toString();
    }
}

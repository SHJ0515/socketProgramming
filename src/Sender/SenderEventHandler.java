package Sender;

import java.awt.event.*;

public class SenderEventHandler implements ActionListener {
    private SenderGUI app;
    private SMTPSender sendLogic;

    public String from = "";
    public String receiver = "";
    public String subject = "";
    public String content = "";
    public String filePath = "";

    // 생성자를 통해 TextAreaApp의 인스턴스를 전달받음
    public SenderEventHandler(SenderGUI app, SMTPSender sendLogic) {
        this.app = app;
        this.sendLogic = sendLogic;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.app.sendButton) { // sendButton을 누른경우 처리한다.
            this.from = this.app.senderField.getText();
            this.receiver = this.app.receiverField.getText();
            this.subject = this.app.subjectField.getText();
            this.content = this.app.contentArea.getText();
            System.out.println("From: " + this.from);
            System.out.println("Receiver: " + this.receiver);
            System.out.println("Subject: " + this.subject);
            System.out.println("Content: " + this.content);
            String[] args = {this.from, this.receiver, this.subject, this.content};
            try {
                sendLogic.main(args);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else if(e.getSource() == this.app.fileButton) { // fileButotn을 누른경우 처리한다.
            this.filePath = this.app.filePathField.getText();
            System.out.println("filePath: " + this.filePath);
        }
    }

    public static void main(String args[]) {
        new SenderEventHandler(new SenderGUI(), new SMTPSender());
    }
}

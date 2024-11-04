package userinterface.panel;

import dto.EmailInfo;
import userinterface.frame.UserFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EmailInfoDisplay extends JPanel {
    private UserFrame userFrame;
    private MailBoxDisplay mailBoxDisplay;
    private JPanel emailListPanel;
    private JButton nextButton;
    private JButton prevButton;
    private JScrollPane scrollPane;

    public EmailInfoDisplay(UserFrame userFrame, MailBoxDisplay mailBoxDisplay) {
        this.userFrame = userFrame;
        this.mailBoxDisplay = mailBoxDisplay;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 테두리 추가 (예: 라인 테두리)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 이메일 목록을 표시할 패널 생성
        emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));

        // JScrollPane 생성 및 emailListPanel 추가
        scrollPane = new JScrollPane(emailListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // "이전" 및 "다음" 버튼 생성
        prevButton = createStyledButton("이전");
        nextButton = createStyledButton("다음");

        // 버튼 클릭 시 페이지 이동 메서드 호출
        prevButton.addActionListener(e -> {
            mailBoxDisplay.prevPage();
            scrollToTop(); // 스크롤을 맨 위로 이동
        });
        nextButton.addActionListener(e -> {
            mailBoxDisplay.nextPage();
            scrollToTop(); // 스크롤을 맨 위로 이동
        });

        // 버튼을 패널에 추가
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        // 버튼 패널을 EmailInfoDisplay 하단에 추가
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 스크롤을 맨 위로 이동시키는 메서드
    private void scrollToTop() {
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // 기본 배경색
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 170), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // 마우스 오버 효과 추가
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    public void updateEmailList(List<EmailInfo> emailInfoList) {
        emailListPanel.removeAll(); // 기존 이메일 목록 제거

        for (EmailInfo email : emailInfoList) {
            JPanel emailPanel = new JPanel();
            emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
            emailPanel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));

            emailPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // 바깥쪽 테두리
                    BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
            ));
            emailPanel.setBackground(new Color(245, 245, 245));

            JLabel senderLabel = new JLabel("발신자: " + email.getFromOrTo());
            senderLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
            senderLabel.setForeground(new Color(60, 60, 60));

            JLabel dateLabel = new JLabel("날짜: " + email.getDate());
            dateLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 11));
            dateLabel.setForeground(new Color(100, 100, 100));

            JLabel subjectLabel = new JLabel("제목: " + email.getSubject());
            subjectLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
            subjectLabel.setForeground(new Color(80, 80, 80));

            emailPanel.add(senderLabel);
            emailPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 발신자와 날짜 사이의 여백
            emailPanel.add(dateLabel);
            emailPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 날짜와 제목 사이의 여백
            emailPanel.add(subjectLabel);

            emailListPanel.add(emailPanel);
            emailListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 이메일 항목 간 간격 추가
        }

        revalidate(); // 컴포넌트 레이아웃을 새로 고침
        repaint(); // 화면을 다시 그리기
    }
}
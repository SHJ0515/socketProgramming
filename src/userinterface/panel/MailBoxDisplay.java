package userinterface.panel;

import dto.EmailInfo;
import service.IMAPEmailInfoFetcher;
import userinterface.frame.UserFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MailBoxDisplay extends JPanel {
    private UserFrame userFrame;
    private EmailInfoDisplay emailInfoDisplay;
    private IMAPEmailInfoFetcher emailInfoFetcher;
    private String currentFolder;
    private int currentPage = 0;
    private final int emailsPerPage = 10;

    public MailBoxDisplay(UserFrame userFrame) {
        this.userFrame = userFrame;
        initUI();
    }

    private void initUI() {
        // 수직으로 버튼을 쌓기 위해 BoxLayout 설정
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 테두리 추가 (예: 라인 테두리)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 버튼 생성 및 스타일 적용
        JButton inboxButton = createStyledButton("받은 메일함");
        inboxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                loadEmails("INBOX");
            }
        });

        JButton sentButton = createStyledButton("보낸 메일함");
        sentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                loadEmails("\"Sent Messages\"");
            }
        });

        JButton trashButton = createStyledButton("휴지통");
        sentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                loadEmails("Trash");
            }
        });


        // 버튼을 패널에 추가
        add(inboxButton);
        add(Box.createVerticalStrut(10)); // 버튼 사이에 간격 추가
        add(sentButton);
        add(Box.createVerticalStrut(10)); // 버튼 사이에 간격 추가
        add(trashButton);
    }

    private void loadEmails(String folderName) {
        try {
            currentFolder = folderName;
            // IMAPEmailInfoFetcher를 통해 폴더에서 이메일 목록 가져오기
            emailInfoFetcher = new IMAPEmailInfoFetcher(folderName);
            List<EmailInfo> emailInfoList = emailInfoFetcher.fetchEmailInfo(folderName, currentPage, emailsPerPage);

            emailInfoDisplay.updateEmailList(emailInfoList); // EmailInfoDisplay에 이메일 목록 업데이트
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load emails from " + folderName,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setEmailInfoDisplay(EmailInfoDisplay emailInfoDisplay) {
        this.emailInfoDisplay = emailInfoDisplay;
        loadEmails("INBOX");
    }

    public void nextPage() {
        currentPage++;
        loadEmails(currentFolder);
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            loadEmails(currentFolder);
        }
    }

    // 스타일이 적용된 버튼 생성 메서드
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // 버튼을 중앙에 정렬
        button.setBackground(new Color(70, 130, 180)); // 버튼 배경색 설정 (스틸 블루)
        button.setForeground(Color.WHITE); // 버튼 텍스트 색상 설정
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // 버튼 폰트 설정
        button.setFocusPainted(false); // 포커스 표시 제거
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 버튼 패딩 설정

        // 버튼이 부모 컨테이너의 너비에 맞게 늘어나도록 설정
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));

        // 마우스 호버 시 색상 변화 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // 라이트 스틸 블루
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // 기본 색상으로 복구
            }
        });

        return button;
    }
}
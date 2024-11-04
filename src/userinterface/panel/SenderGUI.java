package userinterface.panel;

import userinterface.frame.UserFrame;

import javax.swing.*;
import java.awt.*;

public class SenderGUI extends JPanel {
    public SenderGUI(UserFrame userFrame) {
        initUI();
    }

    private void initUI() {
        // 테두리 추가 (예: 라인 테두리)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));
    }
}

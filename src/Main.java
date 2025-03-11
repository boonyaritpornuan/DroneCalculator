
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.io.*;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DroneCalculator calculator = new DroneCalculator();
            calculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            calculator.setVisible(true);
        });
    }
}

class DroneCalculator extends JFrame {
    private JTextField areaField, speedField, batteryField;
    private JTextField fertRate, pestRate, hormRate;
    private JTextArea resultArea;

    public DroneCalculator() {
        initializeUI();
    }

    private void initializeUI() {
        // ตั้งค่าให้รองรับภาษาไทย
        System.setProperty("file.encoding", "UTF-8");
        
        // สร้างฟอนต์ที่รองรับภาษาไทย
        // ลองใช้ฟอนต์ตามลำดับความเหมาะสม
        Font thaiFont = null;
        String[] thaiFontNames = {
            "TH Sarabun New", "Angsana New", "Cordia New", 
            "Tahoma", "Arial Unicode MS", "Microsoft Sans Serif", 
            "Noto Sans Thai", "Waree", "Loma", "Lucida Sans Unicode"
        };
        
        for (String fontName : thaiFontNames) {
            thaiFont = new Font(fontName, Font.PLAIN, 16);
            if (!thaiFont.getFamily().equals("Dialog")) {
                System.out.println("ใช้ฟอนต์: " + fontName);
                break;
            }
        }
        
        // หากไม่พบฟอนต์ที่ต้องการ ใช้ Dialog แทน
        if (thaiFont == null || thaiFont.getFamily().equals("Dialog")) {
            thaiFont = new Font(Font.DIALOG, Font.PLAIN, 16);
            System.out.println("ใช้ฟอนต์ Dialog เนื่องจากไม่พบฟอนต์ภาษาไทย");
        }
        
        // ตั้งค่า Look and Feel ให้เป็นแบบระบบ
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // ตั้งค่าหน้าต่างหลัก
        setTitle("โปรแกรมคำนวณโดรนเกษตรกรรม DJI");
        setSize(650, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // สร้าง Panel หลัก
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255)); // สีพื้นหลังอ่อนๆ
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel("โปรแกรมคำนวณโดรนเกษตรกรรม");
        titleLabel.setFont(new Font(thaiFont.getFamily(), Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 0));
        headerPanel.add(titleLabel);
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 1), 
            "ข้อมูลการคำนวณ"));
        
        // ฟังก์ชันสำหรับเพิ่มข้อมูลฟิลด์
        addField(inputPanel, "พื้นที่การเกษตร (ไร่):", "10", areaField = new JTextField(), thaiFont);
        addField(inputPanel, "ความเร็วการบิน (ไร่/ชม.):", "5", speedField = new JTextField(), thaiFont);
        addField(inputPanel, "เวลาใช้งานแบตเตอรี่ (นาที):", "20", batteryField = new JTextField(), thaiFont);
        addField(inputPanel, "อัตราการฉีดปุ๋ย (ลิตร/ไร่):", "2", fertRate = new JTextField(), thaiFont);
        addField(inputPanel, "อัตราการฉีดยาฆ่าแมลง (ลิตร/ไร่):", "1", pestRate = new JTextField(), thaiFont);
        addField(inputPanel, "อัตราการฉีดฮอร์โมน (ลิตร/ไร่):", "0.5", hormRate = new JTextField(), thaiFont);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton calcButton = new JButton("คำนวณข้อมูล");
        calcButton.setFont(new Font(thaiFont.getFamily(), Font.BOLD, 16));
        calcButton.setForeground(Color.BLACK);
        calcButton.setBackground(new Color(46, 139, 87));
        calcButton.setFocusPainted(false);
        calcButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        calcButton.addActionListener(e -> calculate());
        buttonPanel.add(calcButton);
        
        // Result Area
        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(thaiFont.getFamily(), Font.PLAIN, 16));
        resultArea.setBackground(new Color(255, 255, 240));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 1), 
            "ผลลัพธ์การคำนวณ"));
        resultPanel.setBackground(new Color(240, 248, 255));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(240, 248, 255));
        wrapperPanel.add(mainPanel, BorderLayout.NORTH);
        wrapperPanel.add(resultPanel, BorderLayout.CENTER);
        
        // สร้างเมนูบาร์
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("ไฟล์");
        fileMenu.setFont(thaiFont);
        JMenuItem exitItem = new JMenuItem("ออกจากโปรแกรม");
        exitItem.setFont(thaiFont);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu viewMenu = new JMenu("มุมมอง");
        viewMenu.setFont(thaiFont);
        JMenu fontSizeMenu = new JMenu("ขนาดตัวอักษร");
        fontSizeMenu.setFont(thaiFont);
        
        // เพิ่มตัวเลือกขนาดฟอนต์
        int[] fontSizes = {14, 16, 18, 20, 22, 24};
        for (int size : fontSizes) {
            JMenuItem sizeItem = new JMenuItem(size + " พอยท์");
            sizeItem.setFont(thaiFont);
            final int fontSize = size;
            sizeItem.addActionListener(e -> updateFontSize(fontSize));
            fontSizeMenu.add(sizeItem);
        }
        
        viewMenu.add(fontSizeMenu);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
        
        // Add to frame
        add(wrapperPanel);
    }
    
    private void addField(JPanel panel, String labelText, String defaultValue, JTextField field, Font font) {
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        panel.add(label);
        
        field.setText(defaultValue);
        field.setFont(font);
        panel.add(field);
    }

    private void calculate() {
        try {
            // รับค่าจากฟิลด์ข้อมูล
            double area = Double.parseDouble(areaField.getText());
            double speed = Double.parseDouble(speedField.getText());
            double batteryTime = Double.parseDouble(batteryField.getText());
            double fertilizer = Double.parseDouble(fertRate.getText());
            double pesticide = Double.parseDouble(pestRate.getText());
            double hormone = Double.parseDouble(hormRate.getText());
            
            // คำนวณข้อมูล
            double totalTime = area / speed;
            int batteries = (int) Math.ceil(totalTime * 60 / batteryTime);
            double fertilizerAmount = area * fertilizer;
            double pesticideAmount = area * pesticide;
            double hormoneAmount = area * hormone;
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            DecimalFormat intFormat = new DecimalFormat("#,##0");
            
            // สร้างข้อความผลลัพธ์
            StringBuilder result = new StringBuilder();
            result.append("📊 ผลการคำนวณโดรนเกษตรกรรม 📊\n\n");
            result.append("🕒 เวลาบินทั้งหมด: ").append(df.format(totalTime)).append(" ชั่วโมง\n");
            result.append("🔋 แบตเตอรี่ที่ต้องใช้: ").append(intFormat.format(batteries)).append(" ก้อน\n\n");
            
            result.append("📦 วัตถุดิบที่ต้องใช้:\n");
            result.append("  - ปุ๋ย: ").append(df.format(fertilizerAmount)).append(" ลิตร\n");
            result.append("  - ยาฆ่าแมลง: ").append(df.format(pesticideAmount)).append(" ลิตร\n");
            result.append("  - ฮอร์โมน: ").append(df.format(hormoneAmount)).append(" ลิตร\n\n");
            
            // คำนวณเพิ่มเติม - ค่าใช้จ่ายประมาณการ (สมมติ)
            double batteryPrice = 2000; // บาทต่อก้อน
            double fuelCost = 50 * totalTime; // น้ำมันที่ใช้ต่อชั่วโมง * เวลาบิน
            
            result.append("💰 ค่าใช้จ่ายโดยประมาณ:\n");
            result.append("  - ค่าแบตเตอรี่: ").append(intFormat.format(batteries * batteryPrice)).append(" บาท\n");
            result.append("  - ค่าเชื้อเพลิง: ").append(intFormat.format(fuelCost)).append(" บาท\n");
            
            resultArea.setText(result.toString());
        } catch (NumberFormatException ex) {
            resultArea.setText("⚠️ กรุณาป้อนข้อมูลที่ถูกต้อง (เฉพาะตัวเลขเท่านั้น)");
        }
    }
    
    // เมธอดสำหรับปรับขนาดฟอนต์ทั้งหมดในโปรแกรม
    private void updateFontSize(int size) {
        Font newFont = new Font(resultArea.getFont().getFamily(), Font.PLAIN, size);
        
        // อัพเดทฟอนต์ในทุกคอมโพเนนต์
        Component[] components = getContentPane().getComponents();
        updateComponentsFontRecursive(components, newFont);
        
        // อัพเดทฟอนต์ในผลลัพธ์
        resultArea.setFont(newFont);
        
        // อัพเดทฟอนต์สำหรับเมนู
        JMenuBar menuBar = getJMenuBar();
        if (menuBar != null) {
            updateMenuFontRecursive(menuBar, newFont);
        }
        
        // รีเฟรชการแสดงผล
        revalidate();
        repaint();
    }
    
    // เมธอดสำหรับอัพเดทฟอนต์ในคอมโพเนนต์ทั้งหมดแบบรีเคอร์ซีฟ
    private void updateComponentsFontRecursive(Component[] components, Font newFont) {
        for (Component comp : components) {
            comp.setFont(newFont);
            
            if (comp instanceof JLabel) {
                ((JLabel) comp).setFont(newFont);
            } else if (comp instanceof JTextField) {
                ((JTextField) comp).setFont(newFont);
            } else if (comp instanceof JTextArea) {
                ((JTextArea) comp).setFont(newFont);
            } else if (comp instanceof JButton) {
                ((JButton) comp).setFont(newFont);
            } else if (comp instanceof Container) {
                updateComponentsFontRecursive(((Container) comp).getComponents(), newFont);
            }
        }
    }
    
    // เมธอดสำหรับอัพเดทฟอนต์ในเมนูแบบรีเคอร์ซีฟ
    private void updateMenuFontRecursive(MenuElement element, Font newFont) {
        if (element instanceof JMenuItem) {
            ((JMenuItem) element).setFont(newFont);
        }
        
        MenuElement[] subElements = element.getSubElements();
        for (MenuElement subElement : subElements) {
            updateMenuFontRecursive(subElement, newFont);
        }
    }
}

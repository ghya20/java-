package tusu;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryGUI extends JFrame {
    // 持有原有图书馆系统实例，所有操作通过该实例完成
    private final LibrarySystem librarySystem;
    // 卡片布局：用于切换不同功能页面
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    // 各功能面板（对应不同操作页面）
    private JPanel addBookPanel;
    private JPanel addReaderPanel;
    private JPanel borrowBookPanel;
    private JPanel returnBookPanel;
    private JPanel popularBooksPanel;
    private JPanel exportDataPanel;
    

    // 构造方法：初始化窗口和界面
    public LibraryGUI() {
        // 初始化原有图书馆系统
        librarySystem = new LibrarySystem();
        // 初始化布局
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 1. 初始化窗口基本属性
        setTitle("图书馆管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中
        setLayout(new BorderLayout());

        // 2. 添加顶部功能按钮面板
        add(createButtonPanel(), BorderLayout.NORTH);
        // 3. 添加中间内容面板（卡片布局）
        initAllFunctionPanels();
        add(contentPanel, BorderLayout.CENTER);
    }

    // -------------------------- 1. 顶部功能按钮面板 --------------------------
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // 功能按钮：对应不同操作
        String[] buttonNames = {
                "添加图书", "添加读者", "借阅图书", 
                "归还图书", "热门图书TOP10", "导出数据", "导入豆瓣图书" 
        };
        String[] panelNames = {
                "addBook", "addReader", "borrowBook", 
                "returnBook", "popularBooks", "exportData","importDouban"
        };

        // 为每个按钮绑定事件（切换到对应面板）
        for (int i = 0; i < buttonNames.length; i++) {
            JButton btn = new JButton(buttonNames[i]);
            int finalI = i;
            btn.addActionListener(e -> cardLayout.show(contentPanel, panelNames[finalI]));
            buttonPanel.add(btn);
        }

        return buttonPanel;
    }

    // -------------------------- 2. 初始化所有功能面板 --------------------------
    private void initAllFunctionPanels() {
        // 初始化各功能面板
        addBookPanel = createAddBookPanel();
        addReaderPanel = createAddReaderPanel();
        borrowBookPanel = createBorrowBookPanel();
        returnBookPanel = createReturnBookPanel();
        popularBooksPanel = createPopularBooksPanel();
        exportDataPanel = createExportDataPanel();
        JPanel importDoubanPanel = createImportDoubanPanel();

        // 将面板添加到卡片布局中（面板名与按钮对应）
        contentPanel.add(addBookPanel, "addBook");
        contentPanel.add(addReaderPanel, "addReader");
        contentPanel.add(borrowBookPanel, "borrowBook");
        contentPanel.add(returnBookPanel, "returnBook");
        contentPanel.add(popularBooksPanel, "popularBooks");
        contentPanel.add(exportDataPanel, "exportData");
        contentPanel.add(importDoubanPanel, "importDouban");
    }

    // -------------------------- 3. 各功能面板具体实现 --------------------------
    /**
     * 1. 添加图书面板
     */
    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 标签和输入框：ISBN、标题、作者、分类、库存
        String[] labels = {"ISBN：", "标题：", "作者：", "分类：", "库存："};
        JTextField[] textFields = new JTextField[5];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);

            textFields[i] = new JTextField(20);
            gbc.gridx = 1;
            panel.add(textFields[i], gbc);
        }

        // 添加按钮
        JButton addBtn = new JButton("添加图书");
        gbc.gridx = 1;
        gbc.gridy = labels.length;
        gbc.anchor = GridBagConstraints.CENTER;
        addBtn.addActionListener(e -> {
            try {
                // 获取输入值
                String isbn = textFields[0].getText().trim();
                String title = textFields[1].getText().trim();
                String author = textFields[2].getText().trim();
                String category = textFields[3].getText().trim();
                int stock = Integer.parseInt(textFields[4].getText().trim());

                // 调用原有系统方法添加图书
                librarySystem.addBook(new Book(isbn, title, author, category, stock));
                JOptionPane.showMessageDialog(panel, "添加成功！");

                // 清空输入框
                for (JTextField tf : textFields) {
                    tf.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "库存请输入数字！", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "添加失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addBtn, gbc);

        return panel;
    }
    /**
     * 2. 添加读者面板
     */
    private JPanel createAddReaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 标签和输入框：读者卡号、姓名、联系方式、最大借阅数
        String[] labels = {"读者卡号：", "姓名：", "联系方式：", "最大借阅数："};
        JTextField[] textFields = new JTextField[4];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);

            textFields[i] = new JTextField(20);
            gbc.gridx = 1;
            panel.add(textFields[i], gbc);
        }

        // 添加按钮
        JButton addBtn = new JButton("添加读者");
        gbc.gridx = 1;
        gbc.gridy = labels.length;
        gbc.anchor = GridBagConstraints.CENTER;
        addBtn.addActionListener(e -> {
            try {
                // 获取输入值
                String cardId = textFields[0].getText().trim();
                String name = textFields[1].getText().trim();
                String contact = textFields[2].getText().trim();
                int maxBooks = Integer.parseInt(textFields[3].getText().trim());

                // 调用原有系统方法添加读者
                librarySystem.addReader(new Reader(cardId, name, contact, maxBooks));
                JOptionPane.showMessageDialog(panel, "添加成功！");

                // 清空输入框
                for (JTextField tf : textFields) {
                    tf.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "最大借阅数请输入数字！", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "添加失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addBtn, gbc);

        return panel;
    }

    /**
     * 3. 借阅图书面板
     */
    private JPanel createBorrowBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 标签和输入框：图书ISBN、读者卡号
        String[] labels = {"图书ISBN：", "读者卡号："};
        JTextField[] textFields = new JTextField[2];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);

            textFields[i] = new JTextField(20);
            gbc.gridx = 1;
            panel.add(textFields[i], gbc);
        }

        // 借阅按钮
        JButton borrowBtn = new JButton("借阅图书");
        gbc.gridx = 1;
        gbc.gridy = labels.length;
        gbc.anchor = GridBagConstraints.CENTER;
        borrowBtn.addActionListener(e -> {
            // 获取输入值
            String isbn = textFields[0].getText().trim();
            String readerCardId = textFields[1].getText().trim();

            // 调用原有系统方法借阅图书（原有方法已包含控制台提示，这里新增弹窗提示）
            // 先捕获原有方法的输出，转为弹窗提示（简化处理：直接调用后根据逻辑提示）
            Book book = librarySystem.searchBookByIsbn(isbn);
            boolean readerExists = librarySystem.getReaders().stream()
                    .anyMatch(r -> r.getCardId().equals(readerCardId));

            if (book == null || !readerExists) {
                JOptionPane.showMessageDialog(panel, "书或读者不存在！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (book.getStock() <= 0) {
                JOptionPane.showMessageDialog(panel, "图书库存不足！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            long borrowedCount = librarySystem.getBorrowRecords().stream()
                    .filter(r -> r.getReaderCardId().equals(readerCardId) && !r.isReturned())
                    .count();
            int maxBooks = librarySystem.getReaders().stream()
                    .filter(r -> r.getCardId().equals(readerCardId))
                    .findFirst().get().getMaxBooks();
            if (borrowedCount >= maxBooks) {
                JOptionPane.showMessageDialog(panel, "读者已达到最大借阅数量！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 调用原有方法完成借阅
            librarySystem.borrowBook(isbn, readerCardId);
            JOptionPane.showMessageDialog(panel, "借阅成功！", "提示", JOptionPane.INFORMATION_MESSAGE);

            // 清空输入框
            for (JTextField tf : textFields) {
                tf.setText("");
            }
        });
        panel.add(borrowBtn, gbc);

        return panel;
    }

    /**
     * 4. 归还图书面板
     */
    private JPanel createReturnBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 标签和输入框：图书ISBN、读者卡号
        String[] labels = {"图书ISBN：", "读者卡号："};
        JTextField[] textFields = new JTextField[2];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);

            textFields[i] = new JTextField(20);
            gbc.gridx = 1;
            panel.add(textFields[i], gbc);
        }

        // 归还按钮
        JButton returnBtn = new JButton("归还图书");
        gbc.gridx = 1;
        gbc.gridy = labels.length;
        gbc.anchor = GridBagConstraints.CENTER;
        returnBtn.addActionListener(e -> {
            // 获取输入值
            String isbn = textFields[0].getText().trim();
            String readerCardId = textFields[1].getText().trim();

            // 调用原有系统方法归还图书（捕获逻辑并弹窗提示）
            BorrowRecord record = librarySystem.getBorrowRecords().stream()
                    .filter(r -> r.getBookIsbn().equals(isbn) && r.getReaderCardId().equals(readerCardId) && !r.isReturned())
                    .findFirst().orElse(null);

            if (record == null) {
                JOptionPane.showMessageDialog(panel, "没有找到对应的借阅记录！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 计算逾期和罚款（调用原有方法）
            int overdueDays = record.getOverdueDays();
            double fine = record.getFine();
            if (overdueDays > 0) {
                JOptionPane.showMessageDialog(panel, 
                        "图书已逾期 " + overdueDays + " 天，罚款 " + fine + " 元！", 
                        "逾期提示", JOptionPane.WARNING_MESSAGE);
            }

            // 调用原有方法完成归还
            librarySystem.returnBook(isbn, readerCardId);
            JOptionPane.showMessageDialog(panel, "归还成功！", "提示", JOptionPane.INFORMATION_MESSAGE);

            // 清空输入框
            for (JTextField tf : textFields) {
                tf.setText("");
            }
        });
        panel.add(returnBtn, gbc);

        return panel;
    }

    /**
     * 5. 热门图书TOP10面板
     */
    private JPanel createPopularBooksPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("最受欢迎图书TOP10（已归还记录统计）", SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // 结果显示区域（文本域，不可编辑）
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("宋体", Font.PLAIN, 12));
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // 刷新按钮（重新统计热门图书）
        JButton refreshBtn = new JButton("刷新热门图书");
        panel.add(refreshBtn, BorderLayout.SOUTH);
        refreshBtn.addActionListener(e -> {
            // 调用原有系统方法获取热门图书
            List<Book> popularBooks = librarySystem.getMostPopularBooks();

            // 拼接结果文本
            StringBuilder sb = new StringBuilder();
            if (popularBooks.isEmpty()) {
                sb.append("暂无借阅记录，无法统计热门图书！");
            } else {
                for (int i = 0; i < popularBooks.size(); i++) {
                    Book book = popularBooks.get(i);
                    sb.append((i + 1)).append(". ")
                            .append("ISBN：").append(book.getIsbn()).append(" | ")
                            .append("标题：").append(book.getTitle()).append(" | ")
                            .append("作者：").append(book.getAuthor()).append(" | ")
                            .append("库存：").append(book.getStock()).append("\n");
                }
            }
            resultArea.setText(sb.toString());
        });

        return panel;
    }

    /**
     * 6. 导出数据面板
     */
    private JPanel createExportDataPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("系统数据导出", SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // 数据显示区域（文本域，不可编辑）
        JTextArea dataArea = new JTextArea();
        dataArea.setEditable(false);
        dataArea.setFont(new Font("宋体", Font.PLAIN, 11));
        panel.add(new JScrollPane(dataArea), BorderLayout.CENTER);

        // 导出按钮（调用原有方法并显示数据）
        JButton exportBtn = new JButton("导出所有数据");
        panel.add(exportBtn, BorderLayout.SOUTH);
        exportBtn.addActionListener(e -> {
            // 调用原有系统方法导出数据（捕获控制台输出，转为文本域显示）
            StringBuilder sb = new StringBuilder();

            // 1. 图书数据
            sb.append("=== 图书数据 ===\n");
            librarySystem.getBooks().values().forEach(book -> sb.append(book.toString()).append("\n"));

            // 2. 读者数据
            sb.append("\n=== 读者数据 ===\n");
            librarySystem.getReaders().forEach(reader -> sb.append(reader.toString()).append("\n"));

            // 3. 借阅记录数据
            sb.append("\n=== 借阅记录数据 ===\n");
            librarySystem.getBorrowRecords().forEach(record -> sb.append(record.toString()).append("\n"));

            dataArea.setText(sb.toString());
            JOptionPane.showMessageDialog(panel, "数据导出完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }
    /**
    * 7. 豆瓣图书导入面板
     */
    private JPanel createImportDoubanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        pachong DoubanCrawler = new pachong();
        // 标题
        JLabel titleLabel = new JLabel("一键导入豆瓣Top250图书", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 信息显示区域
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("宋体", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
    
        // 导入按钮
        JButton importBtn = new JButton("开始导入豆瓣Top250");
        importBtn.addActionListener(e -> {
            importBtn.setEnabled(false); // 防止重复点击
            infoArea.setText("开始爬取豆瓣Top250图书...\n");
        
        // 使用SwingWorker在后台执行爬虫任务，避免界面卡顿
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                    try {
                        publish("正在连接豆瓣...\n");
                    
                        // 调用你的豆瓣爬虫
                        
                        List<String> bookTitles = DoubanCrawler.pachong1();

                        List<Book> doubanBooks = new ArrayList<>();
                        for (String title : bookTitles){
                            Book book = new Book(
                                "未知isbn",
                                title,
                                "未知作者",
                                "文学",
                                1
                            );
                            doubanBooks.add(book);
                        }
                        publish("成功爬取 " + doubanBooks.size() + " 本图书信息\n");
                        publish("开始导入系统...\n");
                    
                        int successCount = 0;
                        for (Book book : doubanBooks) {
                            try {
                                librarySystem.addBook(book);
                                successCount++;
                                publish("✓ 导入成功: " + book.getTitle() + "\n");
                            } catch (Exception ex) {
                                publish("✗ 导入失败: " + book.getTitle() + " - " + ex.getMessage() + "\n");
                            }
                            // 稍微延迟，避免太快
                            Thread.sleep(50);
                        }
                    
                            publish("\n导入完成！成功导入 " + successCount + "/" + doubanBooks.size() + " 本图书\n");
                    
                        } catch (Exception ex) {
                            publish("导入失败: " + ex.getMessage() + "\n");
                        }
                    return null;
                }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    infoArea.append(message);
                    // 自动滚动到底部
                    infoArea.setCaretPosition(infoArea.getDocument().getLength());
                }
            }
            
            @Override
            protected void done() {
                importBtn.setEnabled(true);
            }
        };
        
        worker.execute();
    });
    
    // 清空按钮
    JButton clearBtn = new JButton("清空显示");
    clearBtn.addActionListener(e -> infoArea.setText(""));
    
    buttonPanel.add(importBtn);
    buttonPanel.add(clearBtn);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
}
    // -------------------------- 5. 主方法（启动GUI） --------------------------
    public static void main(String[] args) {
        //  Swing线程安全启动
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
package tusu;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

// 图书类
class Book {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private int stock;

    public Book(String isbn, String title, String author, String category, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.stock = stock;
    }

    // Getter 和 Setter 方法
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "ISBN: " + isbn + ", Title: " + title + ", Author: " + author + ", Category: " + category + ", Stock: " + stock;
    }
}

// 读者类
class Reader {
    private String cardId;
    private String name;
    private String contact;
    private int maxBooks;

    public Reader(String cardId, String name, String contact, int maxBooks) {
        this.cardId = cardId;
        this.name = name;
        this.contact = contact;
        this.maxBooks = maxBooks;
    }

    // Getter 和 Setter 方法
    public String getCardId() { return cardId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public int getMaxBooks() { return maxBooks; }
    public void setMaxBooks(int maxBooks) { this.maxBooks = maxBooks; }

    @Override
    public String toString() {
        return "Card ID: " + cardId + ", Name: " + name + ", Contact: " + contact + ", Max Books: " + maxBooks;
    }
}

// 借阅记录类
class BorrowRecord {
    private String bookIsbn;
    private String readerCardId;
    private Date borrowDate;
    private Date dueDate;
    private boolean isReturned;

    public BorrowRecord(String bookIsbn, String readerCardId) {
        this.bookIsbn = bookIsbn;
        this.readerCardId = readerCardId;
        this.borrowDate = new Date();
        this.dueDate = addDaysToCalendar(30); // 设置应还日期为30天后
        this.isReturned = false;
    }

    // Getter 和 Setter 方法
    public String getBookIsbn() { return bookIsbn; }
    public String getReaderCardId() { return readerCardId; }
    public Date getBorrowDate() { return borrowDate; }
    public Date getDueDate() { return dueDate; }
    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    // 计算逾期天数和罚款
    public int getOverdueDays() {
        if (!isReturned) {
            long diff = new Date().getTime() - dueDate.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        }
        return 0;
    }

    public double getFine() {
        return getOverdueDays() * 0.5; // 逾期1天罚款0.5元
    }

    // 辅助方法：计算30天后的日期
    private Date addDaysToCalendar(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Book ISBN: " + bookIsbn + ", Reader Card ID: " + readerCardId + ", Borrow Date: " + sdf.format(borrowDate) + ", Due Date: " + sdf.format(dueDate) + ", Returned: " + isReturned;
    }
}

// 图书馆系统类
class LibrarySystem {
    private Map<String, Book> books = new HashMap<>(); // ISBN -> Book
    private List<Reader> readers = new ArrayList<>(); // 读者列表
    private List<BorrowRecord> borrowRecords = new ArrayList<>(); // 借阅记录列表

    // 添加图书
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    // 删除图书
    public void removeBook(String isbn) {
        books.remove(isbn);
    }

    // 查询图书
    public Book searchBookByIsbn(String isbn) {
        return books.get(isbn);
    }

    // 添加读者
    public void addReader(Reader reader) {
        readers.add(reader);
    }

    // 借阅图书
    public void borrowBook(String isbn, String readerCardId) {
        Book book = searchBookByIsbn(isbn);
        Reader reader = readers.stream()
                .filter(r -> r.getCardId().equals(readerCardId))
                .findFirst()
                .orElse(null);

        if (book == null || reader == null) {
            System.out.println("书或读者不存在！");
            return;
        }

        if (book.getStock() <= 0) {
            System.out.println("图书库存不足！");
            return;
        }

        // 检查读者是否已借满
        long borrowedCount = borrowRecords.stream()
                .filter(record -> record.getReaderCardId().equals(readerCardId) && !record.isReturned())
                .count();
        if (borrowedCount >= reader.getMaxBooks()) {
            System.out.println("读者已达到最大借阅数量！");
            return;
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord(isbn, readerCardId);
        borrowRecords.add(record);

        // 减少图书库存
        book.setStock(book.getStock() - 1);
        System.out.println("借阅成功！");
    }

    // 归还图书
    public void returnBook(String isbn, String readerCardId) {
        BorrowRecord record = borrowRecords.stream()
                .filter(r -> r.getBookIsbn().equals(isbn) && r.getReaderCardId().equals(readerCardId) && !r.isReturned())
                .findFirst()
                .orElse(null);

        if (record == null) {
            System.out.println("没有找到对应的借阅记录！");
            return;
        }

        // 计算逾期天数和罚款
        int overdueDays = record.getOverdueDays();
        double fine = record.getFine();

        if (overdueDays > 0) {
            System.out.println("图书已逾期 " + overdueDays + " 天，罚款 " + fine + " 元！");
        }

        // 标记为已归还
        record.setReturned(true);

        // 增加图书库存
        Book book = searchBookByIsbn(isbn);
        if (book != null) {
            book.setStock(book.getStock() + 1);
        }
        System.out.println("归还成功！");
    }

    // 统计最受欢迎的图书（借阅次数 TOP10）
    public List<Book> getMostPopularBooks() {
        Map<String, Integer> bookBorrowCount = new HashMap<>();
        for (BorrowRecord record : borrowRecords) {
            if (!record.isReturned()) continue; // 只统计已归还的记录
            bookBorrowCount.put(record.getBookIsbn(), bookBorrowCount.getOrDefault(record.getBookIsbn(), 0) + 1);
        }

        // 按借阅次数排序
        List<Book> sortedBooks = bookBorrowCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> books.get(entry.getKey()))
                .collect(Collectors.toList());

        return sortedBooks.subList(0, Math.min(10, sortedBooks.size()));
    }

    // 清理过期记录（已归还超过1年的记录）
    public void cleanExpiredRecords() {
        long oneYearInMillis = 365 * 24 * 60 * 60 * 1000;
        Date oneYearAgo = new Date(System.currentTimeMillis() - oneYearInMillis);

        borrowRecords.removeIf(record -> record.isReturned() && record.getBorrowDate().before(oneYearAgo));
        System.out.println("已清理过期记录！");
    }

    // 数据导出
    public void exportData() {
        System.out.println("图书数据：");
        books.values().forEach(System.out::println);

        System.out.println("\n读者数据：");
        readers.forEach(System.out::println);

        System.out.println("\n借阅记录数据：");
        borrowRecords.forEach(System.out::println);
    }

    // 新增三个getter方法，供GUI访问私有集合（不影响原有业务逻辑）
    public Map<String, Book> getBooks() {
        return books;
    }

    public List<Reader> getReaders() {
        return readers;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }
}

// 主类
public class Library {
    public static void main(String[] args) {
        LibrarySystem librarySystem = new LibrarySystem();

        // 添加图书
        librarySystem.addBook(new Book("9787111658607", "Java编程思想", "Bruce Eckel", "计算机", 10));
        librarySystem.addBook(new Book("9787115555963", "Effective Java", "Joshua Bloch", "计算机", 5));
        librarySystem.addBook(new Book("9787111634630", "算法导论", "Thomas H. Cormen", "计算机", 3));

        // 添加读者
        librarySystem.addReader(new Reader("R001", "张三", "12345678901", 5));
        librarySystem.addReader(new Reader("R002", "李四", "09876543210", 5));

        // 借阅图书
        librarySystem.borrowBook("9787111658607", "R001");
        librarySystem.borrowBook("9787115555963", "R001");
        librarySystem.borrowBook("9787111634630", "R002");

        // 归还图书
        librarySystem.returnBook("9787111658607", "R001");

        // 统计最受欢迎的图书
        System.out.println("\n最受欢迎的图书：");
        librarySystem.getMostPopularBooks().forEach(System.out::println);

        // 数据导出
        System.out.println("\n导出数据：");
        librarySystem.exportData();
    }
}
# java-package test111;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

// ???
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

    // Getter ? Setter ??
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

// ???
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

    // Getter ? Setter ??
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

// ?????
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
        this.dueDate = addDaysToCalendar(30); // ???????30??
        this.isReturned = false;
    }

    // Getter ? Setter ??
    public String getBookIsbn() { return bookIsbn; }
    public String getReaderCardId() { return readerCardId; }
    public Date getBorrowDate() { return borrowDate; }
    public Date getDueDate() { return dueDate; }
    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    // ?????????
    public int getOverdueDays() {
        if (!isReturned) {
            long diff = new Date().getTime() - dueDate.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        }
        return 0;
    }

    public double getFine() {
        return getOverdueDays() * 0.5; // ??1???0.5?
    }

    // ???????30?????
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

// ??????
class LibrarySystem {
    private Map<String, Book> books = new HashMap<>(); // ISBN -> Book
    private List<Reader> readers = new ArrayList<>(); // ????
    private List<BorrowRecord> borrowRecords = new ArrayList<>(); // ??????

    // ????
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    // ????
    public void removeBook(String isbn) {
        books.remove(isbn);
    }

    // ????
    public Book searchBookByIsbn(String isbn) {
        return books.get(isbn);
    }

    // ????
    public void addReader(Reader reader) {
        readers.add(reader);
    }

    // ????
 // ????
    public void borrowBook(String isbn, String readerCardId) {
        Book book = searchBookByIsbn(isbn);
        Reader reader = readers.stream()
                .filter(r -> r.getCardId().equals(readerCardId))
                .findFirst()
                .orElse(null);

        if (book == null || reader == null) {
            System.out.println("????????");
            return;
        }

        if (book.getStock() <= 0) {
            System.out.println("???????");
            return;
        }

        // ???????????????
        long borrowedCount = borrowRecords.stream()
                .filter(record -> record.getReaderCardId().equals(readerCardId) && !record.isReturned())
                .count();
        if (borrowedCount >= reader.getMaxBooks()) {
            System.out.println("????????????");
            return;
        }

        // ??????
        BorrowRecord record = new BorrowRecord(isbn, readerCardId);
        borrowRecords.add(record);

        // ??????
        book.setStock(book.getStock() - 1);
        System.out.println("?????");
    }

    // ????
    public void returnBook(String isbn, String readerCardId) {
        BorrowRecord record = borrowRecords.stream()
                .filter(r -> r.getBookIsbn().equals(isbn) && r.getReaderCardId().equals(readerCardId) && !r.isReturned())
                .findFirst()
                .orElse(null);

        if (record == null) {
            System.out.println("????????????");
            return;
        }

        // ?????????
        int overdueDays = record.getOverdueDays();
        double fine = record.getFine();

        if (overdueDays > 0) {
            System.out.println("????? " + overdueDays + " ???? " + fine + " ??");
        }

        // ??????
        record.setReturned(true);

        // ??????
        Book book = searchBookByIsbn(isbn);
        if (book != null) {
            book.setStock(book.getStock() + 1);
        }
        System.out.println("?????");
    }


 // ?????????????? TOP10?
    public List<Book> getMostPopularBooks() {
        Map<String, Integer> bookBorrowCount = new HashMap<>();
        for (BorrowRecord record : borrowRecords) {
            if (!record.isReturned()) continue; // ?????????
            bookBorrowCount.put(record.getBookIsbn(), bookBorrowCount.getOrDefault(record.getBookIsbn(), 0) + 1);
        }

        // ???????
        List<Book> sortedBooks = bookBorrowCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> books.get(entry.getKey()))
                .collect(Collectors.toList());

        if (sortedBooks.isEmpty()) {
            System.out.println("???????");
            return new ArrayList<>();
        }

        return sortedBooks.subList(0, Math.min(10, sortedBooks.size()));
    }

    // ????????????1?????
    public void cleanExpiredRecords() {
        long oneYearInMillis = 365 * 24 * 60 * 60 * 1000;
        Date oneYearAgo = new Date(System.currentTimeMillis() - oneYearInMillis);

        long cleanedCount = borrowRecords.stream()
                .filter(record -> record.isReturned() && record.getBorrowDate().before(oneYearAgo))
                .count();

        borrowRecords.removeIf(record -> record.isReturned() && record.getBorrowDate().before(oneYearAgo));
        System.out.println("???????????? " + cleanedCount + " ????");
    }

    // ????
    public void exportData() {
        System.out.println("?????");
        books.values().forEach(System.out::println);

        System.out.println("\n?????");
        readers.forEach(System.out::println);

        System.out.println("\n???????");
        borrowRecords.forEach(System.out::println);
    }
}

// ??
public class Main {
    public static void main(String[] args) {
        pachong pachong1 = new pachong();
        LibrarySystem librarySystem = new LibrarySystem();

        // ????
        librarySystem.addBook(new Book("9787111658607", "Java????", "Bruce Eckel", "???", 10));
        librarySystem.addBook(new Book("9787115555963", "Effective Java", "Joshua Bloch", "???", 5));
        librarySystem.addBook(new Book("9787111634630", "????", "Thomas H. Cormen", "???", 3));

        // ????
        librarySystem.addReader(new Reader("R001", "??", "12345678901", 5));
        librarySystem.addReader(new Reader("R002", "??", "09876543210", 5));

        // ????
        librarySystem.borrowBook("9787111658607", "R001");
        librarySystem.borrowBook("9787115555963", "R001");
        librarySystem.borrowBook("9787111634630", "R002");

        // ????
        librarySystem.returnBook("9787111658607", "R001");

        // ?????????
        System.out.println("\n????????");
        librarySystem.getMostPopularBooks().forEach(System.out::println);

        // ????
        System.out.println("\n?????");
        librarySystem.exportData();
    }
}

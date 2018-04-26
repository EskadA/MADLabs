package com.polito.ignorance.lab03.tools;

public class Book {
    private String ISBN, title, author, publisher, year, genre, condition;

    public Book(String ISBN, String title, String author, String publisher, String year, String genre, String condition) {

        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.genre = genre;
        this.condition = condition;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCondition() {
        return condition;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
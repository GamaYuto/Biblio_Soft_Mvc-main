package com.mycompany.biblio_soft_mvc.model;

import java.util.List;

/**
 * Consolida los indicadores principales del dashboard.
 */
public class DashboardMetrics {
    private int borrowedBooks;
    private int pendingReturns;
    private int overdueReturns;
    private int activeUsers;
    private int recentWindowDays;
    private List<AuthorMetric> topAuthors;
    private List<UserActivityMetric> activeUsersDetail;

    public DashboardMetrics() {}

    public DashboardMetrics(int borrowedBooks, int pendingReturns, int overdueReturns, int activeUsers,
            int recentWindowDays, List<AuthorMetric> topAuthors, List<UserActivityMetric> activeUsersDetail) {
        this.borrowedBooks = borrowedBooks;
        this.pendingReturns = pendingReturns;
        this.overdueReturns = overdueReturns;
        this.activeUsers = activeUsers;
        this.recentWindowDays = recentWindowDays;
        this.topAuthors = topAuthors;
        this.activeUsersDetail = activeUsersDetail;
    }

    public int getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(int borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public int getPendingReturns() {
        return pendingReturns;
    }

    public void setPendingReturns(int pendingReturns) {
        this.pendingReturns = pendingReturns;
    }

    public int getOverdueReturns() {
        return overdueReturns;
    }

    public void setOverdueReturns(int overdueReturns) {
        this.overdueReturns = overdueReturns;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getRecentWindowDays() {
        return recentWindowDays;
    }

    public void setRecentWindowDays(int recentWindowDays) {
        this.recentWindowDays = recentWindowDays;
    }

    public List<AuthorMetric> getTopAuthors() {
        return topAuthors;
    }

    public void setTopAuthors(List<AuthorMetric> topAuthors) {
        this.topAuthors = topAuthors;
    }

    public List<UserActivityMetric> getActiveUsersDetail() {
        return activeUsersDetail;
    }

    public void setActiveUsersDetail(List<UserActivityMetric> activeUsersDetail) {
        this.activeUsersDetail = activeUsersDetail;
    }
}

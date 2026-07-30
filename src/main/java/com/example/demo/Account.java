package com.example.demo;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a player account and its cumulative score
 *
 * Accounts are ordered by score in descending order so that they
 * can be displayed in a leaderboard
 */
    public class Account implements Comparable<Account> {
    private long score = 0;
    private String userName ;
    private static ArrayList<Account> accounts = new ArrayList<>();

    /**
     * Creates an account with the supplied username
     *
     * @param userName name associated with the account
     */
    public Account(String userName){
        this.userName=userName;
    }

    @Override
    public int compareTo(Account o) {
        return Long.compare(o.getScore(), score);
    }

    /**
     * Adds an amount to the account's cumulative score.
     *
     * @param score score amount to add
     */
    public void addToScore(long score) {
        this.score += score;
    }

    private long getScore() {
        return score;
    }

    private String getUserName() {
        return userName;
    }

    /**
     * Searches for an existing account with the supplied username
     *
     * @param userName username to locate
     * @return matching account, or {@code null} when no account exists
     */
    static Account accountHaveBeenExist(String userName){
        for(Account account : accounts){
            if(account.getUserName().equals(userName)){
                return account;
            }
        }
        return null;

    }

    /**
     * Creates and stores a new account.
     *
     * @param userName name associated with the new account
     * @return newly created account
     */
    static Account makeNewAccount(String userName){
        Account account = new Account(userName);
        accounts.add(account);
        return account;
    }

}

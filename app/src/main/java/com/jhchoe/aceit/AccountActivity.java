package com.jhchoe.aceit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends Activity {

    private ACEitApplication application;

    public final Context context = this;

    private String[] accounts;

    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        application = (ACEitApplication) getApplication();
        set();
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = (View) findViewById(R.id.whiteBackground);
        v.setVisibility(View.GONE);
    }

    private void set() {
        application.load();
        accounts = application.getAccounts();

        for (int i = 1; i < 3; i++) {
            String temp = "account" + i;
            String temp_delete = "account" + i +"_delete";
            Button b = (Button) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
            if (accounts.length > i-1) {
                if (accounts[i-1] != null) {
                    b.setText(accounts[i-1]);
                    ImageButton b_delete = (ImageButton) findViewById(getResources()
                            .getIdentifier(temp_delete, "id", getPackageName()));
                    if (accounts[i-1].equals("New Account")) {
                        b_delete.setVisibility(ImageButton.GONE);
                    }
                    else {
                        b_delete.setVisibility(ImageButton.VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * Account button methods
     *
     * @param view
     */
    public void play(View view) {
        Button b = (Button) view;
        account = b.getText().toString();
        if (account.equals("New Account")) {
            switch (view.getId()) {
                case R.id.account1:
                    createNewOne();
                    break;
                case R.id.account2:
                    createNewTwo();
                    break;
                default:
                    break;
            }
        }
        else {
            Intent intent = new Intent(this, LevelActivity.class);
            application.setCurrentAccount(account);
            startActivity(intent);
            View v = (View) findViewById(R.id.whiteBackground);
            v.setVisibility(v.VISIBLE);
        }
    }

    /**
     * Create a new account (new file in internal storage).
     * Gather necessary information by using dialog.
     */
    public void createNewOne() {
        // Create and pop up the dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setTitle("Enter Your Name: ");
        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.OK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) dialog.findViewById(R.id.account);
                account = text.getText().toString();
                String a[] = application.getAccounts();
                if ( ! (account.equals("") || account.equals("New Account")
                        || account.equals("Challenge") || account.length() < 3)) {
                    a[0] = account;
                    application.setAccounts(a);
                    application.save();
                    set();
                }
                else {
                    // Error:
                    Toast.makeText(context
                            , "Error: Your account cannot be less than 3 chracters/New Account/Challenge. Try again."
                            , Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialogButton = (ImageButton) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Create a new account (new file in internal storage).
     * Gather necessary information by using dialog.
     */
    public void createNewTwo() {
        // Create and pop up the dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setTitle("Enter Your Name: ");
        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.OK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) dialog.findViewById(R.id.account);
                account = text.getText().toString();
                String a[] = application.getAccounts();
                if ( ! (account.equals("") || account.equals("New Account")
                        || account.equals("Challenge") || account.length() < 3)) {
                    a[1] = account;
                    application.setAccounts(a);
                    application.save();
                    set();
                }
                else {
                    // Error:
                    Toast.makeText(context
                            , "Error: Your account cannot be less than 3 chracters/New Account/Challenge. Try again."
                            , Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialogButton = (ImageButton) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Delete an account (delete a file and update the main file containing account info)
     *
     * @param view
     */
    public void delete(View view) {
        final int viewId;
        String acc;

        switch(view.getId()) {
            case R.id.account1_delete:
                viewId = 1;
                acc = "account1";
                break;
            case R.id.account2_delete:
                viewId = 2;
                acc = "account2";
                break;
            case R.id.challenge_delete:
                viewId = 0;
                acc = "challenge";
                break;
            default:
                viewId = -1;
                acc = "error";
                break;
        }

        if (viewId != -1) {
            Button b = (Button) findViewById(getResources().getIdentifier(acc, "id", getPackageName()));
            account = b.getText().toString();
        }
        else {
            return;
        }

        if (account.equals("New Account")) {
            return;
        }

        // Create and pop up the dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete_custom);
        dialog.setTitle("Are you sure to delete " + account + "?");
        TextView text = (TextView) dialog.findViewById(R.id.account);
        text.setText("All the information about " + account + " will be deleted and will not be able to recover.");

        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.OK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                application.delete(account);
                set();
                dialog.dismiss();
            }
        });

        dialogButton = (ImageButton) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Help button method
     *
     * @param view help button
     */
    public void help(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
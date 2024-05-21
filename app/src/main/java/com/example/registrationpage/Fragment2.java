package com.example.registrationpage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Fragment2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EditText loginEditText, passwordEditText;
        Button loginButton;
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        loginEditText = view.findViewById(R.id.loginField);
        passwordEditText = view.findViewById(R.id.passwordField);
        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String login = loginEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(DBHelper.TABLE_NAME,
                    null, DBHelper.COLUMN_LOGIN + " = ? AND " + DBHelper.COLUMN_PASSWORD + " = ?",
                    new String[]{login, password},
                    null, null, null);

            if (cursor.moveToFirst()) {
                Toast.makeText(getActivity(), "Авторизация прошла успешно", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.putExtra("login", login);

                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            db.close();
        });
        return view;
    }
}
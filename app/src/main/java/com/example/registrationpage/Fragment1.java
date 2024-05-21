package com.example.registrationpage;

import android.content.ContentValues;
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

public class Fragment1 extends Fragment {
    EditText loginEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        loginEditText = view.findViewById(R.id.loginField);
        passwordEditText = view.findViewById(R.id.passwordField);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordField);
        registerButton = view.findViewById(R.id.regButton);

        registerButton.setOnClickListener(v -> {
            String login = loginEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            } else if(!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(DBHelper.TABLE_NAME,
                    null, DBHelper.COLUMN_LOGIN + " = ?", new String[]{login},
                    null, null, null);

            if (cursor.moveToFirst()) {
                Toast.makeText(getActivity(), "Вы уже зарегистрированы", Toast.LENGTH_SHORT).show();
            } else {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COLUMN_LOGIN, login);
                values.put(DBHelper.COLUMN_PASSWORD, password);
                db.insert(DBHelper.TABLE_NAME, null, values);
                Toast.makeText(getActivity(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
                getActivity().finish();
            }
            cursor.close();
            db.close();
        });
        return view;
    }
}
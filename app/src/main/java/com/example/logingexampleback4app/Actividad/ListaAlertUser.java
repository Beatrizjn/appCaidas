package com.example.logingexampleback4app.Actividad;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logingexampleback4app.Adaptador.UserAlertAdapter;
import com.example.logingexampleback4app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ListaAlertUser extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private View popupInputDialogView;
    private EditText titleInput;
    private EditText descriptionInput;
    private Button saveTodoButton;
    private Button cancelUserDataButton;

    private FloatingActionButton openInputPopupDialogButton;
    private RecyclerView recyclerView;
    private TextView empty_text;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.lista_usuarios);
            progressDialog = new ProgressDialog(ListaAlertUser.this);
            initListaAlertUser();
            getTodoList();

            openInputPopupDialogButton.setOnClickListener(fabButtonView -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListaAlertUser.this);
            alertDialogBuilder.setTitle("Añadir Contacto alertar");
            alertDialogBuilder.setCancelable(true);
            initPopupViewControls();
            //We are setting our custom popup view by AlertDialog.Builder
            alertDialogBuilder.setView(popupInputDialogView);
    final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            saveTodoButton.setOnClickListener(saveButtonView -> saveTodo(alertDialog));
            cancelUserDataButton.setOnClickListener(cancelButtonView -> alertDialog.cancel());
            });
            }

    private void saveTodo(AlertDialog alertDialog) {
            ParseObject contacto = new ParseObject("contacto");
            if (titleInput.getText().toString().length() != 0 && descriptionInput.getText().toString().length() != 0) {
            alertDialog.cancel();
            progressDialog.show();
            contacto.put("nombreContacto", titleInput.getText().toString());
            String valor = descriptionInput.getText().toString();
            contacto.put("numeroContacto", Integer.parseInt(valor));
            contacto.saveInBackground(e -> {
            progressDialog.dismiss();
            if (e == null) {
            //We saved the object and fetching data again
            getTodoList();
            } else {
            //We have an error.We are showing error message here.
            showAlert("Error", e.getMessage());
            }
            });
            } else {
            showAlert("Error", "Please enter a title and description");
            }
            }

    private void initListaAlertUser() {
            recyclerView = findViewById(R.id.recyclerView);
            empty_text = findViewById(R.id.empty_text);
            openInputPopupDialogButton = findViewById(R.id.fab);
    }

    private void getTodoList() {
            progressDialog.show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("contacto");
            //We use this code to fetch data from newest to oldest.
            query.orderByDescending("createdAt");
            query.findInBackground((objects, e) -> {
                progressDialog.dismiss();
                if (e == null) {
                    //We are initializing Todo object list to our adapter
                    initTodoList(objects);
                } else {
                    showAlert("Error", e.getMessage());
                }
            });
    }

    private void initTodoList(List<ParseObject> list) {
            if (list == null || list.isEmpty()) {
                empty_text.setVisibility(View.VISIBLE);
                return;
            }

            empty_text.setVisibility(View.GONE);

            UserAlertAdapter adapter = new UserAlertAdapter(list, this);

            adapter.onDeleteListener.observe(this, parseObject -> {
                progressDialog.show();
                parseObject.deleteInBackground(e -> {
                    progressDialog.dismiss();
                    if (e == null) {
                        //We deleted the object and fetching data again.
                        getTodoList();
                    } else {
                        showAlert("Error", e.getMessage());
                    }
                });
            });

            adapter.onEditListener.observe(this, parseObject -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListaAlertUser.this);
                alertDialogBuilder.setTitle("Actualizar contacto");
                alertDialogBuilder.setCancelable(true);
                //We are initializing PopUp Views with title and description parameters of Parse Object
                initPopupViewControls(parseObject.getString("nombreContacto"), parseObject.getInt("numeroContacto"));
                alertDialogBuilder.setView(popupInputDialogView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                saveTodoButton.setOnClickListener(saveTodoButtonView -> {
                    if (titleInput.getText().toString().length() != 0 && descriptionInput.getText().toString().length() != 0) {
                        alertDialog.cancel();
                        progressDialog.show();
                        parseObject.put("nombreContacto", titleInput.getText().toString());
                        String valor = descriptionInput.getText().toString();
                        parseObject.put("numeroContacto", Integer.parseInt(valor));
                        parseObject.saveInBackground(e1 -> {
                            progressDialog.dismiss();
                            if (e1 == null) {
                                getTodoList();
                            } else {
                                showAlert("Error", e1.getMessage());
                            }
                        });
                    } else {
                        showAlert("Error", "Please enter a name and phone number");
                    }
                });

                cancelUserDataButton.setOnClickListener(cancelButtonView -> alertDialog.cancel());
            });


            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            }

    private void initPopupViewControls() {
            LayoutInflater layoutInflater = LayoutInflater.from(ListaAlertUser.this);
            popupInputDialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null);
            titleInput = popupInputDialogView.findViewById(R.id.titleInput);
            descriptionInput = popupInputDialogView.findViewById(R.id.descriptionInput);
            saveTodoButton = popupInputDialogView.findViewById(R.id.button_save_todo);
            cancelUserDataButton = popupInputDialogView.findViewById(R.id.button_cancel_user_data);
            }

    private void initPopupViewControls(String nombre, int number) {
            LayoutInflater layoutInflater = LayoutInflater.from(ListaAlertUser.this);
            popupInputDialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null);
            titleInput = popupInputDialogView.findViewById(R.id.titleInput);
            descriptionInput = popupInputDialogView.findViewById(R.id.descriptionInput);
            saveTodoButton = popupInputDialogView.findViewById(R.id.button_save_todo);
            cancelUserDataButton = popupInputDialogView.findViewById(R.id.button_cancel_user_data);

            titleInput.setText(nombre);
            descriptionInput.setText(String.valueOf(number));
            }

    private void showAlert(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ListaAlertUser.this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(ListaAlertUser.this, ListaAlertUser.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            });
            AlertDialog ok = builder.create();
            ok.show();
            }

    public void pagPrincipal(View view)
    {
           Intent pagPrincipal = new Intent(this, MainActivity.class);
           startActivity(pagPrincipal);
    }
}

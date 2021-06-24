package com.example.passwordgeneratorv2.settings.options;


import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterDeletedPasswords;
import com.example.passwordgeneratorv2.adapters.AdapterPasswords;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.models.Password;
import com.google.android.material.snackbar.Snackbar;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class DeletedPasswords extends AppCompatActivity implements Observer {
    private RecyclerView recyclerDeletedPasswords;
    private DeletedPasswordsViewModel deletedHelper;
    private AdapterDeletedPasswords adapterPasswords;
    private MenuItem menuLockUnlock;
    private Password deletedPassword = null;
    private int deletedPasswordPosition;
    private boolean deleteConfirmation = true;
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_passwords);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toast = new Toast(this);
        deletedHelper = new DeletedPasswordsViewModel();

        deletedHelper.addObserver(this);
        deletedHelper.loadList();
        //Configuring Recycler
        recyclerDeletedPasswords = findViewById(R.id.recyclerDeletedPasswords);
        recyclerDeletedPasswords.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerDeletedPasswords.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterPasswords = new AdapterDeletedPasswords(this, deletedHelper.getPasswordList());
        recyclerDeletedPasswords.setAdapter(adapterPasswords);
        new ItemTouchHelper(getItemHelperCallback()).attachToRecyclerView(recyclerDeletedPasswords);

    }

    @Override
    protected void onStop() {
        deletedHelper.removeListener();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (adapterPasswords.isShowMenu()) {
            adapterPasswords.closeMenu();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_passwords_fragments, menu);
        menu.findItem(R.id.menuSettings).setVisible(false);
        menuLockUnlock = menu.findItem(R.id.menuLockUnlock);
        if (AdapterPasswords.isUnlocked()) {
            menuLockUnlock.setIcon(R.drawable.ic_open_padlock);
        }
        menuLockUnlock.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menuLockUnlock) {
            if (AdapterPasswords.isUnlocked()) {
                lockPasswords();
            } else {
                unlockPasswords();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(DeletedPasswordsViewModel.ARG_LIST_UPDATE)) {
            adapterPasswords.notifyDataSetChanged();
        }
    }

    public void lockPasswords() {
        AdapterPasswords.setUnlocked(false);
        menuLockUnlock.setIcon(R.drawable.ic_padlock);
        adapterPasswords.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void unlockPasswords() {
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;
        Executor executor = getMainExecutor();

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AdapterPasswords.setUnlocked(true);
                menuLockUnlock.setIcon(R.drawable.ic_open_padlock);
                adapterPasswords.notifyDataSetChanged();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock the app")
                .setDescription("Unlock the App to manage your passwords")
                .setDeviceCredentialAllowed(true)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private ItemTouchHelper.SimpleCallback getItemHelperCallback() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.START | ItemTouchHelper.END) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (AdapterPasswords.isUnlocked()) {
                    deletedPassword = adapterPasswords.getPasswordAt(viewHolder.getAdapterPosition());
                    deletedPasswordPosition = viewHolder.getAdapterPosition();
                    adapterPasswords.removeItemAt(viewHolder.getAdapterPosition());

                    if (direction == ItemTouchHelper.START) {
                        Log.i("AdapterDeleted", "SwipedStart");
                        showSnackRestore();
                    } else {
                        Log.i("AdapterDeleted", "SwipedEnd");
                        showSnackDelete();

                    }
                } else {
                    toast.cancel();
                    toast = Toast.makeText(DeletedPasswords.this, "Unlock the App first", Toast.LENGTH_SHORT);
                    toast.show();
                    adapterPasswords.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(DeletedPasswords.this, android.R.color.holo_green_light))
                        .addSwipeLeftActionIcon(R.drawable.ic_restore)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(DeletedPasswords.this, android.R.color.holo_red_light))
                        .addSwipeRightActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();
            }
        };
        return callback;
    }

    private void showSnackDelete() {
        View.OnClickListener clickListener = v -> {
            adapterPasswords.addItemAt(deletedPasswordPosition, deletedPassword);
            deleteConfirmation = false;
        };
        Snackbar.Callback callback = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (deleteConfirmation) {
                    FirebaseHelper.getUserDatabaseReference()
                            .child("deletedPasswords")
                            .child(deletedPassword.getSite()).removeValue();
                }
                deleteConfirmation = true;
            }
        };
        View snackView = findViewById(android.R.id.content);
        Snackbar.make(snackView, deletedPassword.getSite() + "'s password deleted ", Snackbar.LENGTH_SHORT)
                .addCallback(callback)
                .setActionTextColor(ContextCompat.getColor(DeletedPasswords.this, R.color.teal_200))
                .setAction("Undo", clickListener)
                .show();

    }

    private void showSnackRestore() {
        View.OnClickListener clickListener = v -> {
            adapterPasswords.addItemAt(deletedPasswordPosition, deletedPassword);
            deleteConfirmation = false;
        };
        Snackbar.Callback callback = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (deleteConfirmation) {
                    //Deleting the password from the deleted
                    FirebaseHelper.getUserDatabaseReference()
                            .child("deletedPasswords")
                            .child(deletedPassword.getSite()).removeValue();
                    //Putting the password on the main
                    FirebaseHelper.getUserPasswordsReference()
                            .child(deletedPassword.getSite())
                            .setValue(deletedPassword);
                    //Removing the deletedTime
                    FirebaseHelper.getUserPasswordsReference()
                            .child(deletedPassword.getSite())
                            .child("deletedTime")
                            .removeValue();
                }
                deleteConfirmation = true;
            }
        };
        View snackView = findViewById(android.R.id.content);
        Snackbar.make(snackView, deletedPassword.getSite() + " moved to your main passwords ", Snackbar.LENGTH_SHORT)
                .addCallback(callback)
                .setActionTextColor(ContextCompat.getColor(DeletedPasswords.this, R.color.teal_200))
                .setAction("Undo", clickListener)
                .show();
    }
}


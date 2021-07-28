package com.example.passwordgeneratorv2.settings.deleted_passwords;


import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterDeletedPasswords;
import com.example.passwordgeneratorv2.adapters.AdapterPasswords;
import com.example.passwordgeneratorv2.firebase.PasswordsDB;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class DeletedPasswords extends AppCompatActivity {
    private RecyclerView recyclerDeletedPasswords;
    private DeletedPasswordsViewModel model;
    private AdapterDeletedPasswords adapter;
    private MenuItem menuLockUnlock;
    private Password deletedPassword = null;
    private int deletedPasswordPosition;
    private boolean deleteConfirmation = true;
    private ToastH toastH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_passwords);

        toastH = new ToastH(this);
        model = new ViewModelProvider(this).get(DeletedPasswordsViewModel.class);
        initToolbar();
        initRecycler();
    }

    private void initRecycler() {
        recyclerDeletedPasswords = findViewById(R.id.recyclerDeletedPasswords);
        recyclerDeletedPasswords.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerDeletedPasswords.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new AdapterDeletedPasswords();
        model.getPasswordList().observe(this, list -> {
            adapter.updateList(list);
        });
        recyclerDeletedPasswords.setAdapter(adapter);
        new ItemTouchHelper(getItemHelperCallback()).attachToRecyclerView(recyclerDeletedPasswords);
    }

    private void initToolbar() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        model.detachListeners();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (adapter.isShowMenu()) adapter.closeMenu();
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_passwords_fragments, menu);
        menu.findItem(R.id.menuSettings).setVisible(false);
        menuLockUnlock = menu.findItem(R.id.menuToggleDeletedLock);
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
        } else if (item.getItemId() == R.id.menuToggleDeletedLock) {
            if (AdapterPasswords.isUnlocked()) {
                lockPasswords();
            } else {
                unlockPasswords();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void lockPasswords() {
        AdapterPasswords.setUnlocked(false);
        menuLockUnlock.setIcon(R.drawable.ic_padlock);
        adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
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
                    deletedPassword = adapter.getPasswordAt(viewHolder.getAdapterPosition());
                    deletedPasswordPosition = viewHolder.getAdapterPosition();
                    adapter.removeItemAt(viewHolder.getAdapterPosition());

                    if (direction == ItemTouchHelper.START) {
                        Log.i("AdapterDeleted", "SwipedStart");
                        showSnackRestore();
                    } else {
                        Log.i("AdapterDeleted", "SwipedEnd");
                        showSnackDelete();

                    }
                } else {
                    toastH.showToast(getString(R.string.unlock_first));
                    adapter.notifyDataSetChanged();
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
            adapter.addItemAt(deletedPasswordPosition, deletedPassword);
            deleteConfirmation = false;
        };
        Snackbar.Callback callback = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (deleteConfirmation) {
                    new PasswordsDB().deletePermanently(deletedPassword);
                }
                deleteConfirmation = true;
            }
        };
        View snackView = findViewById(android.R.id.content);
        Snackbar.make(snackView, deletedPassword.getSiteName() + "'s password deleted ", Snackbar.LENGTH_SHORT)
                .addCallback(callback)
                .setActionTextColor(ContextCompat.getColor(DeletedPasswords.this, R.color.teal_200))
                .setAction("Undo", clickListener)
                .show();

    }

    private void showSnackRestore() {
        View.OnClickListener clickListener = v -> {
            adapter.addItemAt(deletedPasswordPosition, deletedPassword);
            deleteConfirmation = false;
        };
        Snackbar.Callback callback = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (deleteConfirmation) {
                    new PasswordsDB().restorePassword(deletedPassword);
                }
                deleteConfirmation = true;
            }
        };
        View snackView = findViewById(android.R.id.content);
        Snackbar.make(snackView, deletedPassword.getSiteName() + " moved to your main passwords ", Snackbar.LENGTH_SHORT)
                .addCallback(callback)
                .setActionTextColor(ContextCompat.getColor(DeletedPasswords.this, R.color.teal_200))
                .setAction("Undo", clickListener)
                .show();
    }
}


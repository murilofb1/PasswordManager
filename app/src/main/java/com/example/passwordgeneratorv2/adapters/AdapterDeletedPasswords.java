package com.example.passwordgeneratorv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.models.Password;

import java.util.List;

public class AdapterDeletedPasswords extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Password> passwordList;
    private Context context;
    public static int HIDE_MENU = 0;
    public static int SHOW_DELETE_MENU = 1;
    public static int SHOW_RESTORE_MENU = 2;

    public AdapterDeletedPasswords(Context context) { this.context = context; }

    public void updateList(List<Password> passwords) {
        this.passwordList = passwords;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Password password = passwordList.get(position);
        if (password.getShowMenu() == SHOW_DELETE_MENU) {
            return SHOW_DELETE_MENU;
        } else if (password.getShowMenu() == SHOW_RESTORE_MENU) {
            return SHOW_RESTORE_MENU;
        }
        return HIDE_MENU;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_deleted_passwords, parent, false);

        if (viewType == SHOW_DELETE_MENU) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_deleted_menu_delete, parent, false);
            return new ConfirmDeleteViewHolder(view);
        } else if (viewType == SHOW_RESTORE_MENU) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_deleted_menu_restore, parent, false);
            return new RestorePasswordViewHolder(view);
        }

        return new DeletedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Password password = passwordList.get(position);
        if (holder instanceof DeletedViewHolder) {
            DeletedViewHolder newHolder = (DeletedViewHolder) holder;
            newHolder.textSitename.setText(password.getSite());
            if (AdapterPasswords.isUnlocked()) {
                newHolder.textPassword.setText(Base64H.decode(password.getPassword()));
            } else {
                String maskedpassword = AdapterPasswords.maskedPassword(Base64H.decode(password.getPassword()));
                newHolder.textPassword.setText(maskedpassword);
            }

            Glide.with(holder.itemView)
                    .load(password.getIconLink())
                    .placeholder(R.drawable.default_image)
                    .into(newHolder.imgSiteIcon);
        } else if (holder instanceof ConfirmDeleteViewHolder) {
            ConfirmDeleteViewHolder newHolder = (ConfirmDeleteViewHolder) holder;
        } else if (holder instanceof RestorePasswordViewHolder) {
            RestorePasswordViewHolder newHolder = (RestorePasswordViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    public void showMenu(int type, int position) {
        for (int i = 0; i < passwordList.size(); i++) {
            passwordList.get(i).setShowMenu(HIDE_MENU);
        }
        passwordList.get(position).setShowMenu(type);
        notifyDataSetChanged();
    }


    public boolean isShowMenu() {
        for (int i = 0; i < passwordList.size(); i++) {
            if (passwordList.get(i).getShowMenu() != HIDE_MENU) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < passwordList.size(); i++) {
            passwordList.get(i).setShowMenu(HIDE_MENU);
        }
        notifyDataSetChanged();
    }

    public void removeItemAt(int position) {
        passwordList.remove(position);
        notifyDataSetChanged();
    }

    public void addItemAt(int position, Password password) {
        passwordList.add(position, password);
        notifyDataSetChanged();
    }

    public Password getPasswordAt(int position) {
        return passwordList.get(position);
    }


    public class DeletedViewHolder extends RecyclerView.ViewHolder {
        TextView textSitename;
        TextView textPassword;
        ImageView imgSiteIcon;

        public DeletedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSiteIcon = itemView.findViewById(R.id.imgRecyclerDeletedIcon);
            textSitename = itemView.findViewById(R.id.txtRecyclerDeletedName);
            textPassword = itemView.findViewById(R.id.txtRecyclerDeletedPassword);
        }
    }

    public class ConfirmDeleteViewHolder extends RecyclerView.ViewHolder {

        public ConfirmDeleteViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class RestorePasswordViewHolder extends RecyclerView.ViewHolder {

        public RestorePasswordViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

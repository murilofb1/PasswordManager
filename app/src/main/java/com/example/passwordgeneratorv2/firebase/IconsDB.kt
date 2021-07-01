package com.example.passwordgeneratorv2.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class IconsDB : FirebaseDB() {
    private fun getDefaultIconsReference(): DatabaseReference {
        val reference = rootReference.child(FirebaseKeys.DEFAULT_ICONS_KEY);
        return reference;
    }

    fun loadDefaultIcons(listener: ValueEventListener) {
        val reference = getDefaultIconsReference();
        reference.addListenerForSingleValueEvent(listener)
    }
    /*
    public static void loadDefaultIcons() {
        DatabaseReference reference = FirebaseHelper.getDefaultIconsReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    WebsiteModel model = item.getValue(WebsiteModel.class);
                    getUserIconsReference().child(model.getName()).setValue(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     */
}
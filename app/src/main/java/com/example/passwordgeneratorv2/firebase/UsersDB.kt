package com.example.passwordgeneratorv2.firebase

import com.example.passwordgeneratorv2.constants.FirebaseKeys
import com.example.passwordgeneratorv2.models.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class UsersDB : FirebaseDB() {

    fun getCurrentUserData(eventListener: ValueEventListener) {
        val reference = getCurrentUserReference().child(FirebaseKeys.USER_DATA_KEY)
        reference.addValueEventListener(eventListener)
        addConsult(reference, eventListener)
    }

    fun deleteUser(completeListener: OnCompleteListener<Void>) {
        getCurrentUserReference().child(FirebaseKeys.CUSTOM_ICONS_KEY)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //REMOVING ALL IMAGES THAT THE USER PUTTEND IN THE DATABASE
                    for (item in snapshot.children) {
                        val reference = FirebaseStorage.getInstance().getReference(item.value.toString())
                        reference.delete()
                    }
                    //REMOVING THE USER DATA FROM DATABASE
                    getCurrentUserReference().removeValue()
                    //REMOVING USER FROM AUTHENTICATION
                    FirebaseAuthentication().deleteUser(completeListener)

                    /* // THE COMPLETE LISTENER
                        Toast.makeText(activity, "Your account is now deleted", Toast.LENGTH_SHORT)
                            .show();
                        Intent i = new Intent(
                            activity.getApplicationContext(),
                            AuthenticationActivity.class);
                        activity.startActivity(i);
                        activity.finishAffinity();

                         */
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateCurrentUser(user: UserModel) {
        getCurrentUserReference().child(FirebaseKeys.USER_DATA_KEY).setValue(user.toMap())
    }
}
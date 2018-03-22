package com.chtti.firebaselab1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;
import java.util.List;

//keytool -exportcert -alias androiddebugkey -keystore debug.keystore | openssl sha1 -binary | openssl base64
//o8NwLbcLcL7lqd4M3T1gWDB4RT8=
//2004475962915919
//fd51e752a6bb35a08aef5f68fabd192d
public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private void checkLogin(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            String email = auth.getCurrentUser().getEmail();
            String provider = auth.getCurrentUser().getProviderId();
            String uid = auth.getCurrentUser().getUid();
            String message = String.format("[%s],uid=%s, email=%s",provider,uid, email);
            displaySnake(message);
            //Toast.makeText(this,"user logined",Toast.LENGTH_LONG).show();
        }else{
            AuthUI.IdpConfig emailCofig = new AuthUI.IdpConfig.EmailBuilder().build();
            AuthUI.IdpConfig phoneConfig = new AuthUI.IdpConfig.PhoneBuilder().build();
            AuthUI.IdpConfig googleConfig = new AuthUI.IdpConfig.GoogleBuilder().build();
            AuthUI.IdpConfig fbConfig = new AuthUI.IdpConfig.FacebookBuilder().build();
            AuthUI.IdpConfig twitterConfig = new AuthUI.IdpConfig.TwitterBuilder().build();

            List allLogin = Arrays.asList(emailCofig, phoneConfig, googleConfig, fbConfig, twitterConfig);

            Intent intent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(allLogin).build();
            startActivityForResult(intent, RC_SIGN_IN);
            Toast.makeText(this,"user not login",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){
                checkUserIsNew();
                //Toast.makeText(this,"login successful",Toast.LENGTH_LONG).show();
            }else{
                if (response ==null){
                    displaySnake("HI, you cancel login");
                }else if (response.getErrorCode() == ErrorCodes.NO_NETWORK){
                    displaySnake("Network Error");
                }else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    displaySnake("UNKNOWN_ERROR");
                }
                displaySnake("other Error");
                displaySnake("requestCode "+ requestCode + "/"+RESULT_OK);
            }
        }
    }

    private void checkUserIsNew() {
        FirebaseUserMetadata userMetadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
        if(userMetadata.getCreationTimestamp() == userMetadata.getLastSignInTimestamp()){
            displaySnake("user is new");
        }else{
            displaySnake("Welcome back");
        }
    }

    private void displaySnake(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content)
                , message
                , Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.get_auth:
                checkLogin();
                break;
            case R.id.logout:
                doLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doLogout() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new myCompleteListener());
    }

    private class myCompleteListener implements com.google.android.gms.tasks.OnCompleteListener<Void> {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            displaySnake("SignOut Successful");
        }
    }
}

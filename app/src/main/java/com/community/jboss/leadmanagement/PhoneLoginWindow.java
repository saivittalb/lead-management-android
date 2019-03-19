package com.community.jboss.leadmanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.community.jboss.leadmanagement.main.MainActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginWindow extends Dialog implements View.OnClickListener {

    TextInputEditText phone;
    TextInputEditText code;

    private Button submit;
    private Button verificationBtn;

    TextView validHint;

    private String sentCode;
    private Context context;

    private PhoneAuthProvider phoneAuth;
    private FirebaseAuth mAuth;

    public PhoneLoginWindow(Context context) {
        super(context);
        this.context = context;
        this.setContentView(R.layout.phone_sign_in);

        phoneAuth = PhoneAuthProvider.getInstance();
        mAuth = FirebaseAuth.getInstance();

        validHint = this.findViewById(R.id.codeValideHint);
        phone = this.findViewById(R.id.phone_input);
        code = this.findViewById(R.id.phone_verification);
        submit = this.findViewById(R.id.phone_submit);
        verificationBtn = this.findViewById(R.id.verificationButton);

        verificationBtn.setOnClickListener(view -> {
            if (code.getText() == null) return;
            String codeInput = code.getText().toString();
            verifyCode(codeInput);
        });

        submit.setOnClickListener(this);

        this.show();
    }


    @Override
    public void onClick(View view) {
        sendMobileVerification(phone);
        showCodeVerification(true);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                validHint.setText("Code is valid for next: " + millisUntilFinished / 1000+"s");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                showCodeVerification(false);
            }

        }.start();
    }


    private void showCodeVerification(boolean status){
        if(!status) {
            code.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            verificationBtn.setVisibility(View.GONE);
            validHint.setVisibility(View.GONE);
        }else{
            code.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
            verificationBtn.setVisibility(View.VISIBLE);
            validHint.setVisibility(View.VISIBLE);
        }
    }

    private void sendMobileVerification(TextInputEditText numberField){
        if(numberField.getText() == null) return;
        String number = numberField.getText().toString();

        if(number.isEmpty()){
            numberField.setError("Woops, please enter your phone number!");
            return;
        }

        if(number.length() < 8){
            numberField.setError("Please enter valid phone number!");
            return;
        }

        phoneAuth.verifyPhoneNumber(number, 60, TimeUnit.SECONDS, (Activity) context, verificationCallback);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            showCodeVerification(false);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sentCode = s;
        }
    };

    private void verifyCode(String codeInput){
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sentCode, codeInput);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e) {
            Toast.makeText(context, "Unknown Error occurred! Please try again!", Toast.LENGTH_SHORT).show();
            showCodeVerification(false);
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) this.context, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            code.setError("Invalid Code!");
                        }else{
                            Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        showCodeVerification(false);
                    }
                });
    }
}

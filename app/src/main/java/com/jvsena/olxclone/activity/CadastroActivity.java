package com.jvsena.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.jvsena.olxclone.R;
import com.jvsena.olxclone.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail,campoSenha;
    private Switch tipoAcesso;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //Configurações iniciais
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty() || !senha.isEmpty()){

                    //Verificar estado do switch
                    if (tipoAcesso.isChecked()){
                       //Fazer cadastro
                       firebaseAuth.createUserWithEmailAndPassword(
                             email,senha
                       ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                               }else {
                                   String erroExcecao = "";
                                   try {
                                       throw task.getException();
                                   }catch (FirebaseAuthWeakPasswordException e){
                                       erroExcecao = "Digite uma senha mais forte!";
                                   }catch (FirebaseAuthInvalidCredentialsException e){
                                       erroExcecao = "esta conta já foi cadastrada";
                                   }catch (Exception e){
                                       erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                       e.printStackTrace();
                                   }
                               }
                           }
                       });
                    }else {
                       //Fazer Login
                       firebaseAuth.signInWithEmailAndPassword(
                               email,senha
                       ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(CadastroActivity.this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(getApplicationContext(),AnunciosActivity.class));
                               }else {
                                   Toast.makeText(CadastroActivity.this, "Erro ao fazer login: " + task.getException() , Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                    }

                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);
        botaoAcessar = findViewById(R.id.buttonAcesso);
    }

}

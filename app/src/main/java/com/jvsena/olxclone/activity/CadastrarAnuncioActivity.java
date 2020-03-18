package com.jvsena.olxclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.jvsena.olxclone.R;
import com.santalu.maskedittext.MaskEditText;

import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        inicializarComponentes();
    }

    private void inicializarComponentes(){
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);

        //Configurar localidade para pt-Br
        Locale locale = new Locale("pt","BR");
        campoValor.setLocale(locale);
    }

    public void salvarAnuncio(View view){
        String valor = campoValor.getText().toString();
    }


}

package com.jvsena.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.jvsena.olxclone.R;
import com.jvsena.olxclone.helper.Permissoes;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private ImageView imagem1, imagem2, imagem3;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private Spinner campoEstado, campoCategoria;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponentes();
        carregarDadosSpinner();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }

    }

    public void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no imageview
            if (requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);
            }else if (requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }else if (requestCode ==3){
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }

    }

    private void inicializarComponentes() {
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);

        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        //Configurar localidade para pt-Br
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }

    private void carregarDadosSpinner(){
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        campoEstado.setAdapter(adapter);

        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,
                categorias
        );
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_item);
        campoCategoria.setAdapter(adapterCategoria);
    }

    public void validarDadosAnuncio(View view){
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = String.valueOf(campoValor.getRawValue());
        String telefote = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        if (listaFotosRecuperadas.size() != 0){

            if (estado.isEmpty() || categoria.isEmpty() || titulo.isEmpty() || valor.isEmpty() || valor.equals("0") || telefote.isEmpty() || descricao.isEmpty() ){
                exibirMensagemErro("Preencha todos os campos!");
            }else {
                salvarAnuncio();
            }

        }else {
            exibirMensagemErro("Selecione ao menos uma imagem!");
        }

    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void salvarAnuncio() {
        String valor = campoValor.getText().toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário acessar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

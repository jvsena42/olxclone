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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jvsena.olxclone.R;
import com.jvsena.olxclone.helper.ConfiguracaoFirebase;
import com.jvsena.olxclone.helper.Permissoes;
import com.jvsena.olxclone.model.Anuncio;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private ImageView imagem1, imagem2, imagem3;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private Spinner campoEstado, campoCategoria;
    private Anuncio anuncio;
    private android.app.AlertDialog dialog;


    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Configuracoes iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();

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

    private Anuncio configurarAnuncio(){
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefote = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefote);
        anuncio.setDescricao(descricao);

        return anuncio;
    }

    public void salvarAnuncio() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anuncio")
                .setCancelable(false)
                .build();
        dialog.show();

        //Salvar imagem no storage
        int tamanhoLista = listaFotosRecuperadas.size();
        for (int i=0; i<tamanhoLista; i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            salvarFotosStorage(urlImagem,tamanhoLista,i);
        }

    }

    public void salvarFotosStorage(String urlString, final int totalFotos, int contador){

        //Criar nó no storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                                                .child("anuncios")
                                                .child(anuncio.getIdAnuncio())
                                                .child("imagem"+contador);

        //Fazer upload no firebase Storage
        final UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida = uri.toString();      //Esta url funciona!!!
                        listaUrlFotos.add( urlConvertida );

                        //Testa finalização de upload das imagens
                        if ( listaUrlFotos.size() == totalFotos  ){ //todas as fotos salvas
                            anuncio.setFotos( listaUrlFotos );
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }
                        exibirMensagemErro("Anuncio salvo con sucesso!");
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       exibirMensagemErro("Falha ao fazer upload!");
                        Log.i("INFO","Falha ao fazer upload: "+ e.getMessage());
                    }
                });

    }});}



    public void validarDadosAnuncio(View view){

        anuncio = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());


        if (listaFotosRecuperadas.size() != 0){

            if (anuncio.getEstado().isEmpty() || anuncio.getCategoria().isEmpty() || anuncio.getTitulo().isEmpty() || valor.isEmpty()
                    || valor.equals("0") || anuncio.getTelefone().isEmpty() || anuncio.getDescricao().isEmpty() ){
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

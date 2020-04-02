package com.jvsena.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jvsena.olxclone.R;
import com.jvsena.olxclone.adapter.AdaperAnuncios;
import com.jvsena.olxclone.helper.ConfiguracaoFirebase;
import com.jvsena.olxclone.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttosCategoria;
    private AdaperAnuncios adaperAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        //Configuracoes iniciais
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("anuncios");

        //Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adaperAnuncios = new AdaperAnuncios(listaAnuncios,this);
        recyclerAnunciosPublicos.setAdapter(adaperAnuncios);

        recuperarAnunciosPublicos();
    }

    public void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Anuncios")
                .setCancelable(false)
                .build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot estados : dataSnapshot.getChildren()){
                    for (DataSnapshot categorias: estados.getChildren()){
                        for (DataSnapshot anuncios: categorias.getChildren()){
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);


                        }
                    }
                }

                Collections.reverse(listaAnuncios);
                adaperAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (firebaseAuth.getCurrentUser() == null){
            menu.setGroupVisible(R.id.group_deslogado,true);
        }else {
            menu.setGroupVisible(R.id.group_logado,true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                firebaseAuth.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_cadastrar:
                Intent intent = new Intent(getApplicationContext(),CadastroActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
    }
}

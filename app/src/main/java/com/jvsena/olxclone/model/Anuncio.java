package com.jvsena.olxclone.model;

import com.google.firebase.database.DatabaseReference;
import com.jvsena.olxclone.helper.ConfiguracaoFirebase;

import java.util.List;

public class Anuncio {

    private String estado, categoria, titulo, valor, telefone, descricao;
    private String idAnuncio;
    private List<String> fotos;

    public Anuncio() {

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                        .child("meus_anuncios");
        setIdAnuncio(anuncioRef.push().getKey());
    }

    public void salvar(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meus_anuncios");

        //Salvar meus anuncios
        anuncioRef.child(idUsuario)
                .child(getIdAnuncio())
                .setValue(this);

    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}

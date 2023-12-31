package org.univaq.swa.auleweb.aulewebrest.model;

import java.util.ArrayList;
import java.util.List;

public class Aula {

    private String nome;
    private String luogo;
    private String edificio;
    private int piano;
    private int capienza;
    private String email_responsabile;
    private int numero_prese_rete;
    private int numero_prese_elettriche;
    private String note;

    private List<Attrezzatura> attrezzatura;

    public Aula() {
        nome = "";
        luogo = "";
        edificio = "";
        piano = 0;
        capienza = 0;
        email_responsabile = "";
        numero_prese_rete = 0;
        numero_prese_elettriche = 0;
        note = "";
        attrezzatura = new ArrayList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public int getPiano() {
        return piano;
    }

    public void setPiano(int piano) {
        this.piano = piano;
    }

    public int getCapienza() {
        return capienza;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public String getEmailResponsabile() {
        return email_responsabile;
    }

    public void setEmailResponsabile(String email_responsabile) {
        this.email_responsabile = email_responsabile;
    }

    public int getNumeroPreseRete() {
        return numero_prese_rete;
    }

    public void setNumeroPreseRete(int numero_prese_rete) {
        this.numero_prese_rete = numero_prese_rete;
    }

    public int getNumeroPreseElettriche() {
        return numero_prese_elettriche;
    }

    public void setNumeroPreseElettriche(int numero_prese_elettriche) {
        this.numero_prese_elettriche = numero_prese_elettriche;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Attrezzatura> getAttrezzatura() {
        return attrezzatura;
    }

    public void setAttrezzatura(List<Attrezzatura> attrezzatura) {
        this.attrezzatura = attrezzatura;
    }

    public void addAttrezzatura(Attrezzatura attr) {
        this.attrezzatura.add(attr);
    }

}

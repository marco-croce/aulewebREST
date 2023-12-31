package org.univaq.swa.auleweb.aulewebrest.model;

public class Amministratore {

    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String telefono;

    public Amministratore() {
        nome = "";
        cognome = "";
        email = "";
        password = "";
        telefono = "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}

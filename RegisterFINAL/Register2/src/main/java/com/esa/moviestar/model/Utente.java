package com.esa.moviestar.model;

import javafx.scene.Node;

public class Utente {

    private int codUtente;         // PRIMARY KEY
    private String nome;
    private String gusti;
    private int ImmagineProfilo;        // FOREIGN KEY verso tabella immagine
    private String email;          // FOREIGN KEY verso tabella Account

    // Costruttore completo (es. da DB)
    public Utente(int codUtente, String nome, String gusti, int ImmagineProfilo, String email) {
        this.codUtente = codUtente;
        this.nome = nome;
        this.gusti = gusti;
        this.ImmagineProfilo = ImmagineProfilo;
        this.email = email;
    }

    public Utente(){}

    // Costruttore senza codUtente (es. prima di inserimento nel DB)
    public Utente(String nome, int ImmagineProfilo, String gusti, String email) {
        this.nome = nome;
        this.ImmagineProfilo = ImmagineProfilo;
        this.gusti = gusti;
        this.email = email;
    }

    // Getter
    public int getCodUtente() { return codUtente; }
    public String getNome() { return nome; }
    public String getGusti() { return gusti; }
    public int getImmagineProfilo() { return ImmagineProfilo; }
    public String getEmail() { return email; }

    // Setter
    public void setCodUtente(int codUtente) { this.codUtente = codUtente; }
    public void setNome(String nome) { this.nome = nome; }
    public void setGusti(String gusti) { this.gusti = gusti; }
    public void setImmagineProfilo(int immagineProfilo) { this.ImmagineProfilo = immagineProfilo; }
    public void setEmail(String email) { this.email = email; }

    // Verifica del nome (rimasta invariata)
    public void verificaNomeValido() {
        if (nome == null || nome.isEmpty()) {
            System.out.println("Nome vuoto o nullo");
            return;
        }
        boolean invalid = false;
        char[] caratteri = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
                ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
        };
        for (int i = 0; i < nome.length(); i++) {
            char c = nome.charAt(i);
            for (char carattereVietato : caratteri) {
                if (c == carattereVietato) {
                    invalid = true;
                    break;
                }
            }
            if (invalid) break;
        }
        if (invalid) {
            System.out.println("Il nome ha un carattere non valido");
        } else {
            System.out.println("Il nome ha tutti caratteri ammessi");
        }
    }
}

package com.esa.moviestar.model;

import com.esa.moviestar.Profile.IconSVG;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class Profile {

    private   int codUtente;
    private   String nome;
    private  String taste = "";
    private int lastWatched =5;
    private Group profileIcon;
    //Costruttore
    public Profile(int codUtente,String nome,String taste, int profileIcon){
        this.codUtente=codUtente;
        this.nome=nome;
        this.taste =taste;
        this.profileIcon = IconSVG.takeElement(profileIcon);
    }

    public Group getIcon() {
        return profileIcon;

    }
    public String getName() {
        return nome;
    }

    public int getID() {
        return codUtente;
    }

    public String getTaste() {
        return taste;
    }
    public int getLastWatched(){
        return lastWatched;
    }
    public void setTaste(String taste) {
        this.taste = taste;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package curriculumDigital.core;

import java.io.Serializable;

/**
 *
 * @author Rúben
 */
public class Elemento implements Serializable{
    private String data;
    private String entidade;
    public Elemento (){
        this.data="";
        this.entidade="";
    }
    public Elemento (String data,String entidade){
        this.data=data;
        this.entidade=entidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }
    
}

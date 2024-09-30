package br.com.easyorm.example;

import br.com.easyorm.annotation.Column;
import br.com.easyorm.annotation.Entity;
import br.com.easyorm.annotation.Id;

@Entity
public class Usuario {

    @Id
    @Column(columnName = "IdUsuario")
    private Integer id;
    
    @Column(columnName = "NmUsuario")
    private String nome;
    
    @Column(columnName = "DsCpfCnpj")
    private String cpfCnpj;
    
    @Column(columnName = "StAtividade")
    private Integer StAtividade;
    
    public Usuario(String nome, String cpfCnpj, Integer stAtividade) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        StAtividade = stAtividade;
    }

    public Usuario() {}


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getCpfCnpj() {
        return cpfCnpj;
    }


    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }


    public Integer getStAtividade() {
        return StAtividade;
    }


    public void setStAtividade(Integer stAtividade) {
        StAtividade = stAtividade;
    }


    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nome=" + nome + ", cpfCnpj=" + cpfCnpj + ", StAtividade=" + StAtividade + "]";
    }

}

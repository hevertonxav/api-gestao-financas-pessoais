package com.heverton.api_gestao_financas_pessoais.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idCategoria;

    @Column(nullable = false, unique = true, length = 50)
    private String  nome;

    @OneToMany(mappedBy = "categoria")
    private List<Transacao> transacoes = new ArrayList<>();

    public Categoria(){

    }

    public Categoria( UUID idCategoria, String nome) {
        this.idCategoria = idCategoria;
        this.nome = nome;
    }

    public UUID getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(UUID idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }
}

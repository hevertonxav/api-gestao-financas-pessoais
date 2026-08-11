package com.heverton.api_gestao_financas_pessoais.entities;

import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idTransacao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;


    @Column(nullable = false, length = 150)
    private String descricao;

    @Column
    private LocalDate data;

    @Column
    @CreationTimestamp
    private OffsetDateTime dataCriacao;

    @ManyToOne
    private Categoria categoria;

    public Transacao (){

    }

    public Transacao(
            UUID idTransacao,
            BigDecimal valor,
            TipoTransacao tipo,
            String descricao,
            LocalDate data,
            OffsetDateTime dataCriacao,
            Categoria categoria) {

        this.idTransacao = idTransacao;
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = data;
        this.dataCriacao = dataCriacao;
        this.categoria = categoria;
    }

    public UUID getIdTransacao() {

        return idTransacao;
    }

    public void setIdTransacao(UUID idTransacao) {
        this.idTransacao = idTransacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(OffsetDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}

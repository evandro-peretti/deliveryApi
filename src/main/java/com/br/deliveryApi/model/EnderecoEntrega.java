package com.br.deliveryApi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EnderecoEntrega {

    @Column(name = "entrega_logradouro", nullable = false)
    private String logradouro;

    @Column(name = "entrega_numero", nullable = false)
    private String numero;

    @Column(name = "entrega_complemento")
    private String complemento;

    @Column(name = "entrega_bairro", nullable = false)
    private String bairro;

    @Column(name = "entrega_cidade", nullable = false)
    private String cidade;

    @Column(name = "entrega_estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "entrega_estado", nullable = false)
    private String cep;

    public EnderecoEntrega() {}

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}

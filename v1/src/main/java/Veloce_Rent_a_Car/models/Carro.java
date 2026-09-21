package Veloce_Rent_a_Car.models;

public class Carro {
    private String modelo;
    private String placa;
    private String categoria;
    private String valor_da_diaria;
    private String status_de_disponibilidade;

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getValor_da_diaria() {
        return valor_da_diaria;
    }

    public void setValor_da_diaria(String valor_da_diaria) {
        this.valor_da_diaria = valor_da_diaria;
    }

    public String getStatus_de_disponibilidade() {
        return status_de_disponibilidade;
    }

    public void setStatus_de_disponibilidade(String status_de_disponibilidade) {
        this.status_de_disponibilidade = status_de_disponibilidade;
    }
}

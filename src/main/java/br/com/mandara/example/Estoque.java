package br.com.mandara.example;

import java.sql.Timestamp;

import br.com.mandara.annotation.Compound;
import br.com.mandara.annotation.Entity;
import br.com.mandara.annotation.Id;

@Entity
public class Estoque {

    @Id
    public EstoquePK estoquePk;
    
    public Double qtEstoque;
    
    public Estoque() {}
    
    public Estoque(EstoquePK estoquePk, Double qtEstoque) {
        this.estoquePk = estoquePk;
        this.qtEstoque = qtEstoque;
    }

    @Compound
    public static final class EstoquePK {
        
        private Integer idProduto;
        private Integer idEmpresa;
        private Timestamp dtReferencia;

        public EstoquePK() {}
        
        public EstoquePK(Integer idProduto, Integer idEmpresa, Timestamp dtReferencia) {
            this.idProduto = idProduto;
            this.idEmpresa = idEmpresa;
            this.dtReferencia = dtReferencia;
            
        }

        public Integer getIdProduto() {
            return this.idProduto;
        }
        
        public Integer getIdEmpresa() {
            return this.idEmpresa;
        }
        
        public Timestamp getDtReferencia() {
            return dtReferencia;
        }

        @Override
        public String toString() {
            return "EstoquePK [idProduto=" + idProduto + ", idEmpresa=" + idEmpresa + ", dtReferencia=" + dtReferencia
                    + "]";
        }
    
        
        
    }

    @Override
    public String toString() {
        return "Estoque [estoquePk=" + estoquePk + ", qtEstoque=" + qtEstoque + "]";
    }
    
    
    
}

package br.com.easyorm.example;

import java.sql.Timestamp;

import br.com.easyorm.annotation.Compound;
import br.com.easyorm.annotation.Entity;
import br.com.easyorm.annotation.Id;

@Entity
public class Estoque {

    @Id
    public EstoquePK estoquePk;
    
    public Double qtEstoque;
    
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
        
    }
    
}

package model;

import controller.algoritmos.Ibs;
import model.enums.Estado;
import util.Config;

import java.util.ArrayList;

public class IbsCore {

    private IbsProcesso ibsProcesso;
    private ArrayList<IbsProcesso> aptos;
    private IbsProcesso finalizado;

    public IbsCore() {
        ibsProcesso = null;
        aptos = new ArrayList<>();

    }

    public void atualizar() {
        verificarSeDeveFinalizar();
        verificarSeDeveExecutar();
    }

    public ArrayList<IbsProcesso> getAptos() {
        return aptos;
    }

    private void verificarSeDeveFinalizar() {
        if (isProcessoExecutando()) {
            if (ibsProcesso.getEnd() == Config.timeIBS) {
                finalizado = ibsProcesso;
                finalizado.setEstado(Estado.FINALIZADO.getValor());
                ibsProcesso = null;
            }
        }
    }

    private void verificarSeDeveExecutar() {
        if (!aptos.isEmpty()) {
            if (aptos.get(0).getStart() == Config.timeIBS - 1) {
                ibsProcesso = aptos.get(0);
                ibsProcesso.setEstado(Estado.EXECUTANDO.getValor());
                aptos.remove(0);
            }
        }
    }


    public IbsProcesso getFinalizado() {
        return finalizado;
    }

    public void setAptos(ArrayList<IbsProcesso> aptos) {
        this.aptos = aptos;
    }

    public IbsProcesso getIbsProcesso() {
        return ibsProcesso;
    }

    public void setIbsProcesso(IbsProcesso ibsProcesso) {
        this.ibsProcesso = ibsProcesso;
    }

    public void setFinalizado(IbsProcesso finalizado) {
        this.finalizado = finalizado;
    }

    public boolean isProcessoExecutando() {
        return ibsProcesso != null;
    }
}

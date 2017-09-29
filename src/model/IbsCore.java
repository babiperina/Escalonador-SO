package model;

import model.enums.Estado;

import java.util.ArrayList;

public class IbsCore {

    private IbsProcesso ibsProcesso;
    private ArrayList<IbsProcesso> aptos;

    public IbsCore(ArrayList<IbsProcesso> processosAptos) {
        aptos = processosAptos;
        for (IbsProcesso processos :
                aptos) {
            processos.setEstado(Estado.APTO.getValor());
        }
        verificarSeDeveExecutar();
    }

    private void verificarSeDeveExecutar() {
        if (aptos.get(0).getStartTs().equals(System.currentTimeMillis())) {
            ibsProcesso = aptos.get(0);
            ibsProcesso.setEstado(Estado.EXECUTANDO.getValor());
            aptos.remove(0);
        }
    }

    public void setAptos(ArrayList<IbsProcesso> aptos) {
        this.aptos = aptos;
        for (IbsProcesso processos :
                aptos) {
            processos.setEstado(Estado.APTO.getValor());
        }
        verificarSeDeveExecutar();
    }
}

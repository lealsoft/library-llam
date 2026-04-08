package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.dto.RelatorioDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RelatorioService {

    public RelatorioDTO gerarRelatorioEmprestimos(String tenantId, LocalDate dataInicio, LocalDate dataFim) {
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalEmprestimos", 0);
        resumo.put("emprestimosPendentes", 0);
        resumo.put("emprestimosDevolvidos", 0);
        resumo.put("emprestimosAtrasados", 0);

        return RelatorioDTO.builder()
            .tipo("EMPRESTIMOS")
            .dataInicio(dataInicio)
            .dataFim(dataFim)
            .tenantId(tenantId)
            .totalRegistros(0)
            .resumo(resumo)
            .dados(new ArrayList<>())
            .build();
    }

    public RelatorioDTO gerarRelatorioMultas(String tenantId, LocalDate dataInicio, LocalDate dataFim) {
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalMultas", 0);
        resumo.put("multasPendentes", 0);
        resumo.put("multasPagas", 0);
        resumo.put("valorTotalPendente", 0.0);
        resumo.put("valorTotalArrecadado", 0.0);

        return RelatorioDTO.builder()
            .tipo("MULTAS")
            .dataInicio(dataInicio)
            .dataFim(dataFim)
            .tenantId(tenantId)
            .totalRegistros(0)
            .resumo(resumo)
            .dados(new ArrayList<>())
            .build();
    }

    public RelatorioDTO gerarRelatorioAcervo(String tenantId) {
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalTitulos", 0);
        resumo.put("totalExemplares", 0);
        resumo.put("exemplaresDisponiveis", 0);
        resumo.put("exemplaresEmprestados", 0);

        return RelatorioDTO.builder()
            .tipo("ACERVO")
            .tenantId(tenantId)
            .totalRegistros(0)
            .resumo(resumo)
            .dados(new ArrayList<>())
            .build();
    }

    public RelatorioDTO gerarRelatorioLeitores(String tenantId) {
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalLeitores", 0);
        resumo.put("leitoresAtivos", 0);
        resumo.put("leitoresSuspensos", 0);
        resumo.put("leitoresComPendencia", 0);

        return RelatorioDTO.builder()
            .tipo("LEITORES")
            .tenantId(tenantId)
            .totalRegistros(0)
            .resumo(resumo)
            .dados(new ArrayList<>())
            .build();
    }
}

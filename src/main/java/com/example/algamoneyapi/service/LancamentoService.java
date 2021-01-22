package com.example.algamoneyapi.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.algamoneyapi.model.Lancamento;
import com.example.algamoneyapi.model.Pessoa;
import com.example.algamoneyapi.repository.LancamentoRepository;
import com.example.algamoneyapi.repository.PessoaRepository;
import com.example.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired 
	private LancamentoRepository lancamentoRepository;
	
	public Lancamento salvar(Lancamento lancamento) {
		Optional<Pessoa> pessoaOpt = pessoaRepository.findById(lancamento.getPessoa().getCodigo());

	    if (!pessoaOpt.isPresent() || pessoaOpt.get().isInativo()) {
	        throw new PessoaInexistenteOuInativaException();
		}
		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}

		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
	}

	private void validarPessoa(Lancamento lancamento) {
	    Optional<Pessoa> pessoaOpt = null;  

	    if (lancamento.getPessoa().getCodigo() != null) {
	        pessoaOpt = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
	    }
		
	    if (pessoaOpt == null || pessoaOpt.isEmpty() || pessoaOpt.get().isInativo()) {
	        throw new PessoaInexistenteOuInativaException();
	    }
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
	    Optional<Lancamento> lancamentoSalvoOpt = lancamentoRepository.findById(codigo);

	    // se o valor estiver presente, retorna o valor, senão lança uma exceção
	    return lancamentoSalvoOpt.orElseThrow(() -> new IllegalArgumentException()); 
	}
}

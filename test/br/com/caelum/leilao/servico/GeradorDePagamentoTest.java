package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;

public class GeradorDePagamentoTest {
	
	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {
		
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		
		Leilao leilao = new CriadorDeLeilao()
				.para("Tv de Plasma")
				.lance(new Usuario("Joao"), 2000.0)
				.lance(new Usuario("Maria"), 2500.0)
				.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		
		GeradorDePagamento geradorDePagamento = new GeradorDePagamento(leiloes,
				new Avaliador(), pagamentos);
		geradorDePagamento.gera();
		
		ArgumentCaptor<Pagamento> argumentCaptor = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumentCaptor.capture());
		
		Pagamento pagamentoGerado = argumentCaptor.getValue();
		
		assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
	}
}

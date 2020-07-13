package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;
import br.com.caelum.leilao.infra.relogio.Relogio;

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
	
	public void deveEmpurrarPagamentoParaProximoDiaUtil() {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		Leilao leilao = new CriadorDeLeilao()
				.para("Tv de Plasma")
				.lance(new Usuario("Joao"), 2000.0)
				.lance(new Usuario("Maria"), 2500.0)
				.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		
		Calendar sabado = Calendar.getInstance();
        sabado.set(2012, Calendar.APRIL, 7);
		when(relogio.hoje()).thenReturn(sabado);
		
		GeradorDePagamento geradorDePagamento = new GeradorDePagamento(leiloes,
				new Avaliador(), pagamentos);
		geradorDePagamento.gera();
		
		ArgumentCaptor<Pagamento> argumentCaptor = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumentCaptor.capture());
		
		Pagamento pagamentoGerado = argumentCaptor.getValue();
		
		assertEquals(Calendar.MONDAY, 
	            pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
	        assertEquals(9, 
	            pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}
}

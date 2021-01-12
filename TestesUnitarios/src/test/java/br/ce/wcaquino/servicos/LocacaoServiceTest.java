package br.ce.wcaquino.servicos;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		//service = new LocacaoService();
		//this.dao = Mockito.mock(LocacaoDAO.class);
		//service.setLocacaoDAO(dao);
		//this.spc = Mockito.mock(SPCService.class);
		//service.setSPCService(spc);
		//email = Mockito.mock(EmailService.class);
		//service.setEmailService(email);
	}
	
	//@After
	//public void tearDown() {
	//	System.out.println("After");
	//}
	
	//@BeforeClass
	//public static void setupClass() {
	//	System.out.println("Before Class");
	//}
	
	//@AfterClass
	//public static void tearDownClass() {
	//	System.out.println("After Class");
	//}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		// acao
		//		Locacao locacao;
		//		try {
		//			locacao = service.alugarFilme(usuario, filme);
		//			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		//			error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
		//			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(2)), CoreMatchers.is(false));
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//			Assert.fail("Não deveria lancar excecao");
		//		}
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(4.0)));
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(2)), CoreMatchers.is(false));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));
		// acao
		service.alugarFilme(usuario, filmes);
	}
	
	//	@SuppressWarnings("deprecation")
	//	@Test
	//	public void testLocacao_filmeSemEstoque2(){
	//		// cenario
	//		LocacaoService service = new LocacaoService();
	//		Usuario usuario = new Usuario("Usuario 1");
	//		Filme filme = new Filme("Filme 1", 0, 5.0);
	//		// acao
	//		try {
	//			service.alugarFilme(usuario, filme);
	//			Assert.fail("Deveria ter lancado uma excecao");
			//		} catch (Exception e) {
	//			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
	//		}
	//	}
	
	//	@Test
	//	public void testLocacao_filmeSemEstoque3() throws Exception {
	//		// cenario
	//		LocacaoService service = new LocacaoService();
	//		Usuario usuario = new Usuario("Usuario 1");
	//		Filme filme = new Filme("Filme 1", 0, 5.0);
		
	//	exception.expect(Exception.class);
	//	exception.expectMessage("Filme sem estoque");
	//	// acao
	//	service.alugarFilme(usuario, filme);
		
	//}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		// acao
		
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
		
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		// acao
		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0));
		
		// acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		// verificacao
		// 4+4+3 = 11
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(11.0));
	}
	
	@Test
	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
		
		// acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		// verificacao
		// 4+4+3+2 = 13
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(13.0));
	}
	
	@Test
	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0));
		
		// acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		// verificacao
		// 4+4+3+2+1 = 14
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(14.0));
	}
	
	@Test
	public void devePagar0PctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0));
		
		// acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		// verificacao
		// 4+4+3+2+1 = 14
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(14.0));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		// acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		// verificacao
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		Assert.assertTrue(ehSegunda);
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
		
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(this.spc.possuiNegativacao(usuario)).thenReturn(true);
		
		// acao
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
			// verificacao
		} catch (LocadoraException e) {
			Assert.assertEquals(e.getMessage(), "Usuário Negativado");
		}
		
		Mockito.verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario).agora(), 
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora(),
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora()
				);
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		// acao
		service.notificarAtrasos();
		
		// verificacao
		Mockito.verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		Mockito.verify(email).notificarAtraso(usuario);
		Mockito.verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verifyNoMoreInteractions(email);
		Mockito.verifyZeroInteractions(spc);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		// acao
		service.alugarFilme(usuario, filmes);
		
		// verificacao
		
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		
		// acao
		service.prorrogarLocacao(locacao, 3);
		
		// verificacao
		ArgumentCaptor<Locacao> argCaptor = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCaptor.capture());
		Locacao locacaoRetornada = argCaptor.getValue();
		
		Assert.assertEquals(12.0, locacaoRetornada.getValor().doubleValue(), 0.1);
		Assert.assertThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		Assert.assertThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));
		
	}
	
	
	
	
	
	
	
	
	
	
}

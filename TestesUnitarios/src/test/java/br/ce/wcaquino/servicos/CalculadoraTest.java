package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraTest {
	
	private Calculadora calc;
	
	@Before
	public void start() {
		calc = new Calculadora();
	}

	@Test
	public void deveSomarDoisValores() {
		//cenario
		int a = 5;
		int b = 3;
		
		
		//acao
		int resultado = calc.somar(a,b);
		
		//verificacao
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		int a = 8;
		int b = 5;
		
		int resultado = calc.subtrair(a,b);
		
		Assert.assertEquals(3, resultado);
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		int a = 6;
		int b = 3;
		
		int resultado = calc.divide(a,b);
		
		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		int a = 10;
		int b = 0;
		
		calc.divide(a, b);
	}
}

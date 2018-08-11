package exercicio.model;

public class Dados
{
	private String chave;
	private String dados;
	
	public String getChave() {
		return chave;
	}
	public void setChave(String chave) {
		this.chave = chave;
	}
	public String getDados() {
		return dados;
	}
	public void setDados(String dados) {
		this.dados = dados;
	}
	
	
	@Override
	public String toString() {
		return "Dados [chave=" + chave + ", dados=" + dados + "]";
	}
	
	

}

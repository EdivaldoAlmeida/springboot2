package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
		
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil ReportUtil;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa()); //Ver se o erro era aqui
		modelAndView.addObject("pessoas", pessoaRepository.findAll()); /*Essas duas linhas carregam todas*/
//		modelAndView.addObject("pessoaobj", new Pessoa());			/*as pessoas ao abrir a tela de cadastro*/
		return modelAndView;
	}
	
	/*Resumo do método salvar: Sempre que o o botão "salvarpessoa" for clicado o método intercepta a requisição, 
	 * pega os dados do formulátio (por isso ele recebe um parâmetro pessoa) e envia esses dados ao DB, sempre
	 * utilizando a Interface PessoaRepository. Como retorno, ele redireciona o fluxo para a página "cadastro/cadastropessoa"
	 * */
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa") /*Os dois ** ignoram tudo que vem antes de salvarpessoa*/
	public ModelAndView salvar(Pessoa pessoa) {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId())); //Tem que carregar os telefones antes de qualquer ação
		
		pessoaRepository.save(pessoa);
			
		/*Já irá retornar a lista de pessas após salvar*/
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa());
		/*A linha acima arrega um objeto vazio para, pois a linha 15 do form. cadastro de pessoas 
		 * sempre espera um objeto pessoa ao ser carregado se não houver dará erro. Esse procedimento
		 * é utilizado todas as vezes que se precisa exibir essa tela*/

		return andView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa()); 
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Pessoa pessoa = pessoaRepository.findOne(idpessoa);
		modelAndView.addObject("pessoaobj", pessoa); 
		
		return modelAndView;

	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.delete(idpessoa);
		
		/*Após deletar ele retorna para a mesma tela de cadastro e carrega todas as pessoas*/
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;

	}
	
	@GetMapping("**/pesquisarpessoa")
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if (pesqsexo != null && !pesqsexo.isEmpty()
				&& nomepesquisa != null && !nomepesquisa.isEmpty()) { /*Busca por nome e sexo*/
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
			
		}else if(nomepesquisa != null && !nomepesquisa.isEmpty()) { /*Busca por nome*/
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
			
			}else if(pesqsexo != null && !pesqsexo.isEmpty()) { /*Busca por sexo*/
				pessoas = pessoaRepository.findPessoaBySexo(pesqsexo);
				
				}else {
				Iterable<Pessoa> iterator = pessoaRepository.findAll();
				for (Pessoa pessoa : iterator) { /*Busca todos*/
					pessoas.add(pessoa);
					
				}
			}
		
		/*Chama o serviço que faz a geração do relatório*/
		byte[] pdf = ReportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		/*Tamanho da resposta para o navegador*/
		response.setContentLength(pdf.length);
		
		/*Definir na resposta o tipo de arquivo*/
		response.setContentType("application/octet-stream");
		
		/*Definir o cabeçalho da resposta*/
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		/*Finaliza a resposta ao negavador*/
		response.getOutputStream().write(pdf);
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo) {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesqsexo != null && !pesqsexo.isEmpty()) {
			
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
		}else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;

	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/telefones/{idpessoa}") /*Intercepta os telefones*/
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); /*Retorna para telefones*/
		Pessoa pessoa = pessoaRepository.findOne(idpessoa);
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		
		return modelAndView;

	}
	
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, 
			@PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaRepository.findOne(pessoaid);
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		ModelAndView modelAndView = new ModelAndView("/cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid)); //vem lá do TelefoneRepository		
		
		return modelAndView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removerTelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa pessoa = telefoneRepository.findOne(idtelefone).getPessoa();
		telefoneRepository.delete(idtelefone);
		
		/*Após deletar ele retorna para a mesma tela de cadastro e carrega todos os telefones da pessoas*/
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return modelAndView;

	}
	

}

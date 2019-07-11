package curso.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.springboot.model.Pessoa;

//Atenção com o importe do Transaction, deve ser o do spring...
@Repository
@Transactional  
public interface PessoaRepository extends CrudRepository<Pessoa, Long>{

	/*Onde a ? é o nome do parâmetro e o 1 é a quantidade de parâmetros...*/
	@Query("select p from Pessoa p where p.nome like %?1%")
	List<Pessoa> findPessoaByName(String nome);
}

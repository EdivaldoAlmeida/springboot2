package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration 		
@EnableWebSecurity   
public class WebConfigSecutiry extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;	
	
	@Override //Configura as solicitações de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf()
		.disable() //Desativa as configurações padrão de memória do spring.
		.authorizeRequests()  // Permite restringir acessos.
		.antMatchers(HttpMethod.GET, "/").permitAll() //Qualquer usuário acessa a pág. inicial
		.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN") //Apenas ADMIN terá acesso ao form. Cad. Pessoas
		.anyRequest().authenticated()
		.and().formLogin().permitAll() // permite qualquer usuário
		.loginPage("/login") // Vai para página de login
		.defaultSuccessUrl("/cadastropessoa") //Caso o login esteja ok, redireciona para cadastro de pessoas
		.failureUrl("/login?error=true") //Caso falhe o login, permanece na página de login e mostra um erro
		.and().logout().logoutSuccessUrl("/login") // Mapeia URL de logout e invalida usuário autenticado. Depois manda para pág. login
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));		
	}
	
	@Override //Cria autenticação do usuário com o bd ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		

	/*	auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("Edivaldo")
		.password("$2a$10$y9ox9QUnpoRKAsweNZ8FaOpXA7J9h/RbvDFPSxcn9oPRmLtBbR2ra")
		.roles("ADMIN");
		
	*/	
	}
	
	@Override //Ignora URL específicas
	public void configure(WebSecurity web) throws Exception {
			
		web.ignoring().antMatchers("/materialize/**"); //Ignorar tudo que está na pasta materializa
		
	}

}

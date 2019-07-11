package curso.springboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecutiry extends WebSecurityConfigurerAdapter{
	
	@Override //Configura as solicitações de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf()
		.disable() //Desativa as configurações padrão de memória do spring.
		.authorizeRequests()  // Permite restringir acessos.
		.antMatchers(HttpMethod.GET, "/").permitAll() //Qualquer usuário acessa a pág. inicial
		.anyRequest().authenticated()
		.and().formLogin().permitAll() // permite qualquer usuário
		.and().logout() // Mapeia URL de logout e invalida usuário autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));		
	}
	
	@Override //Cria autenticação do usuário com o bd ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		.withUser("Edivaldo")
		.password("123")
		.roles("ADMIN");
	}
	
	@Override //Ignora URL específicas
	public void configure(WebSecurity web) throws Exception {
			
		web.ignoring().antMatchers("/materialize/**"); //Ignorar tudo que está na pasta materializa
		
	}

}

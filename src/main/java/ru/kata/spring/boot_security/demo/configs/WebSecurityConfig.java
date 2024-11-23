    package ru.kata.spring.boot_security.demo.configs;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import ru.kata.spring.boot_security.demo.service.UserDetailsService;


    @Configuration
    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        private final SuccessUserHandler successUserHandler;
        private final UserDetailsService userDetailsService;


        @Autowired
        public WebSecurityConfig(SuccessUserHandler successUserHandler, UserDetailsService userDetailsService) {
            this.successUserHandler = successUserHandler;
            this.userDetailsService = userDetailsService;
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/").authenticated()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().successHandler(successUserHandler)
                    .permitAll()
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .permitAll();
        }




        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
            daoAuthenticationProvider.setUserDetailsService(userDetailsService);
            return daoAuthenticationProvider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
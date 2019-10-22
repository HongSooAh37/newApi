package me.whiteshop.demoinflearnrestapi.config;

import me.whiteshop.demoinflearnrestapi.accounts.Account;
import me.whiteshop.demoinflearnrestapi.accounts.AccountRole;
import me.whiteshop.demoinflearnrestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account suah = Account.builder()
                                        .email("hsa3737@naver.com")
                                        .password("hsa")
                                        .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                                        .build();
                accountService.saveAccount(suah);
            }
        };
    }
}

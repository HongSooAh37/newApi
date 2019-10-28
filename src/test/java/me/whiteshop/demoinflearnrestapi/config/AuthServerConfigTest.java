package me.whiteshop.demoinflearnrestapi.config;

import me.whiteshop.demoinflearnrestapi.accounts.Account;
import me.whiteshop.demoinflearnrestapi.accounts.AccountRole;
import me.whiteshop.demoinflearnrestapi.accounts.AccountService;
import me.whiteshop.demoinflearnrestapi.common.AppProperties;
import me.whiteshop.demoinflearnrestapi.common.BaseControllerTest;
import me.whiteshop.demoinflearnrestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증 토클을 발급 받는 테스트")
    public void getAuthToKen() throws Exception {
        //Given
/*        Account suah = Account.builder()
                                .email(appProperties.getUserUsername())
                                .password(appProperties.getUserPassword())
                                .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                                .build();
        this.accountService.saveAccount(suah);*/
        this.mockMvc.perform(post("/oauth/token")
                    .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                    .param("username", appProperties.getUserUsername())
                    .param("password", appProperties.getUserPassword())
                    .param("grant_type", "password"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("access_token").exists())
                ;
    }

}
package net.deile.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.deile.entity.User;
import net.deile.service.UserDetailServiceImpl;

@Configuration
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	UserDetailServiceImpl userDetailService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();

	}

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {

		System.out.println("Run CustomAuthenticationProvider");

		User user = (User) auth.getPrincipal();
		Object password = auth.getCredentials();

		// ここで認証とロールの付与
		System.out.println(passwordEncoder().encode(user.getPassword()));

		// DBからユーザを抽出する。
		userDetailService.findByEmail(user.getEmail());

		// パスワードの整合性チェック
		passwordEncoder().matches(user.getPassword(), "{noop}test");

		user.setUser_name("テストユーザ");

		Collection<GrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

		//		return new UsernamePasswordAuthenticationToken(user, password, auth.getAuthorities());
		return new UsernamePasswordAuthenticationToken(user, password, authorityList);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
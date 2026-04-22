package com.LocaDj.security;

import com.LocaDj.models.User;
import com.LocaDj.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public FirebaseTokenFilter(UserDetailsService userDetailsService,UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("OPA! Chegou um token no backend!");

            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String email = decodedToken.getEmail();
                String name = decodedToken.getName();
                System.out.println("Firebase validou! Email: " + email);

                UserDetails userDetails;

                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("Achou o usuário no banco de dados!");

                } catch (UsernameNotFoundException notFoundException) {
                    System.out.println("Usuário não existe no banco! Criando conta para: " + email);


                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name != null ? name : "Usuário App");
                    newUser.setPassword(java.util.UUID.randomUUID().toString());


                    newUser.setRole(User.Role.CLIENT);

                    userRepository.save(newUser);
                    System.out.println("Usuário salvo com sucesso no banco de dados!");


                    userDetails = userDetailsService.loadUserByUsername(email);
                }



                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                System.out.println("DEU RUIM NO FILTRO DO FIREBASE: " + e.getMessage()); // LOG DE ERRO
            }
        }

        filterChain.doFilter(request, response);
    }
}
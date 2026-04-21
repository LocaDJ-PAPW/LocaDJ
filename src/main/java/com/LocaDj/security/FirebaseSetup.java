package com.LocaDj.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseSetup {

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;

                // 1. Tenta ler a variável de ambiente (Para usar no Render)
                String firebaseEnv = System.getenv("FIREBASE_CREDENTIALS");

                if (firebaseEnv != null && !firebaseEnv.isEmpty()) {
                    serviceAccount = new ByteArrayInputStream(firebaseEnv.getBytes());
                    System.out.println("🔥 Firebase inicializado via Variável de Ambiente (Produção)!");
                } else {
                    // 2. Se não achar a variável, lê o arquivo físico (Para usar na sua máquina)
                    serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
                    System.out.println("🔥 Firebase inicializado via Arquivo JSON (Local)!");
                }

                if (serviceAccount == null) {
                    throw new RuntimeException("Credenciais do Firebase não encontradas!");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.out.println("🚨 Erro ao inicializar o Firebase: " + e.getMessage());
        }
    }
}
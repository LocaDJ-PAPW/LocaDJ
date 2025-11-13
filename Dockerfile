# --- Estágio 1: Builder ---
# Usamos a imagem oficial do Maven com o Temurin (OpenJDK) 21, que corresponde ao seu pom.xml
FROM maven:3.9-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache de camadas do Docker
COPY pom.xml .

# Baixa as dependências do projeto
RUN mvn dependency:go-offline

# Copia o restante do código-fonte
COPY src ./src

# Compila o projeto e pula os testes (melhor prática para builds)
# O -DskipTests é importante para o CI/CD
RUN mvn package -DskipTests

# --- Estágio 2: Runner ---
# Usamos uma imagem leve, apenas com o Java Runtime Environment 21, baseada em Alpine Linux
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia o .jar compilado do estágio 'builder' para a imagem final
# O nome do JAR é baseado no <artifactId> e <version> do seu pom.xml
COPY --from=builder /app/target/LocaDJPlataform-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
# O Vercel irá detectar esta porta e mapeá-la automaticamente.
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for executado
ENTRYPOINT ["java", "-jar", "app.jar"]
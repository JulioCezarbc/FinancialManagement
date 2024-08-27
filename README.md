# FinancialManagement

O Financial Management é uma aplicação backend desenvolvida em Java utilizando o framework Spring Boot. O objetivo principal do sistema é fornecer um meio eficiente e seguro para que os usuários gerenciem suas finanças pessoais, permitindo o registro e controle de transações financeiras.

#Funcionalidades

Autenticação JWT: Sistema de autenticação baseado em tokens JWT, garantindo segurança nas operações.
Registro de Usuários: Permite que novos usuários se registrem com nome, e-mail e senha.
Login de Usuários: Realiza a autenticação dos usuários com e-mail e senha.
Gerenciamento de Transações: Usuários podem adicionar, visualizar, editar e deletar suas transações financeiras.
Controle de Acesso: O sistema distingue entre diferentes níveis de acesso, como usuários normais e administradores.

#Tecnologias Utilizadas

Java 17
Spring Boot 3
Spring Security
JWT (JSON Web Token)
JPA/Hibernate
MySQL
Maven

#Pré-requisitos
Antes de começar, você precisará ter as seguintes ferramentas instaladas:

Java 17+
Maven
MySQL

#Instalação
Clone o repositório:
git clone https://github.com/JulioCezarbc/FinancialManagement.git
cd FinancialManagement

#Configure o banco de dados:

Crie um banco de dados MySQL chamado financial_management.
Atualize as credenciais do banco de dados no arquivo src/main/resources/application.properties:
properties
Copiar código

spring.datasource.url=jdbc:mysql://localhost:3306/financial_management
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

#Execute a aplicação:

mvn spring-boot:run

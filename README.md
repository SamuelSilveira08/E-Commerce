Este projeto é uma simples API REST baseada no sistema de um e-commerce.
Aqui, utilizo a linguagem Java em conjunto com o framework Spring.

Abaixo, irei detalhar as camadas do sistema e a lógica por trás delas.
Algumas coisas serão, talvez, explicadas "demais", mas meu intuito com esse texto é fazer com que até mesmo uma pessoa sem conhecimento em programação possa entender como o sistema e suas tecnologias funcionam.

Primeiro gostaria de explicar as classes de domínio da aplicação.

Uma classe de domínio é a classe que define as características/atributos e comportamentos específicos de cada entidade utilizada da aplicação. Essas entidades¹ são relacionadas entre si através de anotações do Java (mais especificamente da Java Persistence API²³) e objetos dessas entidades podem ser instanciados manualmente ou injetados através de anotações do Spring Boot em outras classes, como as classes de serviço. Exemplos de entidades/classes de domínio são: Usuário, Produto, Compra.

As classes de domínio dessa API são: User, UserPrincipal, Product, Item, Purchase e Cart, e as traduções em português são: Usuário, UsuárioAutenticado, Produto, Item, Compra e Carrinho.

A classe User define as propriedades de um Usuário, como nome, email e senha. Ela se relaciona com as classes Cart (um usuário, dentro do sistema, tem um carrinho de compras), e Purchase (um usuário tem uma lista de compras).

A classe UserPrincipal define o usuário que está autenticado na aplicação no momento. Ela se relaciona com a classe User e, através dos atributos email e senha da classe User, ela nos provê acesso às permissões e aos status de um usuário (se aquela conta está bloqueada, expirada ou ativa, por exemplo).

Quanto a classe Item: define os atributos de um Item (item de compra ou de carinho), como a quantidade de um produto. Seus relacionamentos são com Cart (items devem pertencer a um carrinho ou a uma compra), Purchase (como já dito, items devem pertencer a um carrinho ou uma compra) e Product (um item é composto de produtos).
A classe Item existe por conta do relaciomaneto entre as tabelas Product e Purchase, que é um N:M (em modelagem de banco de dados, chamamos um relacionamento de N:M ou N:N (lê-se N para M) quando um registro da tabela N pode estar relacionado a vários registros da tabela M e vice-versa. Por exemplo: o produto com o identificador 1 pode estar relacionado às compras de identificadores 1, 2 e 3 ao mesmo tempo, e as compras 1, 2 e 3 podem ter outros produtos relacionados a elas, além do produto 1. Daí vem o relacionamento N:M (muitos para muitos)). Quando temos este tipo de relacionamento, uma das abordagens que podemos adotar é criar uma tabela intermediária para guardar informações deste relacionamento, e isto foi feito aqui com a classe Item. Nela, além dos atributos que identificam os relacionamentos, como Purchase, Product e Cart, também adicionei o atributo quantidade, que irá identificar quantos produtos cada item tem.

Detalhe: Entre Cart e Product também há um relacionamento N:M, o que resultaria em uma outra entidade/tabela de relacionamento, porém, neste sistema, não haveria nenhuma diferença entre essas entidades, que seriam chamadas de "ItemCart" e "ItemPurchase", então decidi por utilizar apenas uma entidade para expressar os dois relacionamentos e chamá-la apenas de "Item".

Já a classe Product define as propriedades de um produto, como descricao, estoque e preço. Ela tem apenas um relacionamento com outra classe de domínio, que é a classe Item (um produto, ao ser comprado ou inserido em um carrinho, será um item deste carrinho ou desta compra).

Já falamos um pouco sobre a classe Cart acima, mas para detalhar um pouco mais, ela é a classe que define as propriedades do carrinho de compras, como por exemplo o valor total e alguns atributos de relacionamento, como o usuário ao qual ele pertence (da classe User) e uma lista com seus items (da classe Item).

A classe Purchase também já foi citada, mas não falamos muito sobre os atributos dela, os quais são: preço da compra, a data em que ela foi realizada e o usuário ao qual ela pertence e uma lista com os items dela como atributos de relacionamento.

p.s.: Com exceção da classe UserPrincipal, todas elas têm também um atributo chamado id, que será o identificados dos registros dessas classes no banco de dados, que será usado para distinguir um registro do outro.


¹ Java Persistence API (JPA) é uma API do Java que provém uma série de anotações para serem implementadas por algum ORM. Anotações estas que são usadas para definir como as classes e objetos deverão se comportar em relação a persistência de dados (relacionamentos entre as entidades, identificadores, especificar tipo e nomes de atributos de um objeto Java no banco de dados etc.).
² Spring Boot é um framework Java que facilita a configuração de uma aplicação, diminuindo consideravelmente o tempo de desenvolvimento. Alguns exemplos de configurações que o Spring Boot faz automaticamente são: mapeamento de beans (que são "elementos" da aplicação, como entidades/objetos Java, que devem ser identificadas propriamente através de arquivos de configurações, porém com o uso do Spring Boot, isto é feito automaticamente), adicionar dependências (para uma aplicação web, por exemplo, apenas precisamos definir o starter-web e o Spring-Boot irá trazer para nosso projeto o Spring Web, Spring Web MVC, Servlet API etc.), disponibilização e configuração do servidor (o Spring Boot nos fornece um servidor Tomcat pronto para uso, sem necessidade de configurações) e várias outras configurações que serão feitas internamente, dependendo das dependências e tecnologias utilizadas no projeto.
³ API é, resumidamente, um conjunto de regras e padrões que define como um serviço/aplicação deverá ser utilizado (neste caso, a JPA).

A partir das classes de domínio, foram desenvolvidos os DTOs (Data Transfer Objects - Objetos de Transferência de Dados).
Em resumo, são classes Java que especificam um formato específico para transferir os dados entre o servidor e o cliente. Por exemplo, ao transferir dados de um usuário, não queremos enviar sua senha, então não colocamos o atributo senha no DTO de usuário. Além disso, com os DTOs, também evitamos problemas de ciclos infinitos de buscas no banco de dados, pois imagine o seguinte: Um usuário tem uma lista de compras e cada compra tem um usuário associado a ela. Isso resultaria em acessos em cadeia ao banco de dados para buscar esses dados, afinal, ao buscar o usuário, o Spring também busca os dados das entidades relacionadas a ele — neste caso, compras —, mas essas entidades também têm um usuário relacionado a elas, e então o ciclo se repete, e isso causa problemas graves. Uma das formas de resolver isto é através dos DTOs, onde, ao invés de termos um usuário com todos os atributos (inclusive os de relacionamento) em uma compra, podemos ter apenas o identificador do usuário, mantendo o relacionamento entre usuário e compra e resolvendo o problema descrito acima.

Após a criação dos DTOs, foram desenvolvidos controllers (controladores) da aplicação.
Um controller é uma classe que define endpoints (URIs - Uniform Resource Identifier. Por exemplo "/users/login") para acessar cada uma das "funcionalidades" das aplicação. Cada classe de domínio tem um controller correspondente, com exceção da UserPrincipal, pois as informações contidas nela não são acessadas diretamente pelo cliente (que é a pessoa acessando a aplicação), mas sim pelas classes da camada de serviço para fazer as devidas verificações de autenticação e validar as permissões de um usuário.

Então, em geral, nos controllers, temos métodos (veja método como um trecho de código que pode ser reutilizado) para recuperar dados do banco de dados, para salvar dados no banco de dados, atualizar e deletar dados no banco de dados.
Por exemplo, se o usuário quer alterar sua senha, deverá fazer uma requisição para o endpoint "/users/change-password" enviando a senha atual e a nova senha, que será atualizada no banco de dados.

Todos os controllers dessa aplicação seguem o padrão REST, que define um conjunto de restrições para a criação de web services (que, por sua vez, é um conjunto de métodos que serão acessados por outras aplicações. Justamente o que fazemos com um controller.), e o formato da resposta do servidor é o JSON (JavaScript Object Notation).
Então, se o cliente enviar uma requisição buscando por um deterrminado produto (através de seu identificar) para o endpoint correspondente, que nesta aplicação é "/products/{id}" (no verbo HTTP GET), a resposta do servidor seria algo como:

{
  "id": 278,
  "name": "Fone de ouvido",
  "price": 69.90,
  "stock": 1000
}

É importante citar, também, que os controllers recebem e enviam apenas DTOs, não lidando com classes de domínio, que são utilizadas apenas internamente na aplicação.

Este é o formato JSON e é assim que os dados são trafegados entre o servidor e o cliente.

Os controllers fazem uso da camada de serviço da aplicação para retornar uma resposta ao cliente, basicamente agindo como um intermediário entre o cliente (usuário da aplicação) e a camada de serviço, que contém toda a lógica da aplicação.

A camada de serviço é, muito provavelmente, o que mais leva tempo para ser desenvolvido em um sistema, justamente por conter toda a lógica da aplicação e conter diversas regras de negócio, que são definições ou "restrições" que podemos aplicar ao recebimento e entrega dos dados.

Na camada de serviço, temos uma classe de serviço para cada classe de domínio, classes essas que serão responsáveis por buscar dados no banco de dados através dos repositórios (veja abaixo), aplicar as regras de negócios e passar essas informações ao controller, que enviará a resposta da requisição para o cliente. São nas classes de serviço onde fazemos a verificação das permissões de acesso de um usuário para uma determinada funcionalidade da aplicação. Por exemplo: apenas usuários administradores podem cadastrar um produto.
No geral, cada método de um controller tem um método correspondente em uma classe de serviço. Porém outros métodos podem existir em uma classe de serviço.

A camada de serviço acessa os repositórios da aplicação, que nada mais são que interfaces Java responsáveis por acessar o banco de dados e buscar ou alterar os dados que queremos.
Usando o Spring Data, a maioria das operações que precisamos fazer já é definida automaticamente e disponibilizada para uso, apenas precisamos criar a interface e defini-la como uma classe filha de JpaRepository, o que significa que ela irá herdar os métodos da classe mãe (métodos esses que permitem o acesso ao banco de dados).

Na camada de serviço, também é onde é feita a autenticação do usuário, o que garante a segurança da aplicação.
A classe AuthenticationService implementa a interface UserDetailsService. Essa interface define que o método loadUserByUsername deve ser implementado — escrito/especificado —, e ele será responsável por verificar se há um usuário com um username (identificador) específico no banco de dados. Depois disso, outros métodos da classe AuthenticationService verificam se a senha recebida para aquele username está correta e, em caso positivo, o usuário é autenticado e um JWT (JSON Web Token) é gerado para aquele usuário.

O que é um JWT? Um JWT é um token que transfere dados de um usuário de forma compacta entre o cliente e o servidor. 
Um JWT é dividido em 3 partes:

- Header: contém o tipo do token (neste caso, Bearer) e o algoritmo usado para criar a "criptografia" da Signature (veja abaixo), no caso dessa API, foi o HS512. Na aplicação, definimos uma Signing Key que será usada para assinar o token. Desta forma, mesmo que alguém tenha acesso aos dados do token (Header e Payload) — o que é relativamente simples de se conseguir, já que essa parte do token não é criptografada, apenas a Signature é — e possa até mesmo alterá-los, sem a Signing Key para "assinar" o token, o servidor facilmente invalidará esse token e não permitirá que o usuário acesse a aplicação;
- Payload: aqui ficam as informações do usuário para o qual o token foi gerado, como o username, que dentro de um JWT é chamado de subject;
- Signature: nessa seção temos a assinatura do token, que é gerada a partir do conteúdo do header e do payload e criptografada a partir da Signing Key definida na aplicação.
Detalhe: Em um JWT, o Header e o Payload não são criptografados, apenas compactados (o que é facilmente revertido). Se quisermos e/ou precisarmos proteger as informações contidas no Header e/ou no Payload, podemos utilizar o JWE (JSON Web Encryption) que é responsável por criptografar um JWT. Porém, nesta API, para simplificar as coisas, não o utilizamos.

Também na camada de serviço desta aplicação, temos a classe JwtService, que é responsável por diversas operações que precisamos fazer com um JWT.
Algumas delas são a criação de um JWT, extrair o username e a data de expiração de um JWT e gerar o refresh token — um JWT com duração maior que deve ser enviado para o servidor para que um novo JWT de acesso seja gerado. Um refresh token só pode ser utilizado para renovar o token de acesso, se o cliente enviar um refresh token como token de acesso, a API não irá liberar o acesso deste usuário, pois as Signing Keys são diferentes de um token de acesso e de um refresh token —. Além disso, a classe JwtService define a Signing Key para ambos access token e refresh token e seus tempos de expiração.

Quanto à segurança da aplicação, como já dito anteriormente, utilizamos o JWT para validar a identidade e as permissões de um usuário, mas isso é feito através do Spring Security e da biblioteca JJWT, que contém classes e métodos para manipular um JWT.
O fluxo de autenticação se dá da seguinte maneira:

O servidor recebe uma requisição e, através de um filtro que irá ser executado em cada requisição, verifica se nesta requisição existe um JWT e se esse token é do tipo Bearer Authentication, que é o tipo de autenticação que devemos utilizar quando a autenticação é feita através de um token. Também existem a Basic Auth e a Digest Auth, mas elas são utilizadas para autenticação através de username e senha.
Após verificar se é do tipo Bearer, extraímos o username deste token e verificamos se é um username válido (se existe um usuário com este username cadastrado). Em caso positivo, adicionamos esse usuário e suas permissões no contexto do Spring Security, para que possamos acessar o usuário autenticado em nossos controllers ou serviços.

Se qualquer uma das verificações realizadas neste filtro for dada como falsa, o servidor enviará como resposta da requisição uma mensagem de erro com o código HTTP 401 (Unauthorized), ou, em português, não autorizado.

Além deste filtro que realiza a autenticação, também temos um filtro que lida com as exceções (erros) que acontecem nos filtros da aplicação, como o Unauthorized, citado acima.
Esses dois filtros são adicionados manualmente à filter chain (cadeia de filtros) do Java pela classe de configuração do Spring Security.

Os demais erros (o termo correto seria "exceções") conhecidos que podem vir a acontecer durante o uso da aplicação são manipulados por uma classe que chamei de RequestExceptionHandler, que, através de anotações do Spring como a RestControllerAdvice (que define que os erros da aplicação deverão ser manipulados pela classe que é anotada com ela) e a ExceptionHandler (especifica qual erro um método da classe RequestExceptionHandler deverá manipular).

Para cada exceção (ou erro) conhecida, existe uma classe que define um código HTTP para ela e uma mensagem de erro.

Alguns das exceções definidas neste projeto são:

- NotFoundException: quando um determinado registro não é encontrado no banco de dados através de seu identificador. Por exemplo: um usuário está buscando por um produto com identificador 10, e na base de dados da aplicação não existe um produto com identificador 10. Neste caso, uma exceção do tipo NotFoundException é "ativada" e o servidor responde a este usuário com o código HTTP 404 (Not Found) e uma mensagem de erro especificando o recurso que não foi encontrado;
- ForbiddenException: acontece quando um usuário está tentando acessar um recurso ao qual ele não tem permissão para acessar. Por exemplo: quando um usuário está buscando por dados de um outro usuário, uma exceção do tipo ForbiddenException é lançada e o servidor responde com o código HTTP 403 (Forbidden) e uma mensagem especificando o erro;
- BadRequestException: quando uma requisição enviada para o servidor não está no formato correto ou tem informações faltando. Por exemplo, o usuário tenta alterar sua senha enviando apenas a nova senha para a aplicação. Uma exceção do tipo BadRequestException é ativada e a resposta do servidor é o código HTTP 400 (Bad Request) com uma mensagem de erro de acordo.

Por fim, temos outros dois "tipos" de classes, que são as classes de configuração e de "utilidades".
As classes de configuração servem, como o nome já diz, para configurar recursos necessários ao projeto. Neste projeto, elas são duas: 

- SecurityConfig: essa classe inclusive já foi citada anteriormente, mas ela basicamente é responsável pela configuração da segurança da aplicação, como quais endpoints não irão necessitar de autenticação e os filtros personalizados que queremos utilizar nas requisições;
- ModelMapperConfig: Faz configurações de como a conversão entre uma classe de domínio e um DTO deve ser feita. Essa conversão é realizada automaticamente por uma biblioteca Java chamada ModelMapper.

E, para finalizar, no pacote utils temos classes de "utilidades". Aqui neste projeto ela é uma só: PasswordUtils.
Classes utils geralmente são estáticas, ou seja, não precisam terem um objeto instanciado para acessar seus métodos. A PasswordUtils é responsável por criptografar uma senha, para que ela seja salva no banco de dados (já que salvar uma senha sem criptografia não é seguro) e também é responsável por verificar se a senha que o usuário está usando para acessar a aplicação é compatível com a senha criptografada no banco de dados.
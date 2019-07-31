## [cafébinario] :: FileSystem

Sistemas de Arquivo em memória (Java 7 NIO) distribuido, clusterizado, com auto-descobrimento, balanceamento de carga e proxy-reverso entre os nós.

Escalavel verticalmente e horizontalente, permite replicação de dados em tempo real entre diversos data-centers.

Alta-perfomance e alta-disponibilidade para arquivos.

Interface Rest e FTP.

Permite pesquisa com palavras chaves e parte de conteudo multimedia.

Monitoramento de arquivos através de notificações por webhook com eventos de criação, alteração, delete e sobrescrita de arquivos.

![Optional Text](cafebinario_vfs.png)


## Road map:

	+ implementação de controle de acesso RBAC com LDAP
	+ integração com document-propagation
	+ integração com apache-mina sshd
	+ implementação de API para busca de atributos
	+ melhoria na engine de busca (QueryBuilder com opção para atributos)
	+ integração com spring-boot-starter-data
	
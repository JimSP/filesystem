## [cafébinario] :: FileSystem

Sistemas de Arquivo em memória (Java 7 NIO) distribuido, clusterizado, com auto-descobrimento, balanceamento de carga e proxy-reverso entre os nós.

Escalavel verticalmente e horizontalente, permite replicação de dados em tempo real entre diversos data-centers.

Alta-perfomance e alta-disponibilidade para arquivos.

Interface FTP.

Interface Rest:

	+ criação de arquivos e diretórios.
	+ sobrescrita de arquivos.
	+ permite listar paths (arquivos e diretórios).
	+ busca de paths com palavra chave (contains).
	+ pesquisa com palavras chaves e parte de conteúdo multimedia.
	+ edição de documentos.
	+ edição de documentos em lote.
	+ indexação de conteúdo.
	
Monitoramento de arquivos através de notificações por webhook com eventos de criação, alteração, delete e sobrescrita de arquivos.

![Optional Text](cafebinario_vfs.png)


## Road map:

	+ implementação de controle de acesso RBAC com LDAP
	+ integração com document-propagation
	+ integração com apache-mina sshd
	+ implementação de API para busca de atributos
	+ melhoria na engine de busca (QueryBuilder com opção para atributos)
	+ integração com spring-boot-starter-data
	

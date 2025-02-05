CREATE TABLE `products` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador único do produto',
  `name` VARCHAR(80) NOT NULL COMMENT 'Nome comercial do produto',
  `description` VARCHAR(150) NOT NULL COMMENT 'Detalhes/composição do produto',
  `price` DECIMAL(10,2) NOT NULL COMMENT 'Preço de venda',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT 'URL da imagem ilustrativa',
  `category` VARCHAR(255) NOT NULL COMMENT 'Categoria do produto',
  `available` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Disponibilidade para venda',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Tabela de produtos da hamburgueria';
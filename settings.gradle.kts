
rootProject.name = "p001-spring-data"
include("m001-data-source")
include("m001-data-source:sm001-embedded-database-builder")
findProject(":m001-data-source:sm001-embedded-database-builder")?.name = "sm001-embedded-database-builder"
include("m001-data-source:sm002-dbcp2")
findProject(":m001-data-source:sm002-dbcp2")?.name = "sm002-dbcp2"
include("m004-data-source-initializer")
include("m005-jdbc-template-basics")
include("m006-jdbc-template-query-method")
include("m007-jdbc-template-other-methods")
include("m008-jdbc-template-get-auto-generated-keys")
include("m009-transaction-pure-jdbc")
include("m010-transaction-with-spring")
include("m011-simple-jpa-with-spring-boot")
include("m012-jpa-many-to-one-example")
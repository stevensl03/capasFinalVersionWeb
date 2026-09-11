MAKEFILE_DIR := $(patsubst %/,%,$(dir $(abspath $(lastword $(MAKEFILE_LIST)))))

# --- Rutas (independientes del directorio desde el que se invoca make) ---
MYSQL_COMPOSE := $(MAKEFILE_DIR)/mysql/docker-compose.yml
MYSQL_ENV     := $(MAKEFILE_DIR)/mysql/.env

.PHONY: help up down restart logs status reset

help: ## Muestra esta ayuda
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2}'

up: ## Levanta el contenedor MySQL (crea la BD con esquema la primera vez)
	docker compose --env-file $(MYSQL_ENV) -f $(MYSQL_COMPOSE) up -d

down: ## Detiene el contenedor MySQL (conserva el volumen de datos)
	docker compose --env-file $(MYSQL_ENV) -f $(MYSQL_COMPOSE) down

restart: down up ## Reinicia MySQL conservando los datos

logs: ## Sigue los logs del contenedor MySQL
	docker compose --env-file $(MYSQL_ENV) -f $(MYSQL_COMPOSE) logs -f

status: ## Estado del contenedor MySQL
	docker compose --env-file $(MYSQL_ENV) -f $(MYSQL_COMPOSE) ps

reset: ## BORRA el volumen de datos y recrea la BD desde cero con el esquema
	docker compose --env-file $(MYSQL_ENV) -f $(MYSQL_COMPOSE) down -v
	$(MAKE) up
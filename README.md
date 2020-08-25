# Проект Датана.Смарт.UI

Это проект интерфейса пользователя для Датана-Смарт.

## Структура проекта

```plantuml
class "Front Main" as fm {
 dsmart-ui-main
}

class "Backend Gateway" as bg {
 dsmart-back-gateway
}


class "UI Temperature" as ftemp {
  "Frontend Component"
 dsmart-ui-temperature
}

class "UI Events" as fevent {
  "Frontend Component"
 dsmart-ui-events
}

class "UI Messages" as fmess {
  "Frontend Component"
 dsmart-ui-messages
}

fm -> ftemp
fm -> fevent
fm -> fmess

class "MS Temperature" as btemp {
  "Microservice"
 dsmart-app-temperature
}

class "MS Events" as bevent {
  "Microservice"
 dsmart-app-events
}

class "MS Messages" as bmess {
  "Microservice"
 dsmart-app-messages
}

ftemp <--> bg
fevent <--> bg
fmess <--> bg

bg -> btemp
bg -> bevent
bg -> bmess
```

## Модули

1. [`dmart-ui-main`](dsmart-ui-main/README.md) - главный компонент интерфейса пользователя, который управляет всеми 
компонентами UI.

## Сборка и деплой проекта

Компиляция проекта:
```bash
./gradlew build
```

Деплой проекта в реестр докеров:
```bash
#export DOCKER_REGISTRY_PORT=
export DOCKER_REGISTRY_HOST=registry.datana.ru
export DOCKER_REGISTRY_USER=admin
export DOCKER_REGISTRY_PASS=*****
./gradlew deploy
```

## Запуск проекта

Из докера:
```bash
 docker run -p 8080:8080 
```

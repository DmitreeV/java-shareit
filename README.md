
<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="https://x-lines.ru/letters/i/cyrillicscript/1084/000000/46/0/kpwgnhufjf4y.png">

  </a>

  <p align="center">
  </p>
</div>


</details>
<details><summary><b>Содержание</b></summary>
  
    1. Краткое описание
    2. Стек-технологий
    3. Функциональности проекта
    4. Схема базы данных
    5. Системные требования
    
</details>


## Краткое описание

Это сервис совместного использования вещей. Пользователь может брать вещи напрокат, оставлять отзывы о вещах, которые были взяты им в шеринг, создавать запрос если нужной вещи еще нет на платформе. Приложение состоит из нескольких микросервисов. REST-сервисы с использованием Spring-Boot, Maven, Lombok, взаимодействие с базой данных (PostgreSQL) с использованием Hibernate, Docker. База данных также подключается как отдельный микросервис.

## Стек-технологий

* Spring Framework
* Spring Boot
* JPA
* Интеграционные тесты
* Мок-тестирование
* Межсервисное взаимодействие
* Docker
* RestTemplate

## Функциональности проекта

### Функциональности пути /bookings
**GET /bookings?state={state}&from={from}&size={size}** получение списка всех бронирований пользователя отсортированных по дате от более новым к старым. 

**GET /bookings/owner?state={state}&from={from}&size={size}** получение списка всех бронирований вещей принадлежащих пользователю отсортированных по дате от более новым к старым. 

**GET /bookings/{bookingId}** получение бронирования вещи по id.

**POST /bookings** создание бронирования вещи.

**PATCH /bookings/{bookingId}?approved={approved}** подтверждение или отклонение бронирования вещи пользователем.

### Функциональности пути /items
**GET /items?from={from}&size={size}** получение списка всех вещей пользователя.

**GET /items/{id}** получение вещи по id.

**GET /items/search?text={text}&from={from}&size={size}** получение списка вещей по текстовому запросу.

**POST /items** создание вещи.

**POST /items/{itemId}/comment** создания отзыва на вещь. 

**PATCH /items/{id}** внесение изменений пользователем в созданную вещь.

**DELETE /items/{id}** удаление вещи.

### Функциональности пути /requests
**GET /requests** получение пользователем всех его запросов на создание вещей отсортированных по дате от более новым к старым.

**GET /requests/all?from={from}&size={size}** получение списка запросов вещей отсортированных по дате от более новым к старым.

**GET /requests/{requestId}** получение запроса по его id.

**POST /requests** создание запроса на вещь.

### Функциональности пути /users
**GET /users** получение списка всех пользователей.

**GET /users/{id}** получение пользователя по его id.

**POST /users** создание пользователя.

**PATCH /users/{id}** обновление данных пользователя с id.

**DELETE /users/{id}** удаление пользователя по id.

## Схема базы данных
![ShareIt Data Base diagram](https://github.com/DmitreeV/java-shareit/blob/main/image/db%20diagram.png)

## Системные требования

В данном репозитории представлен бэкенд приложения. Работоспособность приложения протестирована, тесты расположены в
папкe: [server/.../test](./server/src/test). Также программа протестирована по WEB API с помощью
Postman-тестов, тесты расположены в папке [postman](./postman/).

Приложение работает корректно в текущем виде при наличии:

- установленный [JDK версии 11](https://docs.aws.amazon.com/corretto/),
- сборка с использованием [Maven](https://maven.apache.org/),
- установленный [Docker](https://www.docker.com/products/docker-desktop/).




 


# Банковская система
## Описание проекта
В проекте есть следующие сущности: банка, пользователя, аккаунта, транзакции, чек транзакции, проверка начисления процентов на остаток счета в конце месяца, генератор чеков в текстовом формате, генератор выписки по транзакциям пользователя за опредленное время. Также есть сущности сервлеты и сущность для работы с базой данных.
## Инструкция по запуску проекта
Для запуска проекта нужна **Java 17, Gradle 8.x**.  
Для запуска базы данных нужно просто запустить docker: **docker compose -f docker-compose.Postgres.yaml**.

Для работы с базой данных, **нужно экспортировать данные с csv файлов в папке db**.
Сделать это можно самостоятельно через postgres в docker container, либо легче через pgadmin: **docker cp ./db pgadmin_container:/home => (choose table) => import/export data => set parse csv with header => choose file from /home**. 

Для запуска servlet container => **Docker образа нету :(** (~~я запускал на glassfish 7, для него docker containera нету~~). Нужно задеплоить war, output из gradle war task, на ваш сервер. Подойдет любой контейнер сервлетов поддерживающий Java 17.

**Многие тесты требуют _наличие включенной базы данных_, включите сначало её, потом запустите тесты**.

## CRUD операции (при их наличии) с примерами входных и выходных данных
На каждой сущности реализованы CRUD операции. Их можно проверить через сервлеты, если приложение задеплоено на сервер.  
### HTTP API: 
#### POST - создаёт сущность, PUT - обновляет контент сущности, GET - получает сущность, DELETE удаляет сущность.

POST: [user, bank, account, transaction]/ , accept: application/json , content-type: 'text'. Возращает id созданной сущности.  
**При этом нужно писать все поля которые есть в сущности, _пример неправильного запроса_**:
curl -XPOST -H "Content-type: application/json" -d '{
 "first_name" : "Kolya"
}' 'http://localhost:8080/user'

**_А вот пример правильного_**:  
curl -XPOST -H "Content-type: application/json" -d '{
 "first_name" : "Kolya",
 "last_name" : "Ur",
 "birth_date": "1920-01-01"
 
}' 'http://localhost:8080/user'.

PUT: [user, bank, account , transaction]/{id} , accept: application/json. **Также как и в POST, нужно заполнить все поля сущности в теле запроса**.

GET: [user, bank, account, transaction]/{id} , content-type: application/json. Возвращает json тело.

DELETE: [user, bank, account, transaction]/{id}.

**Даты передевать в формате ISO-8601, пример: 2018-09-03T10:09:35**.


**Примеры тел POST и PUT запрсов:**

Account example

```json
    {
        "balance": 100,
        "bank_id": 3,
        "user_id": 5,
        "currency": "BYN",
        "opening_date": "2018-09-03T10:09:35",
        "account_number": "123as13BYN"
    }
```


Bank example 

```json
    {
        "name": "Test"
    }
```


User example 

```json
    {
        "first_name": "Inaya",
        "last_name": "Garcia",
        "birth_date": "1991-04-29"
    }
```

Transaction example 

```json
    {
        "created_at": "2018-09-03T10:09:35",
        "origin_account_id": 1,
        "target_account_id": 1,
        "transfer_amount":100,
        "description": "Пополнение"
    }
```


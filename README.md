# RBPO-2026 — распределение мест в общежитии

REST API на Java 17 и Spring Boot 3.5 для управления студентами, комнатами общежития, заявлениями на проживание, договорами и переводами между комнатами. Вариант предметной области: 22.

## Технологии

- Java 17, Spring Boot Web, Validation, Spring Data JPA и Spring Security
- PostgreSQL 16
- Maven
- JSON REST API

## Предметная модель

- `User` — студент или сотрудник; email уникален.
- `Room` — комната с уникальным номером и положительной вместимостью.
- `HousingApplication` — заявление пользователя (`NEW`, `APPROVED`, `REJECTED`).
- `ResidenceContract` — договор по одобренному заявлению и назначенной комнате (`ACTIVE`, `TERMINATED`).
- `RoomTransferRequest` — запрос на перевод по активному договору в другую комнату (`NEW`, `APPROVED`, `REJECTED`).

Внешние ключи связывают заявления с пользователями, договоры с заявлениями и комнатами, а заявки на перевод — с договорами и комнатами. Ограничения уникальности защищают email и номер комнаты. Enum сохраняются строками. Операции сервисного уровня выполняются в транзакциях.

## Запуск

1. Установите Docker и Docker Compose, а также JDK 17 и Maven.
2. Скопируйте `.env.example` в `.env` и задайте два разных длинных пароля.
3. Запустите PostgreSQL: `docker compose up -d db`.
4. Экспортируйте значения из `.env` в shell и запустите API: `set -a && source .env && set +a && mvn spring-boot:run`.
5. API доступен на `http://localhost:8080`; коллекция запросов находится в `postman/RBPO-2026.postman_collection.json`.

## Задание 4 — Spring Security

- HTTP Basic Auth включён для защищённых ресурсов. Все изменения состояния (POST/PATCH/DELETE) защищены CSRF через `HttpSessionCsrfTokenRepository`. Перед изменяющим запросом получите токен через `GET /api/auth/csrf`, сохраните cookie сессии и отправляйте значение токена в заголовке `X-CSRF-TOKEN`.
- `POST /api/auth/register` создаёт только роль `STUDENT`; роль из тела запроса не принимается. Пароль минимум 12 символов, строчная и заглавная буквы, цифра и спецсимвол; хэшируется BCrypt cost 12. Email уникален.
- `GET /api/auth/me` возвращает текущую учётную запись. `GET /api/info/**` открыт для чтения. Все CRUD-контроллеры требуют роль `STAFF` или `ADMIN` через `@PreAuthorize`; студент не может управлять чужими объектами.
- Администратор и сотрудник могут быть созданы при первом запуске через `APP_ADMIN_*` и `APP_STAFF_*` из `.env`. Замените значения `REPLACE_WITH...` на уникальные сильные пароли и не коммитьте `.env`. Эти bootstrap-переменные создают роли только если email ещё не существует; они не повышают роль существующей записи.
- `401` означает отсутствие/неверные учётные данные, `403` — недостаточные полномочия или CSRF-токен отсутствует/неверен, `409` — конфликт бизнес-правил. Учетные записи из демонстрационного `data.sql` не имеют пароля и не могут аутентифицироваться.

### Сценарий безопасности в Postman

1. `GET /api/auth/csrf` и сохранить cookie сессии и поле `token`.
2. `POST /api/auth/register` с заголовком `X-CSRF-TOKEN` и JSON `fullName`, `email`, `password`.
3. Выполнить `GET /api/auth/me` с Basic Auth для зарегистрированного пользователя.
4. Проверить, что студент получает `403` на `/api/rooms`, а чтение `/api/info` доступно.
5. Использовать bootstrap-пользователя STAFF/ADMIN для CRUD и бизнес-операций; перед каждым изменением отправлять CSRF-токен.


При первом создании базы Docker-скрипт создаёт отдельную роль `rbpo_app` без прав суперпользователя, создания баз или ролей. Приложение подключается с этой ролью. Если изменяете учётные данные после первоначального запуска контейнера, обновите роль в PostgreSQL вручную: init-скрипты запускаются только при создании пустого volume.

Настройки читаются из `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVER_PORT`. Не помещайте `.env` в Git. Для учебной среды Hibernate обновляет схему (`ddl-auto=update`); перед production следует перейти на миграции Flyway/Liquibase и `ddl-auto=validate`.

## Данные для проверки

`src/main/resources/data.sql` создаёт демонстрационные записи для всех пяти таблиц. Они предназначены для локальной разработки и не являются персональными данными реальных людей. SQL рассчитан на PostgreSQL и повторный запуск без дублирования ID.

## REST API

- `/api/users` — CRUD пользователей.
- `/api/rooms` — CRUD комнат и сведения о занятости.
- `/api/housing-applications` — CRUD заявлений и рассмотрение заявления.
- `/api/residence-contracts` — CRUD договоров проживания.
- `/api/room-transfer-requests` — CRUD запросов на перевод.
- `/api/info` — сведения и статистика.

## Пять бизнес-операций

1. **Подача заявления** — связывает пользователя с заявлением; запрещает второе нерассмотренное заявление того же пользователя.
2. **Заключение договора** — разрешено только по одобренному заявлению, проверяет существование комнаты, период и наличие свободного места.
3. **Заселение/назначение комнаты через договор** — изменяет состояние занятости, основанное на активных договорах, с проверкой вместимости.
4. **Завершение договора** — переводит активный договор в `TERMINATED` и освобождает место для последующих операций.
5. **Одобрение перевода** — проверяет активность договора и доступность целевой комнаты, затем обновляет комнату договора и состояние заявки.

Операции 1, 2 и 5 затрагивают связанные сущности. Сервис проверяет состояние объектов и возвращает конфликт, если переход недопустим. Проверяйте casos positivos e negativos pela coleção Postman.

## Coleção Postman

Importe `postman/RBPO-2026.postman_collection.json`. A coleção inclui exemplos de CRUD e operações de negócio para os recursos principais. Execute os pedidos de criação antes dos pedidos dependentes e substitua IDs de exemplo quando a base estiver diferente.

## Limitações conhecidas

- O seed de demonstração utiliza IDs fixos e deve ser usado apenas em base local descartável.
- O esquema é gerido por `ddl-auto=update`; para entrega controlada, recomenda-se uma migração versionada.
- A role da aplicação recebe permissões de criação no schema para permitir o bootstrap Hibernate neste projeto académico. Em produção, a criação/alteração de schema deve ser feita por uma role de migração separada e a role runtime deve ter apenas DML.

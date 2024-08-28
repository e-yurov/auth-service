create table user(
    id uuid primary key default gen_random_uuid(),
    name varchar(70),
    email varchar(50),
    keycloak_id varchar(50)
);

insert into user(id, name, email, keycloak_id)
VALUES ('18b3017a-025d-4e16-b041-ded27be2d5c3', 'user', 'user@test.com', '07766a30-8f8a-42f3-ab0c-b3aec1151b59'),
('88c1fee7-fd27-4f06-8aad-5ef93046911a', 'admin', 'admin@test.com', '5fc74e31-8576-4fee-adea-2c60a5408f18'),
('249bd142-75a5-44fe-bb0b-94581fe21242', 'Ivan', 'test@gmail.com', '8c24aa66-adf6-4ecf-a4db-73ba5b60359a');
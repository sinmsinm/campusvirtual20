
create table Campus (id_campus int not null primary key, nom varchar(255), responsable varchar(16)); 
create table Tecnics (id_tecnic varchar(16) primary key, nom varchar(255));

#create table Assistencia (ticket bigint not null primary key, id_campus int not null, id_categoria int not null, consulta varchar(255), prioritat int not null, data_inici date, data_fi date, estat int not null, solucio varchar(255), usuari varchar(16), id_tecnic varchar(16));
#create table Categoria (id_categoria int not null primary key, tipus varchar(255), descripcio varchar(255));

create table Assistencia (ticket serial4 not null primary key, id_campus int not null, id_categoria int not null, consulta varchar(255), prioritat int not null, data_inici date, data_fi date, estat int not null, solucio varchar(255), usuari varchar(16), id_tecnic varchar(16));
create table Categoria (id_categoria serial4 not null primary key, tipus varchar(255), descripcio varchar(255))

alter table Assistencia add column telefon varchar(256);
alter table Assistencia add column localitzacio varchar(256);
alter table Assistencia add column id_edifici int;
alter table Campus add column nom_respo varchar(256);


create table Edifici (id_edifici int not null primary key, id_campus int not null, nom_edifici varchar(32));

ALTER TABLE assistencia ALTER COLUMN consulta TYPE text;

ALTER TABLE assistencia ALTER COLUMN solucio TYPE text;

